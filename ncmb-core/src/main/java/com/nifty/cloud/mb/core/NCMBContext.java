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

