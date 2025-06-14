package xyz.raincards

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.nexgo.oaf.apiv3.APIProxy
import com.nexgo.oaf.apiv3.DeviceEngine
import com.nexgo.oaf.apiv3.device.pinpad.PinPad
import com.nexgo.oaf.apiv3.device.reader.CardSlotTypeEnum
import com.nexgo.oaf.apiv3.platform.BeepVolumeModeEnum
import com.nexgo.oaf.apiv3.platform.Platform
import dagger.hilt.android.HiltAndroidApp
import xyz.raincards.utils.Preferences

@HiltAndroidApp
class AndroidApp : Application() {
    lateinit var deviceEngine: DeviceEngine
    lateinit var platform: Platform
    lateinit var pinPad: PinPad

    companion object {
        private lateinit var _instance: AndroidApp
        val instance: AndroidApp
            get() = _instance
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this

        FirebaseApp.initializeApp(this)

        AppCompatDelegate.setDefaultNightMode(
            if (Preferences.isDarkModeOn()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        // Initialize the DeviceEngine
        deviceEngine = APIProxy.getDeviceEngine(this)
        platform = deviceEngine.platform
        pinPad = deviceEngine.pinPad
    }

    fun beep(milliseconds: Int) {
        platform.setBeepMode(BeepVolumeModeEnum.BEEP_MODE_SYSTEM_VOLUME, 0)
        deviceEngine.beeper.beep(milliseconds)
    }

    fun deviceInfo(): String {
        val deviceInfo = deviceEngine.deviceInfo
        val sb = StringBuilder()
        sb.append("SN:" + deviceInfo.sn + "\n")
        sb.append("KSN:" + deviceInfo.ksn + "\n")
        sb.append("OSVer:" + deviceInfo.osVer + "\n")
        sb.append("FirmWareVer:" + deviceInfo.firmWareVer + "\n")
        sb.append("FirmWareFullVersion:" + deviceInfo.firmWareFullVersion + "\n")
        sb.append("KernelVer:" + deviceInfo.kernelVer + "\n")
        sb.append("SDKVer:" + deviceInfo.sdkVer + "\n")
        sb.append("CoreVer:" + deviceInfo.spCoreVersion + "\n")
        sb.append("BootVer:" + deviceInfo.spBootVersion + "\n")
        sb.append("MODEL:" + deviceInfo.model)
        return sb.toString()
    }

    fun closeIccSlot() {
        deviceEngine.getCPUCardHandler(CardSlotTypeEnum.ICC1).powerOff()
    }

    fun closeRfSlot() {
        deviceEngine.getCPUCardHandler(CardSlotTypeEnum.RF).powerOff()
    }
}
