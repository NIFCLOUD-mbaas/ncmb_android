package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

/**
 * Parameters Object for login to NIFTY Cloud mobile backend with Twitter Account
 */
public class NCMBTwitterParameters {

    protected String userId;

    protected String screenName;

    protected String consumerKey;

    protected String consumerSecret;

    protected String accessToken;

    protected String accessTokenSecret;

    /**
     * Constructor
     * @param aUserId User id of Twitter account
     * @param aScreenName Screen name of Twitter account
     * @param aConsumerKey Consumer key of registered application in Twitter developer site
     * @param aConsumerSecret Consumer secret of registered application in Twitter developer site
     * @param anAccessToken Access token that can be obtained in the OAuth authentication
     * @param anAccessTokenSecret Access token secret that can be obtained in the OAuth authentication
     */
    public NCMBTwitterParameters(
            @NonNull String aUserId,
            @NonNull String aScreenName,
            @NonNull String aConsumerKey,
            @NonNull String aConsumerSecret,
            @NonNull String anAccessToken,
            @NonNull String anAccessTokenSecret
    )
    {
        if (aUserId == null ||
                aScreenName == null ||
                aConsumerKey == null ||
                aConsumerSecret == null ||
                anAccessToken == null ||
                anAccessTokenSecret == null ) {
            throw new IllegalArgumentException("constructor parameters must not be null.");
        }
        userId = aUserId;
        screenName = aScreenName;
        consumerKey = aConsumerKey;
        consumerSecret = aConsumerSecret;
        accessToken = anAccessToken;
        accessTokenSecret = anAccessTokenSecret;
    }
}
