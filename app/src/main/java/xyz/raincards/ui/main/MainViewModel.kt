package xyz.raincards.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.models.requests.ChargeRequest
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEventsViewModel

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseEventsViewModel() {

    fun charge(
        card: String,
        amount: String,
        currency: String,
        pan: String,
        aid: String
    ) = viewModelScope.launch {
        val request = ChargeRequest(card, amount, currency, pan, aid)
        executeWithCommonErrorHandling(
            showLoading = false,
            apiCall = { repository.charge(request) },
            onSuccess = { response ->
                val message = response.message
                _eventChannel.send(Event.ChargeSuccess)
            }
        )
    }

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
        data object ChargeSuccess : Event()
        data object ChargeError : Event()
    }
}
