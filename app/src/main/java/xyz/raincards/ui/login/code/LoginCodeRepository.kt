package xyz.raincards.ui.login.code

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import xyz.raincards.api.Api
import xyz.raincards.api.BaseDataSource
import xyz.raincards.api.Resource
import xyz.raincards.models.requests.CodeRequest

class LoginCodeRepository @Inject constructor(
    private val api: Api
//    private val database: AppDatabase
) : BaseDataSource() {

//    private val userDAO = database.userDAO()
// suspend fun getUser() = userDAO.getUser()
//    suspend fun insertUser(user: User) {
//        userDAO.insertUser(user)
//    }

    suspend fun login(request: CodeRequest) = flow {
        emit(Resource.Loading())
        val result = safeApiCall { api.sendCode(request) }
        emit(result)
    }.flowOn(Dispatchers.IO)
}
