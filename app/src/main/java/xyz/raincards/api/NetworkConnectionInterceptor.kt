package xyz.raincards.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import xyz.raincards.AndroidApp
import xyz.raincards.api.exceptions.NoConnectivityException

class NetworkConnectionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoConnectivityException()
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    companion object {
        val isConnected: Boolean
            get() {
                val connectivityManager: ConnectivityManager =
                    AndroidApp.instance.applicationContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE
                    ) as ConnectivityManager
                val netInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                return netInfo != null && netInfo.isConnected
            }
    }
}