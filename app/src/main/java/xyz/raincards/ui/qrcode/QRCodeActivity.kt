package xyz.raincards.ui.qrcode

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import xyz.raincards.R
import xyz.raincards.databinding.ActivityShowQrCodeBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.PAYMENT_CANCELED
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
            PAYMENT_CANCELED -> {
                setResult(PAYMENT_CANCELED)
                finish()
            }

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
            trash.setOnClickListener {
                goTo.cancelPaymentScreen(launcher)
            }
            description.setOnClickListener { goTo.descriptionScreen(launcher, desc) }
            if (desc.isNotEmpty()) {
                description.text = desc
            }
        }

        // fixme hardcoded
        val qrCodeBitmap = generateQRCode(Constants.PLAY_STORE_LINK)
        binding.qrCode.setImageBitmap(qrCodeBitmap)
    }

    private fun generateQRCode(text: String, size: Int = 512): Bitmap {
        val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] = ContextCompat.getColor(this, if (bitMatrix[x, y]) R.color.primary else R.color.bckg)
            }
        }
        return bitmap
    }
}
