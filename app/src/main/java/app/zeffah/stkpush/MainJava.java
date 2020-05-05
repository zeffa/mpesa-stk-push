package app.zeffah.stkpush;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.jetbrains.annotations.NotNull;

import zeffah.android_stk_push.LipaNaMpesaExpress;
import zeffah.android_stk_push.callbacks.MpesaResponseListener;
import zeffah.android_stk_push.data.AccessToken;
import zeffah.android_stk_push.data.Config;
import zeffah.android_stk_push.data.TransactionType;
import zeffah.android_stk_push.network.request.LNMRequest;
import zeffah.android_stk_push.network.response.ErrorType;
import zeffah.android_stk_push.network.response.LNMResponse;
import zeffah.android_stk_push.utils.Constants;
import zeffah.android_stk_push.utils.EnVars;

public class MainJava extends AppCompatActivity {
    private LipaNaMpesaExpress lipaNaMpesaExpress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatButton btnSend = findViewById(R.id.btnRequest);
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
                    Constants.SANDBOX_ONLINE_PASS_KEY,
                    TransactionType.CustomerPayBillOnline,
                    Float.parseFloat(amount),
                    phone,
                    "174379",
                    phone,
                    "https://payment-app-node.herokuapp.com/confirmation",
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
    }
}
