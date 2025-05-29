package xyz.raincards.ui.login.email

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.models.requests.LoginRequest
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEvent.*
import xyz.raincards.ui._base.BaseEventsViewModel

@HiltViewModel
class LoginEmailViewModel @Inject constructor(
    private val repository: LoginEmailRepository
) : BaseEventsViewModel() {

    fun login(
        email: String
    ) = viewModelScope.launch {
        val request = LoginRequest(email)
//        executeWithCommonErrorHandling(
//            apiCall = { repository.login(request) },
//            onSuccess = { response ->
// //                                 repository.insertUser(response.user)
// //                 Preferences.setAccessToken(response.authorization)
// //                 _eventChannel.send(Event.CodeSent)
//            }
//        )

        _eventChannel.send(Event.CodeSent)
    }

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
        data object CodeSent : Event()
    }
}
