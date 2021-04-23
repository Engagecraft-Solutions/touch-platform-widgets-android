package com.engagecraft.touchplatform.sdk

internal class AuthManager {
    companion object {
        private var userId: String? = null

        fun login(userId: String) {
            AuthManager.userId = userId
            Widget.notify(JSInterface.EVENT_LOGIN, Util.getLoginEventData())
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