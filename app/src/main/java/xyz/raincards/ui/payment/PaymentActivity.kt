package xyz.raincards.ui.payment

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.nexgo.common.ByteUtils
import com.nexgo.common.LogUtils
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.SdkResult
import com.nexgo.oaf.apiv3.device.pinpad.OnPinPadInputListener
import com.nexgo.oaf.apiv3.device.pinpad.PinAlgorithmModeEnum
import com.nexgo.oaf.apiv3.device.pinpad.PinKeyboardModeEnum
import com.nexgo.oaf.apiv3.device.pinpad.PinPad
import com.nexgo.oaf.apiv3.device.reader.CardInfoEntity
import com.nexgo.oaf.apiv3.device.reader.CardReader
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.device.reader.OnCardInfoListener
import com.nexgo.oaf.apiv3.device.reader.ReaderTypeEnum
import com.nexgo.oaf.apiv3.emv.CandidateAppInfoEntity
import com.nexgo.oaf.apiv3.emv.EmvDataSourceEnum
import com.nexgo.oaf.apiv3.emv.EmvEntryModeEnum
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import com.nexgo.oaf.apiv3.emv.EmvOnlineResultEntity
import com.nexgo.oaf.apiv3.emv.EmvProcessFlowEnum
import com.nexgo.oaf.apiv3.emv.EmvProcessResultEntity
import com.nexgo.oaf.apiv3.emv.EmvTransConfigurationEntity
import com.nexgo.oaf.apiv3.emv.OnEmvProcessListener2
import com.nexgo.oaf.apiv3.emv.PromptEnum
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import xyz.raincards.AndroidApp
import xyz.raincards.R
import xyz.raincards.databinding.ActivityPaymentBinding
import xyz.raincards.models.enums.Country
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.ui.customviews.CustomProgressView
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.EXTRA_MESSAGE
import xyz.raincards.utils.Constants.PAYMENT_CANCELED
import xyz.raincards.utils.Constants.PAYMENT_ERROR
import xyz.raincards.utils.Constants.PAYMENT_SUCCESS
import xyz.raincards.utils.EmvUtils
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.Setup.BEEP_LENGTH
import xyz.raincards.utils.Setup.CIRCLE_ANIMATION_LENGTH
import xyz.raincards.utils.Setup.SEARCH_CARD_TIMEOUT
import xyz.raincards.utils.TransactionType
import xyz.raincards.utils.extensions.collectBaseEvents
import xyz.raincards.utils.extensions.collectLifecycleFlow
import xyz.raincards.utils.extensions.showToast
import xyz.raincards.utils.extensions.withCurrency
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class PaymentActivity :
    BaseActivity(),
    OnCardInfoListener,
    OnEmvProcessListener2,
    OnPinPadInputListener {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: PaymentViewModel by viewModels()

    private lateinit var deviceEngine: DeviceEngine
    private lateinit var emvHandler2: EmvHandler2
    private lateinit var pinPad: PinPad
    private lateinit var cardReader: CardReader

    private var total = ""
    private var desc = ""
    private var cardNumber = ""
    private var existSlot: CardSlotTypeEnum? = null
    private var cardReadingFinished = false
    private var cardAnimationFinished = false

    private val launcher = registerForActivityResult(StartActivityForResult()) { result ->
        when (result.resultCode) {
            PAYMENT_CANCELED -> {
                setResult(PAYMENT_CANCELED)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        emvHandler2.emvProcessAbort()
        (application as AndroidApp).closeIccSlot()
        (application as AndroidApp).closeRfSlot()
    }

    private fun enableLogging() {
        emvHandler2.emvDebugLog(true)
        LogUtils.setDebugEnable(true)

        val path = getExternalFilesDir(null)!!.absolutePath + "/" + "sample_emvlog"
        val file = File(path)
        val z = file.isFile
        println(z.toString())

        try {
            Runtime.getRuntime().exec("logcat -v time -f $path")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        total = intent.getStringExtra(EXTRA_AMOUNT)!!
        intent.getStringExtra(EXTRA_DESCRIPTION)?.let {
            desc = it
        }

        deviceEngine = (application as AndroidApp).deviceEngine
        emvHandler2 = deviceEngine.getEmvHandler2("app2")
        pinPad = (application as AndroidApp).pinPad
        cardReader = deviceEngine.cardReader

        enableLogging()
        emvHandler2.emvDebugLog(true)
        emvHandler2.initReader(ReaderTypeEnum.INNER, 0)

        val emvUtils = EmvUtils(this)
        emvUtils.initializeEmvAid(emvHandler2)
        emvUtils.initializeEmvCapk(emvHandler2)

        readCard()

        binding.askForCard.apply {
            trash.setOnClickListener { goTo.cancelPaymentScreen(launcher) }
            qrCode.setOnClickListener { goTo.qrCodeScreen(launcher, total, desc) }
            amount.text = total.withCurrency()
            progress.setCustomListener(object : CustomProgressView.Listener {
                override fun onAnimationFinished() {
                    cardAnimationFinished = true
                    charge()
                }
            })

            collectBaseEvents(viewModel, binding.root)
            collectLifecycleFlow(viewModel.events) { event ->
                when (event) {
                    is PaymentViewModel.Event.ChargeSuccess -> showChargeSuccess(event.message)
                    is PaymentViewModel.Event.ChargeError -> showChargeError(event.message)
                }
            }
        }
    }

    private fun readCard() {
        val slotTypes = HashSet<CardSlotTypeEnum>()

        slotTypes.add(CardSlotTypeEnum.ICC1)
        slotTypes.add(CardSlotTypeEnum.RF)
        cardReader.searchCard(slotTypes, SEARCH_CARD_TIMEOUT, this)
    }

    override fun onCardInfo(retCode: Int, cardInfo: CardInfoEntity) {
        Log.d("payment", "---onCardInfo---")

        (application as AndroidApp).beep(BEEP_LENGTH)

        binding.askForCard.progress.animateProgress(CIRCLE_ANIMATION_LENGTH)

        when (retCode) {
            SdkResult.Success -> {
                cardReader.stopSearch()

                existSlot = cardInfo.cardExistslot

                val formattedAmount = total.replace(".", "").padStart(12, '0')

                val transData = EmvTransConfigurationEntity()
                transData.transAmount = formattedAmount
                // transData.setCashbackAmount("000000000100"); //if support cashback amount
                transData.emvTransType = TransactionType.SALE.code.toByte()
                transData.countryCode = Country.MALTA.code.toString()
                transData.currencyCode = Preferences.getSelectedCurrencyCode().toString()
                transData.termId = "6177B523"
                transData.merId = "2ISW1234567TEST"
                transData.transDate =
                    SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
                transData.transTime =
                    SimpleDateFormat("hhmmss", Locale.getDefault()).format(Date())
                transData.traceNo = "00000000"

                transData.emvProcessFlowEnum = EmvProcessFlowEnum.EMV_PROCESS_FLOW_STANDARD

                when (cardInfo.cardExistslot) {
                    CardSlotTypeEnum.ICC1 -> {
                        transData.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACT
                    }

                    CardSlotTypeEnum.RF -> {
                        transData.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACTLESS
                    }

                    // CardSlotTypeEnum.SWIPE -> {}

                    else -> {
                        showChargeError("Unsupported Card Type")
                        return
                    }
                }

                emvHandler2.emvProcess(transData, this)
            }
            SdkResult.TimeOut -> {
                Log.e("payment", "TimeOut")
                showChargeError("Card reading timeout, try again")
            }
            SdkResult.Fail -> {
                Log.e("payment", "Fail")
                showChargeError("Fail reading card data")
            }
            else -> {
                Log.e("payment", "Error: $retCode")
                showChargeError("Generic Error, retry")
            }
        }
    }

    override fun onSwipeIncorrect() {
        Log.d("payment", "---onSwipeIncorrect---")

        showToast(R.string.incorrect_card_swipe_please_try_again)
    }

    override fun onMultipleCards() {
        Log.d("payment", "---onMultipleCards---")

        cardReader.stopSearch()
        showToast(R.string.multiple_cards_detected_please_try_again)
    }

    override fun onSelApp(
        appNameList: MutableList<String>?,
        appInfoList: MutableList<CandidateAppInfoEntity>?,
        isFirstSelect: Boolean
    ) {
        Log.d("payment", "---onSelApp---")

        emvHandler2.onSetSelAppResponse(0)
    }

    override fun onTransInitBeforeGPO() {
        Log.d("payment", "---onTransInitBeforeGPO---")

        val aid = emvHandler2.getTlv(byteArrayOf(0x4F), EmvDataSourceEnum.FROM_KERNEL)
        if (ByteUtils.byteArray2HexString(aid).uppercase(Locale.getDefault())
            .contains("A000000004")
        ) {
            emvHandler2.setTlv(
                byteArrayOf(0x9F.toByte(), 0x33.toByte()),
                byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())
            )
        } else {
            emvHandler2.setTlv(
                byteArrayOf(0x9F.toByte(), 0x33.toByte()),
                byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())
            )
        }
        emvHandler2.setPureKernelCapab(ByteUtils.hexString2ByteArray("3400400A99"))
        emvHandler2.onSetTransInitBeforeGPOResponse(true)
    }

    override fun onConfirmCardNo(cardInfo: CardInfoEntity) {
        Log.d("payment", "---onConfirmCardNo---")

        Log.d("payment", "card number: " + cardInfo.cardNo)

        cardNumber = cardInfo.cardNo

        emvHandler2.onSetConfirmCardNoResponse(true)
    }

    override fun onCardHolderInputPin(isOnlinePin: Boolean, leftTimes: Int) {
        Log.d("payment", "---onCardHolderInputPin---")

        pinPad.setPinKeyboardMode(PinKeyboardModeEnum.FIXED)

        if (isOnlinePin) {
            pinPad.inputOnlinePin(
                // first parameter is an array of acceptable PIN lengths namely 0 (PIN bypass), and 4 up till 12
                intArrayOf(0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c),
                60,
                emvHandler2.emvCardDataInfo.cardNo.toByteArray(),
                5,
                PinAlgorithmModeEnum.ISO9564FMT1,
                this
            )
        } else {
            pinPad.inputOfflinePin(
                intArrayOf(0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c),
                60,
                this
            )
        }
    }

    override fun onContactlessTapCardAgain() {
        showToast(R.string.please_tap_card_again)
    }

    override fun onOnlineProc() {
        Log.d("nexgo", "onOnlineProc")

        Log.d("nexgo", "getEmvContactlessMode:" + emvHandler2.getEmvContactlessMode())
        // Log.d("nexgo", "getcardinfo:" + Gson().toJson(emvHandler2.getEmvCardDataInfo()))
        Log.d("nexgo", "getEmvCvmResult:" + emvHandler2.getEmvCvmResult())
        Log.d("nexgo", "getSignNeed--" + emvHandler2.getSignNeed())

        val tlv_5A = emvHandler2.getTlv(byteArrayOf(0x5A.toByte()), EmvDataSourceEnum.FROM_KERNEL)
        Log.d("nexgo", "tlv_5A--" + ByteUtils.byteArray2HexString(tlv_5A))

        val tlv_95 = emvHandler2.getTlv(byteArrayOf(0x95.toByte()), EmvDataSourceEnum.FROM_KERNEL)
        Log.d("nexgo", "tlv_95--" + ByteUtils.byteArray2HexString(tlv_95))

        val tlv_84 = emvHandler2.getTlv(byteArrayOf(0x84.toByte()), EmvDataSourceEnum.FROM_KERNEL)
        Log.d("nexgo", "tlv_84--" + ByteUtils.byteArray2HexString(tlv_84))

        val tlv_50 = emvHandler2.getTlv(byteArrayOf(0x50.toByte()), EmvDataSourceEnum.FROM_KERNEL)
        Log.d("nexgo", "tlv_50--" + ByteUtils.byteArray2HexString(tlv_50))

        val emvOnlineResult = EmvOnlineResultEntity()
        emvOnlineResult.setAuthCode("123450")
        emvOnlineResult.setRejCode("00")
        // fill with the host response 55 field EMV data to do second auth, the format should be TLV format.
        // for example: 910870741219600860008a023030  91 = tag, 08 = len, 7074121960086000 = value;
        // 8a = tag, 02 = len, 3030 = value
        emvOnlineResult.recvField55 = null
        emvHandler2.onSetOnlineProcResponse(SdkResult.Success, emvOnlineResult)
    }

    override fun onPrompt(promptEnum: PromptEnum?) {
        Log.d("payment", "---onPrompt---")

        when (promptEnum) {
            PromptEnum.APP_SELECTION_IS_NOT_ACCEPTED -> Log.d("payment", "NO APPLICATION SELECTED")
            PromptEnum.OFFLINE_PIN_CORRECT -> {
                Log.d("payment", "PIN Accepted")
            }

            PromptEnum.OFFLINE_PIN_INCORRECT -> {
                Log.d("payment", "Invalid PIN")
            }

            PromptEnum.OFFLINE_PIN_INCORRECT_TRY_AGAIN -> {
                Log.d("payment", "Invalid PIN, TRY AGAIN")
            }

            else -> Log.d("payment", "Error")
        }

        emvHandler2.onSetPromptResponse(true)
    }

    override fun onRemoveCard() {
        Log.d("payment", "---onRemoveCard---")

        emvHandler2.onSetRemoveCardResponse()
    }

    override fun onFinish(retCode: Int, entity: EmvProcessResultEntity?) {
        Log.d("payment", "---onFinish---")
        cardReadingFinished = true

        var flag = false
        val aid = emvHandler2.getTlv(byteArrayOf(0x4F), EmvDataSourceEnum.FROM_KERNEL)
        if (aid != null) {
            if (existSlot == CardSlotTypeEnum.RF) {
                if (ByteUtils.byteArray2HexString(aid).uppercase(Locale.getDefault())
                    .contains("A000000025")
                ) {
                    if (retCode == SdkResult.Emv_Plz_See_Phone) {
                        // isExpressPaySeePhoneTapCardAgain = true
                        flag = true
                    }
                }
            }
        }
        if (!flag) {
            // isExpressPaySeePhoneTapCardAgain = false
        }

        // get CVM result
        val emvCvmResultEnum = emvHandler2.emvCvmResult
        Log.d("payment", "return code: $retCode")
        Log.d("payment", "cvmr: $emvCvmResultEnum")
        Log.d("payment", "signature needed: " + emvHandler2.signNeed)
        /*Log.d(
            "payment",
            "getEmvCardDataInfo: " + Gson().toJson(emvHandler2.emvCardDataInfo)
        )*/

        if (retCode == SdkResult.Success) {
            val tags = arrayOf(
                // ── EMV Cryptography ───────────────────────────────────────────────────────
                "9f26", // Application Cryptogram
                "9f27", // Cryptogram Information Data
                "9f10", // Issuer Application Data

                // ── Unpredictable & Counters ──────────────────────────────────────────────
                "9f37", // Unpredictable Number
                "9f36", // Application Transaction Counter (ATC)

                // ── Terminal Risk Management ──────────────────────────────────────────────
                "95", // Terminal Verification Results (TVR)
                "9f1a", // Terminal Country Code
                "9f33", // Terminal Capabilities
                "9f35", // Terminal Type
                "9f1e", // Interface Device (IFD) Serial Number

                // ── Transaction Details ───────────────────────────────────────────────────
                "9a", // Transaction Date
                "9c", // Transaction Type
                "9f02", // Amount, Authorized (Numeric)
                "9f03", // Amount, Other (Numeric)
                "9f41", // Transaction Sequence Counter

                // ── Application Identification & Processing ───────────────────────────────
                "82", // Application Interchange Profile (AIP)
                "84", // Dedicated File (DF) Name (AID)
                "9f09", // Application Version Number
                "9f34", // Cardholder Verification Method (CVM) Results

                // ── Currency & Country ────────────────────────────────────────────────────
                "5f2a", // Transaction Currency Code (ISO 4217)
                "9f42", // Application Currency Code (ISO 4217)
                "9f44", // Application Currency Exponent

                // ── Card & Issuer Data ────────────────────────────────────────────────────
                "5a", // Application PAN (Primary Account Number)
                "5f34", // PAN Sequence Number
                "5f20", // Cardholder Name
                "9f0b", // Cardholder Name Extended
                "5f2c", // Cardholder Nationality
                "5f2b", // Date of Birth (YYMMDD)
                "5f2d", // Language Preference (ISO 639 code)
                "5f24", // Application Expiration Date (YYMMDD)
                "5f25", // Application Effective Date (YYMMDD)
                "42", // Issuer Identification Number (IIN)
                "9f11", // Issuer Code Table Index
                "5f28", // Issuer Country Code (numeric ISO 3166)
                "5f55", // Issuer Country Code (alpha-2)
                "5f56" // Issuer Country Code (alpha-3)
            )

            val tlvData = emvHandler2.getTlvByTags(tags)
            Log.d("payment", "tlv data: $tlvData")

//            val tlv_5A = emvHandler2.getTlv(byteArrayOf(0x5A.toByte()), EmvDataSourceEnum.FROM_KERNEL)
//            pan = ByteUtils.byteArray2HexString(tlv_5A)

            charge()
        } else {
            // Handle errors display
            var err = "Unknown"
            if (retCode == SdkResult.Emv_Cancel) {
                err = "Cancelled"
            } else if (retCode == SdkResult.Emv_Declined || retCode == SdkResult.Emv_Offline_Declined) {
                err = "Declined"
            } else if (retCode == SdkResult.Emv_Card_Removed) {
                err = "Card removed"
            } else if (retCode == SdkResult.Emv_Card_Block) {
                err = "Card blocked"
            } else if (retCode == SdkResult.Emv_Candidatelist_Empty) {
                err = "Invalid configuration"
            } else if (retCode == SdkResult.Emv_Communicate_Timeout) err = "Timeout"

            showChargeError("Error: $err")
        }

        emvHandler2.emvProcessAbort()
    }

    override fun onInputResult(retCode: Int, data: ByteArray?) {
        Log.d("payment", "---onInputResult---")

        when (retCode) {
            SdkResult.Success, SdkResult.PinPad_No_Pin_Input, SdkResult.PinPad_Input_Cancel -> {
                if (data != null) {
                    val temp = ByteArray(8)
                    System.arraycopy(data, 0, temp, 0, 8)

                    Log.d("payment", "Pin Data - ${ByteUtils.byteArray2HexString(data)}")
                }

                // (var1 = whether valid input / success, var2 = pin bypass)
                emvHandler2.onSetPinInputResponse(
                    retCode != SdkResult.PinPad_Input_Cancel,
                    retCode == SdkResult.PinPad_No_Pin_Input
                )
            }

            else -> { // PIN entering failed with some other error - Trigger some UI to feedback to user
                emvHandler2.onSetPinInputResponse(false, false)
            }
        }
    }

    override fun onSendKey(keyCode: Byte) {
        Log.d("payment", "---onSendKey---")
    }

    private fun showChargeSuccess(txt: String? = null) = runOnUiThread {
        val intent = Intent()
        intent.putExtra(EXTRA_MESSAGE, txt)
        intent.putExtra(EXTRA_AMOUNT, total.withCurrency())
        setResult(PAYMENT_SUCCESS, intent)
        finish()
    }

    private fun showChargeError(message: String) = runOnUiThread {
        val intent = Intent()
        intent.putExtra(EXTRA_MESSAGE, message)
        setResult(PAYMENT_ERROR, intent)
        finish()
    }

    private fun charge() = runOnUiThread {
        if (cardAnimationFinished && cardReadingFinished) {
            binding.processing.root.isVisible = true
//            binding.processing.loader.setOnClickListener {
//                showChargeError("Error XYZ")
//            }
            rotateIndefinitely(binding.processing.loader)
            cardAnimationFinished = false
            cardReadingFinished = false
            binding.askForCard.progress.stopAnimating()

            viewModel.charge(
//                validRAINcard,
                cardNumber,
                total,
//                pan,
                desc.ifEmpty { "empty" }
            )
        }
    }

    private fun rotateIndefinitely(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, 360f, 0f)
        animator.duration = 2000 // duration of one full rotation in ms
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
}
