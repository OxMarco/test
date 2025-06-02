package xyz.raincards.ui.description

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import xyz.raincards.databinding.ActivityDescriptionBinding
import xyz.raincards.ui._base.BaseActivity
import xyz.raincards.utils.Constants.EXTRA_DESCRIPTION
import xyz.raincards.utils.navigation.GoTo

@AndroidEntryPoint
class DescriptionActivity : BaseActivity() {

    @Inject
    lateinit var goTo: GoTo

    private lateinit var binding: ActivityDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            description.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    okBtn.performClick()
                    return@setOnEditorActionListener true
                }
                false
            }
            cancelBtn.setOnClickListener { finish() }
            okBtn.setOnClickListener {
                val intent = Intent()
                intent.putExtra(EXTRA_DESCRIPTION, description.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}
