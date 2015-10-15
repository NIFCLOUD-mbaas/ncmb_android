package com.nifty.cloud.mb.core;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * NCMBObject is used to retrieve and upload the data in data store
 */
public class NCMBObject extends NCMBBase {

    /**
     * Constructor with class name
     *
     * @param className class name for data store
     */
    public NCMBObject(String className) {
        super(className);
    }

    /**
     * Constructor with class name and default value
     *
     * @param className class name for data store
     * @param params    parameter for setting value. same field name as property(objectId, createDate, updateDate, acl) can't set.
     * @throws NCMBException
     */
    NCMBObject(String className, JSONObject params) throws NCMBException {
        super(className, params);
        mIgnoreKeys = Arrays.asList(
                "objectId", "acl",
                "createDate", "updateDate"
        );
    }

    /**
     * save current NCMBObject to data store
     *
     * @throws NCMBException exception from NIFTY Cloud mobile backend
     */
    public void save() throws NCMBException {
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject res = null;
        if (getObjectId() == null) {
            res = objService.saveObject(mClassName, mFields);
        } else {
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            res = objService.updateObject(mClassName, getObjectId(), updateJson);
        }
        setServerDataToProperties(res);
        mUpdateKeys.clear();
    }

    /**
     * save current NCMBObject to data store asynchronously
     *
     * @param callback callback after object save
     */
    public void saveInBackground(final DoneCallback callback) {
        ExecuteServiceCallback executeCallback = new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    try {
                        setServerDataToProperties(jsonData);
                        mUpdateKeys.clear();
                        if (callback != null) {
                            callback.done(null);
                        }
                    } catch (NCMBException error) {
                        if (callback != null) {
                            callback.done(error);
                        }
                    }
                }

            }
        };
        if (getObjectId() == null) {
            NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
            objService.saveObjectInBackground(mClassName, mFields, executeCallback);
        } else {
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                if (callback != null) {
                    callback.done(new NCMBException(NCMBException.INVALID_JSON, e.getMessage()));
                }
            }

            NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
            objService.updateObjectInBackground(mClassName, getObjectId(), updateJson, executeCallback);
        }

    }

    /**
     * saves each object in the list.
     *
     * @param objects save objects list
     * @return response data
     * @throws NCMBException exception from NIFTY Cloud mobile backend
     */
    static public JSONArray saveAll(List<NCMBBase> objects) throws NCMBException {
        //create content
        JSONArray content = createSaveAllContent(objects);

        //connect
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONArray responseArray = objService.saveAllObject(content);

        //response set
        setResponseArrayForObjects(responseArray, objects);
        return responseArray;
    }

    /**
     * saves each object in the list to asynchronously.
     *
     * @param callback callback after objects save
     * @param objects  save objects list
     */
    static public void saveAllInBackground(final List<NCMBBase> objects, final BatchCallback callback) {
        //create content
        JSONArray content;
        try {
            content = createSaveAllContent(objects);
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(null, e);
            }
            return;
        }

        //connect
        NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
        objService.saveAllObjectInBackground(content, new BatchCallback() {

            @Override
            public void done(JSONArray responseArray, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(responseArray, e);
                    }
                } else {
                    //response set
                    try {
                        setResponseArrayForObjects(responseArray, objects);
                        if (callback != null) {
                            callback.done(responseArray, null);
                        }
                    } catch (NCMBException error) {
                        if (callback != null) {
                            callback.done(responseArray, error);
                        }
                    }

                }
            }
        });
    }

    //Create a content data for saveAll
    static private JSONArray createSaveAllContent(List<NCMBBase> objects) throws NCMBException {
        JSONArray content = new JSONArray();
        try {
            for (NCMBBase obj : objects) {
                if (obj instanceof NCMBInstallation) {
                    //NCMBInstallation
                    content.put(createObjectJSON(obj, NCMBInstallationService.SERVICE_PATH));
                } else if (obj instanceof NCMBObject) {
                    //NCMBObject
                    content.put(createObjectJSON(obj, NCMBObjectService.SERVICE_PATH + obj.getClassName()));
                } else if (obj instanceof NCMBRole) {
                    //NCMBRole
                    content.put(createObjectJSON(obj, NCMBRoleService.SERVICE_PATH));
                } else if (obj instanceof NCMBPush) {
                    //NCMBPush
                    content.put(createObjectJSON(obj, NCMBPushService.SERVICE_PATH));
                } else {
                    throw new IllegalArgumentException("Invalid argument.");
                }
            }
        } catch (JSONException error) {
            throw new NCMBException(error);
        }
        return content;
    }

    //Create a JSONObject of each object
    static private JSONObject createObjectJSON(NCMBBase obj, String classPath) throws JSONException {
        String method;
        String path = NCMB.DEFAULT_API_VERSION + "/";
        JSONObject body;

        if (obj.getObjectId() == null) {
            method = NCMBRequest.HTTP_METHOD_POST;
            path += classPath;
            body = obj.mFields;
        } else {
            method = NCMBRequest.HTTP_METHOD_PUT;
            path += classPath + "/" + obj.getObjectId();
            body = obj.createUpdateJsonData();
        }

        JSONObject json = new JSONObject();
        json.put("method", method);
        json.put("path", path);
        json.put("body", body);
        return json;
    }

    //Set the response array for each object
    static private void setResponseArrayForObjects(JSONArray responseArray, List<NCMBBase> objects) throws NCMBException {
        for (int i = 0; i < responseArray.length(); i++) {
            try {
                if (responseArray.getJSONObject(i).has("success")) {
                    NCMBBase obj = objects.get(i);
                    obj.setServerDataToProperties(responseArray.getJSONObject(i).getJSONObject("success"));
                    obj.mUpdateKeys.clear();
                }
            } catch (JSONException | NCMBException e) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, "saveAll object count " + i + ": Response format is invalid.");
            }
        }
    }

    /**
     * fetch current NCMBObject data from data store
     *
     * @throws NCMBException exception from NIFTY Cloud mobile backend
     */
    public void fetchObject() throws NCMBException {
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject res = objService.fetchObject(mClassName, getObjectId());
        setServerDataToProperties(res);
        try {
            copyFrom(res);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
        }
    }

    /**
     * fetch current NCMBObject data from data store asynchronously
     *
     * @param callback callback after fetch data
     */
    public void fetchObjectInBackground(final DoneCallback callback) {
        NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
        objService.fetchObjectInBackground(mClassName, getObjectId(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    try {
                        setServerDataToProperties(jsonData);
                        copyFrom(jsonData);
                        if (callback != null) {
                            callback.done(null);
                        }
                    } catch (NCMBException error) {
                        if (callback != null) {
                            callback.done(error);
                        }
                    } catch (JSONException jsonError) {
                        if (callback != null) {
                            callback.done(new NCMBException(NCMBException.INVALID_JSON, jsonError.getMessage()));
                        }
                    }
                }

            }
        });
    }

    /**
     * delete current NCMBObject from data store
     *
     * @throws NCMBException exception from NIFTY Cloud mobile backend
     */
    public void deleteObject() throws NCMBException {
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objService.deleteObject(mClassName, getObjectId());
        mFields = new JSONObject();
        mUpdateKeys.clear();
    }

    /**
     * delete current NCMBObject from data store asynchronously
     *
     * @param callback callback after delete object
     */
    public void deleteObjectInBackground(final DoneCallback callback) {
        NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
        objService.deleteObjectInBackground(mClassName, getObjectId(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    mFields = new JSONObject();
                    mUpdateKeys.clear();
                    if (callback != null) {
                        callback.done(null);
                    }
                }
            }
        });
    }

    /**
     * increment the value of the specified key
     * this method is effective for the saved object that contains value of the specified key
     *
     * @param key    field name to increment value
     * @param amount increment amount number
     * @throws NCMBException exception from sdk internal
     */
    public void increment(String key, int amount) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)) {
            if (isIgnoreKey(key)) {
                throw new NCMBException(NCMBException.INVALID_FORMAT, "Can't put data to same name with property key.");
            } else {
                try {
                    JSONObject incrementOperation = new JSONObject("{\"__op\":\"Increment\"}");
                    incrementOperation.put("amount", amount);
                    mFields.put(key, incrementOperation);
                    mUpdateKeys.add(key);
                } catch (JSONException e) {
                    throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON data");
                }
            }
        }
    }

    /**
     * add objects to given key
     * this method is effective for the saved object that contains value of the specified key
     *
     * @param key     field name to add objects
     * @param objects objects to add
     * @throws NCMBException exception from sdk internal
     */
    public void addToList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)) {
            if (isIgnoreKey(key)) {
                throw new NCMBException(NCMBException.INVALID_FORMAT, "Can't put data to same name with property key.");
            } else {
                try {
                    JSONObject addOperation = new JSONObject("{\"__op\":\"Add\"}");
                    String listJsonStr = new Gson().toJson(objects);
                    addOperation.put("objects", new JSONArray(listJsonStr));
                    mFields.put(key, addOperation);
                    mUpdateKeys.add(key);
                } catch (JSONException e) {
                    throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON data");
                }
            }
        }
    }

    /**
     * add objects if object is unique in the specified key
     * this method is effective for the saved object that contains value of the specified key
     *
     * @param key     field name to add objects
     * @param objects objects to add
     * @throws NCMBException exception from sdk internal
     */
    public void addUniqueToList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)) {
            if (isIgnoreKey(key)) {
                throw new NCMBException(NCMBException.INVALID_FORMAT, "Can't put data to same name with property key.");
            } else {
                try {
                    JSONObject addOperation = new JSONObject("{\"__op\":\"AddUnique\"}");
                    String listJsonStr = new Gson().toJson(objects);
                    addOperation.put("objects", new JSONArray(listJsonStr));
                    mFields.put(key, addOperation);
                    mUpdateKeys.add(key);
                } catch (JSONException e) {
                    throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON data");
                }
            }
        }

    }

    /**
     * remove objects from array in the given key
     * this method is effective for the saved object that contains value of the specified key
     *
     * @param key     field name to remove objects
     * @param objects objects to remove
     * @throws NCMBException exception from sdk internal
     */
    public void removeFromList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)) {
            if (isIgnoreKey(key)) {
                throw new NCMBException(NCMBException.INVALID_FORMAT, "Can't put data to same name with property key.");
            } else {
                try {
                    JSONObject removeOperation = new JSONObject("{\"__op\":\"Remove\"}");
                    String listJsonStr = new Gson().toJson(objects);
                    removeOperation.put("objects", new JSONArray(listJsonStr));
                    mFields.put(key, removeOperation);
                } catch (JSONException e) {
                    throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON data");
                }
            }
        }
    }

}
