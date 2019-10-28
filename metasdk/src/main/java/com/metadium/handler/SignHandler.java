package com.metadium.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.metadium.IKeepinService;
import com.metadium.result.Callback;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;

/**
 * Request sign key to keepin service.
 */
public class SignHandler extends ServiceMethodHandler<SignData> {
    private String message;
    private boolean autoRegister;
    private String metaId;

    /**
     * Constructor
     * @param context
     * @param serviceId service id to request
     * @param message   message to sign
     * @param metaId    require to sign with meta id
     * @param callback  callback to response data
     */
    public SignHandler(Context context, String serviceId, String message, boolean autoRegister, String metaId, Callback<SignData> callback) {
        super(context, serviceId, callback);
        this.message = message;
        this.autoRegister = autoRegister;
        this.metaId = metaId;
    }

    public SignHandler(Context context, String serviceId, String message, Callback<SignData> callback) {
        this(context, serviceId, message, false, null, callback);
    }

    @Override
    protected ServiceResult<SignData> getData(Bundle resultData) {
        return new ServiceResult<>(new SignData(resultData.getString(RESULT_PARAM_META_ID), resultData.getString(RESULT_PARAM_META_DID), resultData.getString(RESULT_PARAM_SIGNATURE), resultData.getString(RESULT_PARAM_TRANSACTION_ID)));
    }

    @Override
    protected void send(IKeepinService keepinService, ResultReceiver resultReceiver) throws RemoteException {
        keepinService.requestSign(serviceId, message, autoRegister, metaId, resultReceiver);
    }
}
