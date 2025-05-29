package xyz.raincards.ui._base

import androidx.annotation.StringRes

open class BaseEvent {
    class ShowLoading(val show: Boolean = true) : BaseEvent()
    class ShowStringError(val error: String?) : BaseEvent()
    class ShowIntError(@StringRes val error: Int) : BaseEvent()
    class ShowStringMessage(val message: String?) : BaseEvent()
    class ShowIntMessage(@StringRes val message: Int) : BaseEvent()
}
