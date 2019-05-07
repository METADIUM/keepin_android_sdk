package com.metadium.result;

/**
 * Response data for request sign
 */
public class SignData extends RegisterKeyData {
    public SignData(String metaId, String signature, String transactionId) {
        super(metaId, signature, transactionId);
    }
}
