package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Parameters Object for login to NIFTY Cloud mobile backend with Facebook Account
 */
public class NCMBFacebookParameters {

    /**
     * User id of Facebook account
     */
    protected String userId;

    /**
     * Access token that can be obtained in the OAuth authentication
     */
    protected String accessToken;

    /**
     * Expiration date of access token
     */
    protected Date expirationDate;

    /**
     * Constructor
     * @param aUserId User id of Facebook account
     * @param anAccessToken Access token that can be obtained in the OAuth authentication
     * @param anExpirationDate Expiration date of access token
     */
    public NCMBFacebookParameters(@NonNull String aUserId, @NonNull String anAccessToken, @NonNull Date anExpirationDate) {
        if (aUserId == null || anAccessToken == null || anExpirationDate == null) {
            throw new IllegalArgumentException("aUserId or anAccessToken or anExpirationDate must not be null.");
        }
        userId = aUserId;
        accessToken = anAccessToken;
        expirationDate = anExpirationDate;
    }
}
