package xyz.raincards.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.raincards.databinding.ActivitySplashBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.extensions.getDeviceID
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class StartActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: StartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        FirebaseMessaging.getInstance().subscribeToTopic(getDeviceID())
        FirebaseCrashlytics.getInstance().setCustomKey("Locale", Locale.getDefault().toString())
        FirebaseCrashlytics.getInstance().setCustomKey("ID", getDeviceID())

        lifecycleScope.launch {
            delay(2000)
            if (Preferences.getAccessToken().isNotEmpty()) {
                goTo.mainScreen()
            } else {
                goTo.loginEmailScreen()
            }
        }
    }
}
