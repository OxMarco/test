package xyz.raincards.utils.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface GoTo {
    fun loginEmailScreen()
    fun loginCodeScreen()
    fun mainScreen()
    fun settingsScreen()
    fun descriptionScreen(launcher: ActivityResultLauncher<Intent>)

    fun paymentScreen(
        launcher: ActivityResultLauncher<Intent>,
        amount: String,
        description: String
    )

    fun tipScreen(
        launcher: ActivityResultLauncher<Intent>,
        amount: String,
        description: String
    )

    fun qrCodeScreen(
        amount: String,
        description: String
    )
}
