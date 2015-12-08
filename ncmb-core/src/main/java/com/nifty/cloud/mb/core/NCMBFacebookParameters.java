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
    @NonNull
    public String userId;

    /**
     * Access token that can be obtained in the OAuth authentication
     */
    @NonNull
    public String accessToken;

    /**
     * Expiration date of access token
     */
    @NonNull
    public Date expirationDate;

    /**
     *
     * @param aUserId
     * @param aAccessToken
     * @param aExpirationDate
     */
    public NCMBFacebookParameters(String aUserId, String aAccessToken, Date aExpirationDate) {
        userId = aUserId;
        accessToken = aAccessToken;
        expirationDate = aExpirationDate;
    }
}
