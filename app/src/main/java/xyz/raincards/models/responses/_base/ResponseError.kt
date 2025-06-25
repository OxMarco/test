package xyz.raincards.models.responses._base

data class ResponseError(
    val message: List<String>?,
    val error: String?,
    val statusCode: Int?
)