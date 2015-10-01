package com.nifty.cloud.mb.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 *  NCMBRole is used to retrieve and upload the role data
 */
public class NCMBRole extends NCMBBase{

    static final List<String> ignoreKeys = Arrays.asList(
            "objectId", "roleName",
            "belongRole", "belongUser", "acl",
            "createDate", "updateDate"
    );

    /**
     * Constructor
     */
    public NCMBRole() {
        super("role");
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Constructor with role name
     * @param name String role name
     */
    public NCMBRole(String name) {
        super("role");
        try {
            mFields.put("roleName", name);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Constructor from JSON
     * @param params source JSON
     * @throws NCMBException
     */
    NCMBRole(JSONObject params) throws NCMBException {
        super("role", params);
        mIgnoreKeys = ignoreKeys;
    }


    /**
     * Get role name
     * @return role name
     */
    public String getRoleName() {
        try {
            return mFields.getString("roleName");
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * set role name
     * @param name String role name
     */
    public void setRoleName(String name) {
        try {
            mFields.put("roleName", name);
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * add users to role
     * @param users NCMBUser list to add role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void addUser(List<NCMBUser> users) throws NCMBException{
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);

        try {
            JSONObject res = roleService.addUserRelations(getObjectId(), users);
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            mFields.put("updateDate", res.getString("updateDate"));
        } catch (NCMBException e) {
            throw e;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * add users to role asynchronously
     * @param users NCMBUser list to add role
     * @param callback callback after add user
     */
    public void addUserInBackground(List<NCMBUser> users, final DoneCallback callback) {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        try {
            roleService.addUserRelationsInBackground(getObjectId(), users, new ExecuteServiceCallback() {
                @Override
                public void done(JSONObject json, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        try {
                            mFields.put("updateDate", json.getString("updateDate"));
                            if (callback != null) {
                                callback.done(null);
                            }
                        } catch (JSONException e1) {
                            if (callback != null) {
                                callback.done(new NCMBException(NCMBException.GENERIC_ERROR, e1.getMessage()));
                            }
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * add users to role
     * @param roles NCMBRole list to add role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void addRole(List<NCMBRole> roles) throws NCMBException{
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);

        try {
            JSONObject res = roleService.addRoleRelations(getObjectId(), roles);
            mFields.put("updateDate", res.getString("updateDate"));
        } catch (NCMBException e) {
            throw e;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * add roles to role asynchronously
     * @param roles NCMBRoles list to add role
     * @param callback callback after add role
     */
    public void addRoleInBackground(List<NCMBRole> roles, final DoneCallback callback) {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        try {
            roleService.addRoleRelationsInBackground(getObjectId(), roles, new ExecuteServiceCallback() {
                @Override
                public void done(JSONObject json, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        try {
                            mFields.put("updateDate", json.getString("updateDate"));
                            if (callback != null) {
                                callback.done(null);
                            }
                        } catch (JSONException e1) {
                            if (callback != null) {
                                callback.done(new NCMBException(NCMBException.GENERIC_ERROR, e1.getMessage()));
                            }
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * create role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void createRole () throws NCMBException {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        JSONObject res = roleService.createRole(getRoleName());
        if (res != null) {
            mFields = res;
        }
    }

    /**
     * create role asynchronously
     * @param callback callback after create role
     */
    public void createRoleInBackground (final DoneCallback callback){
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        try {
            roleService.createRoleInBackground(getRoleName(), new ExecuteServiceCallback() {
                @Override
                public void done(JSONObject json, NCMBException e) {
                    if (e != null) {
                        if (callback != null) {
                            callback.done(e);
                        }
                    } else {
                        if (json != null) {
                            mFields = json;
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            if (callback != null) {
                callback.done(e);
            }
        }
    }

    /**
     * fetch role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void fetchObject() throws NCMBException {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        NCMBRole role = roleService.getRole(getObjectId());
        if (role != null) {
            mFields = role.mFields;
        }
    }

    /**
     * fetch role asynchronously
     * @param callback callback after fetch roles
     */
    public void fetchObjectInBackground (final DoneCallback callback) {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        try {
            roleService.getRoleInBackground(getObjectId(), new RoleCallback() {
                @Override
                public void done(NCMBRole role, NCMBException e) {
                    if (e != null) {
                        if (callback != null){
                            callback.done(e);
                        }
                    } else {
                        mFields = role.mFields;
                        if (callback != null) {
                            callback.done(null);
                        }
                    }
                }
            });
        } catch (NCMBException e) {
            if (callback != null){
                callback.done(e);
            }
        }
    }

    /**
     * delete role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void deleteObject () throws NCMBException {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        roleService.deleteRole(getObjectId());
        mFields = new JSONObject();
    }

    public void deleteObjectInBackground (final DoneCallback callback) throws NCMBException {
        NCMBRoleService roleService = (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
        roleService.deleteRoleInBackground(getObjectId(), new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    mFields = new JSONObject();
                    if (callback != null) {
                        callback.done(null);
                    }
                }
            }
        });
    }
}