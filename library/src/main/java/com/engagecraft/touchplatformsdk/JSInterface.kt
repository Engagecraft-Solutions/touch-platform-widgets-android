package com.engagecraft.touchplatformsdk

import android.webkit.JavascriptInterface
import android.webkit.WebView

internal class JSInterface {
    companion object {
        // JS interface name
        const val NAME = "ecTouchPlatformBridge"
        // platform object name
        private const val PLATFORM_NAME = "ecTouchPlatform"

        const val EVENT_LOGIN = "onLogin"
        const val EVENT_LOGOUT = "onLogout"

        fun notify(view: WebView, event: String) {
            view.evaluateJavascript("(function() { window.${PLATFORM_NAME}.events.emit('${event}'); })();", null)
        }
    }

    @JavascriptInterface
    fun showLogin() {
        TouchPlatformSDK.listener?.showLogin()
    }

    @JavascriptInterface
    fun isLoggedIn() : Boolean {
        return TouchPlatformSDK.listener?.isLoggedIn() ?: false
    }

    @JavascriptInterface
    fun getUserID() : String {
        return TouchPlatformSDK.listener?.getUserID() ?: "0"
    }
}