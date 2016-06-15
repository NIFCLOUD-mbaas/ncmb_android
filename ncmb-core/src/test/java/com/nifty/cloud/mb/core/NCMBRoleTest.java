package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * NCMBRoleTest class
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBRoleTest {
    private MockWebServer mServer;
    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {

        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();

        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey",
                mServer.getUrl("/").toString(),
                null);

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @After
    public void teardown() {

    }

    /**
     * TestCase
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createRole() throws Exception {
        NCMBRole role = new NCMBRole("dummyRoleName");
        try {
            role.createRole();
            Assert.assertEquals("dummyObjectId", role.getObjectId());

        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void createRoleInBackground() throws Exception {
        NCMBRole role = new NCMBRole();
        role.setRoleName("dummyRoleName");
        role.createRoleInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertEquals("dummyObjectId", role.getObjectId());

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void addUser() throws Exception {
        NCMBUser user1 = new NCMBUser();
        user1.setObjectId("dummyUserObjectId1");

        NCMBUser user2 = new NCMBUser();
        user2.setObjectId("dummyUserObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.addUser(Arrays.asList(user1, user2));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
    }

    @Test
    public void addUserInBackground() throws Exception {
        NCMBUser user1 = new NCMBUser();
        user1.setObjectId("dummyUserObjectId1");

        NCMBUser user2 = new NCMBUser();
        user2.setObjectId("dummyUserObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.addUserInBackground(Arrays.asList(user1, user2), new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void removeUser() throws Exception {
        NCMBUser user1 = new NCMBUser();
        user1.setObjectId("dummyUserObjectId1");

        NCMBUser user2 = new NCMBUser();
        user2.setObjectId("dummyUserObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.removeUser(Arrays.asList(user1, user2));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertNull(role.getString("belongUser"));
    }

    @Test
    public void removeUserInBackground() throws Exception {
        NCMBUser user1 = new NCMBUser();
        user1.setObjectId("dummyUserObjectId1");

        NCMBUser user2 = new NCMBUser();
        user2.setObjectId("dummyUserObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.removeUserInBackground(Arrays.asList(user1, user2), new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertNull(role.getString("belongUser"));
        Assert.assertTrue(callbackFlag);
    }


    @Test
    public void addRole() throws Exception {
        NCMBRole role1 = new NCMBRole("testRole1");
        role1.setObjectId("dummyRoleObjectId1");

        NCMBRole role2 = new NCMBRole("testRole2");
        role2.setObjectId("dummyRoleObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.addRole(Arrays.asList(role1, role2));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
    }

    @Test
    public void addRoleInBackground() throws Exception {
        NCMBRole role1 = new NCMBRole("testRole1");
        role1.setObjectId("dummyRoleObjectId1");

        NCMBRole role2 = new NCMBRole("testRole2");
        role2.setObjectId("dummyRoleObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.addRoleInBackground(Arrays.asList(role1, role2), new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void removeRole() throws Exception {
        NCMBRole role1 = new NCMBRole("testRole1");
        role1.setObjectId("dummyRoleObjectId1");

        NCMBRole role2 = new NCMBRole("testRole2");
        role2.setObjectId("dummyRoleObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.removeRole(Arrays.asList(role1, role2));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertNull(role.getString("belongRole"));
    }

    @Test
    public void removeRoleInBackground() throws Exception {
        NCMBRole role1 = new NCMBRole("testRole1");
        role1.setObjectId("dummyRoleObjectId1");

        NCMBRole role2 = new NCMBRole("testRole2");
        role2.setObjectId("dummyRoleObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.removeRoleInBackground(Arrays.asList(role1, role2), new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
        Assert.assertNull(role.getString("belongRole"));
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void fetchObject() throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        try {
            role.fetchObject();
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("role_test1", role.getRoleName());
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
    }

    @Test
    public void fetchObjectInBackground() throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.fetchObjectInBackground(new FetchCallback<NCMBRole>() {
            @Override
            public void done(NCMBRole fetchedRole, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertEquals("role_test1", role.getRoleName());
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void deleteObject() throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        try {
            role.deleteObject();
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNull(role.getRoleName());
        Assert.assertNull(role.getObjectId());
    }

    @Test
    public void deleteObjectInBackground() throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertNull(role.getRoleName());
        Assert.assertNull(role.getObjectId());
        Assert.assertTrue(callbackFlag);
    }
}
