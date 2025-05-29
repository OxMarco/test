package xyz.raincards.ui._base

import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.raincards.utils.MyLog

@AndroidEntryPoint
open class BaseFragment(resID: Int) : Fragment(resID) {

    protected lateinit var fragmentName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentName = javaClass.simpleName
        MyLog.d("$fragmentName onCreate()")
    }

    fun showBaseProgressBar(show: Boolean = true) {
        if (activity != null) {
            (activity as BaseActivity).showBaseProgressBar(show)
        }
    }
}
