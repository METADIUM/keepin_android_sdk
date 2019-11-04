package com.metadium.result;

import java.util.Map;

/**
 * Response data for request verifiable presentation
 */
public class VpRequestData extends RegisterKeyData {
    private Map<String, String> userData;

    public VpRequestData(String metaId, String did, String signature, String transactionId, Map<String, String> userData) {
        super(metaId, did, signature, transactionId);
        this.userData = userData;
    }

    public Map<String, String> getUserData() {
        return userData;
    }
}
