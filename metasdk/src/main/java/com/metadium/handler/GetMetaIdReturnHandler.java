package com.metadium.handler;

import android.content.Context;
import android.os.RemoteException;

import com.metadium.IKeepinService;
import com.metadium.result.ReturnCallback;

/**
 *  Get Meta ID to keepin service.
 */
public class GetMetaIdReturnHandler extends ServiceReturnMethodHandler<String> {
    /**
     * Constructor
     * @param context
     * @param callback  callback getMetaId
     */
    public GetMetaIdReturnHandler(Context context, ReturnCallback<String> callback) {
        super(context, callback);
    }

    @Override
    protected String send(IKeepinService service) {
        try {
            return service.getMetaId();
        }
        catch (RemoteException e) {
            return null;
        }
    }
}
