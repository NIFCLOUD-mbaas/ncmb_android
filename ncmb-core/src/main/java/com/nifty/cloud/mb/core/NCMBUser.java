package com.nifty.cloud.mb.core;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * NCMBUser is used to sign up and login/logout the user
 */
public class NCMBUser extends NCMBObject {

    /**
     * current user
     */
    static NCMBUser currentUser;

    /**
     * currenUser fileName
     */
    static final String USER_FILENAME = "currentUser";

    static final List<String> ignoreKeys = Arrays.asList(
            "objectId", "userName", "password",
            "mailAddress", "mailAddressConfirm",
            "acl", "authData",
            "createDate", "updateDate"
    );

    /**
     * Create query for file class
     *
     * @return NCMBQuery for file class
     */
    public static NCMBQuery<NCMBUser> getQuery() {
        return new NCMBQuery<>("user");
    }

    /**
     * Constructor
     */
    public NCMBUser() {
        super("user");
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Constructor
     *
     * @param params input parameters
     */

    NCMBUser(JSONObject params) throws NCMBException {
        super("user", params);
        mIgnoreKeys = ignoreKeys;

        try {
            if (params.has("sessionToken")) {
                NCMB.getCurrentContext().sessionToken = params.getString("sessionToken");
            }
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid user information");
        }
    }

    /**
     * Get user name
     *
     * @return String user name
     */
    public String getUserName() {
        try {
            if (mFields.isNull("userName")) {
                return null;
            }
            return mFields.getString("userName");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * set user name
     *
     * @param userName user name string
     */
    public void setUserName(String userName) {
        try {
            mFields.put("userName", userName);
            mUpdateKeys.add("userName");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    private String getPassword() {
        try {
            if (mFields.isNull("password")) {
                return null;
            }

            return mFields.getString("password");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * set password
     *
     * @param password password string
     */
    public void setPassword(String password) {
        try {
            mFields.put("password", password);
            mUpdateKeys.add("password");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get mail address confirmed flag
     *
     * @return Boolean mail address is confirmed or not
     */
    public Boolean isMailAddressConfirmed() {
        try {
            if (mFields.isNull("mailAddressConfirm")) {
                return null;
            }

            return mFields.getBoolean("mailAddressConfirm");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get mail address
     *
     * @return String mail address
     */
    public String getMailAddress() {
        try {
            if (mFields.isNull("mailAddress")) {
                return null;
            }

            return mFields.getString("mailAddress");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set mail address
     *
     * @param mailAddress String mail address
     */
    public void setMailAddress(String mailAddress) {
        try {
            mFields.put("mailAddress", mailAddress);
            mUpdateKeys.add("mailAddress");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Decision whether logged in
     *
     * @return Return true if logged in
     */
    public boolean isAuthenticated() {
        return ((NCMBUser.getSessionToken() != null) && (getCurrentUser() != null) && (getObjectId().equals(getCurrentUser().getObjectId())));
    }

    /**
     * Check for specified provider's authentication data is linked
     * @param provider facebook or twitter or google
     *
     * @return Return true if authentication data is linked
     */
    public boolean isLinkedWith(String provider) {

        try {
            if (mFields.has("authData") && mFields.getJSONObject("authData").has(provider)) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get authData
     *
     * @return JSONObject or null
     */
    public JSONObject getAuthData() {
        try {
            if (mFields.isNull("authData")) {
                return null;
            }
            return mFields.getJSONObject("authData");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get Specified Authentication Data
     *
     * @param provider String "facebook" or "twitter" or "google" or "anonymous"
     * @return Specified Authentication Data or null
     */
    public JSONObject getAuthData(String provider) {
        try {
            if (mFields.isNull("authData") || mFields.getJSONObject("authData").isNull(provider)) {
                return null;
            }
            return mFields.getJSONObject("authData").getJSONObject(provider);
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    // action methods

    /**
     * sign up to NIFTY Cloud mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void signUp() throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = service.registerByName(getUserName(), getPassword());
        mFields = user.mFields;
        //copyFrom(user.mFields);
    }

    /**
     * sign up to NIFTY Cloud mobile backend
     *
     * @param callback callback for after sign up
     */
    public void signUpInBackground(final DoneCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.registerByNameInBackground(getUserName(), getPassword(), new LoginCallback() {
                @Override
                public void done(NCMBUser user, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        mFields = user.mFields;
                        //copyFrom(user.mFields);
                        if (callback != null) {
                            callback.done(null);
                        }
                    }

                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * Mail request of user authentication
     *
     * @param mailAddress e-mail address for user authentication
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void requestAuthenticationMail(String mailAddress) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        service.inviteByMail(mailAddress);
    }

    /**
     * Mail request of user authentication in background
     *
     * @param mailAddress e-mail address for user authentication
     * @param callback    Callback is executed after mail signUp request
     */
    public static void requestAuthenticationMailInBackground(String mailAddress, DoneCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.inviteByMailInBackground(mailAddress, callback);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * Request Email for the password reset
     *
     * @param mailAddress mail address
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void requestPasswordReset(String mailAddress) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        service.requestPasswordReset(mailAddress);
    }

    /**
     * Request Email for the password reset in background thread
     *
     * @param mailAddress mail address
     * @param callback    callback when process finished
     */
    public static void requestPasswordResetInBackground(String mailAddress, DoneCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.requestPasswordResetInBackground(mailAddress, callback);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }


    /**
     * Login with mailAddress and password
     *
     * @param mailAddress mailAddress
     * @param password    password
     * @return NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static NCMBUser loginWithMailAddress(String mailAddress, String password) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        return service.loginByMail(mailAddress, password);
    }

    /**
     * Login with mailAddress and password in background
     *
     * @param mailAddress mailAddress
     * @param password    password
     * @param callback    Callback is executed after login
     */
    public static void loginWithMailAddressInBackground(String mailAddress, String password,
                                                        LoginCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.loginByMailInBackground(mailAddress, password, callback);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(null, e);
            }
        }
    }

    /**
     * login with username and password
     *
     * @param userName user name
     * @param password password
     * @return NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static NCMBUser login(String userName, String password) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        return service.loginByName(userName, password);
    }

    /**
     * Login with username and password in background
     *
     * @param userName user name
     * @param password password
     * @param callback callback when finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void loginInBackground(String userName, String password,
                                         LoginCallback callback) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        service.loginByNameInBackground(userName, password, callback);

    }

    /**
     * logout from NIFTY Cloud mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void logout() throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        service.logout();
    }

    /**
     * logout from NIFTY Cloud mobile backend
     *
     * @param callback Callback is executed after logout
     */
    public static void logoutInBackground(DoneCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.logoutInBackground(callback);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * login with parameter that can be obtained after the OAuth authentication
     *
     * @param authData NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters
     * @return Authenticated user
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static NCMBUser loginWith(Object authData) throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);

        try {
            return service.registerByOauth(createAuthData(authData));
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
    }

    private static JSONObject createAuthData(Object params) throws JSONException {
        JSONObject authDataJSON = null;
        if (params.getClass().equals(NCMBFacebookParameters.class)) {
            authDataJSON = createFacebookAuthData((NCMBFacebookParameters) params);
            authDataJSON.put("type", "facebook");
        } else if (params.getClass().equals(NCMBTwitterParameters.class)) {
            authDataJSON = createTwitterAuthData((NCMBTwitterParameters) params);
            authDataJSON.put("type", "twitter");
        } else if (params.getClass().equals(NCMBGoogleParameters.class)) {
            authDataJSON = createGoogleAuthData((NCMBGoogleParameters) params);
            authDataJSON.put("type", "google");
        } else {
            throw new IllegalArgumentException("Parameters must be NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters");
        }
        return authDataJSON;
    }

    private static JSONObject createFacebookAuthData(NCMBFacebookParameters params) throws JSONException {
        JSONObject authDataJson = new JSONObject();
        authDataJson.put("id", params.userId);
        authDataJson.put("access_token", params.accessToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        authDataJson.put("expiration_date", df.format(params.expirationDate));

        return authDataJson;
    }

    private static JSONObject createTwitterAuthData(NCMBTwitterParameters params) throws JSONException {
        JSONObject authDataJson = new JSONObject();
        authDataJson.put("id", params.userId);
        authDataJson.put("screen_name", params.screenName);
        authDataJson.put("oauth_consumer_key", params.consumerKey);
        authDataJson.put("consumer_secret", params.consumerSecret);
        authDataJson.put("oauth_token", params.accessToken);
        authDataJson.put("oauth_token_secret", params.accessTokenSecret);

        return authDataJson;
    }

    private static JSONObject createGoogleAuthData(NCMBGoogleParameters params) throws JSONException {
        JSONObject authDataJson = new JSONObject();
        authDataJson.put("id", params.userId);
        authDataJson.put("access_token", params.accessToken);

        return authDataJson;
    }

    private static JSONObject createAnonymousAuthData(NCMBAnonymousParameters params) throws JSONException {
        JSONObject authDataJson = new JSONObject();
        authDataJson.put("id", params.userId);
        return authDataJson;
    }


    /**
     * login asynchronously with parameter that can be obtained after the OAuth authentication
     *
     * @param authData NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters
     * @param callback if login is succeeded, callback include authenticated user.
     */
    public static void loginInBackgroundWith(Object authData, LoginCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.registerByOauthInBackground(createAuthData(authData), callback);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(null, e);
            }
        } catch (JSONException e) {
            if (callback != null) {
                callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage()));
            }
        }
    }

    /**
     * link specified authentication data for current user
     *
     * @param params NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void linkWith(Object params) throws NCMBException {

        JSONObject currentAuthData = null;
        JSONObject linkedData;
        try {
            NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            linkedData = service.registerByOauthSetup(createAuthData(params));
            currentAuthData = getJSONObject("authData");
            mFields.put("authData", linkedData.getJSONObject("authData"));
            mUpdateKeys.add("authData");
            save();
            copyLinkedAuthData(currentAuthData, linkedData.getJSONObject("authData"));
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
        } catch (NCMBException e) {
            try {
                mFields.put("authData", currentAuthData);
            } catch (JSONException e1) {
                throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
            }
            throw e;
        }
    }

    private void copyLinkedAuthData(JSONObject currentAuthData, JSONObject linkedData) throws JSONException {
        if (currentAuthData != null) {
            Iterator<String> keys = linkedData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                currentAuthData.put(key, linkedData.getJSONObject(key));
            }
            mFields.put("authData", currentAuthData);
        }
    }

    /**
     * link specified authentication data asynchronously for current user
     *
     * @param params   NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters
     * @param callback Callback is executed after link or throw Exception
     */
    public void linkInBackgroundWith(Object params, final DoneCallback callback) {
        try {
            NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            final JSONObject linkedData = service.registerByOauthSetup(createAuthData(params));
            final JSONObject currentAuthData = getJSONObject("authData");
            mFields.put("authData", linkedData.getJSONObject("authData"));
            mUpdateKeys.add("authData");
            saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    if (e != null) {
                        try {
                            mFields.put("authData", currentAuthData);
                        } catch (JSONException e1) {
                            throw new IllegalArgumentException(e1.getMessage());
                        }
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        try {
                            copyLinkedAuthData(currentAuthData, linkedData.getJSONObject("authData"));
                        } catch (JSONException e1) {
                            throw new IllegalArgumentException(e1.getMessage());
                        }
                        if (callback != null) {
                            callback.done(null);
                        }
                    }

                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.done(new NCMBException(NCMBException.INVALID_JSON, e.getMessage()));
            }
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    public void unlink(@NonNull String provider) throws NCMBException {
        JSONObject currentAuthData;
        if (provider != null && (provider.equals("facebook") || provider.equals("twitter") || provider.equals("google"))) {
            currentAuthData = getJSONObject("authData");
            JSONObject unlinkData = new JSONObject();
            try {
                unlinkData.put(provider, JSONObject.NULL);
                mFields.put("authData", unlinkData);
                mUpdateKeys.add("authData");
                save();

                mFields.put("authData", currentAuthData.remove(provider));
            } catch (JSONException e) {
                try {
                    mFields.put("authData", currentAuthData);
                } catch (JSONException e1) {
                    throw new NCMBException(NCMBException.INVALID_JSON, e1.getMessage());
                }
                throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("provider must be facebook or twitter or google");
        }
    }

    public void unlinkInBackground(@NonNull final String provider, final DoneCallback callback) {

        final JSONObject currentAuthData;

        if (provider != null && (provider.equals("facebook") || provider.equals("twitter") || provider.equals("google"))) {
            currentAuthData = getJSONObject("authData");
            JSONObject unlinkData = new JSONObject();
            try {
                unlinkData.put(provider, JSONObject.NULL);
                mFields.put("authData", unlinkData);
                mUpdateKeys.add("authData");

                saveInBackground(new DoneCallback() {
                    @Override
                    public void done(NCMBException e) {
                        if (e != null) {
                            try {
                                mFields.put("authData", currentAuthData);
                            } catch (JSONException jsonError) {
                                throw new IllegalArgumentException(jsonError.getMessage());
                            }
                            if (callback != null) {
                                callback.done(e);
                            }
                        } else {
                            try {
                                JSONObject newAuthData = currentAuthData;
                                mFields.put("authData", newAuthData.remove(provider));
                            } catch (JSONException jsonError) {
                                throw new IllegalArgumentException(jsonError.getMessage());
                            }
                            if (callback != null) {
                                callback.done(null);
                            }
                        }
                    }
                });

            } catch (JSONException e) {
                if (callback != null) {
                    callback.done(new NCMBException(NCMBException.INVALID_JSON, e.getMessage()));
                }
            }
        } else {
            throw new IllegalArgumentException("provider must be facebook or twitter or google");

        }

    }

    @Override
    public void save() throws NCMBException {
        if (getObjectId() == null) {
            signUp();
        } else {
            NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            try {
                JSONObject response = service.updateUser(getObjectId(), createUpdateJsonData());
                if (!response.isNull("updateDate")) {
                    mFields.put("updateDate", response.getString("updateDate"));
                }
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
            }
        }

    }

    @Override
    public void saveInBackground(final DoneCallback callback) {
        if (getObjectId() == null) {
            signUpInBackground(callback);
        } else {

            NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            try {
                service.updateUserInBackground(getObjectId(), createUpdateJsonData(), new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        if (json != null && !json.isNull("updateDate")) {
                            try {
                                mFields.put("updateDate", json.getString("updateDate"));

                            } catch (JSONException e1) {
                                throw new IllegalArgumentException(e1.getMessage());
                            }
                            if (callback != null) {
                                callback.done(e);
                            }
                        } else {
                            if (callback != null) {
                                callback.done(e);
                            }
                        }
                    }
                });
            } catch (NCMBException e) {
                if (callback != null) {
                    callback.done(e);
                }
            } catch (JSONException e) {
                if (callback != null) {
                    callback.done(new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage()));
                }
            }
        }
    }

    @Override
    public void fetch() throws NCMBException {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = service.fetchUser(getObjectId());
        mFields = user.mFields;
    }

    @Override
    public void fetchInBackground(final FetchCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.fetchUserInBackground(getObjectId(), new FetchCallback<NCMBUser>() {
                @Override
                public void done(NCMBUser user, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(null, e);
                        }
                    } else {
                        mFields = user.mFields;
                        if (callback != null) {
                            callback.done(user, null);
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(null, e);
            }
        }
    }

    @Override
    public void deleteObject() throws NCMBException {
        try {
            NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            service.deleteUser(getObjectId());
            mFields = new JSONObject();
            mUpdateKeys.clear();
        } catch (NCMBException e) {
            throw e;
        }


    }

    @Override
    public void deleteObjectInBackground(final DoneCallback callback) {
        NCMBUserService service = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.deleteUserInBackground(getObjectId(), new ExecuteServiceCallback() {
                @Override
                public void done(JSONObject json, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        mFields = new JSONObject();
                        mUpdateKeys.clear();
                        if (callback != null) {
                            callback.done(e);
                        }
                    }


                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * Get current user object
     *
     * @return user
     */
    public static NCMBUser getCurrentUser() {
        try {
            //null check
            NCMBLocalFile.checkNCMBContext();

            //create currentUser
            if (currentUser == null) {
                //ローカルファイルにログイン情報があれば取得、なければ新規作成
                File currentUserFile = NCMBLocalFile.create(USER_FILENAME);
                if (currentUserFile.exists()) {
                    //ローカルファイルからログイン情報を取得
                    JSONObject localData = NCMBLocalFile.readFile(currentUserFile);
                    currentUser = new NCMBUser(localData);
                } else {
                    currentUser = new NCMBUser();
                }
            }
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
        return currentUser;
    }

    /**
     * Get sessionToken
     *
     * @return sessionToken
     */
    public static String getSessionToken() {
        if (getCurrentUser().getString("sessionToken") != null) {
            return NCMBUser.getCurrentUser().getString("sessionToken");
        } else {
            return null;
        }
    }


    JSONObject getLocalData() throws NCMBException {
        return mFields;
    }
}
