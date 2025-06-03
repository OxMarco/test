package xyz.raincards.api.adapter

import retrofit2.Response
import xyz.raincards.api.Api
import xyz.raincards.api.BaseDataSource
import xyz.raincards.models.requests.CodeRequest
import xyz.raincards.models.requests.LoginRequest
import xyz.raincards.models.responses.CodeResponse
import xyz.raincards.models.responses.LoginResponse

class RainAdapter : Api, BaseDataSource() {
    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun sendCode(request: CodeRequest): Response<CodeResponse> {
        TODO("Not yet implemented")
    }
}
