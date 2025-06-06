package xyz.raincards.ui.main

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEventsViewModel

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseEventsViewModel() {

    private val _eventChannel = Channel<Event>()
    val events = _eventChannel.receiveAsFlow()

    sealed class Event : BaseEvent() {
//        data object ChargeSuccess : Event()
//        data object ChargeError : Event()
    }
}
