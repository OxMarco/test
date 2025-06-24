package xyz.raincards.ui.customviews

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import xyz.raincards.R
import xyz.raincards.databinding.CustomProgressViewBinding

class CustomProgressView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var _listener: Listener? = null

    interface Listener {
        fun onAnimationFinished()
    }

    fun setCustomListener(listener: Listener) {
        _listener = listener
    }

    private val handler = Handler(Looper.getMainLooper())
    private var counter = 1
    private var inProgress = true

    private var binding = CustomProgressViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun stopAnimating() {
        counter = 1
        inProgress = false
    }

    fun animateProgress(delay: Long) {
        inProgress = true
        handler.postDelayed(
            object : Runnable {
                override fun run() {
                    when (counter % 5) {
                        0 -> {
                            binding.step1.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step2.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step3.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step4.setBackgroundResource(R.drawable.shape_circle_full)
                            _listener?.onAnimationFinished()
                        }

                        1 -> {
                            binding.step1.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step2.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step3.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step4.setBackgroundResource(R.drawable.shape_circle_full)
                        }

                        2 -> {
                            binding.step1.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step2.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step3.setBackgroundResource(R.drawable.shape_circle_full)
                            binding.step4.setBackgroundResource(R.drawable.shape_circle_full)
                        }

                        3 -> {
                            binding.step1.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step2.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step3.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step4.setBackgroundResource(R.drawable.shape_circle_full)
                        }

                        4 -> {
                            binding.step1.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step2.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step3.setBackgroundResource(R.drawable.shape_circle_empty)
                            binding.step4.setBackgroundResource(R.drawable.shape_circle_empty)
                        }
                    }

                    counter++
                    if (inProgress) {
                        handler.postDelayed(this, delay)
                    }
                }
            },
            delay
        )
    }
}