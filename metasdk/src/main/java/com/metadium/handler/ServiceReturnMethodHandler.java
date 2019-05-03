package com.metadium.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.metadium.IKeepinService;

/**
 * Return value service request handler
 * @param <T> return class
 */
public abstract class ServiceReturnMethodHandler<T> extends ServiceBaseHandler implements ServiceConnection {
    private Context context;

    private IKeepinService keepinService;
    private Object keepinServiceNotify = new Object();

    public ServiceReturnMethodHandler(Context context) {
        this.context = context;
    }

    /**
     * request service api
     * @return return value
     * @throws RemoteException
     */
    public T request() throws RemoteException {
        context.bindService(
                serviceIntent(),
                this,
                Context.BIND_AUTO_CREATE
        );

        // waiting service connected
        for (int i = 0; i < 10; i++) {
            try {
                keepinServiceNotify.wait(1000);
            }
            catch (InterruptedException e) {
                if (keepinService != null) {
                    break;
                }
            }
        }

        // request
        if (keepinService != null) {
            return send(keepinService);
        }

        throw new RemoteException("Timeout");
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        keepinService = IKeepinService.Stub.asInterface(iBinder);
        keepinServiceNotify.notify(); // notify keepin service connection
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
