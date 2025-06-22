package xyz.raincards.api

object BinRouter {
    private val binRangesToIssuer = listOf(
        // NIMLAC
        45488800..45488850 to "RAIN",

        // RS-1 through RS-11 (all on base 45488800)
        45488851..45488899 to "RAIN",

        // RS-12 on base 45492405
        45492430 .. 45492455 to "RAIN",

        // RS-13 through RS-18 on base 45492406
        45492406  .. 45492505 to "RAIN",
    )

    private val issuerToUrl = mapOf(
        "RAIN" to "https://localhost:3000/v1/payments"
    )

    private fun lookupIssuer(bin: Int): String? =
        binRangesToIssuer.firstOrNull { bin in it.first }?.second

    private fun lookupUrl(issuer: String): String? =
        issuerToUrl[issuer]

    fun process(cardN: Int): String? {
        val bin = cardN.toString().substring(0, 8)
        val issuer = lookupIssuer(bin.toInt())
        if (issuer.isNullOrBlank()) return ""

        val serverUrl = lookupUrl(issuer)
        if (serverUrl.isNullOrBlank()) return ""

        // @todo api call to the issuer
        /*
        {
          "card number",
          "amount",
          "pan",
          "description"
        }
         */
        return "something"
    }
}
