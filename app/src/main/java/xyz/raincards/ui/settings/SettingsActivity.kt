package xyz.raincards.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import dagger.hilt.android.AndroidEntryPoint
import xyz.raincards.databinding.ActivitySettingsBinding
import xyz.raincards.utils.Constants.PRIVACY_POLICY
import xyz.raincards.utils.Constants.TERMS_OF_SERVICE
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.extensions.openWebURL

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            back.setOnClickListener { finish() }
            darkModeSwitch.isChecked = Preferences.isDarkModeOn()
            darkModeSwitch.setOnCheckedChangeListener { _, b ->
                Preferences.setDarkModeOn(b)
                AppCompatDelegate.setDefaultNightMode(
                    if (b) MODE_NIGHT_YES else MODE_NIGHT_NO
                )
            }

            tipScreenSwitch.isChecked = Preferences.isTipScreenOn()
            tipScreenSwitch.setOnCheckedChangeListener { _, b ->
                Preferences.setTipScreenOn(b)
            }

            printSwitch.isChecked = Preferences.isAutoPrintReceiptOn()
            printSwitch.setOnCheckedChangeListener { _, b ->
                Preferences.setAutoPrintReceiptOn(b)
            }

            privacyPolicy.setOnClickListener { openWebURL(PRIVACY_POLICY) }
            terms.setOnClickListener { openWebURL(TERMS_OF_SERVICE) }
        }
    }
}
