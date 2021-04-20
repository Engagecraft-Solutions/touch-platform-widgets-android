package com.engagecraft.touchplatform.sdk

import android.content.Context
import android.view.View
import org.json.JSONObject

class TouchPlatformSDK {
    companion object {

        internal var clientId: String? = null
        internal var language: String? = null
        internal var preview: Boolean = false
        internal var listener: Listener? = null

        fun init(clientId: String, language: String? = null, preview: Boolean = false, listener: Listener? = null) {
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
            Widget.notify(JSInterface.EVENT_LOGIN, JSONObject().apply { put(Widget.PARAM_USER_ID, userId) })
        }

        fun logout() {
            AuthManager.logout()
            Widget.notify(JSInterface.EVENT_LOGOUT)
        }

        fun getWidget(context: Context, id: String, location: String? = null) : View {
            return Widget.create(context, id, location)
        }

    }

    interface Listener {
        fun showLogin()
    }

}