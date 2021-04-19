package com.engagecraft.touchplatformsdk

internal class Util {
    companion object {
        fun prepareUrl(url: String) : String {
            return String.format(url, when (TouchPlatformSDK.environment) {
                TouchPlatformSDK.ENV_INT -> ".int"
                TouchPlatformSDK.ENV_PRE -> ".pre"
                else -> ""
            })
        }
    }
}