# mpesa-stk-push
[![](https://jitpack.io/v/zeffa/mpesa-stk-push.svg)](https://jitpack.io/#zeffa/mpesa-stk-push)  
An LipaNaMpesa Express library for android devs.

The author was inspired by [https://github.com/jumaallan/android-mpesa-api]  

# How to use mpesa-stk-push library

**Add it in your root build.gradle at the end of repositories**   
     
     allprojects {
        repositories {
        ...
        maven { url 'https://jitpack.io' }
        }
	  }
    
   **Add the dependency in your module level buidl.gradle** 
    
    
    dependencies {
        implementation 'com.github.zeffa:mpesa-stk-push:v1.0.0-beta'
    }
      
      
  Declare LipaNaMpesaExpress  
  <pre><code>
  var lipaNaMpesaExpress: LipaNaMpesaExpress
</code></pre>
  
  In onCreate method of your activity/fragment, initialize LipaNaMpesaExpress  
      <pre><code>
lipaNaMpesaExpress = LipaNaMpesaExpress.Factory()
            .withKeys(EnVars.CONSUMER_KEY, EnVars.CONSUMER_SECRET)
            .run(Config.SANDBOX).build()</code></pre>  
            
 <b>For production app, replace</b>  
 <pre><code>
  Config.SANDBOX with Config.PRODUCTION
</code></pre> 
  in the run() function above
            
<i>Get the consumer key and consumer secret from safaricom daraja portal (https://developer.safaricom.co.ke/) by creating an account</i>  
 
 # To get the access token call  
  <i>Using kotlin with coroutines </i> 
     <code>
     lipaNaMpesaExpress.initToken(MpesaResponseListener<AccessToken>?)
     </code>
 
  <i>For kotlin without coroutine and Java, use</i> 
    <code>
    lipaNaMpesaExpress.initializeToken(MpesaResponseListener<AccessToken>) 
    </code>instead. 
  
**MpesaResponseListener<AccessToken> interface is nullable. You can pass null if you don't need to get the token in your activity/fragment**
  
  # For actually stk push 
  <pre><code>lipaNaMpesaExpress.getMpesaExpress(LNMRequest, MpesaResponseListener<LNMResponse>)</code></pre>  
  
   **LNMRequest** is the actual object you as request body, while **LNMResponse** is the api response object
   
 # Example usage of mpesa-stk-push
 
 **Tip**
      call the initToken function early in the oncreate so that the token is generate while you supply other information before button click. This make improve response time
 
 **Kotlin**
 
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
    
    
**Java**

    lipaNaMpesaExpress = new LipaNaMpesaExpress.Factory()
                .run(Config.SANDBOX)
                .withKeys(EnVars.CONSUMER_KEY, EnVars.CONSUMER_SECRET)
                .build()
                .initializeToken(new MpesaResponseListener<AccessToken>() {
                    @Override
                    public void onFail(@NotNull ErrorType errorType) {

                    }

                    @Override
                    public void onSuccess(AccessToken response) {
                        Toast.makeText(MainJava.this, response.getToken(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String response) {
                        Toast.makeText(MainJava.this, response, Toast.LENGTH_SHORT).show();
                    }
                });

        btnSend.setOnClickListener(v -> {
            String phone = ((EditText) findViewById(R.id.edtPhone)).getText().toString().trim();
            String amount = ((EditText) findViewById(R.id.edtAmount)).getText().toString().trim();
            LNMRequest lnmRequest = new LNMRequest(
                    "174379",
                    Constants.SANDBOX_ONLINE_PASS_KEY, //Replace with your own from daraja portal
                    TransactionType.CustomerPayBillOnline,
                    Float.parseFloat(amount),
                    phone,
                    "174379",
                    phone,
                    "https://payment-app-node.herokuapp.com/confirmation", //Use your own callback
                    "Account",
                    "Payment stk Test"
            );
            lipaNaMpesaExpress.getMpesaExpress(lnmRequest, new MpesaResponseListener<LNMResponse>() {
                @Override
                public void onFail(@NotNull ErrorType errorType) {
                    String msg;
                    switch (errorType) {
                        case NETWORK:
                            msg = "Network Error";
                            break;
                        case TIMEOUT:
                            msg = "Request timed out";
                            break;
                        default:
                            msg = "Unknown Error occurred";
                            break;
                    }
                    Toast.makeText(MainJava.this, msg, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSuccess(LNMResponse response) {
                    Toast.makeText(MainJava.this, response.getCustomerMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String response) {
                    Toast.makeText(MainJava.this, response, Toast.LENGTH_SHORT).show();
                }
            });
        });
    
