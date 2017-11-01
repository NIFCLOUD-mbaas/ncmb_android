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

import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Service class for installation api
 */
public class NCMBInstallationService extends NCMBService {

    /** service path for API category */
    public static final String SERVICE_PATH = "installations";
    /** Status code of installation created */
    public static final int HTTP_STATUS_INSTALLATION_CREATED = 201;
    /** Status code of installation updated */
    public static final int HTTP_STATUS_INSTALLATION_UPDATED = 200;
    /** Status code of installation deleted */
    public static final int HTTP_STATUS_INSTALLATION_DELETED = 200;
    /** Status code of installation gotten */
    public static final int HTTP_STATUS_INSTALLATION_GOTTEN = 200;

    /**
     * Inner class for callback
     */
    abstract class InstallationServiceCallback extends ServiceCallback {
        /** constructors */
        InstallationServiceCallback(NCMBInstallationService service, DoneCallback callback) {
            super(service, callback);
        }

        InstallationServiceCallback(NCMBInstallationService service, ExecuteServiceCallback callback) {
            super(service, callback);
        }

        InstallationServiceCallback(NCMBInstallationService service, FetchCallback callback) {
            super(service, callback);
        }

        InstallationServiceCallback(NCMBInstallationService service, SearchInstallationCallback callback) {
            super(service, callback);
        }
    }

    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    NCMBInstallationService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    // region API method

    /**
     * Create installation object
     *
     * @param registrationId registration id
     * @param params         installation parameters
     * @return JSONObject response of installation create
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject createInstallation(String registrationId, JSONObject params) throws NCMBException {
        //null check
        params = argumentNullCheckForPOST(registrationId, params);

        //set installation data
        try {
            //set registrationId
            params.put("deviceToken", registrationId);
            //set basic data
            setInstallationBasicData(params);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid json format.");
        } catch (PackageManager.NameNotFoundException e) {
            throw new NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.");
        }

        //connect
        RequestParams request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_INSTALLATION_CREATED) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Created failed.");
        }

        //create currentInstallation
        writeCurrentInstallation(params, response.responseData);

        return response.responseData;
    }

    /**
     * Create installation object in background
     *
     * @param registrationId registration id
     * @param params         installation parameters
     * @param callback       JSONCallback
     */
    public void createInstallationInBackground(String registrationId, JSONObject params, final ExecuteServiceCallback callback) {
        try {
            //null check
            final JSONObject argumentParams = argumentNullCheckForPOST(registrationId, params);

            //set installation data
            try {
                //set registrationId
                params.put("deviceToken", registrationId);
                //set basic data
                setInstallationBasicData(params);
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "Invalid json format.");
            } catch (PackageManager.NameNotFoundException e) {
                throw new NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.");
            }

            //connect
            RequestParams request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST);
            sendRequestAsync(request, new InstallationServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

                    //create currentInstallation
                    try {
                        writeCurrentInstallation(argumentParams, response.responseData);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }

                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(response.responseData, null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {

                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(null, error);
            }
        }
    }


    /**
     * Update installation object
     *
     * @param objectId objectId
     * @param params   installation parameters
     * @return result of update installation
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject updateInstallation(String objectId, JSONObject params) throws NCMBException {
        try {
            //null check
            params = argumentNullCheckForPUT(objectId, params);

            //set installation data
            try {
                //set basic data
                setInstallationBasicData(params);
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "Invalid json format.");
            } catch (PackageManager.NameNotFoundException e) {
                throw new NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.");
            }

            //connect
            RequestParams request = createRequestParams(objectId, params, null, NCMBRequest.HTTP_METHOD_PUT);
            NCMBResponse response = sendRequest(request);
            if (response.statusCode != HTTP_STATUS_INSTALLATION_UPDATED) {
                throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Updated failed.");
            }

            //update currentInstallation
            writeCurrentInstallation(params, response.responseData);

            return response.responseData;
        } catch (NCMBException error) {
            //currentInstallation auto delete
            checkDataNotFound(objectId, error.getCode());
            throw error;
        }
    }

    /**
     * Update installation object in background
     *
     * @param objectId objectId
     * @param params   installation parameters
     * @param callback JSONCallback
     */
    public void updateInstallationInBackground(final String objectId, JSONObject params, final ExecuteServiceCallback callback) {
        try {
            //null check
            final JSONObject argumentParams = argumentNullCheckForPOST(objectId, params);

            //set installation data
            try {
                //set basic data
                setInstallationBasicData(params);
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "Invalid json format.");
            } catch (PackageManager.NameNotFoundException e) {
                throw new NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.");
            }

            //connect
            RequestParams request = createRequestParams(objectId, params, null, NCMBRequest.HTTP_METHOD_PUT);
            sendRequestAsync(request, new InstallationServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

                    //update currentInstallation
                    try {
                        writeCurrentInstallation(argumentParams, response.responseData);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }

                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(response.responseData, null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    //currentInstallation auto delete
                    checkDataNotFound(objectId, e.getCode());

                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(null, error);
            }
        }
    }


    /**
     * Delete installation object
     *
     * @param objectId object id
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public void deleteInstallation(String objectId) throws NCMBException {
        try {
            //null check
            if (objectId == null) {
                throw new NCMBException(new IllegalArgumentException("objectId is must not be null."));
            }

            //connect
            RequestParams request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_DELETE);
            NCMBResponse response = sendRequest(request);
            if (response.statusCode != HTTP_STATUS_INSTALLATION_DELETED) {
                throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Deleted failed.");
            }

            //clear currentInstallation
            clearCurrentInstallation();

        } catch (NCMBException error) {
            //currentInstallation auto delete
            checkDataNotFound(objectId, error.getCode());
            throw error;
        }
    }

    /**
     * Delete installation object in background
     *
     * @param objectId objectId
     * @param callback DoneCallback
     */
    public void deleteInstallationInBackground(final String objectId, DoneCallback callback) {
        try {
            //null check
            if (objectId == null) {
                throw new NCMBException(new IllegalArgumentException("objectId is must not be null."));
            }

            //connect
            RequestParams request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_DELETE);
            sendRequestAsync(request, new InstallationServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

                    //clear currentInstallation
                    clearCurrentInstallation();

                    DoneCallback callback = (DoneCallback) mCallback;
                    if (callback != null) {
                        callback.done(null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    //currentInstallation auto delete
                    checkDataNotFound(objectId, e.getCode());

                    DoneCallback callback = (DoneCallback) mCallback;
                    if (callback != null) {
                        callback.done(e);
                    }
                }
            });
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(error);
            }
        }
    }

    /**
     * Get installation object
     *
     * @param objectId object id
     * @return result of get installation
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBInstallation fetchInstallation(String objectId) throws NCMBException {
        //null check
        if (objectId == null) {
            throw new NCMBException(new IllegalArgumentException("objectId is must not be null."));
        }

        //connect
        RequestParams request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_GET);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_INSTALLATION_GOTTEN) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Getting failed.");
        }

        return new NCMBInstallation(response.responseData);
    }

    /**
     * Get installation object in background
     *
     * @param objectId objectId
     * @param callback callback is executed after get installation
     */
    public void fetchInstallationInBackground(String objectId, final FetchCallback callback) {
        try {
            //null check
            if (objectId == null) {
                throw new NCMBException(new IllegalArgumentException("objectId is must not be null."));
            }

            //connect
            RequestParams request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_GET);
            sendRequestAsync(request, new InstallationServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

                    FetchCallback<NCMBInstallation> callback = (FetchCallback) mCallback;
                    if (callback != null) {
                        callback.done(new NCMBInstallation(response.responseData), null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(null, error);
            }
        }
    }

    /**
     * Search installations
     *
     * @param conditions search conditions
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public List searchInstallation(JSONObject conditions) throws NCMBException {
        //connect
        RequestParams request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_INSTALLATION_GOTTEN) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.");
        }

        //return the value of the key 'results'
        return createSearchResults(response.responseData);

    }

    /**
     * Search installations in background
     *
     * @param conditions search conditions
     * @param callback   JSONCallback
     */
    public void searchInstallationInBackground(JSONObject conditions, final SearchInstallationCallback callback) {
        try {
            final RequestParams request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET);
            sendRequestAsync(request, new InstallationServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){
                    //return the value of the key 'results'
                    ArrayList<NCMBInstallation> array = null;
                    try {
                        array = createSearchResults(response.responseData);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }

                    SearchInstallationCallback callback = (SearchInstallationCallback) mCallback;
                    if (callback != null) {
                        callback.done(array, null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    SearchInstallationCallback callback = (SearchInstallationCallback) mCallback;
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(null, error);
            }
        }
    }

    // endregion

    // region internal method

    /**
     * @param params installation parameters
     * @throws JSONException
     * @throws PackageManager.NameNotFoundException
     */
    void setInstallationBasicData(JSONObject params) throws JSONException, PackageManager.NameNotFoundException {
        NCMBLocalFile.checkNCMBContext();

        //value get
        String timeZone = TimeZone.getDefault().getID();
        String packageName = NCMB.getCurrentContext().context.getPackageName();
        PackageManager pm = NCMB.getCurrentContext().context.getPackageManager();
        String applicationName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString();
        String appVersion = pm.getPackageInfo(packageName, 0).versionName;

        //value set
        params.put("deviceType", "android");
        params.put("applicationName", applicationName);
        params.put("appVersion", appVersion);
        params.put("sdkVersion", NCMB.SDK_VERSION);
        params.put("timeZone", timeZone);
    }

    /**
     * Setup params to installation
     *
     * @param objectId    objectId
     * @param params      installation parameters
     * @param queryParams Query parameters
     * @param method      method
     * @return parameters in object
     */
    RequestParams createRequestParams(String objectId, JSONObject params, JSONObject queryParams, String method) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        //url set
        if (objectId != null) {
            //PUT,GET(fetch)
            reqParams.url = mContext.baseUrl + mServicePath + "/" + objectId;
        } else {
            //POST,GET(search)
            reqParams.url = mContext.baseUrl + mServicePath;
        }

        //content set
        if (params != null) {
            reqParams.content = params.toString();
        }

        //query set
        if (queryParams == null && method.equals(NCMBRequest.HTTP_METHOD_GET)) {
            reqParams.query = new JSONObject();
        } else if (queryParams != null && method.equals(NCMBRequest.HTTP_METHOD_GET)) {
            reqParams.query = queryParams;
        }

        //type set
        reqParams.type = method;

        return reqParams;
    }

    /**
     * Argument checking of POST
     *
     * @param registrationId registration id
     * @param params         installation parameters
     * @throws NCMBException
     */
    JSONObject argumentNullCheckForPOST(String registrationId, JSONObject params) throws NCMBException {
        if (registrationId == null) {
            throw new NCMBException(new IllegalArgumentException("registrationId is must not be null."));
        }
        if (params == null) {
            params = new JSONObject();
        }

        return params;
    }

    /**
     * Argument checking of PUT
     *
     * @param objectId objectId
     * @param params   installation parameters
     * @throws NCMBException
     */
    JSONObject argumentNullCheckForPUT(String objectId, JSONObject params) throws NCMBException {
        if (objectId == null) {
            throw new NCMBException(new IllegalArgumentException("objectId is must not be null."));
        }
        if (params == null) {
            params = new JSONObject();
        }
        return params;
    }

    /**
     * Create search results
     *
     * @param responseData API response data
     * @return JSONArray
     * @throws NCMBException
     */
    ArrayList<NCMBInstallation> createSearchResults(JSONObject responseData) throws NCMBException {
        try {
            JSONArray results = responseData.getJSONArray("results");
            ArrayList<NCMBInstallation> array = new ArrayList<>();
            for (int i = 0; i < results.length(); ++i) {
                NCMBInstallation installation = new NCMBInstallation(results.getJSONObject(i));
                array.add(installation);
            }
            return array;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.");
        }
    }

    /**
     * Run at the time of "POST" and "PUT"
     * write the currentInstallation data in the file
     *
     * @param responseData installation parameters
     */
    void writeCurrentInstallation(JSONObject params, JSONObject responseData) throws NCMBException {
        //merge responseData to the params
        mergeJSONObject(params, responseData);

        //merge params to the currentData
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        JSONObject currentData = currentInstallation.getLocalData();
        mergeJSONObject(currentData, params);

        //write file
        File file = NCMBLocalFile.create(NCMBInstallation.INSTALLATION_FILENAME);
        NCMBLocalFile.writeFile(file, currentData);

        //held in a static
        NCMBInstallation.currentInstallation = new NCMBInstallation(currentData);
    }

    /**
     * Run at the time of "Delete" and "POST" or "PUT" and "E404001 Error"
     */
    static void clearCurrentInstallation() {
        //delete file
        File file = NCMBLocalFile.create(NCMBInstallation.INSTALLATION_FILENAME);
        NCMBLocalFile.deleteFile(file);
        //discarded from the static
        NCMBInstallation.currentInstallation = null;
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
            throw new NCMBException(NCMBException.INVALID_JSON, error.getMessage());
        }
    }

    /**
     * automatic deletion of the registration currentInstallation during E404001 return
     * Use at the time of the 'POST' and 'DELETE'
     *
     * @param code error code
     */
    static void checkDataNotFound(String objectId, String code) {
        if (NCMBException.DATA_NOT_FOUND.equals(code)) {
            if (objectId.equals(NCMBInstallation.getCurrentInstallation().getObjectId())) {
                clearCurrentInstallation();
            }
        }
    }

    //endregion
}
