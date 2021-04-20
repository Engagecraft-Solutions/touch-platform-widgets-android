package com.engagecraft.touchplatform.sdk

internal class AuthManager {
    companion object {
        private var userId: String? = null

        fun login(userId: String) {
            AuthManager.userId = userId
        }

        fun logout() {
            userId = null
        }

        fun getUserId() : String? {
            return userId
        }
    }
}