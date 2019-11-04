// IKeepinService.aidl
package com.metadium;

import android.os.ResultReceiver;

interface IKeepinService {
    void requestAddKey(String serviceId, String signature, String nonce, in ResultReceiver resultReceiver);
    void requestRemoveKey(String serviceId, String metaId, in ResultReceiver resultReceiver);
    void requestSign(String serviceId, String nonce, boolean auotRegister, String metaId, in ResultReceiver resultReceiver);
    void requestVerificationPresentation(String serviceId, String nonce, String vp, in ResultReceiver resultReceiver);
    String getMetaId();
    boolean hasKey(String serviceId);
}
