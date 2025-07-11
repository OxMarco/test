package xyz.raincards.models.enums

enum class BANK(
    val title: String,
    val connectionType: String,
    val url: String
) {
    RAIN("RAIN", "rest", "https://5026-77-71-138-87.ngrok-free.app/v1/payment"),
    DEFAULT("DEFAULT", "socket", "");

    companion object {
    }
}
