package com.engagecraft.touchplatform.sdk

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class Environment {
    companion object {
        const val ENV_INT = "debug"
        const val ENV_PRE = "pre"
        const val ENV_PROD = "release"

        internal var current: String = BuildConfig.BUILD_TYPE
        internal var isDebug: Boolean = false
        internal var logListener: LogListener? = null

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun setEnvironment(environment: String) {
            current = environment
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun setDebugMode(debug: Boolean) {
            isDebug = debug
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun setLogListener(listener: LogListener) {
            logListener = listener
        }
    }

    fun interface LogListener {
        fun onLog(msg: String)
    }
}