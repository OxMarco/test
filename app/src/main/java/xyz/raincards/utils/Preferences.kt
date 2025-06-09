package xyz.raincards.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Locale
import xyz.raincards.AndroidApp

class Preferences {
    companion object {

        private val PREFS = "PREFS"
        private const val DARK_MODE_ON = "DARK_MODE_ON"
        private const val TIP_SCREEN_ON = "TIP_SCREEN_ON"
        private const val AUTO_PRINT_RECEIPT_ON = "AUTO_PRINT_RECEIPT_ON"
        private const val CURRENCY = "CURRENCY"
        private const val LANGUAGE = "LANGUAGE"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"

        private const val TRANSMISSION_ID = "TRANSMISSION_ID"

        private val preferences: SharedPreferences
            get() = AndroidApp.instance.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        fun getAccessToken() = preferences.getString(ACCESS_TOKEN, "").orEmpty()
        fun setAccessToken(token: String) {
            preferences.edit { putString(ACCESS_TOKEN, token) }
        }

        fun getSelectedCurrencyCode() = preferences.getInt(CURRENCY, Setup.defaultCurrencyCode)
        fun saveSelectedCurrency(currencyCode: Int) {
            preferences.edit { putInt(CURRENCY, currencyCode) }
        }

        fun getSelectedLanguage() = preferences.getString(LANGUAGE, Setup.defaultLanguageCode) ?: ""
        fun saveSelectedLanguage(language: String) {
            preferences.edit { putString(LANGUAGE, language) }
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

        fun getTransmissionID() = String.format(
            Locale.UK,
            "%02d",
            preferences.getInt(TRANSMISSION_ID, 0)
        )

        fun incrementTransmissionID() {
            val id = preferences.getInt(TRANSMISSION_ID, 0)
            if (id == 99) {
                preferences.edit { putInt(TRANSMISSION_ID, 0) }
            } else {
                preferences.edit { putInt(TRANSMISSION_ID, id + 1) }
            }
        }

        fun clearPreferences() = preferences.edit { clear() }
    }
}
