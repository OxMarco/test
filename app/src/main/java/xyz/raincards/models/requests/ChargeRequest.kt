package xyz.raincards.models.requests

data class ChargeRequest(
    val card: String,
    val amount: String,
    val currency: String,
    val pan: String,
    val aid: String
)
