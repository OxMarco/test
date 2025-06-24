package xyz.raincards.models.enums

enum class BANK(
    val title: String,
    val connectionType: String,
    val url: String
) {
    RAIN("RAIN", "rest", "https://localhost:3000/v1/payments"),
//    RAIN("RAIN", "rest", "https://localhost:3000/v1/payments"), //prod version

    CKB("CKB", "socket", "");

    companion object {

    }
}
