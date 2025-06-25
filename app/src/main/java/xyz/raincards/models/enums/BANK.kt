package xyz.raincards.models.enums

enum class BANK(
    val title: String,
    val connectionType: String,
    val url: String
) {
    RAIN("RAIN", "rest", "https://5edb-77-71-138-87.ngrok-free.app/v1/payment"),
//    RAIN("RAIN", "rest", "https://api.raincards.xyz/v1/payment"), //prod version

    CKB("CKB", "socket", "");

    companion object {
    }
}
