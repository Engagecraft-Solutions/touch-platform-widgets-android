package com.engagecraft.touchplatform.sdk

import android.util.Log

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
            }
        }
    }
}