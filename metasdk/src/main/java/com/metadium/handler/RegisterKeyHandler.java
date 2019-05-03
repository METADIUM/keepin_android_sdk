package com.metadium.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.metadium.IKeepinService;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.ServiceResult;

/**
 *  Request register key to keepin service.
 */
public class RegisterKeyHandler extends ServiceMethodHandler<RegisterKeyData> {
    private String signature;
    private String nonce;

    /**
     * Constructor
     * @param context
     * @param serviceId request service id
     * @param signature set signature if already exists key in the app
     * @param nonce      if keepin app generate key, set message to sign to key
     * @param callback   callback to response data
     */
    public RegisterKeyHandler(Context context, String serviceId, String signature, String nonce, Callback<RegisterKeyData> callback) {
        super(context, serviceId, callback);
        this.signature = signature;
        this.nonce = nonce;
    }

    @Override
    protected void send(IKeepinService keepinService, ResultReceiver resultReceiver) throws RemoteException {
        keepinService.requestAddKey(serviceId, signature, nonce, resultReceiver);
    }

    @Override
    protected ServiceResult<RegisterKeyData> getData(Bundle resultData) {
        return new ServiceResult<>(new RegisterKeyData(resultData.getString(RESULT_PARAM_META_ID), resultData.getString(RESULT_PARAM_SIGNATURE), resultData.getString(RESULT_PARAM_TRANSACTION_ID)));
    }
}
