package xyz.raincards.utils.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import xyz.raincards.BuildConfig
import xyz.raincards.R
import xyz.raincards.utils.Currency
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.Setup
import xyz.raincards.utils.Setup.test_deviceID

fun String.withCurrency(): String {
    val selectedCurrency = Setup.getSelectedCurrency()
    return if (selectedCurrency.code == Currency.USD.code) {
        "${selectedCurrency.symbol}$this"
    } else {
        "$this${selectedCurrency.symbol}"
    }
}

fun Context.getDeviceID(): String {
    return if (BuildConfig.DEBUG) {
        test_deviceID
    } else {
        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }
}

fun Context.getAppVersion(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        "0"
    }
}

fun isUserLoggedIn() = Preferences.getAccessToken().isNotEmpty()

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun Int.resToPx(context: Context): Float = context.resources.getDimension(this)

fun String.accent(context: Context): Spannable {
    val part = SpannableString(this)
    part.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, R.color.accent)),
        0,
        part.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return part
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.onDone(onDone: (String) -> Unit) {
    setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDone.invoke(editableText.toString())
            return@setOnEditorActionListener true
        }
        false
    }
}
