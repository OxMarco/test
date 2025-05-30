package xyz.raincards.ui.qrcode

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import xyz.raincards.databinding.ActivityShowQrCodeBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.extensions.withCurrency
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class QRCodeActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityShowQrCodeBinding
    private var desc = ""
    private var total = ""

    private val launcher = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> result.data?.let { data ->
                data.getStringExtra(EXTRA_DESCRIPTION)?.let {
                    desc = it
                    binding.description.text = desc
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        total = intent.getStringExtra(EXTRA_AMOUNT)!!
        intent.getStringExtra(EXTRA_DESCRIPTION)?.let {
            desc = it
        }

        binding.apply {
            amount.text = total.withCurrency()
            card.setOnClickListener { finish() }
            description.setOnClickListener { goTo.descriptionScreen(launcher) }
            if (desc.isNotEmpty()) {
                description.text = desc
            }
        }
    }
}
