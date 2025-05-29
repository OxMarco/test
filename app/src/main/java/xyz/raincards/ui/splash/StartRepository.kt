package xyz.raincards.ui.splash

import javax.inject.Inject
import xyz.raincards.api.Api
import xyz.raincards.api.BaseDataSource

class StartRepository @Inject constructor(
    private val api: Api
//    private val database: AppDatabase
) : BaseDataSource() {

//    suspend fun getConfig() = flow {
//        emit(Resource.Loading())
//        val result = safeApiCall { api.getConfig() }
//        emit(result)
//    }.flowOn(Dispatchers.IO)

//    suspend fun getUser() = flow {
//        emit(Resource.Loading())
//        val result = safeApiCall { api.getUserProfile() }
//        emit(result)
//    }.flowOn(Dispatchers.IO)
}
