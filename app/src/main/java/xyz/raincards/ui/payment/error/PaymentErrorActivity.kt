package xyz.raincards.ui.payment.error

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityPaymentErrorBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_MESSAGE
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class PaymentErrorActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityPaymentErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_MESSAGE)?.let {
            binding.message.text = it
        }

        binding.errorImg.setOnClickListener {
            finish()
        }
    }
}
