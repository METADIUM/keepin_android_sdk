package com.metadium.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.metadium.IKeepinService;
import com.metadium.result.ReturnCallback;

/**
 * Return value service request handler
 * @param <T> return class
 */
public abstract class ServiceReturnMethodHandler<T> extends ServiceBaseHandler implements ServiceConnection {
    private Context context;
    private ReturnCallback<T> callback;

    public ServiceReturnMethodHandler(Context context, ReturnCallback<T> callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * request service api
     * @return return value
     */
    public void request() {
        context.bindService(
                serviceIntent(),
                ServiceReturnMethodHandler.this,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IKeepinService keepinService = IKeepinService.Stub.asInterface(iBinder);
        callback.onReturn(send(keepinService));
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        context.unbindService(this);
    }

    /**
     * abstract request in service.
     * @param service keepin service to connected
     * @return return value
     */
    abstract protected T send(IKeepinService service);
}
