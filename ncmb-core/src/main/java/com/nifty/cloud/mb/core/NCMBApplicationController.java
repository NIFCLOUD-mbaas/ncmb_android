package com.nifty.cloud.mb.core;

import android.app.Application;

/**
 * Holding the state of the application
 */
public class NCMBApplicationController extends Application {
    private static NCMBApplicationController sApplicationState;

    /**
     * Set application state
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationState = this;
    }

    /**
     * Get application state
     * @return ApplicationController
     */
    public static NCMBApplicationController getApplicationState() {
        return sApplicationState;
    }
}