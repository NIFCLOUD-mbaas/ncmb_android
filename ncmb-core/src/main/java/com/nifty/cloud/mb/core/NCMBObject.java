package com.nifty.cloud.mb.core;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 *  NCMBObject is used to retrieve and upload the data in data store
 */
public class NCMBObject extends NCMBBase{

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    public NCMBObject(String className){
        super(className);
    }

    /**
     * Constructor with class name and default value
     * @param className class name for data store
     * @param params parameter for setting value. same field name as property(objectId, createDate, updateDate, acl) can't set.
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
     * @throws NCMBException exception from NIFTY Cloud mobile backend
     */
    public void save() throws NCMBException {
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject res = null;
        if (getObjectId() == null){
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
                    callback.done(new NCMBException(NCMBException.INVALID_JSON,e.getMessage()));
                }
            }

            NCMBObjectService objService = new NCMBObjectService(NCMB.sCurrentContext);
            objService.updateObjectInBackground(mClassName, getObjectId(), updateJson,executeCallback);
        }

    }

    /**
     * fetch current NCMBObject data from data store
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
     * @param callback callback after fetch data
     */
    public void fetchObjectInBackground (final DoneCallback callback){
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
     * @param callback callback after delete object
     */
    public void deleteObjectInBackground (final DoneCallback callback) {
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
     * @param key field name to increment value
     * @param amount increment amount number
     * @throws NCMBException exception from sdk internal
     */
    public void increment(String key, int amount) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)){
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
     * @param key field name to add objects
     * @param objects objects to add
     * @throws NCMBException exception from sdk internal
     */
    public void addToList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)){
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
     * @param key field name to add objects
     * @param objects objects to add
     * @throws NCMBException exception from sdk internal
     */
    public void addUniqueToList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)){
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
     * @param key field name to remove objects
     * @param objects objects to remove
     * @throws NCMBException exception from sdk internal
     */
    public void removeFromList(String key, List objects) throws NCMBException {
        if (getObjectId() != null && !mFields.isNull(key)){
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

    private void setServerDataToProperties(JSONObject res) throws NCMBException {
        if (res != null) {
            if (res.has("objectId")) {
                try {
                    setObjectId(res.getString("objectId"));
                } catch (JSONException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if (res.has("createDate")){
                try {
                    SimpleDateFormat df = NCMBDateFormat.getIso8601();
                    setCreateDate(df.parse(res.getString("createDate")));
                    setUpdateDate(df.parse(res.getString("createDate")));
                } catch (JSONException | ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if (res.has("updateDate")){
                try {
                    SimpleDateFormat df = NCMBDateFormat.getIso8601();
                    setUpdateDate(df.parse(res.getString("updateDate")));
                } catch (JSONException | ParseException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
            if (res.has("acl")){
                try {
                    NCMBAcl acl = new NCMBAcl(res.getJSONObject("acl"));
                    setAcl(acl);
                } catch (JSONException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }

        }

    }
}
