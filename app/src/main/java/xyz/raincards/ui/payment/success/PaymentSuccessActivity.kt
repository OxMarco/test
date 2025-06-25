package xyz.raincards.ui.payment.success

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityPaymentSuccessBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_MESSAGE
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class PaymentSuccessActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_AMOUNT)?.let {
            binding.amount.text = it
        }
        intent.getStringExtra(EXTRA_MESSAGE)?.let {
            binding.txt.text = it
        }

        binding.okBtn.setOnClickListener {
            finish()
        }
    }
}
