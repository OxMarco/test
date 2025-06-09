package xyz.raincards.models.enums

enum class Currency(
    val code: Int,
    val fullName: String,
    val symbol: String,
    val description: String
) {
    USD(840, "USD", "$", "US Dollar"),
    EUR(978, "EUR", "€", "Euro");

    companion object {
        fun fromCode(code: Int): Currency? {
            return entries.find { it.code == code }
        }

        fun isValidCode(code: Int): Boolean {
            return entries.any { it.code == code }
        }
    }
}
