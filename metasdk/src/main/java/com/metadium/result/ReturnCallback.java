package com.metadium.result;

/**
 * Keepin service return function callback.
 * @param <T> data to callback
 */
public interface ReturnCallback<T> {
    /**
     * Called in response to request
     * @param result response result
     */
    public void onReturn(T result);
}
