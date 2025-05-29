package xyz.raincards.utils

import android.util.Log
import xyz.raincards.BuildConfig

object MyLog {
    private const val TAG = "----------"

    fun v(msg: String, tag: String? = TAG) = println(Log.VERBOSE, tag, msg)

    fun d(msg: String, tag: String? = TAG) = println(Log.DEBUG, tag, msg)

    fun i(msg: String, tag: String? = TAG) = println(Log.INFO, tag, msg)

    fun w(msg: String, tag: String? = TAG) = println(Log.WARN, tag, msg)

    fun e(msg: String, tag: String? = TAG) = println(Log.ERROR, tag, msg)

    private fun println(priority: Int, tag: String?, msg: String): Int {
        return if (BuildConfig.DEBUG) {
            Log.println(priority, tag, msg)
        } else {
            0
        }
    }
}
