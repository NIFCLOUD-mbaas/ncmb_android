package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayList;


@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBRoleServiceTest {

    private MockWebServer mServer;
    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {

        //setup mocServer
        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
        String mocServerUrl = mServer.getUrl("/").toString();

        //initialization
        NCMB.initialize(RuntimeEnvironment.application,
                "appKey",
                "clientKKey",
                mocServerUrl,
                null);

        ShadowLog.stream = System.out;

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @After
    public void teardown() {
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    protected NCMBRoleService getRoleService() {
        return (NCMBRoleService) NCMB.factory(NCMB.ServiceType.ROLE);
    }

    /**
     * - 内容：createRoleが成功する事を確認する
     * - 結果：objectIdが正しく作成されること
     */
    @Test
    public void createRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleName = "dummyRoleName";

        JSONObject json = roleService.createRole(roleName);
        Assert.assertEquals("dummyObjectId", json.getString("objectId"));
    }

    /**
     * - 内容：createRoleInBackground が成功する事を確認する
     * - 結果：objectIdが正しく作成されること
     */
    @Test
    public void createRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleName = "dummyRoleName";

        roleService.createRoleInBackground(roleName, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                Assert.assertEquals(e, null);
                try {
                    Assert.assertEquals("dummyObjectId", json.getString("objectId"));
                } catch (JSONException e1) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：deleteRole が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void deleteRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            roleService.deleteRole(roleId);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：deleteRoleInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void deleteRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        roleService.deleteRoleInBackground(roleId, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：fetchRole が成功する事を確認する
     * - 結果：正しく作成された NCMBRole オブジェクトが返ること
     */
    @Test
    public void fetchRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        NCMBRole role = roleService.fetchRole(roleId);
        Assert.assertEquals(role.getObjectId(), roleId);
    }

    /**
     * - 内容：fetchRoleInBackground が成功する事を確認する
     * - 結果：callback に正しく作成された NCMBRole オブジェクトが返ること
     */
    @Test
    public void fetchRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        final String roleId = "dummyRoleId";

        roleService.fetchRoleInBackground(roleId, new FetchCallback<NCMBRole>() {
            @Override
            public void done(NCMBRole role, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals(role.getObjectId(), roleId);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    protected ArrayList<NCMBUser> generateUsers(int count) throws JSONException, NCMBException {
        JSONObject userJson = new JSONObject();
        userJson.put("userName", "dummyUserName");
        userJson.put("createDate", "2015-10-10T00:00:01.000Z");

        ArrayList<NCMBUser> users = new ArrayList<NCMBUser>();
        for (int i = 1; i <= count; ++i) {
            String objectId = "dummyUserObjectId" + String.valueOf(i);
            userJson.put("objectId", objectId);

            NCMBUser user = new NCMBUser(userJson);
            users.add(user);
        }
        return users;
    }

    /**
     * - 内容：addUserRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void addUserRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numUsers = 2;
            ArrayList<NCMBUser> users = generateUsers(numUsers);
            JSONObject response = roleService.addUserRelations(roleId, users);
            Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：addUserRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void addUserRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numUsers = 2;
        ArrayList<NCMBUser> users = generateUsers(numUsers);

        roleService.addUserRelationsInBackground(roleId, users, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.fail(error.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：removeUserRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void removeUserRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numUsers = 2;
            ArrayList<NCMBUser> users = generateUsers(numUsers);
            JSONObject response = roleService.removeUserRelations(roleId, users);
            Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：removeUserRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void removeUserRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numUsers = 2;
        ArrayList<NCMBUser> users = generateUsers(numUsers);

        roleService.removeUserRelationsInBackground(roleId, users, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.fail(error.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    protected ArrayList<NCMBRole> generateRoles(int count) throws JSONException, NCMBException {
        JSONObject roleJson = new JSONObject();
        roleJson.put("roleName", "dummyUserName");
        roleJson.put("createDate", "2015-10-10T00:00:01.000Z");

        ArrayList<NCMBRole> roles = new ArrayList<NCMBRole>();
        for (int i = 1; i <= count; ++i) {
            String objectId = "dummyRoleObjectId" + String.valueOf(i);
            roleJson.put("objectId", objectId);

            NCMBRole role = new NCMBRole(roleJson);
            roles.add(role);
        }
        return roles;
    }

    /**
     * - 内容：addRoleRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void addRoleRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numRoles = 2;
            ArrayList<NCMBRole> roles = generateRoles(numRoles);
            JSONObject response = roleService.addRoleRelations(roleId, roles);
            Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：addRoleRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void addRoleRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numRoles = 2;
        ArrayList<NCMBRole> roles = generateRoles(numRoles);

        roleService.addRoleRelationsInBackground(roleId, roles, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.fail(error.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：removeRoleRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void removeRoleRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numRoles = 2;
            ArrayList<NCMBRole> roles = generateRoles(numRoles);
            JSONObject response = roleService.removeRoleRelations(roleId, roles);
            Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：removeRoleRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void removeRoleRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numRoles = 2;
        ArrayList<NCMBRole> roles = generateRoles(numRoles);

        roleService.removeRoleRelationsInBackground(roleId, roles, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.fail(error.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    protected NCMBAcl generateAcl() {
        String userId = "dummyRwUserId";
        String roleId = "dummyRwRoleId";

        NCMBAcl acl = new NCMBAcl();
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        acl.setRoleReadAccess(roleId, true);
        acl.setRoleWriteAccess(roleId, true);

        return acl;
    }

    /**
     * - 内容：setAcl が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void setAcl() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            NCMBAcl acl = generateAcl();
            roleService.setAcl(roleId, acl);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：setAclInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void setAclInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        NCMBAcl acl = generateAcl();
        roleService.setAclInBackground(roleId, acl, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：searchRole が成功する事を確認する
     * - 結果：result に NCMBRole が正しく格納されていること
     */
    @Test
    public void searchRole() throws Exception {
        NCMBQuery<NCMBRole> query = new NCMBQuery<>("role");
        query.whereEqualTo("roleName", "testRole");

        NCMBRoleService roleService = getRoleService();

        ArrayList<NCMBRole> result = roleService.searchRole(query.getConditions());
        Assert.assertEquals(result.size(), 1);

        NCMBRole role = result.get(0);
        Assert.assertEquals(role.getObjectId(), "oc8bJVuEWmKgNydn");
        Assert.assertEquals(role.getRoleName(), "testRole");
    }

    /**
     * - 内容：searchRoleInBackground が全件取得で成功する事を確認する
     * - 結果：result に NCMBRole が正しく格納されていること
     */
    @Test
    public void searchRoleInBackground() throws Exception {
        NCMBQuery<NCMBRole> query = new NCMBQuery<>("role");
        query.whereEqualTo("roleName", "testRole");

        NCMBRoleService roleService = getRoleService();
        roleService.searchRoleInBackground(query.getConditions(), new SearchRoleCallback() {
            @Override
            public void done(ArrayList<NCMBRole> result, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals(result.size(), 1);

                NCMBRole role = result.get(0);
                Assert.assertEquals(role.getObjectId(), "oc8bJVuEWmKgNydn");
                Assert.assertEquals(role.getRoleName(), "testRole");
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }
}
