package zeffah.android_stk_push.network

import android.util.Base64
import zeffah.android_stk_push.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val consumerKey: String = "",
    private val consumerSecret: String = ""
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val authKey = "$consumerKey:$consumerSecret"

        val encodedString = Base64.encodeToString(authKey.toByteArray(Charsets.ISO_8859_1), Base64.NO_WRAP)

        val newUrl = chain.request().url.newBuilder()
            .addQueryParameter("grant_type", Constants.CLIENT_CREDENTIALS)
            .build()

        val request = chain.request()
            .newBuilder()
            .url(newUrl).get()
            .addHeader("Authorization", "Basic $encodedString")
            .build()
        return chain.proceed(request)
    }
}