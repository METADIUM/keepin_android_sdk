package com.metadium.result;

/**
 * Response data for request removed key
 */
public class RemoveKeyData {
    private String metaId;
    private String transactionId;

    /**
     * Constructor
     * @param metaId        meta id of keepin
     * @param transactionId transaction id of removed key
     */
    public RemoveKeyData(String metaId, String transactionId) {
        this.metaId = metaId;
        this.transactionId = transactionId;
    }

    /**
     * Get meta id
     * @return meta id
     */
    public String getMetaId() {
        return metaId;
    }

    /**
     * Get removed key
     * @return address
     */
    public String getTransactionId() {
        return transactionId;
    }
}
