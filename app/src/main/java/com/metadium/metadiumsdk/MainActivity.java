package com.metadium.metadiumsdk;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.metadium.KeepinSDK;
import com.metadium.NotInstalledKeepinException;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private KeepinSDK sdk;
    private String metaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            sdk = new KeepinSDK(this);
        }
        catch (NotInstalledKeepinException e) {
            startActivity(e.getIntent());
        }
    }

    public void onClickRegister(View view) {
        sdk.registerKey(getNonce(), new Callback<RegisterKeyData>() {
            @Override
            public void onResult(ServiceResult<RegisterKeyData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("MetaId:"+result.getResult().getMetaId()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    public void onClickSign(View view) {
        sdk.sign(getNonce(), new Callback<SignData>() {
            @Override
            public void onResult(ServiceResult<SignData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("MetaId:"+result.getResult().getMetaId()+"\nsignature:"+result.getResult().getSignature());
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    public void onClickRemove(View view) {
        sdk.removeKey(metaId, new Callback<RemoveKeyData>() {
            @Override
            public void onResult(ServiceResult<RemoveKeyData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("MetaId:"+result.getResult().getMetaId()+"\nRemovedKey:"+result.getResult().getTransactionId());
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    private String getNonce() {
        return UUID.randomUUID().toString();
    }

    private void showErrorToast(final ServiceResult.Error error) {
        showToast("Error code:"+error.getErrorCode()+" message:"+error.getErrorMessage());
    }

    private void showToast(final String message) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(message);
                }
            });
        }
    }
}
