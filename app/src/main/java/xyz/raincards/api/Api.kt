package xyz.raincards.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import xyz.raincards.models.requests.ChargeRequest
import xyz.raincards.models.requests.CodeRequest
import xyz.raincards.models.requests.LoginRequest
import xyz.raincards.models.responses.ChargeResponse
import xyz.raincards.models.responses.CodeResponse
import xyz.raincards.models.responses.LoginResponse

interface Api {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("code")
    suspend fun sendCode(
        @Body request: CodeRequest
    ): Response<CodeResponse>

    @GET("balance")
    suspend fun getBalance(
        @Query("code") code: String
    ): Response<CodeResponse>

    @POST
    suspend fun charge(
        @Url url: String,
        @Body request: ChargeRequest
    ): Response<ChargeResponse>
}
