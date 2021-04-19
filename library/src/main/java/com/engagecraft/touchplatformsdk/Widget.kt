package com.engagecraft.touchplatformsdk

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class Widget(context: Context) : FrameLayout(context) {
    companion object {
        private const val URL = "https://widgets%s.touch.global/js/vendor/static/app.html"
        private const val PARAM_HASH = "hash"
        private const val PARAM_CLIENT_ID = "clientID"
        private const val PARAM_USER_ID = "userID"
        private const val PARAM_LANG = "language"
        private const val PARAM_PREVIEW = "preview"

        private val listeners: MutableList<Widget> = mutableListOf()
        private fun addListener(widget: Widget) {
            listeners.add(widget)
        }
        private fun removeListener(widget: Widget) {
            listeners.remove(widget)
        }
        fun notify(event: String) {
            listeners.forEach {
                JSInterface.notify(it.getChildAt(0) as WebView, event)
            }
        }

        private fun getSize(context: Context, size: Int) : Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                size.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        private fun getUrl(id: String) : String {
            return Uri.parse(Util.prepareUrl(URL)).buildUpon().apply {
                appendQueryParameter(PARAM_HASH, id)
                appendQueryParameter(PARAM_PREVIEW, (if (TouchPlatformSDK.preview) 1 else 0).toString())
                TouchPlatformSDK.clientId?.let { appendQueryParameter(PARAM_CLIENT_ID, it) }
                TouchPlatformSDK.language?.let { appendQueryParameter(PARAM_LANG, it) }
                AuthManager.getUserId()?.let { appendQueryParameter(PARAM_USER_ID, it) }
            }.build().toString()
        }

        fun create(context: Context, id: String) : Widget {
            return Widget(context).apply {
                setWidgetId(id)
            }
        }
    }

    private lateinit var widgetId: String

    private fun setWidgetId(id: String) {
        widgetId = id
    }

    private suspend fun setup() {
        if (childCount == 0) {
            Backend.get().availability(widgetId)?.data?.let {
                if (it.available) {
                    show(it.height)
                } else {
                    hide()
                }
            } ?: hide()
        } else {
           // TODO. re-added. "refresh" the widget - send onLogin/onLogout?
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun show(height: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            addView(WebView(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getSize(context, height))
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                addJavascriptInterface(JSInterface(), JSInterface.NAME)
                loadUrl(getUrl(widgetId))
            })
        }
    }

    private fun hide() {
        GlobalScope.launch(Dispatchers.Main) {
            visibility = GONE
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addListener(this)
        GlobalScope.launch(Dispatchers.IO) {
            setup()
        }
    }

    override fun onDetachedFromWindow() {
        removeListener(this)
        super.onDetachedFromWindow()
    }

}