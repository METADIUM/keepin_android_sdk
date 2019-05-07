package com.metadium;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;

import com.metadium.handler.RegisterKeyHandler;
import com.metadium.handler.RemoveKeyHandler;
import com.metadium.handler.ServiceReturnMethodHandler;
import com.metadium.handler.SignHandler;
import com.metadium.result.Callback;
import com.metadium.result.RegisterKeyData;
import com.metadium.result.RemoveKeyData;
import com.metadium.result.SignData;

/**
 * Keepin SDK<br/>
 *
 * Add service id in AndroidManifest.xml<br/>
 * <code>
 *  <meta-data android:name="KEEPIN_SERVICE_ID"
        android:value="a0ad73f907ab3ce870db6671afb4a417a3315f03e5eca6a2b44cb1fac08d5d60" />
 * </code>
 *
 * Initializing SDK<br/>
 * <code>
 *     try {
 *          KeepinSDK sdk = new KeepinSDK(getContext());
 *     } catch (NotInstalledKeepinException e) {
 *         // goto google play store
 *         startAcitivity(getActivity(), e.getIntent());
 *     }
 * </code>
 *
 */
public class KeepinSDK {
    private static final String KEEPIN_PACKAGE_NAME = "com.coinplug.metadium";

    private Context context;
    private String serviceId;

    /**
     * Construct<br/>
     * @param context
     * @throws NotInstalledKeepinException If Keepin App is not installed on the device
     */
    public KeepinSDK(Context context) throws NotInstalledKeepinException {
        this.context = context;

        serviceId = getServiceId(context);
        if (serviceId == null) {
            throw new RuntimeException("Not found service id");
        }

        if (!isKeepinAvailable(context)) {
            throw new NotInstalledKeepinException(getKeepinInstallIntent());
        }
    }

    Context getContext() {
        return context;
    }

    String getServiceId() {
        return serviceId;
    }

    /**
     * Determine whether the app is installed or not.
     * @param context
     * @return if installed, return true
     */
    public static boolean isKeepinAvailable(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(KEEPIN_PACKAGE_NAME, 0).enabled;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Get an intent to install keepin app.
     * @return
     */
    public static Intent getKeepinInstallIntent() {
        try {
            return new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setData(Uri.parse("market://details?id=" + KEEPIN_PACKAGE_NAME));
        }
        catch (Exception e) {
            // web browser
            return new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + KEEPIN_PACKAGE_NAME));
        }
    }

    /**
     * Requests keepin app to register key for the service.<br/>
     * Keepin app create key and registers key in Metadium block chain.<br/>
     * @param nonce Message to sign with the created key
     * @param callback Response callback to request
     */
    public void registerKey(String nonce, Callback<RegisterKeyData> callback) {
        new RegisterKeyHandler(context, serviceId, null, nonce, callback).request();
    }

    /**
     * Requests keepin app to register already created the key for the service.<br/>
     * Key must be ec algorithm (secp256k1)<br/>
     * Keepin app registers key in Metadium block chain.<br/>
     * @param signaure signed by service_id with key.
     * @param callback Response callback to request
     */
    public void exportKey(String signaure, Callback<RegisterKeyData> callback) {
        new RegisterKeyHandler(context, serviceId, signaure, null, callback).request();
    }

    /**
     * Request keepin app to remove key for service.<br/>
     * Keepin app remove key in Metadium block chain.<br/>
     * @param metaId   meta id to register in service
     * @param callback Response callback to request
     */
    public void removeKey(String metaId, Callback<RemoveKeyData> callback) {
        new RemoveKeyHandler(context, serviceId, metaId, callback).request();
    }

    /**
     * Request keepin app to sign with a key for the service.
     * @param nonce        message to sign
     * @param autoRegister if need register key, add key
     * @param callback Response callback to request
     */
    public void sign(String nonce, boolean autoRegister, Callback<SignData> callback) {
        new SignHandler(context, serviceId, nonce, autoRegister, callback).request();
    }

    /**
     * check to key for the service is registered.
     * @param context
     * @return if already has key for service, return true
     */
    public boolean hasKey(Context context) {
        ServiceReturnMethodHandler<Boolean> returnMethodHandler = new ServiceReturnMethodHandler<Boolean>(context) {
            @Override
            protected Boolean send(IKeepinService service) {
                try {
                    return service.hasKey(getServiceId());
                }
                catch (RemoteException e) {
                    return false;
                }
            }
        };

        try {
            return returnMethodHandler.request();
        }
        catch (RemoteException e) {
            return false;
        }
    }


    private String getServiceId(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString("KEEPIN_SERVICE_ID");
        }
        catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
