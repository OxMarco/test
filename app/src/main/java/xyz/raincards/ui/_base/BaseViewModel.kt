package xyz.raincards.ui._base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import xyz.raincards.utils.Preferences

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val repository: BaseRepository
) : ViewModel() {

    protected val _baseEventChannel = Channel<BaseEvent>()
    val baseEvents = _baseEventChannel.receiveAsFlow()

    fun logout() {
        Preferences.clearPreferences()
        repository.clearDatabase()
    }
}
