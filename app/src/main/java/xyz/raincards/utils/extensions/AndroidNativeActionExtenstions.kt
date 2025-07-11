package xyz.raincards.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.net.toUri
import java.util.Locale
import xyz.raincards.R
import xyz.raincards.models.Language

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

fun Activity.restart() {
    val intent = intent
    finish()
    startActivity(intent)
}

fun Context.changeLocale(lcl: Language) {
    val locale = Locale(lcl.languageCode, lcl.countryCode)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
        createConfigurationContext(config)
    } else {
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

fun Activity.openAppSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts("package", packageName, null)
        startActivity(this)
    }
}

fun Activity.openAndroidSettings() {
    Intent(Settings.ACTION_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
}

fun Activity.openCalculator() {
    val intent = Intent()
    intent.action = Intent.ACTION_MAIN
    intent.addCategory(Intent.CATEGORY_APP_CALCULATOR)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback: try known calculator package names
        val calculatorPackages = listOf(
            "com.android.calculator2", // AOSP
            "com.sec.android.app.popupcalculator", // Samsung
            "com.miui.calculator", // Xiaomi
            "com.oneplus.calculator", // OnePlus
            "com.google.android.calculator" // Google Pixel
        )

        var launched = false
        for (pkg in calculatorPackages) {
            try {
                val launchIntent = packageManager.getLaunchIntentForPackage(pkg)
                if (launchIntent != null) {
                    startActivity(launchIntent)
                    launched = true
                    break
                }
            } catch (e: Exception) {
                // Ignore and try next
            }
        }

        if (!launched) {
            Toast.makeText(this, "Calculator app not found.", Toast.LENGTH_SHORT).show()
        }
    }
}
