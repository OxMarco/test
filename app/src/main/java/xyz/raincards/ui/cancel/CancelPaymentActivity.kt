package xyz.raincards.ui.cancel

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import xyz.raincards.databinding.ActivityCancelBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.PAYMENT_CANCELED

@AndroidEntryPoint
class CancelPaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityCancelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCancelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            dismissBtn.setOnClickListener { finish() }
            cancelPaymentBtn.setOnClickListener {
                setResult(PAYMENT_CANCELED)
                finish()
            }
        }
    }
}
