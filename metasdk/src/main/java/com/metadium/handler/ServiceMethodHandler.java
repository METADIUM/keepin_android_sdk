package com.metadium.handler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;

import com.metadium.IKeepinService;
import com.metadium.result.Callback;
import com.metadium.result.ServiceResult;

/**
 * Async service request handler
 * @param <T> response data class
 */
abstract class ServiceMethodHandler<T> extends  ServiceBaseHandler implements ServiceConnection {
    static final String RESULT_PARAM_META_ID = "meta_id";
    static final String RESULT_PARAM_SIGNATURE = "signature";
    static final String RESULT_PARAM_TRANSACTION_ID = "transaction_id";
    private static final String RESULT_PARAM_ERROR_CODE = "error_code";
    private static final String RESULT_PARAM_ERROR = "error";

    private Context context;
    String serviceId;
    private Callback<T> callback;

    ServiceMethodHandler(Context context, String serviceId, Callback<T> callback) {
        this.context = context;
        this.serviceId = serviceId;
        this.callback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IKeepinService keepinService = IKeepinService.Stub.asInterface(iBinder);

        try {
            // Request when the service is connected with custom result receiver
            send(keepinService, new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == Activity.RESULT_OK) {
                        // request success
                        onSuccessResult(resultData);
                    }
                    else {
                        // request error
                        onError(resultData);
                    }

                    unbindService();
                }
            });
        }
        catch (Exception e) {
            // Remote exception
            callback.onResult(new ServiceResult<T>(new ServiceResult.Error(ServiceResult.Error.CODE_USER_CANCEL, e.getLocalizedMessage())));
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        context.unbindService(this);
    }

    /**
     * Request in keepin service
     */
    public void request() {
        context.bindService(
                serviceIntent(),
                this,
                Context.BIND_AUTO_CREATE
        );
    }

    private void unbindService() {
        try {
            context.unbindService(this);
        }
        catch (Exception e) {
            // // already unbound
        }
    }

    /**
     * Make success result data
     * @param resultData service response
     */
    private void onSuccessResult(Bundle resultData) {
        callback.onResult(getData(resultData));
    }

    /**
     * Make error result data
     * @param resultData service response
     */
    private void onError(Bundle resultData) {
        callback.onResult(new ServiceResult<T>(new ServiceResult.Error(resultData.getInt(RESULT_PARAM_ERROR_CODE, ServiceResult.Error.CODE_USER_CANCEL), resultData.getString(RESULT_PARAM_ERROR))));
    }

    /**
     * Make result data
     * @param resultData service response
     * @return result data
     */
    abstract protected ServiceResult<T> getData(Bundle resultData);

    /**
     * abstract request in service.
     * must implement service request to response in result receiver
     * @param keepinService  keepin service
     * @param resultReceiver ResultReceiver to callback
     * @throws RemoteException
     */
    abstract protected void send(IKeepinService keepinService, ResultReceiver resultReceiver) throws RemoteException;
}