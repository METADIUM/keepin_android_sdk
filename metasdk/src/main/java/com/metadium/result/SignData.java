package com.metadium.result;

/**
 * Response data for request sign
 */
public class SignData {
    private String metaId;
    private String signature;

    /**
     * Constructor
     * @param metaId    meta id of keepin
     * @param signature signature to sign with key for service
     */
    public SignData(String metaId, String signature) {
        this.metaId = metaId;
        this.signature = signature;
    }

    /**
     * Get meta id
     * @return meta id
     */
    public String getMetaId() {
        return metaId;
    }

    /**
     * Get signature
     * @return signature with hex string
     */
    public String getSignature() {
        return signature;
    }
}
