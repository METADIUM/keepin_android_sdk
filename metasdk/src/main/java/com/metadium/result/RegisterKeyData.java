package com.metadium.result;

/**
 * Response data for request register key
 */
public class RegisterKeyData {
    private String metaId;
    private String did;
    private String signature;
    private String transactionId;

    /**
     * Constructor
     * @param metaId        meta id of keepin
     * @param did           did of metadium
     * @param signature     signed value with key generated by app
     * @param transactionId transaction hash of key registered in Metadium
     */
    public RegisterKeyData(String metaId, String did, String signature, String transactionId) {
        this.metaId = metaId;
        this.did = did;
        this.transactionId = transactionId;
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
     * Get signature to sign key for service
     * @return signature with hex string
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Get metadium transaction id
     * @return transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Get meta did
     * @return did
     */
    public String getDid() {
        return did;
    }
}
