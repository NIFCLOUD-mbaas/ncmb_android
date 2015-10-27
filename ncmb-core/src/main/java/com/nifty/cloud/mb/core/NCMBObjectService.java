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

        ObjectServiceCallback(NCMBObjectService service, SearchObjectCallback callback) {
            super(service, (CallbackBase)callback);
        }

        protected NCMBObjectService getObjectService() {
            return (NCMBObjectService)mService;
        }
    }

    /**
     * Constructor
     *
     * @param context
     */
    NCMBObjectService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * Saving JSONObject data to Nifty cloud mobile backend
     * @param className Datastore class name which to save the object
     * @param params Saving Object data
     * @return result of save object
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public JSONObject saveObject(String className, JSONObject params) throws NCMBException {
        if (!validateClassName(className)){
            throw new NCMBException(NCMBException.GENERIC_ERROR, "className is must not be null or empty");
        }
        validateClassName(className);
        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_POST;
        NCMBResponse response = sendRequest(url, type, params.toString());
        if (response.statusCode != NCMBResponse.HTTP_STATUS_CREATED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
        return response.responseData;
    }

    /**
     * Saving JSONObject data to Nifty cloud mobile backend in background thread
     * @param className Datastore class name which to save the object
     * @param params saving Object data
     * @param callback callback for after object save
     */
    public void saveObjectInBackground(String className, JSONObject params, ExecuteServiceCallback callback) {
        if (!validateClassName(className)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "className is must not be null or empty"));
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

    public JSONObject fetchObject(String className,String objectId) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
        return response.responseData;
    }

    public void fetchObjectInBackground(String className, String objectId, ExecuteServiceCallback callback){
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty"));
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

    public JSONObject updateObject(String className, String objectId, JSONObject params) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty");
        }
        validateClassName(className);
        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_PUT;
        NCMBResponse response = sendRequest(url, type, params.toString());
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
        return response.responseData;
    }

    public void updateObjectInBackground(String className, String objectId, JSONObject params, ExecuteServiceCallback callback) {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty"));
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

    public JSONObject deleteObject(String className,String objectId) throws NCMBException {
        if (!validateClassName(className) || !validateObjectId(objectId)){
            throw new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className + "/" + objectId;
        String type = NCMBRequest.HTTP_METHOD_DELETE;
        NCMBResponse response = sendRequest(url, type);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
        return response.responseData;
    }

    public void deleteObjectInBackground(String className, String objectId, ExecuteServiceCallback callback){
        if (!validateClassName(className) || !validateObjectId(objectId)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty"));
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

    public List searchObject (String className, JSONObject conditions) throws NCMBException {
        if (!validateClassName(className)){
            throw new NCMBException(NCMBException.GENERIC_ERROR, "className / objectId is must not be null or empty");
        }

        String url = mContext.baseUrl + mServicePath + className;
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type, null, conditions);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }

        return createSearchResults(className, response.responseData);
    }

    public void searchObjectInBackground(final String className, JSONObject conditions, SearchObjectCallback callback) {
        if (!validateClassName(className)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "className is must not be null or empty"));
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
     * Create search results
     *
     * @param responseData API response data
     * @return JSONArray
     * @throws NCMBException
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
