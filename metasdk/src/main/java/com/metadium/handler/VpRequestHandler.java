package com.metadium.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metadium.IKeepinService;
import com.metadium.result.Callback;
import com.metadium.result.ServiceResult;
import com.metadium.result.SignData;
import com.metadium.result.VpRequestData;

import java.util.Map;

/**
 * Request sign key to keepin service.
 */
public class VpRequestHandler extends ServiceMethodHandler<VpRequestData> {
    private String message;
    private String vpName;


    /**
     * Constructor
     * @param context
     * @param serviceId service id to request
     * @param message   message to sign
     * @param vpName    verifiable presentation to request
     * @param callback  callback to response data
     */
    public VpRequestHandler(Context context, String serviceId, String message, String vpName, Callback<VpRequestData> callback) {
        super(context, serviceId, callback);
        this.message = message;
        this.vpName = vpName;
    }

    @Override
    protected ServiceResult<VpRequestData> getData(Bundle resultData) {
        String userData = resultData.getString(RESULT_PARAM_USER_DATA);
        Map<String, String> dataMap = null;
        try {
            dataMap = new ObjectMapper().readValue(userData, Map.class);
        }
        catch (Exception e) {
        }
        return new ServiceResult<>(new VpRequestData(resultData.getString(RESULT_PARAM_META_ID), resultData.getString(RESULT_PARAM_META_DID), resultData.getString(RESULT_PARAM_SIGNATURE), resultData.getString(RESULT_PARAM_TRANSACTION_ID), dataMap));
    }

    @Override
    protected void send(IKeepinService keepinService, ResultReceiver resultReceiver) throws RemoteException {
        keepinService.requestVerificationPresentation(serviceId, message, vpName, resultReceiver);
    }
}
