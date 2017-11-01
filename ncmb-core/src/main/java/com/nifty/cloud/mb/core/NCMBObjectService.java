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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for data store api
 */
public class NCMBObjectService extends NCMBService{

    public static final String SERVICE_PATH = "classes/";

    /**
     * Inner class for callback
     */
    abstract class ObjectServiceCallback extends ServiceCallback {
        /** constructors */
        ObjectServiceCallback(NCMBObjectService service, ExecuteServiceCallback callback) {
            super(service, (CallbackBase)callback);
        }

        ObjectServiceCallback(NCMBObjectService service, FetchCallback callback) {
            super(service, (CallbackBase)callback);
        }

        ObjectServiceCallback(NCMBObjectService service, SearchObjectCallback callback) {
            super(service, (CallbackBase)callback);
        }

        ObjectServiceCallback(NCMBObjectService service, CountCallback callback) {
            super(service, (CallbackBase)callback);
        }

        protected NCMBObjectService getObjectService() {
            return (NCMBObjectService)mService;
        }
    }

    /**
     * Constructor
     *
     * @param context runtime context
     */
    NCMBObjectService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * Saving JSONObject data to NIF Cloud mobile backend
     * @param className Datastore class name which to save the object
     * @param params Saving Object data
     * @return result of save object
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject saveObject(String className, JSONObject params) throws NCMBException {
        if (!validateClassName(className)){
            throw new NCMBException(NCMBException.REQUIRED, "className is must not be null or empty");
        }
        validateClassName(className);
        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_POST;
        NCMBResponse response = sendRequest(url, type, params.toString());
        if (response.statusCode != NCMBResponse.HTTP_STATUS_CREATED) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }
        return response.responseData;
    }

    /**
     * Saving JSONObject data to NIF Cloud mobile backend in background thread
     * @param className Datastore class name which to save the object
     * @param params saving Object data
     * @param callback callback for after object save
     */
    public void saveObjectInBackground(String className, JSONObject params, ExecuteServiceCallback callback) {
        if (!validateClassName(className)){
            callback.done(null, new NCMBException(NCMBException.REQUIRED, "className is must not be null or empty"));
        }

        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_POST;
        RequestParams requestParams = new RequestParams();
        requestParams.url = url;
        requestParams.type = type;
        requestParams.content = params.toString();

        try {
            sendRequestAsync(requestParams, new ObjectServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response) {

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
        } catch (NCMBException e) {
            //Exception handling for NCMBRequest
            if (callback != null) {
                callback.done(null, e);
            }

        }

    }

    /**
     * Fetching JSONObject data from NIF Cloud mobile backend
     * @param className Datastore class name which to fetch the object
     * @param objectId Datastore object id of fetch data
     * @return NCMBObject of fetch data
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBObject fetchObject(String className,String objectId) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }
        return new NCMBObject(className, response.responseData);
    }

    /**
     * Fetching JSONObject data from NIF Cloud mobile backend in background thread
     * @param className Datastore class name which to fetch the object
     * @param objectId Datastore object id of fetch data
     * @param callback callback for after object fetch
     */
    public void fetchObjectInBackground(final String className, String objectId, final FetchCallback callback){
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty"));
        } else {

            String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
            String type = NCMBRequest.HTTP_METHOD_GET;
            RequestParams requestParams = new RequestParams();
            requestParams.url = url;
            requestParams.type = type;

            try {
                sendRequestAsync(requestParams, new ObjectServiceCallback(this, callback) {
                    @Override
                    public void handleResponse(NCMBResponse response) {

                        FetchCallback<NCMBObject> callback = (FetchCallback) mCallback;
                        if (callback != null) {
                            callback.done(new NCMBObject(className, response.responseData), null);
                        }
                    }

                    @Override
                    public void handleError(NCMBException e) {
                        if (callback != null) {
                            callback.done(null, e);
                        }
                    }
                });
            } catch (NCMBException e) {
                //Exception handling for NCMBRequest
                if (callback != null) {
                    callback.done(null, e);
                }

            }
        }

    }

    /**
     * Updating JSONObject data to NIF Cloud mobile backend
     * @param className Datastore class name which to update the object
     * @param objectId Datastore object id of update data
     * @param params JSONObject of update data
     * @return JSONObject of update result
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject updateObject(String className, String objectId, JSONObject params) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty");
        }
        validateClassName(className);
        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_PUT;
        NCMBResponse response = sendRequest(url, type, params.toString());
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }
        return response.responseData;
    }

    /**
     * Updating JSONObject data to NIF Cloud mobile backend in background thread
     * @param className Datastore class name which to update the object
     * @param objectId Datastore object id of update data
     * @param params JSONObject of update data
     * @param callback callback for after object update
     */
    public void updateObjectInBackground(String className, String objectId, JSONObject params, ExecuteServiceCallback callback) {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty"));
        } else {

            String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
            String type = NCMBRequest.HTTP_METHOD_PUT;

            try {
                sendRequestAsync(url, type, params.toString(), null, new ObjectServiceCallback(this, callback) {
                    @Override
                    public void handleResponse(NCMBResponse response) {

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
            } catch (NCMBException e) {
                //Exception handling for NCMBRequest
                if (callback != null) {
                    callback.done(null, e);
                }

            }
        }

    }

    /**
     * Deleting JSONObject data from NIF Cloud mobile backend
     * @param className Datastore class name which to delete the object
     * @param objectId Datastore object id of delete data
     * @return JSONObject of delete result
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject deleteObject(String className,String objectId) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_DELETE;
        NCMBResponse response = sendRequest(url, type);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }
        return response.responseData;
    }

    /**
     * Deleting JSONObject data from NIF Cloud mobile backend in background thread
     * @param className Datastore class name which to delete the object
     * @param objectId Datastore object id of delete data
     * @param callback callback for after object delete
     */
    public void deleteObjectInBackground(String className, String objectId, ExecuteServiceCallback callback){
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty"));
        } else {
            String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
            String type = NCMBRequest.HTTP_METHOD_DELETE;
            RequestParams requestParams = new RequestParams();
            requestParams.url = url;
            requestParams.type = type;

            try {
                sendRequestAsync(requestParams, new ObjectServiceCallback(this, callback) {
                    @Override
                    public void handleResponse(NCMBResponse response) {

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
            } catch (NCMBException e) {
                //Exception handling for NCMBRequest
                if (callback != null) {
                    callback.done(null, e);
                }

            }
        }

    }

    /**
     * Searching JSONObject data from NIF Cloud mobile backend
     * @param className Datastore class name which to search the object
     * @param conditions JSONObject of search conditions
     * @return List of NCMBObject of search results
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public List searchObject (String className, JSONObject conditions) throws NCMBException {
        if (!validateClassName(className)){
            throw new NCMBException(NCMBException.REQUIRED, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type, null, conditions);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }

        return createSearchResults(className, response.responseData);
    }

    /**
     * Searching JSONObject data to NIF Cloud mobile backend in background thread
     * @param className Datastore class name which to search the object
     * @param conditions JSONObject of search conditions
     * @param callback callback for after object search
     */
    public void searchObjectInBackground(final String className, JSONObject conditions, SearchObjectCallback callback) {
        if (!validateClassName(className)){
            callback.done(null, new NCMBException(NCMBException.REQUIRED, "className is must not be null or empty"));
        }

        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_GET;
        RequestParams reqParams = new RequestParams();
        reqParams.url = url;
        reqParams.type = type;
        reqParams.query = conditions;
        try {
            sendRequestAsync(reqParams, new ObjectServiceCallback(this, callback){
                @Override
                public void handleResponse(NCMBResponse response) {

                    SearchObjectCallback callback = (SearchObjectCallback) mCallback;
                    if (callback != null) {
                        try {
                            callback.done(createSearchResults(className, response.responseData), null);
                        } catch (NCMBException callbackException) {
                            callback.done(null, callbackException);
                        }

                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    SearchObjectCallback callback = (SearchObjectCallback) mCallback;
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException e) {
            //Exception handling for NCMBRequest
            if (callback != null) {
                callback.done(null, e);
            }

        }
    }

    /**
     * create url to request object search api
     * @param className class name for search object
     * @return Request URL string of object search api
     */
    private String createUrlForCount (String className) {
        if (className.equals("user")) {
            return mContext.baseUrl + "users";
        } else if (className.equals("role")) {
            return mContext.baseUrl + "roles";
        } else if (className.equals("push")) {
            return mContext.baseUrl + "push";
        } else if (className.equals("installation")) {
            return mContext.baseUrl + "installations";
        } else if (className.equals("file")) {
            return mContext.baseUrl + "files";
        } else {
            return mContext.baseUrl + mServicePath + className;
        }
    }

    /**
     * Counting search object results from NIF Cloud mobile backend
     * @param className Datastore class name which to search the object
     * @param conditions JSONObject of search conditions
     * @return number of search results
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public int countObject (String className, JSONObject conditions) throws NCMBException  {
        if (!validateClassName(className)){
            throw new NCMBException(NCMBException.REQUIRED, "className is must not be null or empty");
        }

        String url = createUrlForCount(className);
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type, null, conditions);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code");
        }

        try {
            return response.responseData.getInt("count");
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
        }
    }

    /**
     * Counting search object results from NIF Cloud mobile backend
     * @param className Datastore class name which to search the object
     * @param conditions JSONObject of search conditions
     * @param callback callback for after object search and count data
     */
    public void countObjectInBackground(final String className, JSONObject conditions, CountCallback callback) {
        if (!validateClassName(className)){
            callback.done(0, new NCMBException(NCMBException.REQUIRED, "className is must not be null or empty"));
        }

        String url = createUrlForCount(className);
        String type = NCMBRequest.HTTP_METHOD_GET;
        RequestParams reqParams = new RequestParams();
        reqParams.url = url;
        reqParams.type = type;
        reqParams.query = conditions;
        try {
            sendRequestAsync(reqParams, new ObjectServiceCallback(this, callback){
                @Override
                public void handleResponse(NCMBResponse response) {

                    CountCallback callback = (CountCallback) mCallback;
                    if (callback != null) {
                        try {
                            callback.done(response.responseData.getInt("count"), null);
                        } catch (JSONException e) {
                            callback.done(0, new NCMBException(NCMBException.INVALID_JSON, e.getMessage()));
                        }

                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    CountCallback callback = (CountCallback) mCallback;
                    if (callback != null) {
                        callback.done(0, new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.getMessage()));
                    }
                }
            });
        } catch (NCMBException e) {
            //Exception handling for NCMBRequest
            if (callback != null) {
                callback.done(0, e);
            }

        }
    }

    /**
     * Create search results
     *
     * @param responseData API response data
     * @return JSONArray
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    List<NCMBObject> createSearchResults(String className, JSONObject responseData) throws NCMBException {
        try {
            JSONArray results = responseData.getJSONArray("results");
            List<NCMBObject> array = new ArrayList<>();
            for (int i = 0; i < results.length(); ++i) {
                NCMBObject object = new NCMBObject(className, results.getJSONObject(i));
                array.add(object);
            }
            return array;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.");
        }
    }

    private boolean validateClassName (String className){
        if (className == null || className.isEmpty()){
            return false;
        }
        return true;
    }

    private boolean validateObjectId (String objectId){
        if (objectId == null || objectId.isEmpty()){
            return false;
        }
        return true;
    }
}
