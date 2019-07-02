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
        public static final int CODE_INVALID_PARAM = 5;
        /**
         * Invalid signature
         */
        public static final int CODE_INVALID_SIGNATURE = 6;
        /**
         * Keepin App has not yet created a meta id.
         */
        public static final int CODE_NOT_CREATE_META_ID = 1;
        /**
         * Keepin App not has meta id.
         */
        public static final int CODE_NOT_MATCHED_META_ID = 2;
        /**
         * The key for this service is not registered by user.
         */
        public static final int CODE_UN_LINKED_SERVICE = 3;
        /**
         * This service is not registered. Request register service. https://github.com/METADIUM/static/blob/master/services.json
         */
        public static final int ERROR_CODE_NOT_REGISTER_SERVICE = 4;

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
