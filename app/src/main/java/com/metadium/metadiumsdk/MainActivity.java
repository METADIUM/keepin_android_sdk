package com.metadium.metadiumsdk;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metadium.KeepinExtSDK;
import com.metadium.KeepinSDK;
import com.metadium.NotInstalledKeepinException;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.ReturnCallback;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;
import com.metadium.result.VpRequestData;
import com.metaidum.did.resolver.client.DIDResolverAPI;
import com.metaidum.did.resolver.client.document.DidDocument;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private KeepinSDK sdk;
    private String metaId;

    private boolean isTestNet = true;

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

        RadioGroup netGroup = findViewById(R.id.net);
        netGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isTestNet = checkedId == R.id.test_net;
            }
        });
        netGroup.check(R.id.test_net);
    }

    public void onClickRegister(View view) {
        final String nonce = getNonce();
        sdk.registerKey(nonce, new Callback<RegisterKeyData>() {
            @Override
            public void onResult(ServiceResult<RegisterKeyData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("서비스 등록 성공");
                    showToast("MetaId:"+result.getResult().getMetaId()+"\ndid="+result.getResult().getDid()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());

                    DidDocument didDocument = DIDResolverAPI.getInstance().getDocument(result.getResult().getDid(), true);
                    if (didDocument != null) {
                        try {
                            if (didDocument.hasRecoverAddressFromSignature(nonce.getBytes(Charset.defaultCharset()), result.getResult().getSignature())) {
                                showToast("검증성공");
                            }
                        }
                        catch (SignatureException e) {
                            showToast("검증실패 "+e.toString());
                        }
                    }
                    else {
                        showToast("Not found did "+result.getResult().getDid());
                    }
                }
                else {
//                    showErrorToast(result.getError());
                    showToast("서비스 등록 실패");
                }
            }
        });
    }

    public void onClickExternalRegister(View view) {
        try {
            KeepinExtSDK keepinExtSDK = new KeepinExtSDK(this);

            keepinExtSDK.exportGeneratedKey("", new Callback<RegisterKeyData>() {
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
        catch (Exception e) {
            showToast(e.getLocalizedMessage());
        }
    }

    public static Sign.SignatureData stringToSignatureData(String signature) {
        byte[] bytes = Numeric.hexStringToByteArray(signature);
        return new Sign.SignatureData(bytes[64], Arrays.copyOfRange(bytes, 0, 32), Arrays.copyOfRange(bytes, 32, 64));
    }

    public void onClickSign(View view) {
        String nonce = getNonce();
        sdk.sign(nonce, false, new Callback<SignData>() {
            @Override
            public void onResult(ServiceResult<SignData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("MetaId:"+result.getResult().getMetaId()+"\ndid="+result.getResult().getDid()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());

                    DidDocument didDocument = DIDResolverAPI.getInstance().getDocument(result.getResult().getDid(), true);
                    if (didDocument != null) {
                        try {
                            if (didDocument.hasRecoverAddressFromSignature(nonce.getBytes(Charset.defaultCharset()), result.getResult().getSignature())) {
                                showToast("검증성공");
                            }
                        }
                        catch (SignatureException e) {
                            showToast("검증실패 "+e.toString());
                        }
                    }
                    else {
                        showToast("Not found did "+result.getResult().getDid());
                    }
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    public void onClickSignAndRegister(View view) {
        String nonce = getNonce();
        sdk.sign(nonce, true, new Callback<SignData>() {
            @Override
            public void onResult(ServiceResult<SignData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("MetaId:"+result.getResult().getMetaId()+"\ndid="+result.getResult().getDid()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());

                    DidDocument didDocument = DIDResolverAPI.getInstance().getDocument(result.getResult().getDid(), true);
                    if (didDocument != null) {
                        try {
                            if (didDocument.hasRecoverAddressFromSignature(nonce.getBytes(Charset.defaultCharset()), result.getResult().getSignature())) {
                                showToast("검증성공");
                            }
                        }
                        catch (SignatureException e) {
                            showToast("검증실패 "+e.toString());
                        }
                    }
                    else {
                        showToast("Not found did "+result.getResult().getDid());
                    }
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    public void onClickVpRequest(View view) {
        String nonce = getNonce();
        sdk.requestVp(nonce, "DemoPresentation", new Callback<VpRequestData>() {
            @Override
            public void onResult(ServiceResult<VpRequestData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    Map<String, String> userData = result.getResult().getUserData();
                    try {
                        showToast("did=" + result.getResult().getDid() + "\nsignature:" + result.getResult().getSignature() + "\ntransactionId:" + result.getResult().getTransactionId() + "\nuserData:" + new ObjectMapper().writeValueAsString(userData));
                    }
                    catch (JsonProcessingException e) {
                    }

                    DidDocument didDocument = DIDResolverAPI.getInstance().getDocument(result.getResult().getDid(), true);
                    if (didDocument != null) {
                        try {
                            if (didDocument.hasRecoverAddressFromSignature(nonce.getBytes(Charset.defaultCharset()), result.getResult().getSignature())) {
                                showToast("검증성공");
                            }
                        }
                        catch (SignatureException e) {
                            showToast("검증실패 "+e.toString());
                        }
                    }
                    else {
                        showToast("Not found did "+result.getResult().getDid());
                    }
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
                    showToast("MetaId:"+result.getResult().getMetaId()+"\ntransactinId:"+result.getResult().getTransactionId());
                }
                else {
                    showErrorToast(result.getError());
                }
            }
        });
    }

    public void onClickGetMetaId(View view) {
        sdk.getMetaId(new ReturnCallback<String>() {
            @Override
            public void onReturn(String result) {
                showToast("MetaID="+result);
            }
        });

    }

    public void onClickHasKey(View view) {
        sdk.hasKey(new ReturnCallback<Boolean>() {
            @Override
            public void onReturn(Boolean result) {
                showToast("hasKey="+result);
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
            Log.d("mansud", message);
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
