package zeffah.android_stk_push.api

import zeffah.android_stk_push.network.RequestInterceptor
import zeffah.android_stk_push.network.TokenInterceptor
import okhttp3.OkHttpClient

object ApiFactory {

    fun lipaNaMpesa(token: String) : OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(RequestInterceptor(token)).build()

    fun authToken(CONSUME_KEY: String, CONSUMER_SECRET:String) : OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(TokenInterceptor(CONSUME_KEY, CONSUMER_SECRET)).build()
}