package com.metadium.result;

/**
 * Response data for request sign
 */
public class SignData extends RegisterKeyData {
    public SignData(String metaId, String did, String signature, String transactionId) {
        super(metaId, did, signature, transactionId);
    }
}
