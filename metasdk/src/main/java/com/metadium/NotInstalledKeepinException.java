package com.metadium;

import android.content.Intent;

/**
 * Cause keepin app not insallted in device
 */
public class NotInstalledKeepinException extends Exception {
    private Intent intent;

    protected NotInstalledKeepinException(Intent intent) {
        this.intent = intent;
    }

    /**
     * Get an intent to install the app
     * @return
     */
    public Intent getIntent() {
        return intent;
    }
}
