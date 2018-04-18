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
    NCMBObject(String className, JSONObject params){
        super(className, params);
        mIgnoreKeys = Arrays.asList(
                "objectId", "acl",
                "createDate", "updateDate"
        );
    }

    /**
     * save current NCMBObject to data store
     * @throws NCMBException exception from NIF Cloud mobile backend
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
            NCMBObjectService objService = new NCMBObjectService(NCMB.getCurrentContext());
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

            NCMBObjectService objService = new NCMBObjectService(NCMB.getCurrentContext());
            objService.updateObjectInBackground(mClassName, getObjectId(), updateJson,executeCallback);
        }

    }

    /**
     * fetch current NCMBObject data from data store
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    public void fetch() throws NCMBException {
        NCMBObjectService objService = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        NCMBObject obj = objService.fetchObject(mClassName, getObjectId());
        mFields = obj.mFields;
    }

    /**
     * Get object in Background without callback
     */
    public void fetchInBackground() {
        fetchInBackground(null);
    }

    /**
     * fetch current NCMBObject data from data store asynchronously
     * @param callback callback after fetch data
     */
    public void fetchInBackground (final FetchCallback callback){
        NCMBObjectService objService = new NCMBObjectService(NCMB.getCurrentContext());
        objService.fetchObjectInBackground(mClassName, getObjectId(), new FetchCallback<NCMBObject>() {
            @Override
            public void done(NCMBObject object, NCMBException e) {
                NCMBException error = null;
                if (e != null) {
                    error = e;
                } else {
                    mFields = object.mFields;
                }
                if (callback != null) {
                    callback.done(object, error);
                }
            }
        });
    }

    /**
     * delete current NCMBObject from data store
     * @throws NCMBException exception from NIF Cloud mobile backend
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
        NCMBObjectService objService = new NCMBObjectService(NCMB.getCurrentContext());
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

    /**
     * Set server data to ignore key properties
     * @param res
     * @throws NCMBException
     */
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
