package zeffah.android_stk_push.api

import zeffah.android_stk_push.data.AccessToken
import retrofit2.Call
import retrofit2.http.GET


interface AuthToken {

    @GET("oauth/v1/generate")
    suspend fun getAccessToken(): AccessToken?

    @GET("oauth/v1/generate")
    fun getToken(): Call<AccessToken>?
}