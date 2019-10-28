package com.metadium.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.metadium.IKeepinService;
import com.metadium.result.Callback;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.ServiceResult;

/**
 * Request remove key to keepin service.
 */
public class RemoveKeyHandler extends ServiceMethodHandler<RemoveKeyData> {
    private String metaId;

    /**
     * Constructor
     * @param context
     * @param serviceId service id to request
     * @param metaId    meta id of account
     * @param callback  callback to response data
     */
    public RemoveKeyHandler(Context context, String serviceId, String metaId, Callback<RemoveKeyData> callback) {
        super(context, serviceId, callback);
        this.metaId = metaId;
    }

    @Override
    protected void send(IKeepinService keepinService, ResultReceiver resultReceiver) throws RemoteException {
        keepinService.requestRemoveKey(serviceId, metaId, resultReceiver);
    }

    @Override
    protected ServiceResult<RemoveKeyData> getData(Bundle resultData) {
        return new ServiceResult<>(new RemoveKeyData(resultData.getString(RESULT_PARAM_META_ID), resultData.getString(RESULT_PARAM_META_DID), resultData.getString(RESULT_PARAM_TRANSACTION_ID)));
    }
}
