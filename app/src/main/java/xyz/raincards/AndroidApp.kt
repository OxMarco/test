package xyz.raincards

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.nexgo.oaf.apiv3.APIProxy
import dagger.hilt.android.HiltAndroidApp
import xyz.raincards.utils.Preferences
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.device.pinpad.PinPad
import com.nexgo.oaf.apiv3.platform.Platform

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
}
