package xyz.raincards.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import com.nexgo.oaf.apiv3.emv.CandidateAppInfoEntity
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
import xyz.raincards.databinding.ActivityPaymentBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_AMOUNT
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.Constants.PAYMENT_CANCELED
import xyz.raincards.utils.Setup
import xyz.raincards.utils.TransactionType
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

    private lateinit var deviceEngine: DeviceEngine
    private lateinit var emvHandler2: EmvHandler2
    private lateinit var cardReader: CardReader
    private lateinit var pinPad: PinPad

    private var total = ""
    private var desc = ""

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

        (application as AndroidApp).deviceEngine.beeper?.beep(1000) // Beep for 100 milliseconds

        total = intent.getStringExtra(EXTRA_AMOUNT)!!
        intent.getStringExtra(EXTRA_DESCRIPTION)?.let {
            desc = it
        }

        binding.apply {
            progress.animateProgress()
            trash.setOnClickListener { goTo.cancelPaymentScreen(launcher) }
            qrCode.setOnClickListener { goTo.qrCodeScreen(launcher, total, desc) }
            root.isVisible = true
            amount.text = total.withCurrency()
        }

        deviceEngine = (application as AndroidApp).deviceEngine
        emvHandler2 = deviceEngine.getEmvHandler2("app2")
        cardReader = deviceEngine.cardReader
        pinPad = deviceEngine.pinPad

        emvHandler2.emvDebugLog(true)
        LogUtils.setDebugEnable(true)
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
        } else if (returnCode == SdkResult.Fail) {
        }
    }

    override fun onSwipeIncorrect() {
    }

    override fun onMultipleCards() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        when (retCode) {
            SdkResult.Success, SdkResult.Emv_Declined, SdkResult.Emv_Success_Arpc_Fail -> {
            }

            SdkResult.Emv_Card_Block -> {
                // @todo Alert customer that card is blocked
            }

            SdkResult.Emv_Qpboc_Online -> {
                pullUpKeyPad(true)
            }

            SdkResult.Emv_Cancel, SdkResult.Emv_Communicate_Timeout -> {
                // @todo End emv activity and close fragment
            }

            SdkResult.Emv_FallBack -> {
                // @todo Card error - Give user feedback
            }
        }
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
}
