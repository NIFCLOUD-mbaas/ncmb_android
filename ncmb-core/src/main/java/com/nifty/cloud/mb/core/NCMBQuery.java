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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * NCMBQuery is used to search data from NIF Cloud mobile backend
 */
public class NCMBQuery<T extends NCMBBase> {
    private String mClassName;

    private JSONObject mWhereConditions = new JSONObject();
    private int limitNumber = 0;
    private int skipNumber = 0;
    private String includeKey = "";
    private List<String> order = new ArrayList<>();
    private boolean countFlag = false;

    /**
     * Constructor
     * @param className class name string for search data
     */
    public NCMBQuery(String className) {
        mClassName = className;
        mWhereConditions = new JSONObject();
    }

    /**
     * search data from NIF Cloud mobile backend
     * @return NCMBObject(include extend class) list of search result
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public List<T> find () throws NCMBException {
        if (mClassName.equals("user")) {
            NCMBUserService userServ = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            return (List<T>) userServ.searchUser(getConditions());
        } else if (mClassName.equals("role")) {
            NCMBRoleService roleServ = (NCMBRoleService) NCMB.factory(NCMB.ServiceType.ROLE);
            return (List<T>) roleServ.searchRole(getConditions());
        } else if (mClassName.equals("push")) {
            NCMBPushService pushServ = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
            return (List<T>) pushServ.searchPush(getConditions());
        } else if (mClassName.equals("installation")){
            NCMBInstallationService installationServ = (NCMBInstallationService)NCMB.factory(NCMB.ServiceType.INSTALLATION);
            return (List<T>)installationServ.searchInstallation(getConditions());
        } else if (mClassName.equals("file")){
            NCMBFileService fileServ = (NCMBFileService)NCMB.factory(NCMB.ServiceType.FILE);
            return (List<T>)fileServ.searchFile(getConditions());
        } else {
            NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
            return objServ.searchObject(mClassName, getConditions());
        }

    }

    /**
     * search data from NIF Cloud mobile backend asynchronously
     * @param callback executed callback after data search
     */
    public void findInBackground (final FindCallback<T> callback) {
        if (mClassName.equals("user")) {
            NCMBUserService userServ = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            userServ.searchUserInBackground(getConditions(), new SearchUserCallback() {
                @Override
                public void done(ArrayList<NCMBUser> users, NCMBException e) {
                    callback.done((List<T>) users, e);
                }
            });
        } else if (mClassName.equals("role")) {
            NCMBRoleService roleServ = (NCMBRoleService) NCMB.factory(NCMB.ServiceType.ROLE);
            roleServ.searchRoleInBackground(getConditions(), new SearchRoleCallback() {
                @Override
                public void done(ArrayList<NCMBRole> users, NCMBException e) {
                    callback.done((List<T>) users, e);
                }
            });
        } else if (mClassName.equals("push")) {
            NCMBPushService pushServ = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
            pushServ.searchPushInBackground(getConditions(), new SearchPushCallback() {
                @Override
                public void done(ArrayList<NCMBPush> users, NCMBException e) {
                    callback.done((List<T>) users, e);
                }
            });
        } else if (mClassName.equals("installation")) {
            NCMBInstallationService installationServ = (NCMBInstallationService)NCMB.factory(NCMB.ServiceType.INSTALLATION);
            installationServ.searchInstallationInBackground(getConditions(), new SearchInstallationCallback () {
                @Override
                public void done(ArrayList<NCMBInstallation> users, NCMBException e) {
                    callback.done((List<T>) users, e);
                }
            });
        } else if (mClassName.equals("file")) {
            NCMBFileService fileServ = (NCMBFileService)NCMB.factory(NCMB.ServiceType.FILE);
            fileServ.searchFileInBackground(getConditions(), new SearchFileCallback() {
                @Override
                public void done(List<NCMBFile> files, NCMBException e) {
                    callback.done((List<T>) files, e);
                }
            });
        }else {
            NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
            objServ.searchObjectInBackground(mClassName, getConditions(), new SearchObjectCallback() {
                @Override
                public void done(List<NCMBObject> objects, NCMBException e) {
                    callback.done((List<T>) objects, e);
                }
            });
        }
    }

    /**
     * get current search condition
     * @return current search condition
     */
    public JSONObject getConditions() {
        JSONObject conditions = new JSONObject();
        try {
            if (mWhereConditions != null && mWhereConditions.length() > 0){
                conditions.put("where", mWhereConditions);
            }
            if (limitNumber != 0) {
                conditions.put("limit", limitNumber);
            }
            if (skipNumber != 0) {
                conditions.put("skip", skipNumber);
            }
            if (includeKey != null && !includeKey.isEmpty()) {
                conditions.put("include", includeKey);
            }
            if (order != null && order.size() != 0) {
                String orderString = "";
                Iterator iterator = order.iterator();
                while(iterator.hasNext()) {
                    orderString = orderString + iterator.next() + ",";
                }

                conditions.put("order", orderString.replaceAll(",$", ""));
            }
            if (countFlag) {
                conditions.put("count", 1);
            }
            return conditions;
        } catch (JSONException e) {
            return null;
        }

    }

    private Object convertConditionValue(Object value) throws JSONException{
        if (value instanceof Date){
            JSONObject dateJson = new JSONObject("{'__type':'Date'}");
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            dateJson.put("iso", df.format((Date) value));
            return dateJson;
        } else if (value instanceof Location) {
            JSONObject locationJson = new JSONObject("{'__type':'GeoPoint'}");
            locationJson.put("latitude", ((Location) value).getLatitude());
            locationJson.put("longitude", ((Location) value).getLongitude());
            return locationJson;
        } else if (value instanceof List) {
            Gson gson = new Gson();
            return new JSONArray(gson.toJson(value));
        }else if (value instanceof Map) {
            Gson gson = new Gson();
            return new JSONObject(gson.toJson(value));
        } else {
            return value;
        }
    }

    /**
     * set the conditions to search the data that matches the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereEqualTo(String key, Object value){
        try {
            mWhereConditions.put(key, convertConditionValue(value));
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that not matches the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereNotEqualTo(String key, Object value){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$ne", convertConditionValue(value));
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that less than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereLessThan(String key, Object value) {
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$lt", convertConditionValue(value));
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that greater than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereGreaterThan(String key, Object value) {
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$gt", convertConditionValue(value));
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that less than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereLessThanOrEqualTo(String key, Object value) {
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$lte", convertConditionValue(value));
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that greater than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    public void whereGreaterThanOrEqualTo(String key, Object value) {
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$gte", convertConditionValue(value));
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    public void whereContainedIn(String key, Collection<? extends Object> objects){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            JSONArray array = new JSONArray();
            for (Object value : objects) {
                array.put(convertConditionValue(value));
            }
            newCondition.put("$in", array);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that not contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    public void whereNotContainedIn(String key, Collection<? extends Object> objects){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            JSONArray array = new JSONArray();
            for (Object value : objects) {
                array.put(convertConditionValue(value));
            }
            newCondition.put("$nin", array);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that specified key exists
     * @param key field name to set the conditions
     */
    public void whereExists(String key){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$exists", true);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that specified key exists
     * @param key field name to set the conditions
     */
    public void whereDoesNotExists(String key){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$exists", false);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions
     * @param elements condition elements in the specified key array
     */
    public void whereContainedInArray(String key, Collection<? extends Object> elements){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            JSONArray array = new JSONArray();
            for (Object value : elements) {
                array.put(convertConditionValue(value));
            }
            newCondition.put("$inArray", array);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions
     * @param elements condition elements in the specified key array
     */
    public void whereNotContainedInArray(String key, Collection<? extends Object> elements){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            JSONArray array = new JSONArray();
            for (Object value : elements) {
                array.put(convertConditionValue(value));
            }
            newCondition.put("$ninArray", array);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that contains all elements of array in the specified key
     * @param key field name to set the conditions
     * @param elements condition elements in the specified key array
     */
    public void whereContainsAll(String key, Collection<? extends Object> elements){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            JSONArray array = new JSONArray();
            for (Object value : elements) {
                array.put(convertConditionValue(value));
            }
            newCondition.put("$all", array);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that matches any one of the given query
     * all queries must be same class name
     * @param queries condition queries
     */
    public void or(Collection<NCMBQuery> queries){
        try {
            JSONArray array = new JSONArray();
            for (NCMBQuery query : queries) {
                JSONObject queryJson = query.mWhereConditions;
                array.put(queryJson);
            }
            mWhereConditions.put("$or", array);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    /**
     * set the conditions to search the data that matches inQuery condition and inQueryKey value of the specified key
     * @param key field name to set conditions
     * @param inQueryKey condition field name for inQuery
     * @param inQuery condition for Subquery
     */
    public void whereMatchesKeyInQuery(String key, String inQueryKey, NCMBQuery inQuery){
        try {
            JSONObject inQueryJson = new JSONObject();
            inQueryJson.put("where",inQuery.mWhereConditions);
            inQueryJson.put("className", inQuery.mClassName);

            JSONObject selectJson = new JSONObject();
            selectJson.put("query", inQueryJson);
            selectJson.put("key", inQueryKey);

            JSONObject newCondition = new JSONObject();
            newCondition.put("$select", selectJson);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that has  pointer that match inQuery condition object
     * @param key field name that has pointer for inQuery conditions
     * @param inQuery condition for Subquery
     */
    public void whereMatchesQuery(String key, NCMBQuery inQuery){
        try {
            JSONObject inQueryJson = new JSONObject();
            inQueryJson.put("where",inQuery.mWhereConditions);
            inQueryJson.put("className", inQuery.mClassName);

            JSONObject newCondition = new JSONObject();
            newCondition.put("$inQuery", inQueryJson);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data that related parent object in specified key
     * @param parent parent NCMBObject(include subclass)
     * @param key field name that has relation to search class data
     */
    public void whereRelatedTo(NCMBObject parent, String key){
        try {
            JSONObject parentJson = new JSONObject();
            parentJson.put("__type","Pointer");
            parentJson.put("className", parent.getClassName());
            parentJson.put("objectId", parent.getObjectId());

            JSONObject newCondition = new JSONObject();
            newCondition.put("object", parentJson);
            newCondition.put("key", key);
            mWhereConditions.put("$relatedTo", newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data with location information
     * @param key field name that contains location information
     * @param southwest lower left location information for search area
     * @param northeast Upper right location information for search area
     */
    public void whereWithinGeoBox(String key, Location southwest, Location northeast){
        try {

            JSONArray boxArray = new JSONArray();
            boxArray.put(convertConditionValue(southwest));
            boxArray.put(convertConditionValue(northeast));

            JSONObject boxJson = new JSONObject();
            boxJson.put("$box", boxArray);

            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$within", boxJson);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data with location information
     * @param key field name that contains location information
     * @param center center location for data searching
     * @param distance search radius distance from center point in kilometers
     */
    public void whereWithinKilometers(String key, Location center, double distance){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$nearSphere", convertConditionValue(center));
            newCondition.put("$maxDistanceInKilometers", distance);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data with location information
     * @param key field name that contains location information
     * @param center center location for data searching
     * @param distance search radius distance from center point in miles
     */
    public void whereWithinMiles(String key, Location center, int distance){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$nearSphere", convertConditionValue(center));
            newCondition.put("$maxDistanceInMiles", distance);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the conditions to search the data with location information
     * @param key field name that contains location information
     * @param center center location for data searching
     * @param distance search radius distance from center point in radians
     */
    public void whereWithinRadians(String key, Location center, int distance){
        try {
            JSONObject newCondition = new JSONObject();
            if (mWhereConditions.has(key)){
                Object currentCondition = mWhereConditions.get(key);
                if (currentCondition instanceof JSONObject) {
                    newCondition = (JSONObject)currentCondition;
                }
            }
            newCondition.put("$nearSphere", convertConditionValue(center));
            newCondition.put("$maxDistanceInRadians", distance);
            mWhereConditions.put(key, newCondition);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * set the number of acquisition of search results
     * @param number number of acquisition (0 ~ 1000)
     */
    public void setLimit (int number) {
        limitNumber = number;
    }

    /**
     * set the number to skip the search results
     * @param number number for skipping
     */
    public void setSkip (int number) {
        skipNumber = number;
    }

    /**
     * set to include nested Object of the specified key in the search results
     * @param key key with pointer to a nested object
     */
    public void setIncludeKey (String key) {
        if (key != null && !key.isEmpty()) {
            includeKey = key;
        }
    }

    /**
     * add Order by ascending with specified key
     * Search Results is sorted in order which key was added.
     * @param key key for order by ascending
     */
    public void addOrderByAscending (String key) {
        if (key != null && !key.isEmpty()) {
            order.add(key);
        }
    }

    /**
     * add Order by descending with specified key
     * @param key key for order by descending
     */
    public void addOrderByDescending (String key) {
        if (key != null && !key.isEmpty()) {
            order.add("-" + key);
        }
    }

    /**
     * remove the specified key sort conditions
     * @param key key for remove sort conditions
     */
    public void deleteOrder (String key) {
        String descendingKey = "-" + key;
        if (order.contains(key)) {
            order.remove(key);
        } else if (order.contains(descendingKey)) {
            order.remove(descendingKey);
        }
    }

    /**
     * return the number of search results
     * @return number of search results
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public int count () throws NCMBException {
        countFlag = true;
        limitNumber = 1;
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        return objServ.countObject(mClassName, getConditions());
    }

    /**
     * return number of search results asynchronously
     * @param callback callback for after object search and count results
     */
    public void countInBackground(CountCallback callback) {
        countFlag = true;
        limitNumber = 1;
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.countObjectInBackground(mClassName, getConditions(), callback);
    }

}
