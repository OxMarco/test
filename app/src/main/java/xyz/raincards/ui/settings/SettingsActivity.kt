package xyz.raincards.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import dagger.hilt.android.AndroidEntryPoint
import xyz.raincards.R
import xyz.raincards.databinding.ActivitySettingsBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.PRIVACY_POLICY
import xyz.raincards.utils.Constants.TERMS_OF_SERVICE
import xyz.raincards.utils.Preferences
import xyz.raincards.utils.Setup
import xyz.raincards.utils.extensions.changeLocale
import xyz.raincards.utils.extensions.openAndroidSettings
import xyz.raincards.utils.extensions.openWebURL
import xyz.raincards.utils.extensions.restart

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

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

            settings.setOnClickListener { openAndroidSettings() }
        }

        showCurrencies()
        showLanguages()
    }

    private fun showCurrencies() {
        var settingUp = true
        val adapter = ArrayAdapter(this, R.layout.row_spinner, Setup.currencies)
        adapter.setDropDownViewResource(R.layout.row_spinner)
        val index = Setup.currencies.indexOfFirst { it == Preferences.getSelectedCurrency() }

        binding.currencies.adapter = adapter
        binding.currencies.setSelection(index)
        binding.currencies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, position: Int, id: Long) {
                if (settingUp) {
                    settingUp = false
                } else {
                    Preferences.saveSelectedCurrency(Setup.currencies[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun showLanguages() {
        var settingUp = true

        val adapter = ArrayAdapter(this, R.layout.row_spinner, Setup.languages)
        adapter.setDropDownViewResource(R.layout.row_spinner)
        val index = Setup.languages.indexOfFirst {
            it.countryCode == Preferences.getSelectedLanguage()
        }

        binding.languages.adapter = adapter
        binding.languages.setSelection(index)
        binding.languages.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, position: Int, id: Long) {
                if (settingUp) {
                    settingUp = false
                } else {
                    changeLocale(Setup.languages[position])
                    Preferences.saveSelectedLanguage(Setup.languages[position].countryCode)
                    restart()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}
