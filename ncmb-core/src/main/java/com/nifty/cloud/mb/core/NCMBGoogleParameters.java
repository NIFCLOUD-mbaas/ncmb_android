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
 * Parameters Object for login to NIF Cloud mobile backend with Google Account
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
