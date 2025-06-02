package xyz.raincards.utils

import xyz.raincards.BuildConfig
import xyz.raincards.models.GB
import xyz.raincards.models.Language
import xyz.raincards.models.ME

object Setup {

    const val test_deviceID = "testPOS"

    private const val prodBaseURL = "https://api.raincards.xyz/v1/"
    private const val devBaseURL = "http://localhost:3000/v1/"

    val BASE_URL = if (BuildConfig.IS_DEV) devBaseURL else prodBaseURL

    const val defaultCurrency = "$"
    const val USD = "$"
    val currencies = listOf<String>(
        USD,
        "€",
        "£"
    )

    const val defaultLanguageCode = GB
    val languages = listOf<Language>(
        Language("ENG", "en", GB),
        Language("MNE", "sr", ME)
    )
}
