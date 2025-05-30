package xyz.raincards.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import xyz.raincards.models.requests.CodeRequest
import xyz.raincards.models.requests.LoginRequest
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

//    @POST("brands/user-favorites")
//    suspend fun saveAsFavorite(
//        @Body request: SaveFavoriteBrandRequest,
//        @Header("Authorization") authHeader: String = getAuthHeader()
//    ): Response<ResponseData<Brand>>
}
