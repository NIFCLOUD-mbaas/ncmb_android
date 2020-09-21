/*
 * Copyright 2017-2020 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
package com.nifcloud.mbaas.core;

import android.support.annotation.NonNull;

/**
 * Parameters Object for login to NIFCLOUD mobile backend with Apple Account
 */
public class NCMBAppleParameters {
    protected String userId;
    protected String accessToken;
    protected String clientId;

    /***
     * Constructor
     *
     * @param aUserId User id of Apple account
     * @param anAccessToken Access token that can be obtained in the OAuth authentication
     * @param aClientId
     */
    public NCMBAppleParameters(@NonNull String aUserId, @NonNull String anAccessToken, @NonNull String aClientId) {
        if (aUserId == null || anAccessToken == null || aClientId == null) {
            throw new IllegalArgumentException("aUserId or anAccessToken or aClientId must not be null.");
        }
        this.userId = aUserId;
        this.accessToken = anAccessToken;
        this.clientId = aClientId;
    }
}
