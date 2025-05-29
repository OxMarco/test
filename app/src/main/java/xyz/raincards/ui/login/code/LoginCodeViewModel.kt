package xyz.raincards.ui.login.code

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.models.requests.CodeRequest
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEvent.*
import xyz.raincards.ui._base.BaseEventsViewModel
import xyz.raincards.utils.Preferences

@HiltViewModel
class LoginCodeViewModel @Inject constructor(
    private val repository: LoginCodeRepository
) : BaseEventsViewModel() {

    fun sendCode(
        code: String
    ) = viewModelScope.launch {
        val request = CodeRequest(code)
//        executeWithCommonErrorHandling(
//            apiCall = { repository.sendCode(request) },
//            onSuccess = { response ->
// //                                repository.insertUser(response.user)
// //                Preferences.setAccessToken(response.authorization)
// //                _eventChannel.send(Event.CodeSent)
//            }
//        )

        Preferences.setAccessToken("token to skip login screens")
        _eventChannel.send(Event.CodeValidated)
    }

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
        data object CodeValidated : Event()
    }
}
