package xyz.raincards.ui.description

import android.content.Intent
import android.os.Bundle
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
