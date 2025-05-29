package xyz.raincards.ui._base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xyz.raincards.R
import xyz.raincards.api.Resource

open class BaseEventsViewModel : ViewModel() {

    protected fun <T> executeWithCommonErrorHandling(
        showLoading: Boolean = true,
        apiCall: suspend () -> Flow<Resource<T>>,
        onSuccess: suspend (T) -> Unit,
        onError: (suspend (String) -> Unit)? = null // Optional error callback
    ) = viewModelScope.launch {
        apiCall()
            .catch { e -> _baseEventChannel.send(BaseEvent.ShowStringError(e.toString())) }
            .collect { resource ->
                when (resource) {
                    is Resource.Loading -> _baseEventChannel.send(
                        BaseEvent.ShowLoading(showLoading)
                    )
                    is Resource.Error -> onError?.invoke(resource.error.orEmpty()) ?: run {
                        _baseEventChannel.send(BaseEvent.ShowStringError(resource.error))
                    }
                    is Resource.Success -> {
                        _baseEventChannel.send(BaseEvent.ShowLoading(false))
                        resource.data?.let {
                            onSuccess(it)
                        } ?: let {
                            _baseEventChannel.send(
                                BaseEvent.ShowIntError(R.string.response_is_null)
                            )
                        }
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        _baseEventChannel.close() // Close the channel to avoid memory leaks
    }

    protected val _baseEventChannel = Channel<BaseEvent>()
    val baseEvents = _baseEventChannel.receiveAsFlow()
}
