package xyz.raincards.utils

import xyz.raincards.BuildConfig
import xyz.raincards.models.GB
import xyz.raincards.models.Language
import xyz.raincards.models.enums.Country
import xyz.raincards.models.enums.Currency

// import xyz.raincards.models.MT

object Setup {

    const val test_deviceID = "testPOS"
    const val test_merchantID = "000000000000001"

    const val SEARCH_CARD_TIMEOUT = 60000
    const val BEEP_LENGTH = 500
    const val CIRCLE_ANIMATION_LENGTH = 300L

    private const val prodBaseURL = "https://api.raincards.xyz/v1/"
    private const val devBaseURL = "https://5026-77-71-138-87.ngrok-free.app/v1/"

    val BASE_URL = if (BuildConfig.IS_DEV) devBaseURL else prodBaseURL

    val defaultCurrencyCode = Currency.EUR.code
    fun getSelectedCurrency() = Currency.entries.find {
        it.code == Preferences.getSelectedCurrencyCode()
    }!!

    val defaultCountryCode = Country.MALTA.code

    const val defaultLanguageCode = GB
    val languages = listOf<Language>(
        Language("EN", "en", GB)
//        Language("MT", "mt", MT)
    )
}
