package xyz.raincards.api

import com.google.gson.Gson
import retrofit2.Response
import xyz.raincards.AndroidApp
import xyz.raincards.R
import xyz.raincards.api.exceptions.NoConnectivityException
import xyz.raincards.models.responses._base.ResponseError

abstract class BaseDataSource {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: kotlin.run {
                    Resource.Error(
                        AndroidApp.instance.applicationContext.getString(R.string.response_is_null)
                    )
                }
            } else {
                val error = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(error, ResponseError::class.java)
                Resource.Error(errorResponse.message?.get(0))
            }
        } catch (e: Exception) {
            if (e is NoConnectivityException) {
                Resource.Error(
                    AndroidApp.instance.applicationContext.getString(
                        R.string.no_internet_connection
                    )
                )
            } else {
                Resource.Error(
                    AndroidApp.instance.applicationContext
                        .getString(R.string.error_message) + e.message
                )
            }
        }
    }
}
