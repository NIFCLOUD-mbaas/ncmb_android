package com.nifty.cloud.mb.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for role api
 */
public class NCMBRoleService extends NCMBService {
    /** service path for API category */
    public static final String SERVICE_PATH = "roles";

    /** Status code of role created */
    public static final int HTTP_STATUS_ROLE_CREATED = 201;

    /** Status code of role created */
    public static final int HTTP_STATUS_ROLE_UPDATED = 200;

    /** Status code of role deleted */
    public static final int HTTP_STATUS_ROLE_DELETED = 200;

    /**
     * Inner class for callback
     */
    abstract class RoleServiceCallback extends ServiceCallback {
        /** constructors */
        RoleServiceCallback(NCMBRoleService service, ExecuteServiceCallback callback) {
            super((NCMBService)service, callback);
        }
        RoleServiceCallback(NCMBRoleService service, SearchRoleCallback callback) {
            super((NCMBService)service, callback);
        }
        RoleServiceCallback(NCMBRoleService service, DoneCallback callback) {
            super((NCMBService)service, callback);
        }
        RoleServiceCallback(NCMBRoleService service, RoleCallback callback) {
            super((NCMBService)service, callback);
        }

        protected NCMBRoleService getRoleService() {
            return (NCMBRoleService)mService;
        }

        /**
         * Check response in each casse, then throe exception when it's wrong
         * @param response response object
         * @throws NCMBException
         */
        public void checkResponse(NCMBResponse response) throws NCMBException {
            // do nothing in default
        }
     }

    /**
     * Constructor
     * @param context NCMBContext object for current context
     */
    NCMBRoleService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * Setup params to create role with gevin name
     * @param roleName role name
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams createRoleParams(String roleName) throws NCMBException {
        RequestParams reqParams = new RequestParams();

        reqParams.url = mContext.baseUrl + mServicePath;
        reqParams.type = NCMBRequest.HTTP_METHOD_POST;

        JSONObject params = new JSONObject();
        try {
            params.put("roleName", roleName);
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.MISSING_VALUE, "roleName required");
        }
        reqParams.content = params.toString();
        return reqParams;
    }

    /**
     * Check response to create role with gevin name
     * @param response
     * @throws NCMBException
     */
    protected void createRoleCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_ROLE_CREATED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Create role with given name
     * @param roleName role name
     * @return role Id
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public JSONObject createRole(String roleName) throws NCMBException {
        RequestParams reqParams = createRoleParams(roleName);
        NCMBResponse response = sendRequest(reqParams);
        createRoleCheckResponse(response);
        return response.responseData;
    }

    /**
     * Create role with gevin name in background
     * @param roleName role name
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void createRoleInBackground(String roleName, ExecuteServiceCallback callback) throws NCMBException {
        RequestParams reqParams = createRoleParams(roleName);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().createRoleCheckResponse(response);

                ExecuteServiceCallback executeServiceCallback = (ExecuteServiceCallback)mCallback;
                executeServiceCallback.done(response.responseData, null);
            }

            @Override
            public void handleError(NCMBException e) {
                ExecuteServiceCallback executeServiceCallback = (ExecuteServiceCallback)mCallback;
                executeServiceCallback.done(null, e);
            }
        });
    }

    /**
     * Setup params to delete role
     * @param roleId object id for role
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams deleteRoleParams(String roleId) throws NCMBException {
        RequestParams reqParams = new RequestParams();
        reqParams.url = mContext.baseUrl + mServicePath + "/" + roleId;
        reqParams.type = NCMBRequest.HTTP_METHOD_DELETE;
        return reqParams;
    }

    /**
     * Check response to delete role
     * @param response
     * @throws NCMBException
     */
    protected void deleteRoleCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_ROLE_DELETED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Delete role
     * @param roleId object id of role
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void deleteRole(String roleId) throws NCMBException {
        RequestParams reqParams = deleteRoleParams(roleId);
        NCMBResponse response = sendRequest(reqParams);
        deleteRoleCheckResponse(response);
        // delete completed, do noghing more
    }

    /**
     * Delete role in background
     * @param roleId object id of role
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void deleteRoleInBackground(String roleId, DoneCallback callback) throws NCMBException {
        RequestParams reqParams = deleteRoleParams(roleId);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().deleteRoleCheckResponse(response);

                DoneCallback doneCallback = (DoneCallback) mCallback;
                doneCallback.done(null);
            }

            @Override
            public void handleError(NCMBException e) {
                DoneCallback doneCallback = (DoneCallback) mCallback;
                doneCallback.done(e);
            }
        });
    }

    /**
     * Setup params to get role information
     * @param roleId role id
     * @return parameters in object
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public RequestParams getRoleParams(String roleId) throws NCMBException {
        RequestParams reqParams = new RequestParams();
        reqParams.url = mContext.baseUrl + mServicePath + "/" + roleId;
        reqParams.type = NCMBRequest.HTTP_METHOD_GET;
        return reqParams;
    }

    /**
     * Check response to get role information
     * @param response
     * @throws NCMBException
     */
    protected void getRoleCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Get role information
     * @param roleId role id
     * @return role object
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBRole getRole(String roleId) throws NCMBException {
        RequestParams reqParams = getRoleParams(roleId);
        NCMBResponse response = sendRequest(reqParams);
        getRoleCheckResponse(response);
        JSONObject result = response.responseData;
        return new NCMBRole(result);
    }

    /**
     * Get role information in background
     * @param roleId role id
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void getRoleInBackground(String roleId, RoleCallback callback) throws NCMBException {
        RequestParams reqParams = getRoleParams(roleId);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().getRoleCheckResponse(response);
                NCMBRole role = new NCMBRole(response.responseData);

                RoleCallback roleCallback = (RoleCallback)mCallback;
                roleCallback.done(role, null);
            }

            @Override
            public void handleError(NCMBException e) {
                RoleCallback roleCallback = (RoleCallback)mCallback;
                roleCallback.done(null, e);
            }
        });
    }

    /**
     * Search role synchronously
     * @param conditions conditions for role search
     * @return result list of role search
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public List<NCMBRole> searchRole (JSONObject conditions) throws NCMBException{

        String url = mContext.baseUrl + mServicePath;
        String type = NCMBRequest.HTTP_METHOD_GET;
        NCMBResponse response = sendRequest(url, type, null, conditions);
        if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }

        return createSearchResults(response.responseData);
    }

    /**
     * Search role asynchronously
     * @param conditions conditions for role search
     * @param callback Callback is executed after search role
     */
    public void searchRoleInBackground (JSONObject conditions, SearchRoleCallback callback) {

        String url = mContext.baseUrl + mServicePath;
        String type = NCMBRequest.HTTP_METHOD_GET;
        RequestParams reqParams = new RequestParams();
        reqParams.url = url;
        reqParams.type = type;
        reqParams.query = conditions;
        try {
            sendRequestAsync(reqParams, new RoleServiceCallback(this, callback){
                @Override
                public void handleResponse(NCMBResponse response) {

                    SearchRoleCallback callback = (SearchRoleCallback) mCallback;
                    if (callback != null) {
                        try {
                            callback.done(createSearchResults(response.responseData), null);
                        } catch (NCMBException callbackException) {
                            callback.done(null, callbackException);
                        }

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



    ArrayList<NCMBRole> createSearchResults(JSONObject responseData) throws NCMBException {
        try {
            JSONArray results = responseData.getJSONArray("results");
            ArrayList<NCMBRole> array = new ArrayList<>();
            for (int i = 0; i < results.length(); ++i) {
                NCMBRole role = new NCMBRole(results.getJSONObject(i));
                array.add(role);
            }
            return array;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.");
        }
    }

    /**
     * Build relation query as JSONObject
     * @param type "user" or "role"
     * @param roleId role id
     * @return query JSON
     * @throws JSONException exception from JSONObject
     */
    protected JSONObject relationQueryJson(String type, String roleId) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("__type", "Pointer");
        object.put("className", "role");
        object.put("objectId", roleId);

        JSONObject relatedTo = new JSONObject();
        relatedTo.put("object", object);
        String key = (type.equals("role"))? "belongRole": "belongUser";
        relatedTo.put("key", key);

        JSONObject where = new JSONObject();
        where.put("$relatedTo", relatedTo);

        JSONObject query = new JSONObject();
        query.put("where", where.toString());
        return query;
    }

    /**
     * Get belong users with gevin role id
     * @param roleId role id
     * @return belong user ids
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public ArrayList<String> findBelongUser(String roleId) throws NCMBException {
        String url = mContext.baseUrl + mServicePath;
        String type = NCMBRequest.HTTP_METHOD_GET;

        try {
            JSONObject query = relationQueryJson("user", roleId);
            NCMBResponse response = sendRequest(url, type, null, query);

            if (response.statusCode != NCMBResponse.HTTP_STATUS_OK) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
            }
            // NOT IMPLEMENTED YET
            // check return format

            return null;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid JSON");
        }
    }


    /**
     * Setup params to add users to role
     * @param roleId role id
     * @param users users added
     * @return parameters in object
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    protected RequestParams addUserRelationsParams(String roleId, List<NCMBUser> users) throws NCMBException {
        try {
            RequestParams reqParams = new RequestParams();

            reqParams.url = mContext.baseUrl + mServicePath + "/" + roleId;
            reqParams.type = NCMBRequest.HTTP_METHOD_PUT;

            JSONArray objects = new JSONArray();
            List<NCMBObject> objectList = new ArrayList<>();
            for (NCMBUser user : users) {
                NCMBRelation.addUser(objects, user.getObjectId());
                objectList.add(user);
            }
            JSONObject params = new JSONObject();
            params.put("belongUser", NCMBRelation.addRelation(objectList));
            reqParams.content = params.toString();

            return reqParams;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "JSON error");
        }
    }

    /**
     * Check response to add users to role
     * @param response
     * @throws NCMBException
     */
    protected void addUserRelationsCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_ROLE_UPDATED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Add users to role
     * @param roleId role id
     * @param users users added
     * @return result of add user to relations
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public JSONObject addUserRelations(String roleId, List<NCMBUser> users) throws NCMBException {
        RequestParams reqParams = addUserRelationsParams(roleId, users);
        NCMBResponse response = sendRequest(reqParams);
        addUserRelationsCheckResponse(response);
        return response.responseData;
        // update completed, do nothing more
    }

    /**
     * Add users to role in background
     * @param roleId role id
     * @param users users added
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void addUserRelationsInBackground(String roleId, List<NCMBUser> users,
                                             ExecuteServiceCallback callback) throws NCMBException {
        RequestParams reqParams = addUserRelationsParams(roleId, users);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().addUserRelationsCheckResponse(response);

                ExecuteServiceCallback doneCallback = (ExecuteServiceCallback)mCallback;
                doneCallback.done(response.responseData, null);
            }

            @Override
            public void handleError(NCMBException e) {
                ExecuteServiceCallback doneCallback = (ExecuteServiceCallback)mCallback;
                doneCallback.done(null, e);
            }
        });
    }

    /**
     * Setup params to add roles to role
     * @param roleId role id
     * @param roles roles added
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams addRoleRelationParams(String roleId, List<NCMBRole> roles) throws NCMBException {
        try {
            RequestParams reqParams = new RequestParams();
            reqParams.url = mContext.baseUrl + mServicePath + "/" + roleId;
            reqParams.type = NCMBRequest.HTTP_METHOD_PUT;

            JSONArray objects = new JSONArray();
            List<NCMBObject> roleArray = new ArrayList<>();
            for (NCMBRole role : roles) {
                //NCMBRelation.addRole(objects, role.getObjectId());
                roleArray.add(new NCMBObject("role", role.mFields));
            }
            JSONObject params = new JSONObject();
            params.put("belongRole", NCMBRelation.addRelation(roleArray));
            reqParams.content = params.toString();

            return reqParams;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "JSON error");
        }
    }

    /**
     * Check response to add roles to role
     * @param response
     * @throws NCMBException
     */
    protected void addRoleRelationsCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_ROLE_UPDATED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Add roles to role
     * @param roleId role id
     * @param roles roles added
     * @return result of add role to relations
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public JSONObject addRoleRelations(String roleId, List<NCMBRole> roles) throws NCMBException {
        RequestParams reqParams = addRoleRelationParams(roleId, roles);
        NCMBResponse response = sendRequest(reqParams);
        addRoleRelationsCheckResponse(response);
        return response.responseData;
        // update completed, do nothing more
    }

    /**
     * Add roles to role in background
     * @param roleId role id
     * @param roles roles added
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void addRoleRelationsInBackground(String roleId, List<NCMBRole> roles,
                                             ExecuteServiceCallback callback) throws NCMBException {
        RequestParams reqParams = addRoleRelationParams(roleId, roles);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().addRoleRelationsCheckResponse(response);

                ExecuteServiceCallback doneCallback = (ExecuteServiceCallback)mCallback;
                doneCallback.done(response.responseData, null);
            }

            @Override
            public void handleError(NCMBException e) {
                ExecuteServiceCallback doneCallback = (ExecuteServiceCallback)mCallback;
                doneCallback.done(null, e);
            }
        });
    }

    /**
     * Setup params to set ACL to role
     * @param roleId role id
     * @param acl Assiend ACL
     * @return parameters in object
     * @throws NCMBException
     */
    protected RequestParams setAclParams(String roleId, NCMBAcl acl) throws NCMBException {
        try {
            RequestParams reqParams = new RequestParams();
            reqParams.url = mContext.baseUrl + mServicePath + "/" + roleId;
            reqParams.type = NCMBRequest.HTTP_METHOD_PUT;

            JSONObject params = new JSONObject();
            params.put("acl", acl.toJson());
            reqParams.content = params.toString();

            return reqParams;
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "JSON error");
        }
    }

    /**
     * Check response to set ACL to role
     * @param response
     * @throws NCMBException
     */
    protected void setAclCheckResponse(NCMBResponse response) throws NCMBException {
        if (response.statusCode != HTTP_STATUS_ROLE_UPDATED) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, "Invalid status code");
        }
    }

    /**
     * Set ACL to role
     * @param roleId role id
     * @param acl Assigned ACL
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void setAcl(String roleId, NCMBAcl acl) throws NCMBException {
        RequestParams reqParams = setAclParams(roleId, acl);
        NCMBResponse response = sendRequest(reqParams);
        setAclCheckResponse(response);
        // update completed, do nothing more
    }

    /**
     * Set ACL to role in background
     * @param roleId role id
     * @param acl Assigned ACL
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public void setAclInBackground(String roleId, NCMBAcl acl, DoneCallback callback) throws NCMBException {
        RequestParams reqParams = setAclParams(roleId, acl);
        sendRequestAsync(reqParams, new RoleServiceCallback(this, callback) {
            @Override
            public void handleResponse(NCMBResponse response) throws NCMBException {
                getRoleService().setAclCheckResponse(response);
                DoneCallback doneCallback = (DoneCallback)mCallback;
                doneCallback.done(null);
            }

            @Override
            public void handleError(NCMBException e) {
                DoneCallback doneCallback = (DoneCallback)mCallback;
                doneCallback.done(e);
            }
        });
    }
}
