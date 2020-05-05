package app.zeffah.stkpush

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zeffah.android_stk_push.LipaNaMpesaExpress
import zeffah.android_stk_push.callbacks.MpesaResponseListener
import zeffah.android_stk_push.data.AccessToken
import zeffah.android_stk_push.data.Config
import zeffah.android_stk_push.data.TransactionType
import zeffah.android_stk_push.network.request.LNMRequest
import zeffah.android_stk_push.network.response.ErrorType
import zeffah.android_stk_push.network.response.LNMResponse
import zeffah.android_stk_push.utils.Constants
import zeffah.android_stk_push.utils.EnVars

class MainActivity : AppCompatActivity() {

    private lateinit var lipaNaMpesaExpress: LipaNaMpesaExpress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lipaNaMpesaExpress = LipaNaMpesaExpress.Factory()
            .withKeys(EnVars.CONSUMER_KEY, EnVars.CONSUMER_SECRET)
            .run(Config.SANDBOX).build()

        //without coroutines
        lipaNaMpesaExpress.initializeToken(object : MpesaResponseListener<AccessToken>{
            override fun onSuccess(response: AccessToken?) {
                Toast.makeText(this@MainActivity, response?.token, Toast.LENGTH_LONG).show()
            }

            override fun onFail(message: String?) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }

            override fun onFail(errorType: ErrorType) {
                val msg = when(errorType) {
                    ErrorType.NETWORK -> "Network Error"
                    ErrorType.TIMEOUT -> "Request timed out"
                    else -> "Unknown Error occurred"
                }
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
            }

        })

        //with coroutines
        CoroutineScope(Dispatchers.Main).launch {
            lipaNaMpesaExpress.initToken(object : MpesaResponseListener<AccessToken>{
                override fun onSuccess(response: AccessToken?) {
                    Toast.makeText(this@MainActivity, response?.token, Toast.LENGTH_LONG).show()
                }

                override fun onFail(message: String?) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }

                override fun onFail(errorType: ErrorType) {
                    val msg = when(errorType) {
                        ErrorType.NETWORK -> "Network Error"
                        ErrorType.TIMEOUT -> "Request timed out"
                        else -> "Unknown Error occurred"
                    }
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    override fun onStart() {
        super.onStart()
        btnRequest.setOnClickListener {
            val phoneNumber = edtPhone.text.toString().trim()
            val amount = edtAmount.text.toString().trim()

            //without coroutines
            lipaNaMpesaResponse(TransactionType.CustomerPayBillOnline, amount.toFloat(), phoneNumber)

            //with coroutines
            CoroutineScope(Dispatchers.Main).launch {
                val lnmResponse: LNMResponse? = getLipaNaMpesaResponse(TransactionType.CustomerPayBillOnline, amount.toFloat(), phoneNumber)
                lnmResponse?.let {
                    Toast.makeText(this@MainActivity, it.CustomerMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //with coroutines
    private suspend fun getLipaNaMpesaResponse(transactionType: TransactionType, amount: Float, phone: String):LNMResponse? {
        return withContext(Dispatchers.IO){
            val lnmRequest = LNMRequest(
                BusinessShortCode =  "174379",
                PassKey = Constants.SANDBOX_ONLINE_PASS_KEY,
                Type = transactionType,
                Amount = amount,
                PartyA = phone,
                PartyB = "174379",
                PhoneNumber = phone,
                CallBackURL = "https://payment-app-node.herokuapp.com/confirmation",
                AccountReference = "Account",
                TransactionDesc = "Payment stk Test"
            )
            lipaNaMpesaExpress.mpesaExpress(lnmRequest)
        }
    }

    //without coroutine
    private fun lipaNaMpesaResponse(transactionType: TransactionType, amount: Float, phone: String) {
        val lnmRequest = LNMRequest(
            BusinessShortCode =  "174379",
            PassKey = Constants.SANDBOX_ONLINE_PASS_KEY, //Replace with your own from daraja portal
            Type = transactionType,
            Amount = amount,
            PartyA = phone,
            PartyB = "174379",
            PhoneNumber = phone,
            CallBackURL = "https://payment-app-node.herokuapp.com/confirmation", //Use your own callback
            AccountReference = "Account",
            TransactionDesc = "Payment stk Test"
        )
        lipaNaMpesaExpress.getMpesaExpress(lnmRequest, object : MpesaResponseListener<LNMResponse> {
            override fun onSuccess(response: LNMResponse?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFail(message: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFail(errorType: ErrorType) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
}
