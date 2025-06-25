package xyz.raincards.models.responses

data class ChargeResponse(
    val id: String,
    val createdAt: String,
    val amount: String,
    val description: String
)
