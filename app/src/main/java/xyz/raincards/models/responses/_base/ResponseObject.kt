package xyz.raincards.models.responses._base

data class ResponseObject<T>(
    val data: T,
    val message: String?
)
