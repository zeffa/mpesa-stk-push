package zeffah.android_stk_push

import zeffah.android_stk_push.api.EndPoints
import zeffah.android_stk_push.callbacks.MpesaResponseListener
import zeffah.android_stk_push.data.AccessToken
import zeffah.android_stk_push.data.Config
import zeffah.android_stk_push.data.Mpesa
import zeffah.android_stk_push.data.TransactionType
import zeffah.android_stk_push.network.request.LNMRequest
import zeffah.android_stk_push.network.response.ErrorType
import zeffah.android_stk_push.network.response.LNMResponse
import zeffah.android_stk_push.utils.MpesaTransaction
import zeffah.android_stk_push.utils.generateLNMExpressPassword
import zeffah.android_stk_push.utils.getCurrentTimestamp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zeffah.android_stk_push.api.ApiClient

class LipaNaMpesaExpress private constructor(
    private var baseUrl: String = EndPoints.SANDOX_BASE_URL,
    private var consumerKey: String = "",
    private var consumerSecret: String = "",
    private var accessTokenListener: MpesaResponseListener<AccessToken>? = null
) {
    private var accessToken: AccessToken? = null
    private var errorMessage: String? = "Failed to generate access token"

    class Factory {
        private var consumerKey: String = ""
        private var consumerSecret: String = ""
        private var factoryBaseUrl = EndPoints.SANDOX_BASE_URL
        private var accessTokenListener: MpesaResponseListener<AccessToken>? = null

        fun withKeys(consumerKey: String, consumerSecret: String): Factory {
            this.consumerKey = consumerKey
            this.consumerSecret = consumerSecret
            return this
        }

        fun run(config: Config? = Config.SANDBOX): Factory {
            factoryBaseUrl =
                if (config == Config.PRODUCTION) EndPoints.PRODUCTION_BASE_URL else EndPoints.SANDOX_BASE_URL
            return this
        }

        fun build(): LipaNaMpesaExpress {
            return LipaNaMpesaExpress(
                factoryBaseUrl,
                consumerKey,
                consumerSecret,
                accessTokenListener
            )
        }
    }

    suspend fun mpesaExpress(mpesaRequest: LNMRequest): LNMResponse? {
        try {
            if (accessToken == null) {
                accessToken =
                    ApiClient.authTokenApi(baseUrl, consumerKey, consumerSecret).getAccessToken()
            }
            val timestamp = getCurrentTimestamp()
            val mpesa = Mpesa(
                BusinessShortCode = mpesaRequest.BusinessShortCode,
                Password = generateLNMExpressPassword(
                    mpesaRequest.BusinessShortCode,
                    mpesaRequest.PassKey,
                    timestamp
                ),
                Timestamp = timestamp,
                TransactionType = if (mpesaRequest.Type == TransactionType.CustomerPayBillOnline) MpesaTransaction.CUSTOMER_PAYBILL_ONLINE else MpesaTransaction.CUSTOMER_BUY_GOODS_ONLINE,
                Amount = mpesaRequest.Amount,
                PartyA = mpesaRequest.PartyA,
                PartyB = mpesaRequest.PartyB,
                PhoneNumber = mpesaRequest.PhoneNumber,
                CallBackURL = mpesaRequest.CallBackURL,
                AccountReference = mpesaRequest.AccountReference,
                TransactionDesc = mpesaRequest.TransactionDesc
            )
            return ApiClient.lipaNaMpesaApi(baseUrl, accessToken?.token!!).lipaNaMpesaRequest(mpesa)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getMpesaExpress(
        mpesaRequest: LNMRequest,
        mpesaExpressResponseListener: MpesaResponseListener<LNMResponse>
    ) {
        try {
            if (accessToken == null) {
                getAccessToken(
                    consumerKey,
                    consumerSecret,
                    object : MpesaResponseListener<AccessToken> {
                        override fun onSuccess(response: AccessToken?) {
                            stkPushCall(mpesaRequest, mpesaExpressResponseListener)
                        }

                        override fun onFail(response: String?) {
                            mpesaExpressResponseListener.onFail(response)
                        }

                        override fun onFail(errorType: ErrorType) {
                            mpesaExpressResponseListener.onFail(errorType)
                        }

                    })
            } else {
                stkPushCall(mpesaRequest, mpesaExpressResponseListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mpesaExpressResponseListener.onFail(e.localizedMessage)
        }
    }

    suspend fun initToken(accessTokenListener: MpesaResponseListener<AccessToken>?): LipaNaMpesaExpress {
        this.accessTokenListener = accessTokenListener
        return getToken(consumerKey, consumerSecret, accessTokenListener)
    }

    fun initializeToken(accessTokenListener: MpesaResponseListener<AccessToken>): LipaNaMpesaExpress {
        this.accessTokenListener = accessTokenListener
        return getAccessToken(consumerKey, consumerSecret, accessTokenListener)
    }

    private suspend fun getToken(
        CONSUMER_KEY: String,
        CONSUMER_SECRET: String,
        responseListener: MpesaResponseListener<AccessToken>?
    ): LipaNaMpesaExpress {
        try {
            accessToken =
                ApiClient.authTokenApi(baseUrl, CONSUMER_KEY, CONSUMER_SECRET).getAccessToken()
            responseListener?.onSuccess(accessToken)
        } catch (e: Exception) {
            e.printStackTrace()
            responseListener?.onFail(e.localizedMessage)
        }
        return this
    }

    private fun getAccessToken(
        CONSUMER_KEY: String,
        CONSUMER_SECRET: String,
        responseListener: MpesaResponseListener<AccessToken>?
    ): LipaNaMpesaExpress {
        try {
            ApiClient.authTokenApi(baseUrl, CONSUMER_KEY, CONSUMER_SECRET).getToken()
                ?.enqueue(object : Callback<AccessToken> {
                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        responseListener?.onFail(t.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<AccessToken>,
                        response: Response<AccessToken>
                    ) {
                        if (response.isSuccessful) {
                            accessToken = response.body()
                            responseListener?.onSuccess(accessToken)
                        } else {
                            responseListener?.onFail(response.message())
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            responseListener?.onFail(e.localizedMessage)
        }
        return this
    }

    private fun stkPushCall(
        mpesaRequest: LNMRequest,
        mpesaExpressResponseListener: MpesaResponseListener<LNMResponse>
    ) {
        val timestamp = getCurrentTimestamp()
        val mpesa = Mpesa(
            BusinessShortCode = mpesaRequest.BusinessShortCode,
            Password = generateLNMExpressPassword(
                mpesaRequest.BusinessShortCode,
                mpesaRequest.PassKey,
                timestamp
            ),
            Timestamp = timestamp,
            TransactionType = if (mpesaRequest.Type == TransactionType.CustomerPayBillOnline) MpesaTransaction.CUSTOMER_PAYBILL_ONLINE else MpesaTransaction.CUSTOMER_BUY_GOODS_ONLINE,
            Amount = mpesaRequest.Amount,
            PartyA = mpesaRequest.PartyA,
            PartyB = mpesaRequest.PartyB,
            PhoneNumber = mpesaRequest.PhoneNumber,
            CallBackURL = mpesaRequest.CallBackURL,
            AccountReference = mpesaRequest.AccountReference,
            TransactionDesc = mpesaRequest.TransactionDesc
        )
        ApiClient.lipaNaMpesaApi(baseUrl, accessToken?.token!!).lipanaMpesaRequest(mpesa)
            .enqueue(object : Callback<LNMResponse> {
                override fun onFailure(call: Call<LNMResponse>, t: Throwable) {
                    mpesaExpressResponseListener.onFail(t.localizedMessage)
                }

                override fun onResponse(call: Call<LNMResponse>, response: Response<LNMResponse>) {
                    if (response.isSuccessful) {
                        mpesaExpressResponseListener.onSuccess(response.body())
                    } else {
                        mpesaExpressResponseListener.onFail(response.message())
                    }
                }

            })
    }
}