package com.engagecraft.touchplatform.sdk

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject


internal class JSInterface(private val context: Context, private val widgetId: String) {
    companion object {
        // JS interface name
        const val NAME = "ecTouchPlatformBridge"
        // platform object name
        private const val PLATFORM_NAME = "ecTouchPlatform"

        const val EVENT_LOGIN = "onLogin"
        const val EVENT_LOGOUT = "onLogout"

        fun notify(view: WebView?, event: String, data: JSONObject? = null) {
            val jsEvent = "(function() { window.$PLATFORM_NAME.events.emit('$event', ${data?.toString() ?: "null"}); })();"
            view?.evaluateJavascript(jsEvent, null)
            Util.debug("Emitting JS event: $jsEvent")
        }
    }

    @JavascriptInterface
    fun share(data: String) {
        context.startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "text/plain"
        }, null))
        Util.debug("Sharing data: $data")
    }

    @JavascriptInterface
    fun showLogin() {
        TouchPlatformSDK.listener?.showLogin()
    }

    @JavascriptInterface
    fun isLoggedIn() : Boolean {
        return !TextUtils.isEmpty(AuthManager.getUserId())
    }

    @JavascriptInterface
    fun getUserID() : String? {
        return AuthManager.getUserId()
    }

    @JavascriptInterface
    fun putSettingsData(data: String?) {
        DataManager.put(context, widgetId, data)
        Util.debug("Saving settings ($widgetId): $data")
    }

    @JavascriptInterface
    fun getSettingsData() : String? {
        return DataManager.get(context, widgetId).apply {
            Util.debug("Getting settings ($widgetId): $this")
        }
    }

}