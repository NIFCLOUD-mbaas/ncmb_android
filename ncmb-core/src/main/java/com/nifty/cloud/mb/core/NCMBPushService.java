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
import java.util.Arrays;
import java.util.List;

/**
 * Service class for push notification api
 */
public class NCMBPushService extends NCMBService {
    /** service path for API category */
    public static final String SERVICE_PATH = "push";
    /** Status code of push created */
    public static final int HTTP_STATUS_PUSH_CREATED = 201;
    /** Status code of push updated */
    public static final int HTTP_STATUS_PUSH_UPDATED = 200;
    /** Status code of push deleted */
    public static final int HTTP_STATUS_PUSH_DELETED = 200;
    /** Status code of push gotten */
    public static final int HTTP_STATUS_PUSH_GOTTEN = 200;
    /** Status code of push receiptStatus */
    public static final int HTTP_STATUS_PUSH_RECEIPTSTATUS = 200;

    /**
     * Inner class for callback
     */
    abstract class PushServiceCallback extends ServiceCallback {
        /** constructors */
        PushServiceCallback(NCMBPushService service, DoneCallback callback) {
            super(service, callback);
        }

        PushServiceCallback(NCMBPushService service, ExecuteServiceCallback callback) {
            super(service, callback);
        }

        PushServiceCallback(NCMBPushService service, FetchCallback callback) {
            super(service, callback);
        }

        PushServiceCallback(NCMBPushService service, SearchPushCallback callback) {
            super(service, callback);
        }

    }


    /**
     * Constructor
     *
     * @param context NCMBContext object for current context
     */
    NCMBPushService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * Create push object
     *
     * @param params push parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject sendPush(JSONObject params) throws NCMBException {
        if (params == null) {
            throw new NCMBException(NCMBException.INVALID_JSON, "params must not be null");
        } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
            throw new NCMBException(NCMBException.INVALID_JSON, "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time.");
        }

        if (!params.has("deliveryTime")) {
            try {
                params.put("immediateDeliveryFlag", true);
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON.");
            }
        }

        RequestParams request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_CREATED) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Created failed.");
        }

        return response.responseData;
    }

    /**
     * Create push object in background
     *
     * @param params   push parameters
     * @param callback ExecuteServiceCallback
     */
    public void sendPushInBackground(final JSONObject params, ExecuteServiceCallback callback) {
        try {
            if (params == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "params must not be null");
            } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
                throw new NCMBException(NCMBException.INVALID_JSON, "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time");
            }

            if (!params.has("deliveryTime")) {
                try {
                    params.put("immediateDeliveryFlag", true);
                } catch (JSONException e) {
                    throw new NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON");
                }
            }

            //connect
            RequestParams request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

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
     * Update push object
     * It can only be updated before delivery
     *
     * @param pushId object id
     * @param params update information
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public JSONObject updatePush(String pushId, JSONObject params) throws NCMBException {
        if (pushId == null) {
            throw new NCMBException(NCMBException.INVALID_JSON, "pushId must no be null");
        } else if (params == null) {
            throw new NCMBException(NCMBException.INVALID_JSON, "params must no be null");
        } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
            throw new NCMBException(NCMBException.INVALID_JSON, "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time.");
        }

        RequestParams request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT);
        NCMBResponse response = sendRequest(request);

        if (response.statusCode != HTTP_STATUS_PUSH_UPDATED) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Updated failed.");
        }

        return response.responseData;
    }

    /**
     * Update push object in background
     * It can only be updated before delivery
     *
     * @param pushId   object id
     * @param params   update information
     * @param callback ExecuteServiceCallback
     */
    public void updatePushInBackground(final String pushId, final JSONObject params, final ExecuteServiceCallback callback) {
        try {
            if (pushId == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "pushId must no be null");
            } else if (params == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "params must no be null");
            } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
                throw new NCMBException(NCMBException.INVALID_JSON, "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time.");
            }

            //connect
            RequestParams request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

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
     * Delete push object
     *
     * @param pushId object id
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public void deletePush(String pushId) throws NCMBException {
        //null check
        if (pushId == null) {
            throw new NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.");
        }

        //connect
        RequestParams request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_DELETE);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_PUSH_DELETED) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Deleted failed.");
        }
    }

    /**
     * Delete push object in background
     *
     * @param pushId   objectId
     * @param callback ActionCallback
     */
    public void deletePushInBackground(String pushId, DoneCallback callback) {
        try {
            //null check
            if (pushId == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.");
            }

            //connect
            RequestParams request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_DELETE);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

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
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(error);
            }
        }
    }

    /**
     * Get push object
     *
     * @param pushId object id
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBPush fetchPush(String pushId) throws NCMBException {
        //null check
        if (pushId == null) {
            throw new NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.");
        }

        //connect
        RequestParams request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_GET);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_PUSH_GOTTEN) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.");
        }
        return new NCMBPush(response.responseData);
    }

    /**
     * Get push object in background
     *
     * @param pushId   object id
     * @param callback ExecuteServiceCallback
     */
    public void fetchPushInBackground(final String pushId, final FetchCallback callback) {
        try {
            if (pushId == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "pushId must no be null");
            }

            //connect
            RequestParams request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_GET);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

                    FetchCallback<NCMBPush> callback = (FetchCallback) mCallback;
                    if (callback != null) {
                        callback.done(new NCMBPush(response.responseData), null);
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
     * Search push
     *
     * @param conditions search conditions
     * @return List
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public List searchPush(JSONObject conditions) throws NCMBException {
        //connect
        RequestParams request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET);
        NCMBResponse response = sendRequest(request);
        if (response.statusCode != HTTP_STATUS_PUSH_GOTTEN) {
            throw new NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.");
        }
        //return the value of the key 'results'
        return createSearchResults(response.responseData);
    }

    /**
     * Search installations in background
     *
     * @param conditions search conditions
     * @param callback   ExecuteServiceCallback
     */
    public void searchPushInBackground(JSONObject conditions, final SearchPushCallback callback) {
        try {
            final RequestParams request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){
                    //return the value of the key 'results'
                    ArrayList<NCMBPush> results = null;
                    try {
                        results = createSearchResults(response.responseData);
                    } catch (NCMBException e) {
                        callback.done(null, e);
                    }

                    SearchPushCallback callback = (SearchPushCallback) mCallback;
                    if (callback != null) {
                        callback.done(results, null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    SearchPushCallback callback = (SearchPushCallback) mCallback;
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
     * Open push registration in background
     *
     * @param pushId   open push object id
     * @param callback ExecuteServiceCallback
     */
    public void sendPushReceiptStatusInBackground(String pushId, ExecuteServiceCallback callback) {
        try {
            //null check
            if (pushId == null) {
                throw new NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.");
            }

            JSONObject params;
            try {
                params = new JSONObject("{deviceType:android}");
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON");
            }

            //connect
            RequestParams request = createRequestParams(pushId + "/openNumber", params, null, NCMBRequest.HTTP_METHOD_POST);
            sendRequestAsync(request, new PushServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response){

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
     * Setup params to installation
     *
     * @param installationId installation id
     * @param params         installation parameters
     * @param queryParams    query parameters
     * @param method         method
     * @return parameters in object
     */
    RequestParams createRequestParams(String installationId, JSONObject params, JSONObject queryParams, String method) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        //url set
        if (installationId != null) {
            //PUT,GET(fetch)
            reqParams.url = mContext.baseUrl + mServicePath + "/" + installationId;
        } else {
            //POST,GET(search)
            reqParams.url = mContext.baseUrl + mServicePath;
        }

        //content set
        if (params != null) {
            removeNotSetKeys(params);
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
     * Create search results
     *
     * @param responseData API response data
     * @return List
     * @throws NCMBException
     */
    ArrayList<NCMBPush> createSearchResults(JSONObject responseData) throws NCMBException {
        try {
            JSONArray results = responseData.getJSONArray("results");
            ArrayList<NCMBPush> array = new ArrayList<>();
            for (int i = 0; i < results.length(); ++i) {
                NCMBPush push = new NCMBPush(results.getJSONObject(i));
                array.add(push);
            }
            return array;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.");
        }
    }

    /**
     * Remove the keys that can not be set to params
     *
     * @param params Parameter
     */
    void removeNotSetKeys(JSONObject params) {
        List<String> removeKeys = Arrays.asList("objectId", "deliveryPlanNumber", "deliveryNumber", "status", "error", "createDate", "updateDate");
        for (int i = 0; i < removeKeys.size(); i++) {
            if (params.has(removeKeys.get(i))) {
                params.remove(removeKeys.get(i));
            }
        }
    }
}
