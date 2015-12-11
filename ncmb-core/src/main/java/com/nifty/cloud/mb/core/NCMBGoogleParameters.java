package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

/**
 * Parameters Object for login to NIFTY Cloud mobile backend with Google Account
 */
public class NCMBGoogleParameters {

    /**
     * User id of Facebook account
     */
    protected String userId;

    /**
     * Access token that can be obtained in the OAuth authentication
     */
    protected String accessToken;

    /**
     * Constructor
     * @param aUserId User id of Facebook account
     * @param anAccessToken Access token that can be obtained in the OAuth authentication
     */
    public NCMBGoogleParameters(@NonNull String aUserId, @NonNull String anAccessToken) {
        if (aUserId == null || anAccessToken == null) {
            throw new IllegalArgumentException("aUserId or anAccessToken must not be null.");
        }
        userId = aUserId;
        accessToken = anAccessToken;
    }
}
