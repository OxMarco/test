package xyz.raincards.models.responses._base

data class ResponseList<T>(
    val data: List<T>,
    val success: Boolean,
    val message: String?
)
