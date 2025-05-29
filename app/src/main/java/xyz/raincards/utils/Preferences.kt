package xyz.raincards.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import xyz.raincards.AndroidApp

class Preferences {
    companion object {
        private const val PREFS = "PREFS"
        private const val DARK_MODE_ON = "DARK_MODE_ON"
        private const val TIP_SCREEN_ON = "TIP_SCREEN_ON"
        private const val AUTO_PRINT_RECEIPT_ON = "AUTO_PRINT_RECEIPT_ON"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"

        private val preferences: SharedPreferences
            get() = AndroidApp.instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        fun getAccessToken() = preferences.getString(ACCESS_TOKEN, "").orEmpty()
        fun setAccessToken(token: String) {
            preferences.edit { putString(ACCESS_TOKEN, token) }
        }

        fun isDarkModeOn() = preferences.getBoolean(DARK_MODE_ON, false)
        fun setDarkModeOn(on: Boolean) {
            preferences.edit { putBoolean(DARK_MODE_ON, on) }
        }
        fun isTipScreenOn() = preferences.getBoolean(TIP_SCREEN_ON, false)
        fun setTipScreenOn(on: Boolean) {
            preferences.edit { putBoolean(TIP_SCREEN_ON, on) }
        }

        fun isAutoPrintReceiptOn() = preferences.getBoolean(AUTO_PRINT_RECEIPT_ON, false)
        fun setAutoPrintReceiptOn(on: Boolean) {
            preferences.edit { putBoolean(AUTO_PRINT_RECEIPT_ON, on) }
        }

        fun clearPreferences() = preferences.edit { clear() }
    }
}
