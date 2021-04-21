package com.engagecraft.touchplatform.sdk

import org.json.JSONObject

internal class AuthManager {
    companion object {
        private var userId: String? = null

        fun login(userId: String) {
            AuthManager.userId = userId
            Widget.notify(JSInterface.EVENT_LOGIN, JSONObject().apply { put(Widget.PARAM_USER_ID, userId) })
        }

        fun logout() {
            userId = null
            Widget.notify(JSInterface.EVENT_LOGOUT)
        }

        fun getUserId() : String? {
            return userId
        }
    }
}