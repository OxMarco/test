package xyz.raincards.utils.navigation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import javax.inject.Inject
import xyz.raincards.ui.cancel.CancelPaymentActivity
import xyz.raincards.ui.description.DescriptionActivity
import xyz.raincards.ui.login.code.LoginCodeActivity
import xyz.raincards.ui.login.email.LoginEmailActivity
import xyz.raincards.ui.main.MainActivity
import xyz.raincards.ui.payment.PaymentActivity
import xyz.raincards.ui.qrcode.QRCodeActivity
import xyz.raincards.ui.settings.SettingsActivity
import xyz.raincards.ui.tip.TipActivity
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION

class GoToImplementation @Inject constructor(private val activity: Activity) : GoTo {
    override fun loginEmailScreen() {
        Intent(activity, LoginEmailActivity::class.java).apply {
            activity.startActivity(this)
            activity.finish()
        }
    }

    override fun loginCodeScreen() {
        Intent(activity, LoginCodeActivity::class.java).apply {
            activity.startActivity(this)
        }
    }

    override fun mainScreen() {
        Intent(activity, MainActivity::class.java).apply {
            activity.startActivity(this)
            activity.finish()
        }
    }

    override fun settingsScreen() {
        Intent(activity, SettingsActivity::class.java).apply {
            activity.startActivity(this)
        }
    }

    override fun paymentScreen(
        launcher: ActivityResultLauncher<Intent>,
        amount: String,
        description: String
    ) = launcher.launch(
        Intent(activity, PaymentActivity::class.java).apply {
            putExtra(EXTRA_DESCRIPTION, description)
            putExtra(EXTRA_AMOUNT, amount)
        }
    )

    override fun descriptionScreen(
        launcher: ActivityResultLauncher<Intent>,
        description: String
    ) = launcher.launch(
        Intent(activity, DescriptionActivity::class.java).apply {
            putExtra(EXTRA_DESCRIPTION, description)
        }
    )

    override fun cancelPaymentScreen(
        launcher: ActivityResultLauncher<Intent>
    ) = launcher.launch(
        Intent(activity, CancelPaymentActivity::class.java).apply {
//            putExtra(EXTRA_AFTER_LOGIN, afterLogin)
        }
    )

    override fun tipScreen(
        launcher: ActivityResultLauncher<Intent>,
        amount: String,
        description: String
    ) = launcher.launch(
        Intent(activity, TipActivity::class.java).apply {
            putExtra(EXTRA_DESCRIPTION, description)
            putExtra(EXTRA_AMOUNT, amount)
        }
    )

    override fun qrCodeScreen(
        launcher: ActivityResultLauncher<Intent>,
        amount: String,
        description: String
    ) {
        Intent(activity, QRCodeActivity::class.java).apply {
            putExtra(EXTRA_DESCRIPTION, description)
            putExtra(EXTRA_AMOUNT, amount)
            activity.startActivity(this)
        }
    }
}
