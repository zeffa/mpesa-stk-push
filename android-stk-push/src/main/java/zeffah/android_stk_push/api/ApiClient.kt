package zeffah.android_stk_push.api

import zeffah.android_stk_push.api.ApiFactory
import zeffah.android_stk_push.api.AuthToken
import zeffah.android_stk_push.api.LipaNaMpesa
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    companion object {
        fun lipaNaMpesaApi(baseUrl: String, accessToken: String): LipaNaMpesa {
            val lipaNaMpesaClient = ApiFactory.lipaNaMpesa(accessToken)
            return Retrofit.Builder().client(lipaNaMpesaClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build().create(LipaNaMpesa::class.java)
        }

        fun authTokenApi(
            baseUrl: String,
            CONSUMER_KEY: String,
            CONSUMER_SECRET: String
        ): AuthToken {
            val authTokenClient = ApiFactory.authToken(CONSUMER_KEY, CONSUMER_SECRET)
            return Retrofit.Builder().client(authTokenClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build().create(AuthToken::class.java)
        }
    }
}