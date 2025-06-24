package xyz.raincards.ui.payment

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.api.BinRouter
import xyz.raincards.models.enums.BANK
import xyz.raincards.models.requests.ChargeRequest
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEventsViewModel

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository
) : BaseEventsViewModel() {

    fun charge(
        cardNumber: String,
        total: String,
        pan: String,
        desc: String
    ) = viewModelScope.launch {
        when (val bank = BinRouter.getBank(cardNumber)) {
            BANK.RAIN -> {
                println("-----send charge request to RAIN")

                val request = ChargeRequest(cardNumber, total.replace(".", ""), pan, desc)
                executeWithCommonErrorHandling(
                    showLoading = false,
                    apiCall = { repository.charge(bank.url, request) },
                    onSuccess = { response ->
                        val message = response.message
                        _eventChannel.send(Event.ChargeSuccess)
                    }
                )
            }

            BANK.CKB -> {
                println("-----start socket connection to CKB")
                // socket connection
            }
        }

        // viewModelScope.launch {
        // delay(3000)
        // _eventChannel.send(Event.ChargeSuccess)
        // }
    }

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
        data object ChargeSuccess : Event()
        data object ChargeError : Event()
    }
}
