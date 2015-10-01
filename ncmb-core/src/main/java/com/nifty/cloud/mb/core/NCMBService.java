package com.nifty.cloud.mb.core;

import org.json.JSONObject;

/**
 * Abstract class for Service class
 */
public abstract class NCMBService {
    /**
     * Serivce context
     */
    protected NCMBContext mContext;

    /**
     * Serivce path of API URL
     */
    protected String mServicePath;

    /**
     * Innter class for params of request
     */
    protected class RequestParams {
        public String url = null;
        public String type = null;
        public String content = null;
        public JSONObject query = null;
    }

    abstract class ServiceCallback implements RequestApiCallback {
        /** service instance */
        public NCMBService mService = null;
        /** callback object given from developer */
        public CallbackBase mCallback = null;
        /** options */
        public JSONObject mOptions = null;

        /**
         * Generic constructor
         *
         * @param service
         * @param callback
         */
        ServiceCallback(NCMBService service, CallbackBase callback) {
            mService = service;
            mCallback = callback;
            mOptions = null;
        }

        /**
         * Constructor
         *
         * @param service
         * @param callback callback for action
         */
        ServiceCallback(NCMBService service, DoneCallback callback) {
            this(service, (CallbackBase) callback);
        }

        /**
         * Constructor
         *
         * @param service
         * @param callback callback for getting user
         */
        ServiceCallback(NCMBService service, UserCallback callback) {
            this(service, (CallbackBase) callback);
        }

        /**
         * Constructor
         *
         * @param service
         * @param callback callback for login
         */
        ServiceCallback(NCMBService service, LoginCallback callback) {
            this(service, (CallbackBase) callback);
        }

        /**
         * Constructor for NCMBObject callback
         */
        ServiceCallback(NCMBService service, ExecuteServiceCallback callback) {
            this(service, (CallbackBase) callback);
        }


        /**
         * Constructor with options
         *
         * @param service  service object
         * @param callback callback object
         * @param options  options as JSON
         */
        ServiceCallback(NCMBService service, LoginCallback callback, JSONObject options) {
            this(service, (CallbackBase) callback);
            mOptions = options;
        }

        ServiceCallback(NCMBService service, RoleCallback callback) {
            this(service, (CallbackBase) callback);
        }

        ServiceCallback(NCMBService service, SearchUserCallback callback) {
            this(service, (CallbackBase) callback);
        }

        ServiceCallback(NCMBService service, SearchPushCallback callback) {
            this(service, (CallbackBase) callback);
        }

        ServiceCallback(NCMBService service, SearchInstallationCallback callback) {
            this(service, (CallbackBase) callback);
        }

        abstract public void handleResponse(NCMBResponse response) throws NCMBException;

        abstract public void handleError(NCMBException e);

        /**
         * override SendRequestCallback#done
         *
         * @param response response obejct
         * @param e        excettion when error
         */
        @Override
        public void done(NCMBResponse response, NCMBException e) {
            try {
                if (e != null) {
                    throw e;
                }
                handleResponse(response);
            } catch (NCMBException e2) {
                handleError(e2);
            }
        }
    }

    /**
     * constructor
     *
     * @param context Service context
     */
    NCMBService(NCMBContext context) {
        mContext = context;
        mServicePath = null;
    }

    /**
     * sendRequest shortcut
     *
     * @param url  URL
     * @param type http method
     * @return NCMBResponse object
     */
    protected NCMBResponse sendRequest(String url, String type) throws NCMBException {
        return sendRequest(url, type, null, null);
    }

    /**
     * sendRequest shortcut
     *
     * @param url     URL
     * @param type    http method
     * @param content content body
     * @return NCMBResponse object
     */
    protected NCMBResponse sendRequest(String url, String type, String content) throws NCMBException {
        return sendRequest(url, type, content, null);
    }

    /**
     * send request
     *
     * @param url         URL
     * @param type        http method
     * @param content     content body
     * @param queryString query string
     * @return NCMBResponse response object
     */
    protected NCMBResponse sendRequest(String url, String type, String content, JSONObject queryString)
            throws NCMBException {

        if (mContext.sessionToken == null) {
            mContext.sessionToken = NCMBUser.getSessionToken();
        }
        String sessionToken = mContext.sessionToken;
        String applicationKey = mContext.applicationKey;
        String clientKey = mContext.clientKey;

        NCMBRequest request = new NCMBRequest(url, type, content, queryString,
                sessionToken, applicationKey, clientKey);

        NCMBConnection connection = new NCMBConnection(request);
        NCMBResponse response = connection.sendRequest();
        return response;
    }

    protected NCMBResponse sendRequest(RequestParams params) throws NCMBException {
        return this.sendRequest(params.url, params.type, params.content, params.query);
    }

    /**
     * Send request in asynchronously
     *
     * @param url         URL
     * @param type        http method
     * @param content     contnt body
     * @param queryString query string
     * @param callback    callback on finished
     * @throws NCMBException
     */
    protected void sendRequestAsync(String url, String type, String content, JSONObject queryString,
                                    RequestApiCallback callback)
            throws NCMBException {
        
        if (mContext.sessionToken == null) {
            mContext.sessionToken = NCMBUser.getSessionToken();
        }
        String sessionToken = mContext.sessionToken;
        String applicationKey = mContext.applicationKey;
        String clientKey = mContext.clientKey;

        NCMBRequest request = new NCMBRequest(url, type, content, queryString,
                sessionToken, applicationKey, clientKey);

        NCMBConnection connection = new NCMBConnection(request);
        connection.sendRequestAsynchronously(callback);
    }

    /**
     * Send request in asynchronously with parameter bag
     *
     * @param params   params for NCMBRequest
     * @param callback callback on finished
     * @throws NCMBException
     */
    protected void sendRequestAsync(RequestParams params, RequestApiCallback callback)
            throws NCMBException {
        sendRequestAsync(params.url, params.type, params.content, params.query, callback);
    }
}
