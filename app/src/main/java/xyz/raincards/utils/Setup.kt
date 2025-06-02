package xyz.raincards.utils

import xyz.raincards.BuildConfig

object Setup {

    const val test_deviceID = "testPOS"

    private const val prodBaseURL = "https://api.raincards.xyz/v1/"
    private const val devBaseURL = "http://localhost:3000/v1/"

    val BASE_URL = if (BuildConfig.IS_DEV) devBaseURL else prodBaseURL

    fun getCurrency() = "$"
}
