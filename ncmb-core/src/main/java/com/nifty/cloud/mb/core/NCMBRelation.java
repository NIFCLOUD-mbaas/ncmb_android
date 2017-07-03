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

import java.util.Iterator;
import java.util.List;

/**
 * Utility for relation of ojbjects
 */
public class NCMBRelation {
    /** key ob operation */
    public static final String KEY_OP = "__op";

    /** operation AddRelation */
    public static final String OP_ADD_RELATION = "AddRelation";

    /** operation RemoveRelation */
    public static final String OP_REMOVE_RELATION = "RemoveRelation";

    /** type of Pointer */
    public static final String VAL_TYPE_POINTER = "Pointer";

    /** className of user */
    public static final String VAL_CLASS_USER = "user";

    /** className of role */
    public static final String VAL_CLASS_ROLE = "role";

    /**
     * Add user to relation array
     * @param objects relation objects array
     * @param userId user id
     * @return relatoin objects array
     * @throws JSONException exception from JSONObject
     */
    public static JSONArray addUser(JSONArray objects, String userId) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("type", VAL_TYPE_POINTER);
        obj.put("className", VAL_CLASS_USER);
        obj.put("objectId", userId);

        objects.put(obj);
        return objects;
    }

    /**
     * Add role to relation array
     * @param objects relation objects array
     * @param roleId role id
     * @return relation objects array
     * @throws JSONException exception from JSONObject
     */
    public static JSONArray addRole(JSONArray objects, String roleId) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("type", VAL_TYPE_POINTER);
        obj.put("className", VAL_CLASS_ROLE);
        obj.put("objectId", roleId);

        objects.put(obj);
        return objects;
    }

    /**
     * Create JSONObject to add relation
     * @param objects relation objects array
     * @return object contains relation objects
     * @throws JSONException exception from JSONObject
     */
    public static JSONObject addRelation(List<NCMBObject> objects) throws JSONException {
        JSONArray relationArray = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(KEY_OP, OP_ADD_RELATION);
        for(Iterator<NCMBObject> iterator = objects.iterator(); iterator.hasNext();) {
            NCMBObject pointerObj = iterator.next();
            JSONObject pointer = new JSONObject("{\"__type\":\"Pointer\"}");
            pointer.put("className", pointerObj.getClassName());
            pointer.put("objectId", pointerObj.getObjectId());
            relationArray.put(pointer);
        }
        obj.put("objects", relationArray);
        return obj;
    }

    /**
     * Create JSONObject to remove relation
     * @param objects relation objects array
     * @return objects contains relation objects
     * @throws JSONException exception from JSONObject
     */
    public static JSONObject removeRelation(List<NCMBObject> objects) throws JSONException {
        JSONArray relationArray = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(KEY_OP, OP_REMOVE_RELATION);
        for(Iterator<NCMBObject> iterator = objects.iterator(); iterator.hasNext();) {
            NCMBObject pointerObj = iterator.next();
            JSONObject pointer = new JSONObject("{\"__type\":\"Pointer\"}");
            pointer.put("className", pointerObj.getClassName());
            pointer.put("objectId", pointerObj.getObjectId());
            relationArray.put(pointer);
        }
        obj.put("objects", relationArray);
        return obj;
    }
}
