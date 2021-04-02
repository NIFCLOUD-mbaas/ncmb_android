/*
 * Copyright 2017-2018 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.Map;

/**
 * The NCMB Class contains sdk initialize method and factory method for Service class
 */
public class NCMB {
    /**
     * Version of this SDK
     */
    public static final String SDK_VERSION = "4.0.0";

    /**
     * Prefix of keys in metadata for NCMB settings
     */
    public static final String METADATA_PREFIX = "com.nifcloud.mbaas.";

    /**
     * Default base URL of API
     */
    public static final String DEFAULT_DOMAIN_URL = "https://mbaas.api.nifcloud.com/";

    /**
     * Default API version
     */
    public static final String DEFAULT_API_VERSION = "2013-09-01";

    /**
     * OAuth type of Twitter
     */
    public static final String OAUTH_TWITTER = "twitter";

    /**
     * OAuth type of Facebook
     */
    public static final String OAUTH_FACEBOOK = "facebook";

    /**
     * OAuth type of Google
     */
    public static final String OAUTH_GOOGLE = "google";

    /**
     * Anonymous authentication
     */
    public static final String OAUTH_ANONYMOUS = "anonymous";

    // SharedPreferences key name
    private static final String APPLICATION_KEY = "applicationKey";
    private static final String CLIENT_KEY = "clientKey";
    private static final String API_BASE_URL = "apiBaseUrl";
    private static final String RESPONSE_VALIDATION = "responseValidation";

    // SharedPreferences file name
    private static final String PREFERENCE_FILE_NAME = "NCMB";

    /**
     * Service types
     */
    public enum ServiceType {
        OBJECT,
        USER,
        ROLE,
        INSTALLATION,
        PUSH,
        FILE,
        SCRIPT
    }

    ;

    /**
     * Runtime Context
     */
    @SuppressLint("StaticFieldLeak")
    private static NCMBContext sCurrentContext;

    /**
     * Response signature valid flag
     */
    private static boolean sResponseValidation;

    /**
     * service pool
     */
    protected static NCMBServicePool sServicePool;

    /**
     * Setup SDK internals
     *
     * @param context        Application context
     * @param applicationKey application key
     * @param clientKey      client key
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey) {
        initialize(context, applicationKey, clientKey, null, null, null);
    }

    /**
     * Setup SDK internals
     *
     * @param context                  Application context
     * @param applicationKey           application key
     * @param clientKey                client key
     * @param installationCustomFields installation custom fileds
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey,
                                  Map<String,String> installationCustomFields) {
        initialize(context, applicationKey, clientKey, null, null, installationCustomFields);
    }

    /**
     * Setup SDK internals with api server host name
     *
     * @param context        Application context
     * @param applicationKey application key
     * @param clientKey      client key
     * @param domainUrl      host name for api request
     * @param apiVersion     version for rest api
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey,
                                  String domainUrl,
                                  String apiVersion) {
        initialize(context, applicationKey, clientKey, domainUrl, apiVersion, null);
    }
    /**
     * Setup SDK internals with api server host name
     *
     * @param context                  Application context
     * @param applicationKey           application key
     * @param clientKey                client key
     * @param domainUrl                host name for api request
     * @param apiVersion               version for rest api
     * @param installationCustomFields installation custom fileds
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey,
                                  String domainUrl,
                                  String apiVersion,
                                  Map<String,String> installationCustomFields) {
        String aDomainUrl = domainUrl;
        if (aDomainUrl == null) {
            aDomainUrl = getMetadata(context, METADATA_PREFIX + "DOMAIN_URL");
        }
        if (aDomainUrl == null) {
            aDomainUrl = DEFAULT_DOMAIN_URL;
        }

        String aApiVersion = apiVersion;
        if (aApiVersion == null) {
            aApiVersion = getMetadata(context, METADATA_PREFIX + "API_VERSION");
        }
        if (aApiVersion == null) {
            aApiVersion = DEFAULT_API_VERSION;
        }

        String apiBaseUrl = aDomainUrl + aApiVersion + "/";
        sCurrentContext = new NCMBContext(context, applicationKey, clientKey, apiBaseUrl);
        sServicePool = new NCMBServicePool();

        // 永続化
        Context appState = NCMBApplicationController.getApplicationState();
        if (appState != null) { // Manifestに設定が追加されていない場合はnull
            SharedPreferences.Editor editor = createSharedPreferences().edit();
            editor.putString(APPLICATION_KEY, applicationKey);
            editor.putString(CLIENT_KEY, clientKey);
            editor.putString(API_BASE_URL, apiBaseUrl);
            editor.apply();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NCMBNotificationUtils utils = new NCMBNotificationUtils(context);
            utils.settingDefaultChannels();
        }

        NCMBInstallationUtils.saveInstallation(installationCustomFields);
    }

    /**
     * Create service instance from given type string
     *
     * @param serviceType identifier for service API
     * @return Object of each service class
     */
    public static NCMBService factory(ServiceType serviceType) throws IllegalArgumentException {
        return sServicePool.get(serviceType, sCurrentContext);
    }

    /**
     * Getting metadata from given context
     *
     * @param context Application context
     * @param name    Name of metadata
     * @return String or null;
     */
    protected static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // if we can’t find it in the manifest, just return null
        }
        return null;
    }

    /**
     * Setting time out
     * Default 10000 milliseconds
     *
     * @param timeout milliseconds
     */
    public static void setTimeout(int timeout) {
        NCMBConnection.sConnectionTimeout = timeout;
    }

    /**
     * Getting time out
     *
     * @return timeout milliseconds
     */
    public static int getTimeout() {
        return NCMBConnection.sConnectionTimeout;
    }

    /**
     * Get NCMBContext
     */
    public static NCMBContext getCurrentContext() {
        if (sCurrentContext == null) {
            Context context = NCMBApplicationController.getApplicationState();
            if (!getApplicationKey().isEmpty() && !getClientKey().isEmpty() && !getApiBaseUrl().isEmpty() && context == null) {
                throw new IllegalArgumentException("Please call the NCMB.initialize() method.");
            }

            // staticが破棄(プロセスの終了やGCによる解放など)された後にinitializeメソッドが実行されていない場合は永続化したデータを元に再生成
            sCurrentContext = new NCMBContext(
                    context,
                    getApplicationKey(),
                    getClientKey(),
                    getApiBaseUrl());
        }
        return sCurrentContext;
    }

    private static String getApplicationKey() {
        return createSharedPreferences().getString(APPLICATION_KEY, "");
    }

    private static String getClientKey() {
        return createSharedPreferences().getString(CLIENT_KEY, "");
    }

    private static String getApiBaseUrl() {
        return createSharedPreferences().getString(API_BASE_URL, "");
    }

    static boolean getResponseValidation() {
        return sResponseValidation || createSharedPreferences().getBoolean(RESPONSE_VALIDATION, false);
    }

    /**
     * Determining whether a response has not been tampered<br>
     * The default is invalid<br>
     * true = valid , false = inValid
     *
     * @param responseValidation response validation enable flag
     */
    public static void enableResponseValidation(boolean responseValidation) {
        sResponseValidation = responseValidation;
        if (getCurrentContext().context != null) {
            SharedPreferences data = getCurrentContext().context.getApplicationContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean(RESPONSE_VALIDATION, responseValidation);
            editor.apply();
        }
    }

    private static SharedPreferences createSharedPreferences() {
        Context appState = NCMBApplicationController.getApplicationState();
        if (appState != null) {
            return NCMBApplicationController.getApplicationState().getApplicationContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        }
        return NCMB.getCurrentContext().context.getApplicationContext().getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

}
