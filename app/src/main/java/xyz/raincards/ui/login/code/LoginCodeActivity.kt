package xyz.raincards.ui.login.code

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityLoginCodeBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.ui.customviews.CustomEnterDigitView
import xyz.raincards.utils.extensions.collectBaseEvents
import xyz.raincards.utils.extensions.collectLifecycleFlow
import xyz.raincards.utils.extensions.hideKeyboard
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class LoginCodeActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityLoginCodeBinding
    private val viewModel: LoginCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCodeViews()

        binding.apply {
            cancelBtn.setOnClickListener { finish() }
            enterBtn.setOnClickListener {
                hideKeyboard()
                viewModel.sendCode(
                    firstDigit.digit() + secondDigit.digit() + thirdDigit.digit() +
                        fourthDigit.digit() + fifthDigit.digit() + sixthDigit.digit()
                )
            }

            collectBaseEvents(viewModel, root)
            collectLifecycleFlow(viewModel.events) { event ->
                when (event) {
                    is LoginCodeViewModel.Event.CodeValidated -> {
                        goTo.mainScreen()
                    }
                }
            }
        }
    }

    private fun setCodeViews() {
        binding.apply {
            firstDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    secondDigit.requestFocus()
                }

                override fun onDigitDeleted() {}
            })
            secondDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    thirdDigit.requestFocus()
                }

                override fun onDigitDeleted() {
                    firstDigit.requestFocus()
                }
            })
            thirdDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    fourthDigit.requestFocus()
                }

                override fun onDigitDeleted() {
                    secondDigit.requestFocus()
                }
            })
            fourthDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    fifthDigit.requestFocus()
                }

                override fun onDigitDeleted() {
                    thirdDigit.requestFocus()
                }
            })
            fifthDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    sixthDigit.requestFocus()
                }

                override fun onDigitDeleted() {
                    fourthDigit.requestFocus()
                }
            })
            sixthDigit.setListener(object : CustomEnterDigitView.Listener {
                override fun onDigitEntered() {
                    enterBtn.performClick()
                }

                override fun onDigitDeleted() {
                    fifthDigit.requestFocus()
                }
            })
        }
    }
}
