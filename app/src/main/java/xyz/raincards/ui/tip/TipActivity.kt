package xyz.raincards.ui.tip

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityTipBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.EXTRA_TOTAL_WITH_TIP
import xyz.raincards.utils.extensions.withCurrency
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class TipActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityTipBinding
    private var desc = ""
    private var total = ""

    private val launcher = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> result.data?.let { data ->
                data.getStringExtra(EXTRA_DESCRIPTION)?.let {
                    desc = it
                    binding.tipButtonsLayout.description.text = desc
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        total = intent.getStringExtra(EXTRA_AMOUNT)!!
        intent.getStringExtra(EXTRA_DESCRIPTION)?.let {
            desc = it
        }

        binding.tipButtonsLayout.apply {
            amount.text = total.withCurrency()

            if (desc.isNotEmpty()) {
                description.text = desc
            }
            description.setOnClickListener { goTo.descriptionScreen(launcher) }

            noTipBtn.setOnClickListener { goBackToPayment() }

            tip5Btn.setOnClickListener {
                total = (total.toDouble() * 1.05).toString()
                goBackToPayment()
            }

            tip10Btn.setOnClickListener {
                total = (total.toDouble() * 1.1).toString()
                goBackToPayment()
            }

            customTipBtn.setOnClickListener {
                binding.customTipLayout.root.isVisible = true
                root.isVisible = false
            }
        }

        binding.customTipLayout.apply {
            cancelBtn.setOnClickListener { finish() }
            okBtn.setOnClickListener {
                total = (total.toDouble() + customTip.text.toString().toDouble()).toString()
                goBackToPayment()
            }

            customTip.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    okBtn.performClick()
                    return@setOnEditorActionListener true
                }
                false
            }
        }
    }

    private fun goBackToPayment() {
        val intent = Intent()
        intent.putExtra(EXTRA_DESCRIPTION, desc)
        intent.putExtra(EXTRA_TOTAL_WITH_TIP, total)
        setResult(RESULT_OK, intent)
        finish()
    }
}
