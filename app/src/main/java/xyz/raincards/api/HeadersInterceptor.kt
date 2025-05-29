package xyz.raincards.api

import android.annotation.SuppressLint
import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class HeadersInterceptor(private val appContext: Context) : Interceptor {

    @SuppressLint("HardwareIds")
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val headers = request.headers.newBuilder()
            .add("Accept", "application/json")
            .add("Content-Type", "application/json")
            .build()

        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
    }
}
