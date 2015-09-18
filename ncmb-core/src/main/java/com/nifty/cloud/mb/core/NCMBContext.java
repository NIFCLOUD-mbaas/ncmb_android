package com.nifty.cloud.mb.core;

import android.content.Context;

/**
 * Runtime context
 */
public class NCMBContext {
    /**
     * Application key
     */
    public String applicationKey;

    /**
     * Client key
     */
    public String clientKey;

    /**
     * Base URL of API
     */
    public String baseUrl;

    /**
     * Session key
     */
    public String sessionToken;

    /**
     * Current user id
     */
    public String userId;

    /**
     * Application context
     */
    public Context context;

    /**
     * Empty arguments constructor
     */
    NCMBContext() {
    }

    /**
     * Constructor without session key
     *
     * @param aContext
     * @param aApplicationKey
     * @param aClientKey
     * @param aBaseUrl
     */
    NCMBContext(Context aContext,
                String aApplicationKey,
                String aClientKey,
                String aBaseUrl) {
        context = aContext;
        applicationKey = aApplicationKey;
        clientKey = aClientKey;
        baseUrl = aBaseUrl;
    }

    /**
     * Constructor
     * @param aApplicationKey
     * @param aClientKey
     * @param aBaseUrl
     * @param aSessionToken
     */
    NCMBContext(String aApplicationKey,
                String aClientKey,
                String aBaseUrl,
                String aSessionToken) {
        applicationKey = aApplicationKey;
        clientKey = aClientKey;
        baseUrl = aBaseUrl;
        sessionToken = aSessionToken;
    }
}

