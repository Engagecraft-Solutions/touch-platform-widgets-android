package com.engagecraft.touchplatform.sdk

import android.util.Log
import org.json.JSONObject

internal class Util {
    companion object {
        fun prepareUrl(url: String) : String {
            return String.format(url, when (Environment.current) {
                Environment.ENV_INT -> ".int"
                Environment.ENV_PRE -> ".pre"
                else -> ""
            })
        }

        fun debug(data: String) {
            if (Environment.isDebug) {
                Log.d("EC_TOUCH", data)
                Environment.logListener?.onLog("EC_TOUCH $data")
            }
        }

        fun getLoginEventData() : JSONObject {
            return JSONObject().apply { put(Widget.PARAM_USER_ID, AuthManager.getUserId()) }
        }
    }
}