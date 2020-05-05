package zeffah.android_stk_push.network

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val authToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $authToken")
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}