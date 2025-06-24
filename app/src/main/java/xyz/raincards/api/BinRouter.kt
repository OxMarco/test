package xyz.raincards.api

import xyz.raincards.models.enums.BANK

object BinRouter {

    private val binRangesToIssuer = listOf(
        // NIMLAC
        45488800..45488850 to BANK.RAIN,

        // RS-1 through RS-11 (all on base 45488800)
        45488851..45488899 to BANK.RAIN,

        // RS-12 on base 45492405
        45492430..45492455 to BANK.RAIN,

        // RS-13 through RS-18 on base 45492406
        45492406..45492505 to BANK.RAIN
    )

    private fun lookupIssuer(bin: Int) = binRangesToIssuer.firstOrNull { bin in it.first }?.second

    fun getBank(cardN: String): BANK {
        val bin = cardN.substring(0, 8)
        val issuer = lookupIssuer(bin.toInt()) ?: BANK.CKB

        return issuer
    }
}
