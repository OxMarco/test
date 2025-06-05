package xyz.raincards.utils

enum class Country(val code: Int, val shortName2: String, val shortName3: String, val fullName: String) {
    UNITED_STATES(840, "US", "USA", "United States of America"),
    ITALY(380, "IT", "ITA", "Italy"),
    MONTENEGRO(499, "ME", "MNE", "Montenegro"),
    MALTA(470, "MT", "MLT", "Malta");

    companion object {
        fun fromCode(code: Int): Country? {
            return entries.find { it.code == code }
        }

        fun fromShortName2(name2: String): Country? {
            return entries.find { it.shortName2 == name2 }
        }

        fun isValidCode(code: Int): Boolean {
            return entries.any { it.code == code }
        }
    }
}
