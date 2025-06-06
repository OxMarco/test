package xyz.raincards.ui.payment

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import xyz.raincards.api.Api
import xyz.raincards.api.BaseDataSource
import xyz.raincards.api.Resource
import xyz.raincards.models.requests.ChargeRequest

class PaymentRepository @Inject constructor(
    private val api: Api
//    private val database: AppDatabase
) : BaseDataSource() {

    suspend fun charge(request: ChargeRequest) = flow {
        emit(Resource.Loading())
        val result = safeApiCall { api.charge(request) }
        emit(result)
    }.flowOn(Dispatchers.IO)
}
