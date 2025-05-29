package xyz.raincards.ui.login.email

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityLoginEmailBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.extensions.afterTextChanged
import xyz.raincards.utils.extensions.collectBaseEvents
import xyz.raincards.utils.extensions.collectLifecycleFlow
import xyz.raincards.utils.extensions.hideKeyboard
import xyz.raincards.utils.extensions.isValidEmail
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class LoginEmailActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityLoginEmailBinding
    private val viewModel: LoginEmailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            email.afterTextChanged {
                sendCodeBtn.isEnabled = email.text.toString().isValidEmail()
                checkIcon.isVisible = email.text.toString().isValidEmail()
            }
            sendCodeBtn.setOnClickListener {
                hideKeyboard()
                viewModel.login(email.text.toString())
            }

            collectBaseEvents(viewModel, root)
            collectLifecycleFlow(viewModel.events) { event ->
                when (event) {
                    is LoginEmailViewModel.Event.CodeSent -> goTo.loginCodeScreen()
                }
            }
        }
    }
}
