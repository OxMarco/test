package xyz.raincards.ui.payment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.view.isVisible
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
import com.nexgo.oaf.apiv3.emv.AidEntity
import com.nexgo.oaf.apiv3.emv.CandidateAppInfoEntity
import com.nexgo.oaf.apiv3.emv.CapkEntity
import com.nexgo.oaf.apiv3.emv.EmvDataSourceEnum
import com.nexgo.oaf.apiv3.emv.EmvEntryModeEnum
import com.nexgo.oaf.apiv3.emv.EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACTLESS
import com.nexgo.oaf.apiv3.emv.EmvHandler2
import com.nexgo.oaf.apiv3.emv.EmvOnlineResultEntity
import com.nexgo.oaf.apiv3.emv.EmvProcessFlowEnum
import com.nexgo.oaf.apiv3.emv.EmvProcessResultEntity
import com.nexgo.oaf.apiv3.emv.EmvTransConfigurationEntity
import com.nexgo.oaf.apiv3.emv.OnEmvProcessListener2
import com.nexgo.oaf.apiv3.emv.PromptEnum
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import xyz.raincards.AndroidApp
import xyz.raincards.R
import xyz.raincards.databinding.ActivityPaymentBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.ui.customviews.CustomProgressView
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.PAYMENT_CANCELED
import xyz.raincards.utils.Constants.PAYMENT_ERROR
import xyz.raincards.utils.Constants.PAYMENT_SUCCESS
import xyz.raincards.utils.EmvUtils
import xyz.raincards.utils.Setup
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
    private lateinit var cardReader: CardReader
    private lateinit var pinPad: PinPad
    private lateinit var emvUtils: EmvUtils

    private var total = ""
    private var desc = ""

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
        cardReader = deviceEngine.cardReader
        pinPad = deviceEngine.pinPad

        emvHandler2.emvDebugLog(true)
        LogUtils.setDebugEnable(true)

        readCard()
        emvHandler2.initReader(ReaderTypeEnum.INNER, 0)
        emvUtils = EmvUtils(this)
        initEmvAid()
        initEmvCapk()

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
                    is PaymentViewModel.Event.ChargeSuccess -> showChargeSuccess()
                    is PaymentViewModel.Event.ChargeError -> showChargeError("xyz")
                }
            }
        }
    }

    private fun readCard() {
        val slotTypes = HashSet<CardSlotTypeEnum>()
        slotTypes.add(CardSlotTypeEnum.ICC1)
        slotTypes.add(CardSlotTypeEnum.RF)

        cardReader.searchCard(slotTypes, 60, this)
    }

    override fun onCardInfo(returnCode: Int, cardInfo: CardInfoEntity?) {
        if (returnCode == SdkResult.Success && cardInfo != null) {
            cardReader.stopSearch()

            (application as AndroidApp).deviceEngine.beeper?.beep(1000)
            binding.askForCard.progress.animateProgress(500)

            val emvTransDataEntity = EmvTransConfigurationEntity()

            emvTransDataEntity.transAmount = total
            emvTransDataEntity.emvTransType = TransactionType.SALE.code.toByte()
            emvTransDataEntity.countryCode = Setup.defaultCountryCode.toString()
            emvTransDataEntity.currencyCode = Setup.getSelectedCurrency().code.toString()
            emvTransDataEntity.termId = Setup.test_deviceID
            emvTransDataEntity.merId = Setup.test_merchantID
            emvTransDataEntity.transDate = SimpleDateFormat(
                "yyMMdd",
                Locale.getDefault()
            ).format(Date())
            emvTransDataEntity.transTime = SimpleDateFormat(
                "hhmmss",
                Locale.getDefault()
            ).format(Date())
            emvTransDataEntity.traceNo = "00000000"
            emvTransDataEntity.emvProcessFlowEnum = EmvProcessFlowEnum.EMV_PROCESS_FLOW_STANDARD

            when (cardInfo.cardExistslot) {
                CardSlotTypeEnum.ICC1 -> {
                    emvTransDataEntity.emvEntryModeEnum = EmvEntryModeEnum.EMV_ENTRY_MODE_CONTACT
                }

                CardSlotTypeEnum.RF -> {
                    emvTransDataEntity.emvEntryModeEnum = EMV_ENTRY_MODE_CONTACTLESS
                }

                CardSlotTypeEnum.SWIPE -> {
                    Log.d("payment", "We don't support sucker's cards")
                }

                else -> {
                    Log.e("payment", "Unknown Card Type")
                }
            }

            emvHandler2.emvProcess(emvTransDataEntity, this)
        } else if (returnCode == SdkResult.TimeOut) {
            // card not detected
            finish()
        } else if (returnCode == SdkResult.Fail) {
        }
    }

    override fun onSwipeIncorrect() {
        showToast(R.string.incorrect_card_swipe_please_try_again)
    }

    override fun onMultipleCards() {
        showToast(R.string.multiple_cards_detected_please_try_again)
    }

    override fun onSelApp(
        appNameList: MutableList<String>?,
        appInfoList: MutableList<CandidateAppInfoEntity>?,
        isFirstSelect: Boolean
    ) {
        emvHandler2.onSetSelAppResponse(1 + 1)
    }

    override fun onTransInitBeforeGPO() {
        val aid = emvHandler2.getTlv(byteArrayOf(0x4F), EmvDataSourceEnum.FROM_KERNEL)
        emvHandler2.onSetTransInitBeforeGPOResponse(true)
    }

    override fun onConfirmCardNo(p0: CardInfoEntity?) {
    }

    override fun onCardHolderInputPin(isOnlinePin: Boolean, leftTimes: Int) {
        pullUpKeyPad(isOnlinePin)
    }

    private fun pullUpKeyPad(isOnlinePin: Boolean) {
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
        val emvOnlineResult = EmvOnlineResultEntity()

        if (emvHandler2.emvCvmResult == null) {
            // @todo End the entire EMV flow and provide feedback to user
        }

        emvHandler2.onSetOnlineProcResponse(SdkResult.Success, emvOnlineResult)
    }

    override fun onPrompt(promptEnum: PromptEnum?) {
        // @todo show feedback to user
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

            else -> Log.d("payment", "Error - else branch running")
        }

        emvHandler2.onSetPromptResponse(true)
    }

    override fun onRemoveCard() {
        emvHandler2.onSetRemoveCardResponse()
    }

    override fun onFinish(retCode: Int, resultEntity: EmvProcessResultEntity?) {
        cardReadingFinished = true
        charge()
//        when (retCode) {
//            SdkResult.Success, SdkResult.Emv_Declined, SdkResult.Emv_Success_Arpc_Fail -> {
//                showToast("success")
//            }
//
//            SdkResult.Emv_Card_Block -> {
//                showToast("Your card is blocked")
//                // @todo Alert customer that card is blocked
//            }
//
//            SdkResult.Emv_Qpboc_Online -> {
//                pullUpKeyPad(true)
//            }
//
//            SdkResult.Emv_Cancel, SdkResult.Emv_Communicate_Timeout -> {
//                showToast(getString(R.string.transaction_canceled))
//                finish()
//                // @todo End emv activity and close fragment
//            }
//
//            SdkResult.Emv_FallBack -> {
//                showToast(R.string.there_s_been_processing_error_please_try_again)
//                // @todo Card error - Give user feedback
//            }
//        SdkResult.Emv_Candidatelist_Empty -> {
//            showToast("No supported card application found.")
//            finish()
//        }
//            else -> {
//                showToast(getString(R.string.code_x, retCode))
//            }
//        }
    }

    override fun onInputResult(retCode: Int, data: ByteArray?) {
        when (retCode) {
            SdkResult.Success, SdkResult.PinPad_No_Pin_Input, SdkResult.PinPad_Input_Cancel -> {
                if (data != null) {
                    val temp = ByteArray(8)
                    System.arraycopy(data, 0, temp, 0, 8)

                    Log.d("payment", "Pin Data Err")
                }

                // (var1 = whether valid input / success, var2 = pin bypass)
                emvHandler2.onSetPinInputResponse(
                    retCode != SdkResult.PinPad_Input_Cancel,
                    retCode == SdkResult.PinPad_No_Pin_Input
                )
            }

            else -> {
                // PIN entering failed with some other error - Trigger some UI to feedback to user
                emvHandler2.onSetPinInputResponse(false, false)
            }
        }
    }

    override fun onSendKey(keyCode: Byte) {}

    private fun showChargeSuccess() {
        binding.processing.root.isVisible = false
        binding.success.root.isVisible = true
        binding.success.amount.text = total.withCurrency()
        binding.success.chargeBtn.setOnClickListener {
            setResult(PAYMENT_SUCCESS)
            finish()
        }
    }

    private fun showChargeError(message: String) {
        binding.processing.root.isVisible = false
        binding.error.root.isVisible = true
        binding.error.errorMessage.text = message
        binding.error.errorImg.setOnClickListener {
            setResult(PAYMENT_ERROR)
            finish()
        }
    }

    private fun charge() = runOnUiThread {
        if (cardAnimationFinished && cardReadingFinished) {
            binding.processing.root.isVisible = true
            binding.processing.loader.setOnClickListener {
                showChargeError("Error XYZ")
            }
            rotateIndefinitely(binding.processing.loader)
            binding.askForCard.progress.stopAnimating()

            viewModel.charge("", "", "", "", "")
        }
    }

    private fun rotateIndefinitely(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ROTATION, 360f, 0f)
        animator.duration = 2000 // duration of one full rotation in ms
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    private fun initEmvAid() {
        emvHandler2.delAllAid()
        if (emvHandler2.getAidListNum() <= 0) {
            val aidEntityList: MutableList<AidEntity?>? = emvUtils.aidList
            if (aidEntityList == null) {
                Log.d("nexgo", "initAID failed")
                return
            }

            val i = emvHandler2.setAidParaList<AidEntity?>(aidEntityList)
            Log.d("nexgo", "setAidParaList " + i)
//            showMessage("setAidParaList: " + (if (i == SdkResult.Success) "success" else "ret:" + i))
        } else {
            Log.d("nexgo", "setAidParaList " + "already load aid")
        }
    }

    private fun initEmvCapk() {
        emvHandler2.delAllCapk()
        val capk_num = emvHandler2.getCapkListNum()
        Log.d("nexgo", "capk_num " + capk_num)
        if (capk_num <= 0) {
            val capkEntityList: MutableList<CapkEntity?>? = emvUtils.capkList
            if (capkEntityList == null) {
                Log.d("nexgo", "initCAPK failed")
                return
            }
            val j = emvHandler2.setCAPKList<CapkEntity?>(capkEntityList)
            Log.d("nexgo", "setCAPKList " + j)
//            showMessage("setCAPKList: " + (if (j == SdkResult.Success) "success" else "ret:" + j))
        } else {
            Log.d("nexgo", "setCAPKList " + "already load capk")
        }
    }
}
