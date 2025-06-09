package xyz.raincards.socketconnection

import xyz.raincards.models.enums.TransactionCode

data class ParsedResponse(
    val transactionCode: String,
    val status: String? = null,
    val message: String? = null
) {
    fun getType(): TransactionCode {
        return when (transactionCode) {
            "50" -> TransactionCode.LOGON
            "95" -> TransactionCode.HANDSHAKE
            "00" -> TransactionCode.TRANSACTION
            else -> TransactionCode.ERROR
        }
    }

    fun isSuccessful(): Boolean {
        return status?.let {
            it.toInt() <= 10
        } ?: run {
            false
        }
    }
}
