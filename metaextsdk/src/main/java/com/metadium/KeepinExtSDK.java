package com.metadium;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metadium.handler.RemoveKeyHandler;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;
import com.metadium.util.SecureFileUtils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

/**
 * Extended Keepin SDK<br/>
 * Added managing ec key pair<br/>
 * <br/>
 * Export key : {@link #exportGeneratedKey(String, Callback)}<br/>
 * Remove key : {@link #removeKey(String, Callback)}<br/>
 * Sign : {@link #sign(String, String, Callback)}<br/>
 * Chanage wallet password : {@link #updateWalletPassword(String, String)}<br/>
 * Remove wallet : {@link #deleteWallet()}<br/>
 * Cheak wallet : {@link #existsWallet()}<br/>
 */
public class KeepinExtSDK extends KeepinSDK {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String WALLET_FILE_NAME = "keepin_asist.json";

    static {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider != null && !provider.getClass().equals(BouncyCastleProvider.class)) {
            Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }

    /**
     * Constructor<br/>
     * @see KeepinSDK#KeepinSDK(Context)
     * @param context
     * @throws NotInstalledKeepinException
     */
    public KeepinExtSDK(Context context) throws NotInstalledKeepinException {
        super(context);
    }

    public KeepinExtSDK(Context context, String serviceId) throws NotInstalledKeepinException {
        super(context, serviceId);
    }

    private void saveWalletFile(Context context, MetaWalletFile metaWalletFile) {
        try {
            SecureFileUtils.save(context, getWalletFile(context), objectMapper.writeValueAsBytes(metaWalletFile));
        }
        catch (Exception e) {
            Log.e("mansud", "saveWallet", e);
        }
    }

    private MetaWalletFile loadWallet(Context context) {
        try {
            return objectMapper.readValue(SecureFileUtils.load(context, getWalletFile(context)), MetaWalletFile.class);
        }
        catch (Exception e) {
            Log.e("mansud", "loadWallet", e);
            return null;
        }
    }

    private File getWalletFile(Context context) {
        return new File(context.getFilesDir(), WALLET_FILE_NAME);
    }

    private MetaWalletFile generateWalletFile(Context context, String password) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return createWalletFile(context, Keys.createEcKeyPair(), password);
    }

    private MetaWalletFile createWalletFile(Context context, ECKeyPair ecKeyPair, String password) throws CipherException {
        MetaWalletFile wallet = new MetaWalletFile(null, Wallet.createLight(password, ecKeyPair));
        saveWalletFile(context, wallet);
        return wallet;
    }

    private void setWalletMetaId(String metaId) {
        MetaWalletFile walletFile = loadWallet(getContext());
        if (walletFile != null) {
            walletFile.setMetaId(metaId);
            saveWalletFile(getContext(), walletFile);
        }
    }

    /**
     * Update managed wallet password
     * @param oldPassword before password
     * @param newPassword new password
     * @throws IOException
     */
    public void updateWalletPassword(String oldPassword, String newPassword) throws IOException {
        MetaWalletFile walletFile = loadWallet(getContext());
        try {
            if (walletFile != null) {
                walletFile.setPassword(oldPassword, newPassword);
                saveWalletFile(getContext(), walletFile);
            }
        }
        catch (CipherException e) {
            throw new IOException(e);
        }
    }

    /**
     * Delete managed wallet
     */
    public void deleteWallet() {
        getWalletFile(getContext()).delete();
    }

    /**
     * Check exists wallet file
     * @return if exists wallet, return true
     */
    public boolean existsWallet() {
        MetaWalletFile walletFile = loadWallet(getContext());
        if (walletFile != null) {
            if (walletFile.getMetaId() == null) {
                deleteWallet();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Generated key in SDK and Request register
     * @param password password of creating wallet
     * @param callback callback to response data
     */
    public void exportGeneratedKey(final String password, final Callback<RegisterKeyData> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // create wallet
                    MetaWalletFile walletFile = generateWalletFile(getContext(), password);
                    saveWalletFile(getContext(), walletFile);

                    // sign service id with key
                    String signature = walletFile.sign(password, getServiceId().getBytes("utf-8"));

                    // request register key
                    exportKey(signature, new Callback<RegisterKeyData>() {
                        @Override
                        public void onResult(ServiceResult<RegisterKeyData> result) {
                            if (result.isSuccess() || result.getError().getErrorCode() == ServiceResult.Error.CODE_UN_LINKED_SERVICE) {
                                // set meta id in wallet
                                setWalletMetaId(result.getResult().getMetaId());
                            }

                            callback.onResult(result);
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Request remove key
     * @param metaId   meta id to register in service
     * @param callback Response callback to request
     */
    @Override
    public void removeKey(String metaId, final Callback<RemoveKeyData> callback) {
        new RemoveKeyHandler(getContext(), getServiceId(), metaId, new Callback<RemoveKeyData>() {
            @Override
            public void onResult(ServiceResult<RemoveKeyData> result) {
                if (result.isSuccess()) {
                    // delete wallet file
                    if (result.getResult().getMetaId().equals(metaId)) {
                        deleteWallet();
                    }
                }

                callback.onResult(result);
            }
        }).request();
    }

    /**
     * Request sign
     * @param password password of wallet
     * @param nonce    message to sign
     * @param callback to response data
     */
    public void sign(final String password, final String nonce, final Callback<SignData> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // load wallet
                MetaWalletFile wallet = loadWallet(getContext());
                if (wallet == null) {
                    callback.onResult(new ServiceResult<SignData>(new ServiceResult.Error(Activity.RESULT_CANCELED, "Not found wallet")));
                    return;
                }

                try {
                    // sign
                    String signature = wallet.sign(password, nonce.getBytes("utf-8"));

                    callback.onResult(new ServiceResult<>(new SignData(wallet.getMetaId(), null, signature, null)));
                } catch (Exception e) {
                    callback.onResult(new ServiceResult<SignData>(new ServiceResult.Error(Activity.RESULT_CANCELED, e.getLocalizedMessage())));
                }
            }
        }).start();
    }
}
