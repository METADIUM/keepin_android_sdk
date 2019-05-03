package com.metadium;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metadium.util.SignatureUtils;
import org.web3j.crypto.*;

/**
 * Metadium wallet file
 */
public class MetaWalletFile {
    private String metaId;
    private WalletFile wallet;

    public MetaWalletFile() {
    }

    public MetaWalletFile(String metaId, WalletFile wallet) {
        this.metaId = metaId;
        this.wallet = wallet;
    }

    @JsonProperty("meta_id")
    public String getMetaId() {
        return metaId;
    }

    @JsonProperty("meta_id")
    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public WalletFile getWallet() {
        return wallet;
    }

    public void setWallet(WalletFile wallet) {
        this.wallet = wallet;
    }

    /**
     * sign message with private key of wallet
     * @param password wallet password
     * @param message  message to sign
     * @return signed message. encoded hex string to r + s + v
     * @throws CipherException
     */
    @JsonIgnore
    public String sign(String password, byte[] message) throws CipherException {
        Credentials credentials = getCredentials(password);
        Sign.SignatureData signatureData = Sign.signMessage(message, credentials.getEcKeyPair());
        return SignatureUtils.signatureDataToString(signatureData);
    }

    /**
     * change password of wallet
     * @param oldPassword before password
     * @param newPassword password to replace
     * @throws CipherException
     */
    @JsonIgnore
    public void setPassword(String oldPassword, String newPassword) throws CipherException {
        Credentials credentials = getCredentials(oldPassword);
        wallet = Wallet.createLight(newPassword, credentials.getEcKeyPair());
    }

    /**
     * Get private key
     * @param password password of wallet
     * @return key-pair
     * @throws CipherException
     */
    @JsonIgnore
    private Credentials getCredentials(String password) throws CipherException {
        return Credentials.create(Wallet.decrypt(password, wallet));
    }
}
