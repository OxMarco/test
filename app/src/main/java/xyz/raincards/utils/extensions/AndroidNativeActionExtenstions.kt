package xyz.raincards.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import xyz.raincards.R
import androidx.core.net.toUri

fun Activity.showToast(message: String?) {
    Toast.makeText(this, message ?: getString(R.string.error), Toast.LENGTH_LONG).show()
}

fun Activity.showToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.openWebURL(link: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
        startActivity(intent)
    } catch (e: Exception) {
        showToast(getString(R.string.browser_error))
    }
}

fun Activity.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts("package", packageName, null)
        startActivity(this)
    }
}
