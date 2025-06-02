package xyz.raincards.ui.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import xyz.raincards.R
import xyz.raincards.databinding.ActivityMainBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.EXTRA_TOTAL_WITH_TIP
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.extensions.withCurrency
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityMainBinding
    private val inputDigits = StringBuilder()

    private var desc = ""
    private var total = "0"

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                binding.askForCard.root.isVisible -> showCancelLayout()
                binding.pinboard.root.isVisible -> finish()
                else -> binding.pinboard.root.isVisible = true
            }
        }
    }

    private val launcher = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> result.data?.let { data ->
                data.getStringExtra(EXTRA_DESCRIPTION)?.let {
                    desc = it
                    updateDescText()
                }
                data.getStringExtra(EXTRA_TOTAL_WITH_TIP)?.let {
                    total = it
                    binding.pinboard.amount.text = total
                    showAskForCardLayout()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateAmountText()

        binding.cancel.apply {
            noBtn.setOnClickListener { root.isVisible = false }
            yesBtn.setOnClickListener {
                root.isVisible = false
                resetPinboard()
            }
        }

        binding.pinboard.apply {
            chargeBtn.setOnClickListener {
                if (total.toDouble() > 0) {
                    if (Preferences.isTipScreenOn()) {
                        goTo.tipScreen(launcher, amount.text.toString(), desc)
                    } else {
                        showAskForCardLayout()
                    }
                }
            }
            settings.setOnClickListener { goTo.settingsScreen() }
            description.setOnClickListener { goTo.descriptionScreen(launcher) }

            listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn00)
                .forEach { btn ->
                    btn.setOnClickListener {
                        if (inputDigits.length < 7) { // max number 99999.99
                            inputDigits.append((it as TextView).text)
                            updateAmountText()
                        }
                    }
                }

            btnX.setOnClickListener {
                if (inputDigits.isNotEmpty()) {
                    inputDigits.deleteAt(inputDigits.length - 1)
                    updateAmountText()
                }
            }
        }
    }

    private fun showCancelLayout() {
        binding.cancel.root.isVisible = true
    }

    private fun updateDescText() {
        if (desc.isNotEmpty()) {
            binding.pinboard.description.text = desc
        } else {
            binding.pinboard.description.setText(R.string.add_description)
        }
    }

    private fun updateAmountText() {
        val cents = if (inputDigits.isEmpty()) 0 else inputDigits.toString().toLong()
        total = String.format(Locale.getDefault(), "%.2f", cents / 100.0)
        binding.pinboard.amount.text = total.withCurrency()
    }

    private fun showAskForCardLayout() {
        binding.pinboard.root.isVisible = false
        binding.askForCard.apply {
            trash.setOnClickListener { showCancelLayout() }
            qrCode.setOnClickListener { goTo.qrCodeScreen(total, desc) }
            root.isVisible = true
            amount.text = total.withCurrency()
            card.setOnClickListener {
                root.isVisible = false
                binding.processing.root.isVisible = true
                rotateIndefinitely(binding.processing.loader)
            }
        }

        binding.processing.loader.setOnClickListener {
            binding.processing.root.isVisible = false
            binding.success.root.isVisible = true
            binding.success.amount.text = total.withCurrency()
            binding.success.chargeBtn.setOnClickListener { resetPinboard() }
        }
    }

    private fun rotateIndefinitely(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, 360f, 0f)
        animator.duration = 2000 // duration of one full rotation in ms
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun resetPinboard() {
        binding.success.root.isVisible = false
        binding.pinboard.root.isVisible = true

        inputDigits.clear()
        updateAmountText()

        desc = ""
        updateDescText()
    }
}
