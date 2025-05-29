package xyz.raincards

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import xyz.raincards.utils.Preferences

@HiltAndroidApp
class AndroidApp : Application() {

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
    }
}
