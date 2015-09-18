package com.nifty.cloud.mb.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Core class for NCMB API
 */
public class NCMB {
    /** Version of this SDK */
    public static final String SDK_VERSION = "2.0.0";

    /** Prefix of keys in metadata for NCMB settings */
    public static final String METADATA_PREFIX = "com.nifty.cloud.mb.";

    /** Default base URL of API */
    public static final String DEFAULT_DOMAIN_URL = "https://mb.api.cloud.nifty.com/";

    /** Default API version */
    public static final String DEFAULT_API_VERSION = "2013-09-01";

    /** OAuth type of Twitter */
    public static final String OAUTH_TWITTER = "twitter";

    /** OAuth type of Facebook */
    public static final String OAUTH_FACEBOOK = "facebook";

    /** OAuth type of Google */
    public static final String OAUTH_GOOGLE = "google";

    /** Anonymous authentication */
    public static final String OAUTH_ANONYMOUS = "anonymous";

    /** Service types */
    public enum ServiceType {
        OBJECT,
        USER,
        ROLE,
        INSTALLATION,
        PUSH
    };

    /**
     * Runtime Context
     */
    protected static NCMBContext sCurrentContext;

    /**
     * Setup SDK internals
     *
     * @param context Application context
     * @param applicationKey application key
     * @param clientKey client key
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey) {
        initialize(context, applicationKey, clientKey, null, null);
    }

    /**
     * Setup SDK internals with api server host name
     *
     * @param context Application context
     * @param applicationKey application key
     * @param clientKey client key
     * @param domainUrl host name for api request
     * @param apiVersion version for rest api
     */
    public static void initialize(Context context,
                                  String applicationKey,
                                  String clientKey,
                                  String domainUrl,
                                  String apiVersion) {
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
    }

    /**
     * Create service instance from given type string
     *
     * @param serviceType identifier for service API
     * @return Object of each service class
     */
    public static NCMBService factory(ServiceType serviceType) throws IllegalArgumentException {
        NCMBService service;

        switch (serviceType) {
            case OBJECT:
                service = (NCMBService)new NCMBObjectService(sCurrentContext);
                break;
            case USER:
                service = (NCMBService)new NCMBUserService(sCurrentContext);
                break;
            case ROLE:
                service = (NCMBService)new NCMBRoleService(sCurrentContext);
                break;
            case INSTALLATION:
                service = (NCMBInstallationService)new NCMBInstallationService(sCurrentContext);
                break;
            case PUSH:
                service = (NCMBPushService)new NCMBPushService(sCurrentContext);
                break;
            default:
                throw new IllegalArgumentException("Invalid serviceType");
        }
        return service;
    }

    /**
     * Getting metadata from given context
     *
     * @param context Application context
     * @param name Name of metadata
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
     * @param timeout milliseconds
     */
    public static void setTimeout(int timeout){
        NCMBConnection.sConnectionTimeout = timeout;
    }

    /**
     * Getting time out
     * @return timeout milliseconds
     */
    public static int getTimeout(){
        return NCMBConnection.sConnectionTimeout;
    }
}
