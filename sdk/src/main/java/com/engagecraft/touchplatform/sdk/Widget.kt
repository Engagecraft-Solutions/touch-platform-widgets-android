package com.engagecraft.touchplatform.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.SoftReference


internal class Widget(context: Context) : FrameLayout(context) {
    companion object {
        private const val URL = "https://widgets%s.touch.global/js/vendor/static/app.html"
        private const val PARAM_HASH = "hash"
        private const val PARAM_CLIENT_ID = "clientID"
        private const val PARAM_LANG = "language"
        private const val PARAM_PREVIEW = "preview"
        private const val PARAM_DEBUG = "debug"
        private const val PARAM_LOCATION = "location"
        internal const val PARAM_USER_ID = "userID"

        private val listeners: MutableList<SoftReference<Widget>> = mutableListOf()
        private fun addListener(widget: Widget) {
            listeners.add(SoftReference(widget))
        }
        private fun removeListener(widget: Widget) {
            listeners.removeAll { it.get() == widget }
        }
        fun notify(event: String, data: JSONObject? = null) {
            listeners.forEach {
                JSInterface.notify(it.get()?.getWidget(), event, data)
            }
        }

        private fun getSize(context: Context, size: Int) : Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun create(context: Context, id: String, location: String? = null) : Widget {
            return Widget(context).apply {
                setWidgetId(id)
                setLocation(location)
            }
        }
    }

    private lateinit var widgetId: String
    private var location: String? = null

    private fun setWidgetId(id: String) {
        this.widgetId = id
    }

    private fun setLocation(location: String?) {
        this.location = location
    }

    private fun setup() {
        getWidget()?.let { widget ->
            AuthManager.getUserId()?.let {
                JSInterface.notify(widget, JSInterface.EVENT_LOGIN, Util.getLoginEventData())
            } ?: run {
                JSInterface.notify(widget, JSInterface.EVENT_LOGOUT)
            }
        } ?: run {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    Backend.get().availability(widgetId)?.data?.let {
                        if (it.available) {
                            show(it.params?.height ?: 0)
                        } else {
                            hide()
                        }
                    } ?: hide()
                } catch (e: Exception) {
                    hide()
                }
            }
        }
    }

    private fun getWidgetUrl() : String {
        return Uri.parse(Util.prepareUrl(URL)).buildUpon().apply {
            appendQueryParameter(PARAM_HASH, widgetId)
            appendQueryParameter(PARAM_PREVIEW, TouchPlatformSDK.preview.toString())
            appendQueryParameter(PARAM_DEBUG, Environment.isDebug.toString())
            TouchPlatformSDK.clientId?.let { appendQueryParameter(PARAM_CLIENT_ID, it) }
            TouchPlatformSDK.language?.let { appendQueryParameter(PARAM_LANG, it) }
            AuthManager.getUserId()?.let { appendQueryParameter(PARAM_USER_ID, it) }
            location?.let { appendQueryParameter(PARAM_LOCATION, it) }
        }.build().toString()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun show(height: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            addView(WebView(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getSize(context, height)
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(m: ConsoleMessage): Boolean {
                        Util.debug("WebView log ($widgetId) - [${m.messageLevel()}] ${m.message()}")
                        return true
                    }
                }
                addJavascriptInterface(JSInterface(this.context, widgetId), JSInterface.NAME)
                loadUrl(getWidgetUrl())
                Util.debug("Starting widget: $url")
            })
        }
    }

    private fun hide() {
        GlobalScope.launch(Dispatchers.Main) {
            visibility = GONE
        }
    }

    private fun getWidget() : WebView? {
        return getChildAt(0) as? WebView
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addListener(this)
        setup()
        Util.debug("Widget $widgetId attached to window")
    }

    override fun onDetachedFromWindow() {
        removeListener(this)
        super.onDetachedFromWindow()
        Util.debug("Widget $widgetId detached from window")
    }

}