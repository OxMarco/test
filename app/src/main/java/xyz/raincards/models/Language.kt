package xyz.raincards.models

const val GB = "GB"
// const val MT = "MT"

data class Language(
    val title: String,
    val languageCode: String,
    val countryCode: String
) {
    override fun toString(): String {
        return title
    }
}
