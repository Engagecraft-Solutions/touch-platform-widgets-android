package com.engagecraft.touchplatformsdk

import android.content.Context
import android.view.View
import androidx.annotation.StringDef

class TouchPlatformSDK {
    companion object {

        const val ENV_INT = "debug"
        const val ENV_PRE = "pre"
        const val ENV_PROD = "release"
        @StringDef(ENV_INT, ENV_PRE, ENV_PROD)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Env

        internal var environment: String = BuildConfig.BUILD_TYPE
        internal var clientId: String? = null
        internal var language: String? = null
        internal var preview: Boolean = false
        internal var listener: Listener? = null

        fun init(clientId: String, @Env environment: String? = null, language: String? = null, preview: Boolean = false, listener: Listener? = null) {
            environment?.let { TouchPlatformSDK.environment = it }
            TouchPlatformSDK.clientId = clientId
            TouchPlatformSDK.language = language
            TouchPlatformSDK.preview = preview
            TouchPlatformSDK.listener = listener
        }

        suspend fun isAvailable(id: String) : Boolean {
            return try {
                Backend.get().availability(id)?.data?.available == true
            } catch (e: Exception) {
                false
            }
        }

        fun login(userId: String) {
            AuthManager.login(userId)
            Widget.notify(JSInterface.EVENT_LOGIN)
        }

        fun logout() {
            AuthManager.logout()
            Widget.notify(JSInterface.EVENT_LOGOUT)
        }

        fun getWidget(context: Context, id: String) : View {
            return Widget.create(context, id)
        }

    }

    interface Listener {
        fun showLogin()
        fun isLoggedIn() : Boolean
        fun getUserID() : String?
    }
}