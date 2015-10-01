package com.nifty.cloud.mb.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *  NCMBUser is used to sign up and login/logout the user
 */
public class NCMBUser extends NCMBObject{

    /** current user */
    static NCMBUser currentUser;

    /** currenUser fileName */
    static final String USER_FILENAME = "currentUser";

    static final List<String> ignoreKeys = Arrays.asList(
            "objectId", "userName","password",
            "mailAddress", "mailAddressConfirm",
            "acl", "authData",
            "createDate", "updateDate"
    );

    /**
     * Constructor
     */
    public NCMBUser(){
        super("user");
        mIgnoreKeys = ignoreKeys;
    }
    /**
     * Constructor
     * @param params input parameters
     */

    NCMBUser(JSONObject params) throws NCMBException {
        super("user", params);
        mIgnoreKeys = ignoreKeys;

        try {
            if (params.has("sessionToken")) {
                NCMB.sCurrentContext.sessionToken = params.getString("sessionToken");
            }
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid user information");
        }
    }

    /**
     * Get user name
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
     * Get authData
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

    // action methods

    /**
     * sign up to NIFTY Cloud mobile backend
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void signUp () throws NCMBException {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = service.registerByName(getUserName(), getPassword());
        mFields = user.mFields;
        //copyFrom(user.mFields);
    }

    /**
     * sign up to NIFTY Cloud mobile backend
     * @param callback callback for after sign up
     */
    public void signUpInBackground (final DoneCallback callback){
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
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
     * login with username and password
     * @param userName user name
     * @param password password
     * @return NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static NCMBUser login(String userName, String password) throws NCMBException {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        return service.loginByName(userName, password);
    }

    /**
     * Login with username and password in background
     * @param userName user name
     * @param password password
     * @param callback callback when finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void loginInBackground(String userName, String password,
                                         LoginCallback callback) throws NCMBException {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        service.loginByNameInBackground(userName, password, callback);

    }

    /**
     * logout from NIFTY Cloud mobile backend
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public static void logout () throws NCMBException {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        service.logout();
    }

    /**
     * logout from NIFTY Cloud mobile backend
     * @param callback Callback is executed after logout
     */
    public static void logoutInBackground (DoneCallback callback) {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.logoutInBackground(callback);
        } catch (NCMBException e) {
            if (callback != null) {

            }
        }
    }

    @Override
    public void save() throws NCMBException {
        if (getObjectId() == null){
            signUp();
        } else {
            NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
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
            NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
            try {
                service.updateUserInBackground(getObjectId(), createUpdateJsonData(), new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        if (!json.isNull("updateDate")) {
                            try {
                                mFields.put("updateDate", json.getString("updateDate"));
                            } catch (JSONException e1) {
                                if (callback != null) {
                                    callback.done(new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage()));
                                }
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
    public void fetchObject() throws NCMBException {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = service.getUser(getObjectId());
        mFields = user.mFields;
    }

    @Override
    public void fetchObjectInBackground (final DoneCallback callback) {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
        try {
            service.getUserInBackground(getObjectId(), new UserCallback() {
                @Override
                public void done(NCMBUser user, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        mFields = user.mFields;
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

    @Override
    public void deleteObject () throws NCMBException {
        try {
            NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
            service.deleteUser(getObjectId());
            mFields = new JSONObject();
            mUpdateKeys.clear();
        } catch (NCMBException e) {
            throw e;
        }


    }

    @Override
    public void deleteObjectInBackground (final DoneCallback callback) {
        NCMBUserService service = (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
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
     * @return sessionToken
     */
    public static String getSessionToken(){
        if(getCurrentUser().getString("sessionToken") != null){
            return NCMBUser.getCurrentUser().getString("sessionToken");
        } else {
            return null;
        }
    }


    JSONObject getLocalData() throws NCMBException {
        return mFields;
    }
}
