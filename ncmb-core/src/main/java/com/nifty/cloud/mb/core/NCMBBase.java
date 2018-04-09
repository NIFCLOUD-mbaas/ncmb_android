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

import android.location.Location;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * NCMBBase class provide an interface to edit data
 */
public class NCMBBase {

    protected String mClassName;

    protected JSONObject mFields;

    protected HashSet<String> mUpdateKeys;

    /** ignore key list */
    protected List<String> mIgnoreKeys;

    NCMBBase(String className) {
        mClassName = className;
        mFields = new JSONObject();
        mUpdateKeys = new HashSet<>();
        mIgnoreKeys = new ArrayList<>();
    }

    NCMBBase(String className, JSONObject params) {
        this(className);
        try {
            copyFrom(params);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void setObjectId (String objectId) {
        try {
            mFields.put("objectId", objectId);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public String getObjectId () {
        try {
            return mFields.getString("objectId");
        } catch (JSONException e) {
            return null;
        }
    }

    protected void setCreateDate (Date createDate){
        try {
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            mFields.put("createDate", df.format(createDate));
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public Date getCreateDate () {
        try {
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            return df.parse(mFields.getString("createDate"));
        } catch (JSONException | ParseException e) {
            return null;
        }
    }

    protected void setUpdateDate (Date updateDate) {
        try {
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            mFields.put("updateDate", df.format(updateDate));
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Date getUpdateDate () {
        try {
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            return df.parse(mFields.getString("updateDate"));
        } catch (JSONException | ParseException e) {
            return null;
        }
    }

    protected void setAclFromInternal (NCMBAcl acl) {
        try {
            if(acl == null){
                mFields.put("acl", null);
            }else {
                mFields.put("acl", acl.toJson());
            };

        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void setAcl(NCMBAcl acl) {
        setAclFromInternal(acl);
        mUpdateKeys.add("acl");
    }

    public NCMBAcl getAcl () {
        try {
            return new NCMBAcl(mFields.getJSONObject("acl"));
        } catch (JSONException e) {
            return null;
        }
    }

    public String getClassName () { return this.mClassName; }

    /**
     * Copy from another JSON
     * @param from JSON that copy from
     */
    void copyFrom(JSONObject from) throws JSONException {
        for (Iterator<String> itor = from.keys(); itor.hasNext();) {
            String key = itor.next();
            if (isIgnoreKey(key)) {
                continue;
            }
            mFields.put(key, from.get(key));
        }
    }

    /**
     * remove the value for the specified key
     * @param key field name for remove the value
     */
    public void remove (String key) {
        try {
            if (mFields.has(key)) {
                mFields.put(key, null);
                mUpdateKeys.add(key);
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    /**
     * confirm whether the specified field is present
     * @param key field name for confirm
     * @return if field is exists, return true
     */
    public boolean containsKey(String key) {
        return mFields.has(key);
    }

    /**
     * Check key is in ignore list
     * @param key key name
     * @return ignore list contains given key or not
     */
    boolean isIgnoreKey(String key) {
        return mIgnoreKeys.contains(key);
    }


    protected JSONObject createUpdateJsonData() throws JSONException{
        JSONObject json = new JSONObject();
        for (String key: mUpdateKeys) {
            if (mFields.isNull(key)){
                json.put(key, JSONObject.NULL);
            } else {
                json.put(key, mFields.get(key));
            }
        }
        return json;
    }

    /**
     * put string value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, String value){
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

    }

    /**
     * put boolean value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, boolean value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put int value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, int value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put long value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, long value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put float value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, float value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, (double) value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put double value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, double value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put Date value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, Date value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                SimpleDateFormat df = NCMBDateFormat.getIso8601();
                JSONObject dateJson = new JSONObject("{'__type':'Date'}");
                dateJson.put("iso", df.format(value));
                mFields.put(key, dateJson);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put Location value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, Location value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                JSONObject locationJson = new JSONObject("{'__type':'GeoPoint'}");
                locationJson.put("longitude", value.getLongitude());
                locationJson.put("latitude", value.getLatitude());
                mFields.put(key, locationJson);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put JSONObject value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, JSONObject value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put JSONArray value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, JSONArray value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                mFields.put(key, value);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put Map value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, Map value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                String mapJsonStr = new Gson().toJson(value);
                mFields.put(key, new JSONObject(mapJsonStr));
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put List value to given key
     * @param key field name for put the value
     * @param value value to put
     */
    public void put(String key, List value) {
        if (isIgnoreKey(key)){
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else {
            try {
                String listJsonStr = new Gson().toJson(value);
                mFields.put(key, new JSONArray(listJsonStr));
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * put NCMBObject value to given key as pointer object
     * @param key field name for put the value
     * @param object NCMBObject to put as pointer
     */
    public void put(String key, NCMBObject object) {
        if (isIgnoreKey(key)) {
            throw new IllegalArgumentException("Can't put data to same name with property key.");
        } else if (object.getObjectId() == null){
            throw new IllegalArgumentException("objectId must not be null.");
        } else {
            try {
                JSONObject pointerJson = new JSONObject("{\"__type\":\"Pointer\"}");
                pointerJson.put("className", object.getClassName());
                pointerJson.put("objectId", object.getObjectId());
                mFields.put(key, pointerJson);
                mUpdateKeys.add(key);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * get string value from given key
     * @param key field name to get the value
     * @return value of specified key or null
     */
    public String getString(String key) {
        try {
            return mFields.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * get boolean value from given key
     * @param key field name to get the value
     * @return value of specified key (defalut value is false)
     */
    public boolean getBoolean(String key) {
        try {
            return mFields.getBoolean(key);
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * get int value from given key
     * @param key field name to get the value
     * @return value of specified key or 0
     */
    public int getInt(String key) {
        try {
            return mFields.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    /**
     * get long value from given key
     * @param key field name to get the value
     * @return value of specified key or 0
     */
    public long getLong(String key) {
        try {
            return mFields.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get double value from given key
     * @param key field name to get the value
     * @return value of specified key or 0
     */
    public double getDouble(String key) {
        try {
            return mFields.getDouble(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    /**
     * Get Date object from given key
     * @param key key name for getting object
     * @return Date object from given key or null
     */
    public Date getDate(String key) {
        SimpleDateFormat sdf = NCMBDateFormat.getIso8601();

        try {
            // Date型変換
            if (mFields.has(key)){
                JSONObject dateJson = this.getJSONObject(key);
                if (dateJson != null) {
                    if (dateJson.has("iso")) {
                        return sdf.parse(dateJson.getString("iso"));
                    }
                }
            }

            return null;

        } catch (JSONException | ParseException e) {
            return null;
        }
    }

    /**
     * Get Location object from given key
     * @param key key name for getting object
     * @return Location object from given key or null
     */
    public Location getGeolocation(String key) {
        try {
            if (mFields.has(key)){
                JSONObject geolocationJson = getJSONObject(key);
                Location location = new Location("ncmb-core");
                location.setLongitude(geolocationJson.getDouble("longitude"));
                location.setLatitude(geolocationJson.getDouble("latitude"));
                return location;
            } else {
                return null;
            }
        } catch (JSONException e) {
            return null;
        }

    }

    /**
     * Get JSONObject from given key
     * @param key key name for getting object
     * @return JSONObject from given key or null
     */
    public JSONObject getJSONObject(String key) {
        try {
            return mFields.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Get JSONArray from given key
     * @param key key name for getting object
     * @return JSONArray from given key or null
     */
    public JSONArray getJSONArray(String key) {
        try {
            return mFields.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Get List object from given key
     * @param key key name for getting object
     * @return List object from given key or null
     */
    public List getList(String key) {
        if (mFields.has(key)){
            return new Gson().fromJson(getJSONArray(key).toString(), List.class);
        } else {
            return null;
        }
    }

    /**
     * Get Map object from given key
     * @param key key name for getting object
     * @return Map object from given key or null
     */
    public Map getMap(String key) {
        if (mFields.has(key)){
            return new Gson().fromJson(getJSONObject(key).toString(), Map.class);
        } else {
            return null;
        }

    }

    /***
     * Get NCMBObject if given key data include pointer object data
     * @param key key name for getting include object
     * @return instance of NCMBObject / NCMBUser / NCMBPush / NCMBInstallation / NCMBRole or null
     */
    public <T extends NCMBBase> T getIncludeObject(String key) {
        if (mFields.has(key)) {
            try {
                JSONObject json = mFields.getJSONObject(key);
                if (json.has("__type") && json.getString("__type").equals("Object")) {
                    String className = json.getString("className");
                    json.remove("__type");
                    json.remove("className");
                    switch (className) {
                        case "user":
                            return (T) new NCMBUser(json);
                        case "installation":
                            return (T) new NCMBInstallation(json);
                        case "role":
                            return (T) new NCMBRole(json);
                        case "push":
                            return (T) new NCMBPush(json);
                        default:
                            return (T) new NCMBObject(className, json);
                    }
                } else {
                    return null;
                }
            } catch (JSONException e) {
                return null;
            } catch (NCMBException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     *  Get list all keys of object
     *  @return List all keys or null 
     */
    public List allKeys(){
        JSONArray listdata = this.mFields.names();
        if (listdata != null) return new Gson().fromJson(listdata.toString(), List.class);
        return null;
    }

}
