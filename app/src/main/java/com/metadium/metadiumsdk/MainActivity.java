package com.metadium.metadiumsdk;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.metadium.KeepinExtSDK;
import com.metadium.KeepinSDK;
import com.metadium.NotInstalledKeepinException;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.ReturnCallback;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
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
        sdk.registerKey(getNonce(), new Callback<RegisterKeyData>() {
            @Override
            public void onResult(ServiceResult<RegisterKeyData> result) {
                if (result.isSuccess()) {
                    metaId = result.getResult().getMetaId();
                    showToast("서비스 등록 성공");
//                    showToast("MetaId:"+result.getResult().getMetaId()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());
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
//                    showToast("MetaId:"+result.getResult().getMetaId()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());

                    Sign.SignatureData signatureData = stringToSignatureData(result.getResult().getSignature());

                    BigInteger publicKey;
                    try {
                        publicKey = Sign.signedMessageToKey(nonce.getBytes(), signatureData);
                    }
                    catch (SignatureException e) {
                        showToast(e.toString());
                        showToast("올바르지 않은 서명");
                        return;
                    }

                    BigInteger ein = Numeric.toBigInt(result.getResult().getMetaId());
                    String key = Numeric.prependHexPrefix(Keys.getAddress(publicKey));

//                    Web3j web3j = Web3j.build(new HttpService("https://api.metadium.com/prod"));
                    Web3j web3j = Web3j.build(new HttpService(isTestNet ? "https://api.metadium.com/dev" : "https://api.metadium.com/prod"));
                    IdentityRegistry identityRegistry = IdentityRegistry.load(
//                            "0x42bbff659772231bb63c7c175a1021e080a4cf9d",
                            isTestNet ? "0xbe2bb3d7085ff04bde4b3f177a730a826f05cb70" : "0x42bbff659772231bb63c7c175a1021e080a4cf9d",
                            web3j,
                            new ReadonlyTransactionManager(web3j, null),
                            new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
                    );

                    String resolverAddress;
                    try {
                        Tuple4<String, List<String>, List<String>, List<String>> identity = identityRegistry.getIdentity(ein).send();
                        if (identity.getValue4().size() > 0) {
                            resolverAddress = identity.getValue4().get(0);
                        }
                        else {
                            showToast("Not exists resolver");
                            return;
                        }
                    }
                    catch (Exception e) {
                        showToast("Not exists contract, function, identity");
                        return;
                    }

                    ServiceKeyResolver serviceKeyResolver = ServiceKeyResolver.load(
                            resolverAddress,
                            web3j,
                            new ReadonlyTransactionManager(web3j, null),
                            new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
                    );

                    try {
                        boolean hasForKey = serviceKeyResolver.isKeyFor(key, ein).send();
                        String symbol = serviceKeyResolver.getSymbol(key).send();
                            if (hasForKey) {
                                if (KeepinSDK.getServiceId(MainActivity.this).equalsIgnoreCase(symbol)) {
                                    showToast("서명확인 성공");
                                }
                                else {
                                    showToast("서명확인 실패");
                                }
                            }
                            else {
                                showToast("Not exists key in Resolver");
                            }

                    }
                    catch (Exception e) {
                        showToast("Not exists contract, function");
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
//                    showToast("MetaId:"+result.getResult().getMetaId()+"\nsignature:"+result.getResult().getSignature()+"\ntransactionId:"+result.getResult().getTransactionId());

                    Sign.SignatureData signatureData = stringToSignatureData(result.getResult().getSignature());

                    BigInteger publicKey;
                    try {
                        publicKey = Sign.signedMessageToKey(nonce.getBytes(), signatureData);
                    }
                    catch (SignatureException e) {
                        showToast(e.toString());
                        showToast("올바르지 않은 서명");
                        return;
                    }

                    BigInteger ein = Numeric.toBigInt(result.getResult().getMetaId());
                    String key = Numeric.prependHexPrefix(Keys.getAddress(publicKey));

//                    Web3j web3j = Web3j.build(new HttpService("https://api.metadium.com/prod"));
                    Web3j web3j = Web3j.build(new HttpService(isTestNet ? "https://api.metadium.com/dev" : "https://api.metadium.com/prod"));
                    IdentityRegistry identityRegistry = IdentityRegistry.load(
//                            "0x42bbff659772231bb63c7c175a1021e080a4cf9d",
                            isTestNet ? "0xbe2bb3d7085ff04bde4b3f177a730a826f05cb70" : "0x42bbff659772231bb63c7c175a1021e080a4cf9d",
                            web3j,
                            new ReadonlyTransactionManager(web3j, null),
                            new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
                    );

                    String resolverAddress;
                    try {
                        Tuple4<String, List<String>, List<String>, List<String>> identity = identityRegistry.getIdentity(ein).send();
                        if (identity.getValue4().size() > 0) {
                            resolverAddress = identity.getValue4().get(0);
                        }
                        else {
                            showToast("Not exists resolver");
                            return;
                        }
                    }
                    catch (Exception e) {
                        showToast("Not exists contract, function, identity");
                        return;
                    }

                    ServiceKeyResolver serviceKeyResolver = ServiceKeyResolver.load(
                            resolverAddress,
                            web3j,
                            new ReadonlyTransactionManager(web3j, null),
                            new StaticGasProvider(BigInteger.ZERO, BigInteger.ZERO)
                    );

                    try {
                        boolean hasForKey = serviceKeyResolver.isKeyFor(key, ein).send();
                        String symbol = serviceKeyResolver.getSymbol(key).send();
                        if (hasForKey) {
                            if (KeepinSDK.getServiceId(MainActivity.this).equalsIgnoreCase(symbol)) {
                                showToast("서명확인 성공");
                            }
                            else {
                                showToast("서명확인 실패");
                            }
                        }
                        else {
                            showToast("Not exists key in Resolver");
                        }

                    }
                    catch (Exception e) {
                        showToast("Not exists contract, function");
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
