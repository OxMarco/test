package xyz.raincards.ui.customviews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.raincards.databinding.CustomEnterDigitViewBinding

class CustomEnterDigitView(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
) {

    private lateinit var _listener: Listener

    interface Listener {
        fun onDigitEntered()
        fun onDigitDeleted()
    }

    fun setListener(listener: Listener) {
        _listener = listener
    }

    private var binding = CustomEnterDigitViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.root.addTextChangedListener(textWatcher)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.root.removeTextChangedListener(textWatcher)
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (charSequence.isNotEmpty()) {
                _listener.onDigitEntered()
            } else {
                _listener.onDigitDeleted()
            }
        }
    }

    fun digit() = binding.root.text.toString().trim()
}
