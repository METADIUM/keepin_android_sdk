package com.metadium.result;

/**
 * Keepin service callback.
 * @param <T> data to callback
 */
public interface Callback<T> {
    /**
     * Called in response to request
     * @param result response result
     */
    public void onResult(ServiceResult<T> result);
}
