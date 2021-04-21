package com.engagecraft.touchplatform.sdk

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal class DataManager {
    companion object {
        private const val SETTINGS_FILE = "ec_touch_widget_settings"

        private fun sp(context: Context) : SharedPreferences {
            return context.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE)
        }

        private fun getKey(id: String) : String {
            return "widget_$id${AuthManager.getUserId()?.let { "_$it" } ?: ""}"
        }

        fun put(context: Context, id: String, data: String?) {
            return sp(context).edit { putString(getKey(id), data) }
        }

        fun get(context: Context, id: String) : String? {
            return sp(context).getString(getKey(id), null)
        }

    }
}