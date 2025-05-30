package xyz.raincards.ui._base

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import xyz.raincards.databinding.ActivityBaseBinding
import xyz.raincards.utils.MyLog

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding
    private lateinit var content: FrameLayout
    private val viewModel: BaseViewModel by viewModels()

    override fun setContentView(view: View?) {
        val activityName = javaClass.simpleName
        MyLog.d("$activityName setContentView()")

        binding = ActivityBaseBinding.inflate(layoutInflater)

        content = binding.content
        content.addView(view)

        super.setContentView(binding.root)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        adjustFontScale(resources.configuration)
    }

    private fun adjustFontScale(configuration: Configuration) {
        configuration.fontScale = 1.0.toFloat()
        val metrics = resources.displayMetrics
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    fun showBaseProgressBar(show: Boolean = true) {
        if (show) {
            binding.baseProgressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.baseProgressBar.visibility = View.GONE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}
