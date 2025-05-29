package xyz.raincards.utils.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.ui._base.BaseEvent
import xyz.raincards.ui._base.BaseEventsViewModel

fun BaseActivity.collectBaseEvents(baseViewModel: BaseEventsViewModel, root: View) {
    this.collectLifecycleFlow(baseViewModel.baseEvents) { event ->
        showBaseProgressBar(event is BaseEvent.ShowLoading && event.show)
        when (event) {
            is BaseEvent.ShowLoading -> Unit
            is BaseEvent.ShowStringError -> showToast(event.error) // root.showSnackBar(event.error)
            is BaseEvent.ShowIntError -> showToast(event.error) // root.showSnackBar(event.error)
            is BaseEvent.ShowIntMessage -> showToast(event.message)
            is BaseEvent.ShowStringMessage -> showToast(event.message)
        }
    }
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = currentFocus
    if (view == null) view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
