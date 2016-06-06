package com.nifty.cloud.mb.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Service for user api
 */
public class NCMBUserService extends NCMBService {
    /**
     * service path for API category
     */
    public static final String SERVICE_PATH = "users";

    /**
     * Status code of register success
     */
    public static final int HTTP_STATUS_REGISTERED = 201;

    /**
     * Status code of authorize success
     */
    public static final int HTTP_STATUS_AUTHORIZED = 200;

    /**
     * Status code of invite api accespted
     */
    public static final int HTTP_STATUS_REQUEST_ACCEPTED = 201;

    /**
     * Inner class for callback
     */
    abstract class UserServiceCallback extends ServiceCallback {
        /**
         * constructors
         */
        UserServiceCallback(NCMBUserService service, ExecuteServiceCallback callback) {
            super((NCMBService) service, callback);
        }

        UserServiceCallback(NCMBUserService service, DoneCallback callback) {
            super((NCMBService) service, callback);
        }

        UserServiceCallback(NCMBUserService service, FetchCallback callback) {
            super((NCMBService) service, callback);
        }

        UserServiceCallback(NCMBUserService service, LoginCallback callback) {
            super((NCMBService) service, callback);
        }

        UserServiceCallback(NCMBUserService service, LoginCallback callback, JSONObject options) {
            super((NCMBService) service, callback, options);
        }

        UserServiceCallback(NCMBUserService service, SearchUserCallback callback) {
            super((NCMBService) service, callback);
        }

        public NCMBUserService getUserService() {
            return (NCMBUserService) mService;
        }
    }

    /**
     * constructor
     *
     * @param context NCMBContext object for current context
     */
    NCMBUserService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * Register new user by name
     *
     * @param userName user name
     * @param password password
     * @return new NCMBUser object that registered
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBUser registerByName(String userName, String password) throws NCMBException {
        try {
            JSONObject params = new JSONObject();
            params.put("userName", userName);
            params.put("password", password);
            return registerUser(params, false);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "userName/password required");
        }
    }

    /**
     * Register new user by name in background
     *
     * @param userName user name
     * @param password password
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void registerByNameInBackground(String userName, String password, LoginCallback callback)
            throws NCMBException {
        try {
            JSONObject params = new JSONObject();
            params.put("userName", userName);
            params.put("password", password);
            registerUserInBackground(params, false, callback);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "userName/password required");
        }
    }

    /**
     * Setup OAuth parameters to register new user with OAuth
     *
     * @param oauthOptions OAuth options
     * @return "authData" params in JSONObject
     * @throws NCMBException
     */
    protected JSONObject registerByOauthSetup(JSONObject oauthOptions) throws NCMBException {
        try {
            String authType = oauthOptions.getString("type");
            JSONObject authData = new JSONObject();
            switch (authType) {
                case NCMB.OAUTH_FACEBOOK:
                    // adjust expiration_date format
                    Object edVal = oauthOptions.get("expiration_date");
                    if (edVal instanceof String) {
                        JSONObject edObj = new JSONObject();
                        edObj.put("__type", "Date");
                        edObj.put("iso", edVal);
                        oauthOptions.put("expiration_date", edObj);
                    }

                    String[] fbKeys = {
                            "id",
                            "access_token",
                            "expiration_date"
                    };
                    authData.put("facebook", fillParameters(fbKeys, oauthOptions));
                    break;
                case NCMB.OAUTH_TWITTER:
                    String[] twKeys = {
                            "id",
                            "screen_name",
                            "oauth_consumer_key",
                            "consumer_secret",
                            "oauth_token",
                            "oauth_token_secret"
                    };
                    authData.put("twitter", fillParameters(twKeys, oauthOptions));
                    break;
                case NCMB.OAUTH_GOOGLE:
                    String[] gglKeys = {
                            "id",
                            "access_token"
                    };
                    authData.put("google", fillParameters(gglKeys, oauthOptions));
                    break;
                case NCMB.OAUTH_ANONYMOUS:
                    String[] anKeys = {
                            "id"
                    };
                    authData.put("anonymous", fillParameters(anKeys, oauthOptions));
                    break;
                default:
                    // Unsupported type
                    throw new NCMBException(NCMBException.OAUTH_FAILURE, "Unknown OAuth type");
            }
            JSONObject params = new JSONObject();
            params.put("authData", authData);
            return params;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, e.getMessage());
        }
    }

    /**
     * Register new user by OAuth services
     *
     * @param oauthOptions OAuth options
     * @return new NCMBUser object that registered
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBUser registerByOauth(JSONObject oauthOptions) throws NCMBException {
        JSONObject params = registerByOauthSetup(oauthOptions);
        return registerUser(params, true);
    }

    /**
     * Register new user by OAuth services in background
     *
     * @param oauthOptions OAuth options
     * @param callback     callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void registerByOauthInBackground(JSONObject oauthOptions, LoginCallback callback) throws NCMBException {
        JSONObject params = registerByOauthSetup(oauthOptions);
        registerUserInBackground(params, true, callback);
    }

    /**
     * Create JSONObject and copy values with given keys
     *
     * @param keys String[] keys to copy
     * @param src  JSONObject source of copy
     * @return JSONObject
     */
    protected JSONObject fillParameters(String[] keys, JSONObject src) throws NCMBException {
        JSONObject result = new JSONObject();
        for (String key : keys) {
            try {
                result.put(key, src.get(key));
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.MISSING_VALUE, "Missing value: " + key);
            }
        }
        return result;
    }

    /**
     * Setup params to invite new user by Email
     *
     * @param mailAddress mail address
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams inviteByMailParams(String mailAddress) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        reqParams.url = mContext.baseUrl + "requestMailAddressUserEntry";
        reqParams.type = NCMBRequest.HTTP_METHOD_POST;

        JSONObject params = new JSONObject();
        try {
            params.put("mailAddress", mailAddress);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "mailAddress required");
        }
        reqParams.content = params.toString();
        return reqParams;
    }

    /**
     * Check response to invite new user by Email
     *
     * @param response
     * @throws NCMBException
     */
    protected void inviteByMailCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_REQUEST_ACCEPTED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid response from API");
        }
    }

    /**
     * Invite new user by Email
     * ( /requestMailAddressUserEntry API )
     *
     * @param mailAddress mail address
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void inviteByMail(String mailAddress) throws NCMBException {
        RequestParams reqParams = inviteByMailParams(mailAddress);

        NCMBResponse response = sendRequest(reqParams);
        inviteByMailCheckResponse(response);
        // return nothing
    }

    /**
     * Invite new user by Email in background
     *
     * @param mailAddress mail address
     * @param callback    callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void inviteByMailInBackground(String mailAddress, DoneCallback callback) throws NCMBException {
        RequestParams reqParams = inviteByMailParams(mailAddress);

        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                DoneCallback callback = (DoneCallback) mCallback;
                callback.done(null);
            }

            @Override
            public void handleError(NCMBException e) {
                DoneCallback callback = (DoneCallback) mCallback;
                callback.done(e);
            }
        });
    }

    protected RequestParams requestPasswordResetParams(String mailAddress) throws Exception {
        if (mailAddress == null) {
            //throw new NCMBException(NCMBException.MISSING_VALUE, "mail address required.");
            throw new IllegalArgumentException("mail address required.");
        } else {
            RequestParams reqParams = new RequestParams();

            reqParams.url = mContext.baseUrl + "requestPasswordReset";
            reqParams.type = NCMBRequest.HTTP_METHOD_POST;

            JSONObject params = new JSONObject();
            try {
                params.put("mailAddress", mailAddress);
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "invalid JSON data.");
            }
            reqParams.content = params.toString();
            return reqParams;
        }
    }

    /**
     * Send Email for the password reset in background thread
     *
     * @param mailAddress mail address
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void requestPasswordReset(String mailAddress) throws NCMBException {
        RequestParams reqParams = null;
        try {
            reqParams = requestPasswordResetParams(mailAddress);
        } catch (Exception e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, e.getMessage());
        }
        sendRequest(reqParams);
    }

    /**
     * Send Email for the password reset in background thread
     *
     * @param mailAddress mail address
     * @param callback    callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void requestPasswordResetInBackground(String mailAddress, DoneCallback callback) throws NCMBException {
        RequestParams reqParams = null;
        try {
            reqParams = requestPasswordResetParams(mailAddress);
        } catch (Exception e) {
            if (callback != null) {
                callback.done(new NCMBException(NCMBException.MISSING_VALUE, e.getMessage()));
            }
            return;
        }

        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                DoneCallback callback = (DoneCallback) mCallback;
                if (callback != null) {
                    callback.done(null);
                }
            }

            @Override
            public void handleError(NCMBException e) {
                DoneCallback callback = (DoneCallback) mCallback;
                if (callback != null) {
                    callback.done(e);
                }
            }
        });
    }

    /**
     * Setup params to register new user
     *
     * @param params user parameters
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams registerUserParams(JSONObject params) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        reqParams.url = mContext.baseUrl + mServicePath;
        reqParams.type = NCMBRequest.HTTP_METHOD_POST;
        reqParams.content = params.toString();

        return reqParams;
    }

    /**
     * Check response to register new user
     *
     * @param response
     * @param oauth    use oauth or not
     * @throws NCMBException
     */
    protected void registerUserCheckResponse(NCMBResponse response, boolean oauth) throws NCMBException {
        switch (response.statusCode) {
            case HTTP_STATUS_REGISTERED:
                // success, go to next step
                break;
            case HTTP_STATUS_AUTHORIZED:
                if (!oauth) {
                    throw new NCMBException(NCMBException.AUTH_FAILURE, "User registration failed");
                }
                break;
            default:
                // otherwise, throw exception
                if (oauth) {
                    throw new NCMBException(NCMBException.OAUTH_FAILURE, "Oauth failed");
                } else {
                    throw new NCMBException(NCMBException.AUTH_FAILURE, "User registration failed");
                }
        }
    }

    /**
     * Internal method to register user
     *
     * @param params parameters
     * @param oauth  use oauth or not
     * @return new NCMBUser object that logged-in
     * @throws NCMBException
     */
    protected NCMBUser registerUser(JSONObject params, boolean oauth) throws NCMBException {
        RequestParams reqParams = registerUserParams(params);

        NCMBResponse response = sendRequest(reqParams);
        registerUserCheckResponse(response, oauth);

        return postLoginProcess(response);
    }

    /**
     * Internal method to register user in background
     *
     * @param params   parameters
     * @param oauth    use oauth or not
     * @param callback callback when process finished
     * @throws NCMBException
     */
    protected void registerUserInBackground(JSONObject params, boolean oauth, final LoginCallback callback)
            throws NCMBException {
        try {
            RequestParams reqParams = registerUserParams(params);

            JSONObject options = new JSONObject();
            options.put("oauth", oauth);

            sendRequestAsync(reqParams, new UserServiceCallback(this, callback, options) {
                @Override
                public void handleResponse(NCMBResponse response) {
                    /*
                    try {
                        boolean oauth = mOptions.getBoolean("oauth");
                        getUserService().registerUserCheckResponse(response, oauth);
                    } catch (JSONException e) {
                        throw new NCMBException(NCMBException.INVALID_JSON, "Bad oauth option");
                    }
                    */

                    NCMBUser user = null;
                    try {
                        user = getUserService().postLoginProcess(response);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }
                    LoginCallback callback = (LoginCallback) mCallback;
                    callback.done(user, null);
                }

                @Override
                public void handleError(NCMBException e) {
                    LoginCallback callback = (LoginCallback) mCallback;
                    callback.done(null, e);
                }
            });
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Bad oauth option");
        }
    }

    /**
     * Setup params to get user entity
     *
     * @param userId user id
     * @return parameters for NCMBRequest
     * @throws NCMBException
     */
    protected RequestParams getUserParams(String userId) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        reqParams.url = mContext.baseUrl + mServicePath + "/" + userId;
        reqParams.type = NCMBRequest.HTTP_METHOD_GET;
        return reqParams;
    }

    /**
     * Check response to get user entity
     *
     * @param response
     * @throws NCMBException
     */
    protected void getUserCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.DATA_NOT_FOUND, "Getting user info failure");
        }
    }

    /**
     * Get user entity from given id
     *
     * @param userId user id
     * @return NCMBUser instance
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBUser fetchUser(String userId) throws NCMBException {
        RequestParams reqParams = getUserParams(userId);
        NCMBResponse response = sendRequest(reqParams);
        getUserCheckResponse(response);

        return new NCMBUser(response.responseData);
    }

    /**
     * Get user entity from given id in background
     *
     * @param userId   user id
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void fetchUserInBackground(String userId, final FetchCallback callback) throws NCMBException {
        RequestParams reqParams = getUserParams(userId);
        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                NCMBUser user = null;
                try {
                    user = new NCMBUser(response.responseData);
                } catch (NCMBException e) {
                    callback.done(null, e);
                }
                FetchCallback<NCMBUser> callback = (FetchCallback) mCallback;
                callback.done(user, null);
            }

            @Override
            public void handleError(NCMBException e) {
                callback.done(null, e);
            }
        });
    }

    /**
     * Set up to update user information
     *
     * @param userId user id
     * @param params update values
     * @return parameters for NCMBRequest
     */
    protected RequestParams updateUserParams(String userId, JSONObject params) {
        RequestParams reqParams = new RequestParams();

        reqParams.url = mContext.baseUrl + mServicePath + "/" + userId;
        reqParams.type = NCMBRequest.HTTP_METHOD_PUT;
        reqParams.content = params.toString();
        return reqParams;
    }

    /**
     * Check response to update user information
     *
     * @param response
     * @throws NCMBException
     */
    protected void updateUserCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Update user info failed");
        }
    }

    /**
     * Update user information
     *
     * @param userId user id
     * @param params update values
     * @return result of update user
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public JSONObject updateUser(String userId, JSONObject params) throws NCMBException {
        RequestParams reqParams = updateUserParams(userId, params);
        NCMBResponse response = sendRequest(reqParams);
        //update currentUser
        try {
            params.put("objectId", userId);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
        updateUserCheckResponse(response);
        writeCurrentUser(params, response.responseData);
        return response.responseData;
    }

    /**
     * Update user information in background
     *
     * @param userId   user id
     * @param params   update values
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void updateUserInBackground(final String userId, final JSONObject params, final ExecuteServiceCallback callback) throws NCMBException {
        RequestParams reqParams = updateUserParams(userId, params);
        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {

                //update currentUser
                try {
                    params.put("objectId", userId);
                    writeCurrentUser(params, response.responseData);
                } catch (NCMBException e) {
                    callback.done(null, e);
                } catch (JSONException e) {
                    callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage()));
                }

                ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                callback.done(response.responseData, null);
            }

            @Override
            public void handleError(NCMBException e) {
                ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                callback.done(null, e);
            }
        });
    }

    /**
     * Setup params to login by user name
     *
     * @param userName user name
     * @param password password
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams loginByNameParams(String userName, String password) throws NCMBException {
        try {
            RequestParams params = new RequestParams();
            params.url = mContext.baseUrl + "login";
            params.type = NCMBRequest.HTTP_METHOD_GET;

            JSONObject query = new JSONObject();
            query.put("userName", userName);
            query.put("password", password);
            params.query = query;
            return params;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "userName/password required");
        }
    }

    /**
     * Check response to login by user name
     *
     * @param response
     * @throws NCMBException
     */
    protected void loginByNameCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.AUTH_FAILURE, "login failed");
        }
    }

    /**
     * Login by user name
     *
     * @param userName user name
     * @param password password
     * @return new NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBUser loginByName(String userName, String password) throws NCMBException {
        RequestParams requestParams = loginByNameParams(userName, password);
        NCMBResponse response = sendRequest(requestParams);
        loginByNameCheckResponse(response);
        return postLoginProcess(response);
    }

    /**
     * Login by user name in background
     *
     * @param userName user name
     * @param password password
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void loginByNameInBackground(String userName, String password,
                                        LoginCallback callback) throws NCMBException {
        RequestParams reqParams = loginByNameParams(userName, password);
        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                NCMBException error = null;
                NCMBUser user = null;
                try {
                    getUserService().loginByNameCheckResponse(response);
                    user = getUserService().postLoginProcess(response);
                } catch (NCMBException e) {
                    error = e;
                }

                LoginCallback loginCallback = (LoginCallback) mCallback;
                loginCallback.done(user, error);
            }

            @Override
            public void handleError(NCMBException e) {
                LoginCallback loginCallback = (LoginCallback) mCallback;
                loginCallback.done(null, e);
            }
        });
    }

    /**
     * Setup params to login by mail address
     *
     * @param mailAddress mail address
     * @param password    password
     * @return parameters in object
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    protected RequestParams loginByMailParams(String mailAddress, String password) throws NCMBException {
        try {
            RequestParams params = new RequestParams();

            params.url = mContext.baseUrl + "login";
            params.type = NCMBRequest.HTTP_METHOD_GET;

            JSONObject query = new JSONObject();
            query.put("mailAddress", mailAddress);
            query.put("password", password);
            params.query = query;

            return params;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "mailAddress/password required");
        }
    }

    /**
     * Check response to login by mail address
     *
     * @param response
     * @throws NCMBException
     */
    protected void loginByMailCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.AUTH_FAILURE, "login failed");
        }
    }

    /**
     * login by mail address
     *
     * @param mailAddress mail address
     * @param password    password
     * @return new NCMBUser object that loggged-in
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBUser loginByMail(String mailAddress, String password) throws NCMBException {
        RequestParams params = loginByMailParams(mailAddress, password);
        NCMBResponse response = sendRequest(params);
        loginByMailCheckResponse(response);
        return postLoginProcess(response);
    }

    /**
     * Login by mail address in background
     *
     * @param mailAddress mail address
     * @param password    password
     * @param callback    callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void loginByMailInBackground(String mailAddress, String password,
                                        LoginCallback callback) throws NCMBException {
        RequestParams params = null;
        try {
            params = loginByMailParams(mailAddress, password);
        } catch (NCMBException e) {
            callback.done(null, e);
        }
        sendRequestAsync(params, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                NCMBUser user = null;
                NCMBException error = null;
                try {
                    user = getUserService().postLoginProcess(response);
                } catch (NCMBException e) {
                    error = e;
                }

                LoginCallback loginCallback = (LoginCallback) mCallback;
                loginCallback.done(user, error);
            }

            @Override
            public void handleError(NCMBException e) {
                LoginCallback loginCallback = (LoginCallback) mCallback;
                loginCallback.done(null, e);
            }
        });
    }

    /**
     * process after login
     *
     * @param response response object
     * @return new NCMBUser object
     * @throws NCMBException
     */
    protected NCMBUser postLoginProcess(NCMBResponse response) throws NCMBException {
        try {
            JSONObject result = response.responseData;
            String userId = result.getString("objectId");
            String newSessionToken = result.getString("sessionToken");

            // register with login, sessionToken updated
            mContext.sessionToken = newSessionToken;
            mContext.userId = userId;
            // create currentUser. empty JSONObject for POST
            writeCurrentUser(new JSONObject(), result);

            return new NCMBUser(result);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid user info");
        }
    }

    /**
     * Setup params to delete user
     *
     * @param userId user id
     * @return parameters in object
     */
    protected RequestParams deleteUserParams(String userId) {
        RequestParams params = new RequestParams();

        params.url = mContext.baseUrl + mServicePath + "/" + userId;
        params.type = NCMBRequest.HTTP_METHOD_DELETE;
        return params;
    }

    /**
     * Check responkse to delete user
     *
     * @param response
     * @throws NCMBException
     */
    protected void deleteUserCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Delete user failed");
        }
    }

    /**
     * Delete user by given id
     *
     * @param userId user id
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void deleteUser(String userId) throws NCMBException {
        RequestParams reqParams = deleteUserParams(userId);
        NCMBResponse response = sendRequest(reqParams);
        deleteUserCheckResponse(response);

        if (userId.equals(NCMBUser.currentUser.getObjectId())) {
            // unregister login informations
            clearCurrentUser();
        }
    }

    /**
     * Delete user by given id in background
     *
     * @param userId   user id
     * @param callback Callback is executed after delete user
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void deleteUserInBackground(final String userId, ExecuteServiceCallback callback) throws NCMBException {
        RequestParams reqParams = deleteUserParams(userId);
        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {
                if (userId.equals(NCMBUser.currentUser.getObjectId())) {
                    // unregister login informations
                    clearCurrentUser();
                }

                ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                callback.done(response.responseData, null);
            }

            @Override
            public void handleError(NCMBException e) {
                ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                callback.done(null, e);
            }
        });
    }

    /**
     * Setup parameters to logout
     *
     * @return request params in object
     */
    protected RequestParams logoutParams() {
        RequestParams params = new RequestParams();

        params.url = mContext.baseUrl + "logout";
        params.type = NCMBRequest.HTTP_METHOD_GET;
        return params;
    }

    /**
     * Check response to logout
     *
     * @param response
     * @throws NCMBException
     */
    protected void logoutCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Logout failed");
        }
    }

    /**
     * Logout from session
     *
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void logout() throws NCMBException {
        RequestParams reqParams = logoutParams();
        NCMBResponse response = sendRequest(reqParams);
        logoutCheckResponse(response);

        // unregister login informations
        mContext.sessionToken = null;
        mContext.userId = null;
        clearCurrentUser();
    }

    /**
     * Logout from session in background
     *
     * @param callback callback when logout completed
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void logoutInBackground(DoneCallback callback) throws NCMBException {
        RequestParams reqParams = logoutParams();

        sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) {

                // unregister login informations
                mContext.sessionToken = null;
                mContext.userId = null;
                clearCurrentUser();

                DoneCallback callback = (DoneCallback) mCallback;
                callback.done(null);
            }

            @Override
            public void handleError(NCMBException e) {
                DoneCallback callback = (DoneCallback) mCallback;
                callback.done(e);
            }
        });
    }

    /**
     * Setup parameters to search users
     *
     * @param conditions search conditions
     * @return request params in object
     */
    protected RequestParams searchUserParams(JSONObject conditions) {
        RequestParams params = new RequestParams();

        params.url = mContext.baseUrl + mServicePath;
        params.type = NCMBRequest.HTTP_METHOD_GET;

        if (conditions != null && conditions.length() > 0) {
            params.query = conditions;
        }
        return params;
    }

    /**
     * Check response to search users
     *
     * @param response
     * @throws NCMBException
     */
    protected void searchUserCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Search user failed");
        }
    }

    /**
     * Post process to search users
     *
     * @param data response body in JSONObject
     * @return found users in ArrayList
     * @throws NCMBException
     */
    protected ArrayList<NCMBUser> searchUserPostProcess(JSONObject data) throws NCMBException {
        try {
            JSONArray items = data.getJSONArray("results");

            ArrayList<NCMBUser> result = new ArrayList<NCMBUser>();
            int len = items.length();
            for (int i = 0; i < len; ++i) {
                JSONObject item = items.getJSONObject(i);
                NCMBUser user = new NCMBUser(item);
                result.add(user);
            }
            return result;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format");
        }
    }

    /**
     * Search users
     *
     * @param conditions search conditions, if no condition set to null
     * @return result of search user
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public ArrayList<NCMBUser> searchUser(JSONObject conditions) throws NCMBException {
        RequestParams reqParams = searchUserParams(conditions);
        NCMBResponse response = sendRequest(reqParams);
        searchUserCheckResponse(response);
        return searchUserPostProcess(response.responseData);
    }

    /**
     * Search users in background
     *
     * @param conditions search conditions, if no condition set to null
     * @param callback   callback when process finished
     */
    public void searchUserInBackground(JSONObject conditions, final SearchUserCallback callback) {
        try {
            RequestParams reqParams = searchUserParams(conditions);

            sendRequestAsync(reqParams, new UserServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response) {

                    ArrayList<NCMBUser> users = null;
                    try {
                        users = getUserService().searchUserPostProcess(response.responseData);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }
                    SearchUserCallback callback = (SearchUserCallback) mCallback;
                    callback.done(users, null);
                }

                @Override
                public void handleError(NCMBException e) {
                    SearchUserCallback callback = (SearchUserCallback) mCallback;
                    callback.done(null, e);
                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(null, e);
            }
        }

    }

    /**
     * Run at the time of "POST" and "PUT"
     * write the currentUser data in the file
     *
     * @param responseData user parameters
     */
    void writeCurrentUser(JSONObject params, JSONObject responseData) throws NCMBException {
        //merge responseData to the params
        mergeJSONObject(params, responseData);

        //merge params to the currentData
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        JSONObject currentData = currentUser.getLocalData();
        mergeJSONObject(currentData, params);

        //write file
        File file = NCMBLocalFile.create(NCMBUser.USER_FILENAME);
        NCMBLocalFile.writeFile(file, currentData);

        //held in a static
        NCMBUser.currentUser = new NCMBUser(currentData);
        if (currentData.has("sessionToken")) {
            try {
                NCMB.getCurrentContext().sessionToken = currentData.getString("sessionToken");
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
            }
        }
    }

    /**
     * Run at the time of "Delete" and "Logout" and "E404001 Error"
     */
    static void clearCurrentUser() {
        //delete file
        File file = NCMBLocalFile.create(NCMBUser.USER_FILENAME);
        NCMBLocalFile.deleteFile(file);
        //discarded from the static
        NCMBUser.currentUser = null;
        NCMB.getCurrentContext().sessionToken = null;
        NCMB.getCurrentContext().userId = null;
    }

    /**
     * merge the JSONObject
     *
     * @param base    base JSONObject
     * @param compare merge JSONObject
     * @throws NCMBException
     */
    static void mergeJSONObject(JSONObject base, JSONObject compare) throws NCMBException {
        try {
            Iterator keys = compare.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                base.put(key, compare.get(key));
            }
        } catch (JSONException error) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, error.getMessage());
        }
    }
}
