package com.metadium.handler;

import android.content.Context;
import android.os.RemoteException;

import com.metadium.IKeepinService;
import com.metadium.result.ReturnCallback;

/**
 *  Has service key of service id to keepin service.
 */
public class HasKeyReturnHandler extends ServiceReturnMethodHandler<Boolean> {
    private String serviceId;

    /**
     * Constructor
     * @param context
     * @param serviceId service id to check
     * @param callback  callback hasKey
     */
    public HasKeyReturnHandler(Context context, String serviceId, ReturnCallback<Boolean> callback) {
        super(context, callback);
        this.serviceId = serviceId;
    }

    @Override
    protected Boolean send(IKeepinService service) {
        try {
            return service.hasKey(serviceId);
        }
        catch (RemoteException e) {
            return null;
        }
    }
}
