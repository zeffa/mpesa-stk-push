package zeffah.android_stk_push.api

import zeffah.android_stk_push.data.Mpesa
import zeffah.android_stk_push.network.response.LNMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface LipaNaMpesa {
    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun lipaNaMpesaRequest(@Body mpesa: Mpesa?): LNMResponse?

    @POST("mpesa/stkpush/v1/processrequest")
    fun lipanaMpesaRequest(@Body mpesa: Mpesa?): Call<LNMResponse>
}