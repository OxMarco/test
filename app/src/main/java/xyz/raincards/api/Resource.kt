package xyz.raincards.api

import androidx.annotation.Keep

@Keep sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {

    @Keep class Success<T>(data: T?) : Resource<T>(data)

    @Keep class Loading<T>(data: T? = null) : Resource<T>(data)

    @Keep class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)
}
