package com.metadium.result;

/**
 * Response result
 * @param <T> data to response
 */
public class ServiceResult<T> {
    private Error error;
    private T result;

    /**
     * Constructor when response is success
     * @param result success response data
     */
    public ServiceResult(T result) {
        this.result = result;
    }

    /**
     * Constructor when response is error
     * @param error error response data
     */
    public ServiceResult(Error error) {
        this.error = error;
    }

    /**
     * whether success or error response
     * @return if success return true
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Get error response data
     * @return error
     */
    public Error getError() {
        return error;
    }

    /**
     * Get success response data
     * @return result data
     */
    public T getResult() {
        return result;
    }

    /**
     * Error Response
     */
    public static class Error {
        /**
         * User cancel error code (key register/sign/remove)
         */
        public static final int CODE_USER_CANCEL = 0;
        /**
         * Invalid parameter value error code
         */
        public static final int CODE_INVALID_PARAM = -100500;
        /**
         * Invalid signature
         */
        public static final int CODE_INVALID_SIGNATURE = CODE_INVALID_PARAM-1;
        /**
         * Keepin App has not yet created a meta id.
         */
        public static final int CODE_NOT_CREATE_META_ID = CODE_INVALID_SIGNATURE-1;
        /**
         * The key for this service is not registered.
         */
        public static final int CODE_UN_LINKED_SERVICE = CODE_NOT_CREATE_META_ID-1;

        private int errorCode;
        private String errorMessage;

        /**
         * Constructor
         * @param errorCode    error code
         * @param errorMessage error message
         */
        public Error(int errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        /**
         * Get error code
         * @return error code
         */
        public int getErrorCode() {
            return errorCode;
        }

        /**
         * Get error message<br/>
         * if error code is user cancel, message is null
         * @return error message
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
