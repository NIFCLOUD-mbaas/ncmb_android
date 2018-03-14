/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

/**
 * Parameters Object for login to NIF Cloud mobile backend with Twitter Account
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
