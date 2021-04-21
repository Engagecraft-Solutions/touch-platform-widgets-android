package com.engagecraft.touchplatform.demo

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val RESULT_RELOAD = Activity.RESULT_FIRST_USER + 1
        const val RESULT_AUTH = Activity.RESULT_FIRST_USER + 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
        private var reloadResult = false
        private var authResult = true

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceManager.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
            if (key == getString(R.string.settings_use_cards)) {
                reloadResult = true
            }
            authResult = authResult && key == getString(R.string.settings_user_id)

            activity?.setResult(when {
                reloadResult -> RESULT_RELOAD
                authResult -> RESULT_AUTH
                else -> Activity.RESULT_OK
            })
        }

    }

}