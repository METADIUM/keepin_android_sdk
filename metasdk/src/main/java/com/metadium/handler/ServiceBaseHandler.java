package com.metadium.handler;

import android.content.Intent;

/**
 * Keepin service base handler
 */
abstract class  ServiceBaseHandler {
    static final String INTENT_ACTION = "keepin.service";
    static final String PACKAGE_NAME = "com.coinplug.metadium";

    /**
     * Get keepin service intent
     * @return service intent
     */
    Intent serviceIntent() {
        // bind meta service
        Intent serviceIntent = new Intent(INTENT_ACTION);
        serviceIntent.setPackage(PACKAGE_NAME);
        return serviceIntent;
    }
}
