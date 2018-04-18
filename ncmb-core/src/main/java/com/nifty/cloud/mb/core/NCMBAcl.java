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

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * NCMBAcl class contains acl setting for datastore , user , role
 */
public class NCMBAcl {
    /**
     * Inner class of representation for permission
     */
    public static class Permission {
        /** access permitted to read */
        public boolean readable = false;
        /** access permitted to write */
        public boolean writable = false;

        /**
         * Constructor
         */
        Permission() {
        }

        /**
         * Constructor
         * @param read read permission
         * @param write write permission
         */
        Permission(Boolean read, Boolean write) {
            readable = read;
            writable = write;
        }

        /**
         * Construct from JSON
         */
        Permission(JSONObject json) {
            readable = json.optBoolean("read", false);
            writable = json.optBoolean("write", false);
        }

        /**
         * Represent to JSON object
         * @return JSONObject JSON representation
         */
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                if (readable) {
                    json.put("read", true);
                }
                if (writable) {
                    json.put("write", true);
                }
            } catch (JSONException e) {
                // do nohting
            }
            return json;
        }

        /**
         * Represent to string
         * @return String string representation
         */
        public String toString() {
            return toJson().toString();
        }
    }

    /** public ACL */
    public static final String ACL_PUBLIC = "*";

    /** prefix of role */
    public static final String PREFIX_ROLE = "role:";

    /**
     * ACL for user/public
     */
    private Map<String, Permission> userAcl;

    /**
     * ACL for role
     */
    private Map<String, Permission> roleAcl;

    /**
     * Constructor
     */
    public NCMBAcl() {
        userAcl = new HashMap<String, Permission>();
        roleAcl = new HashMap<String, Permission>();
    }

    /**
     * Constructor from JSON
     * @param json json object of defalut acl
     * @throws JSONException exception from JSONObject
     */
    public NCMBAcl(JSONObject json) throws JSONException {
        this();
        parse(json);
    }

    /**
     * parse json from JSONObject
     * @param input input JSONObject
     * @throws JSONException exception from JSONObject
     */
    public void parse(JSONObject input) throws JSONException {
       for (Iterator<String> it = input.keys(); it.hasNext(); ) {
           String id = it.next();
           JSONObject json = input.getJSONObject(id);
           Permission perm = new Permission(json);

           if (id.startsWith(PREFIX_ROLE)) {
               String roleName = id.substring(PREFIX_ROLE.length());
               roleAcl.put(roleName, perm);
           } else {
               userAcl.put(id, perm);
           }
       }
    }

    /**
     * Convert to JSONObject
     * @return converted value
     * @throws JSONException exception from JSONException
     */
    public JSONObject toJson() throws JSONException {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Permission> uentry: userAcl.entrySet()) {
            result.put(uentry.getKey(), uentry.getValue().toJson());
        }

        for (Map.Entry<String, Permission> rentry: roleAcl.entrySet()) {
            String key = PREFIX_ROLE + rentry.getKey();
            result.put(key, rentry.getValue().toJson());
        }
        return result;
    }

    /**
     * Check ACL is empty or not
     * @return return true when acl is empty
     */
    public boolean isEmpty() {
        return userAcl.isEmpty() || roleAcl.isEmpty();
    }

    // Primitive acl methods

    /**
     * set user access permissions
     * @param userId user id
     * @param permission permission
     */
    public void setAccess(String userId, Permission permission) {
        userAcl.put(userId, permission);
    }

    /**
     * set role access permissions
     * @param roleName role name
     * @param permission permission
     */
    public void setRoleAccess(String roleName, Permission permission) {
        roleAcl.put(roleName, permission);
    }

    /**
     * get user access permissions
     * @param userId user id
     * @return permissions
     */
    public Permission getAccess(String userId) {
        Permission p = userAcl.get(userId);
        if (p == null) {
            p = new Permission();
            // not set permission to ACL
        }
        return p;
    }

    /**
     * get role access permissions
     * @param roleName role name
     * @return permissions
     */
    public Permission getRoleAccess(String roleName) {
        Permission p = roleAcl.get(roleName);
        if (p == null) {
            p = new Permission();
            // not set permission to ACL
        }
        return p;
    }

    /**
     * Remove permission for user
     * @param userId user id
     * @return success to remove permission
     */
    public boolean removePermission(String userId) {
        boolean result = false;
        if (userAcl.containsKey(userId)) {
            userAcl.remove(userId);
            result = true;
        }
        return result;
    }

    /**
     * Remove permisson for role
     * @param roleName role name
     * @return success to remove permission
     */
    public boolean removeRolePermission(String roleName) {
        boolean result = false;
        if (roleAcl.containsKey(roleName)) {
            roleAcl.remove(roleName);
            result = true;
        }
        return result;
    }

    // readable/writable shortcut methods

    /**
     * Get whether the given user id is allowed to read
     * @param userId user id
     * @return can read or not
     */
    public boolean getReadAccess(String userId) {
        return getAccess(userId).readable;
    }

    /**
     * Get whether the given user id is allowed to write
     * @param userId user id
     * @return can write or not
     */
    public boolean getWriteAccess(String userId) {
        return getAccess(userId).writable;
    }

    /**
     * Get whether the gevin role name is allowed to read
     * @param roleName role name
     * @return can read or not
     */
    public boolean getRoleReadAccess(String roleName) {
        return getRoleAccess(roleName).readable;
    }

    /**
     * Get whether the gevin role name is allowed to write
     * @param roleName role name
     * @return can write or not
     */
    public boolean getRoleWriteAccess(String roleName) {
        return getRoleAccess(roleName).writable;
    }

    /**
     * Set whether the given user id is allowed to read.
     * @param userId user id
     * @param allowed can read or not
     */
    public void setReadAccess(String userId, boolean allowed) {
        Permission p = getAccess(userId);
        if (p.readable == allowed) {
            // already set
            return;
        }
        p.readable = allowed;
        setAccess(userId, p);
    }

    /**
     * Set whether the given user is allowed to write.
     * @param userId user id
     * @param allowed can write or not
     */
    public void setWriteAccess(String userId, boolean allowed) {
        Permission p = getAccess(userId);
        if (p.writable == allowed) {
            // already set
            return;
        }
        p.writable = allowed;
        setAccess(userId, p);
    }

    /**
     * Set whether the given role is allowed to read.
     * @param roleName role name
     * @param allowed can reaad or not
     */
    public void setRoleReadAccess(String roleName, boolean allowed) {
        Permission p = getRoleAccess(roleName);
        if (p.readable == allowed) {
            // already set
            return;
        }
        p.readable = allowed;
        setRoleAccess(roleName, p);
    }

    /**
     * Set whether the given role is allowed to write.
     * @param roleName role name
     * @param allowed can write or not
     */
    public void setRoleWriteAccess(String roleName, boolean allowed) {
        Permission p = getRoleAccess(roleName);
        if (p.writable == allowed) {
            // already set
            return;
        }
        p.writable = allowed;
        setRoleAccess(roleName, p);
    }

    // public ACL shortcuts

    /**
     * Get whether the public is allowed to read.
     * @return can read or not
     */
    public boolean getPublicReadAccess() {
        return getReadAccess(ACL_PUBLIC);
    }

    /**
     * Get whether the public is allowed to write.
     * @return can write or not
     */
    public boolean getPublicWriteAccess() {
        return getWriteAccess(ACL_PUBLIC);
    }

    /**
     * Set whether the public is allowed to read.
     * @param allowed can read or not
     */
    public void setPublicReadAccess(boolean allowed) {
        setReadAccess(ACL_PUBLIC, allowed);
    }

    /**
     * Set whether the public is allowed to write.
     * @param allowed can write or not
     */
    public void setPublicWriteAccess(boolean allowed) {
        setWriteAccess(ACL_PUBLIC, allowed);
    }
}
