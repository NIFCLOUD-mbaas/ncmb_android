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
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * NCMBRoleTest class
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class NCMBRoleTest {
    private MockWebServer mServer;

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

        Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();
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
    public void creat_role () throws Exception {
        NCMBRole role = new NCMBRole("dummyRoleName");
        try {
            role.createRole();
            Assert.assertEquals("dummyObjectId", role.getObjectId());

        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void create_role_in_background () throws Exception {
        NCMBRole role = new NCMBRole();
        role.setRoleName("dummyRoleName");
        role.createRoleInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        Assert.assertEquals("dummyObjectId", role.getObjectId());
    }

    @Test
    public void add_user () throws Exception {
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
    public void add_user_in_background () throws Exception {
        NCMBUser user1 = new NCMBUser();
        user1.setObjectId("dummyUserObjectId1");

        NCMBUser user2 = new NCMBUser();
        user2.setObjectId("dummyUserObjectId2");

        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.addUserInBackground(Arrays.asList(user1, user2), new DoneCallback(){
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
    }

    @Test
    public void add_role () throws Exception {
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
    public void add_role_in_background () throws Exception {
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
            }
        });

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), role.getUpdateDate());
    }

    @Test
    public void fetch_role () throws Exception {
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
    public void fetch_role_in_background () throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.fetchObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        Assert.assertEquals("role_test1", role.getRoleName());
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
        Assert.assertEquals(df.parse("2013-08-30T05:04:19.045Z"), role.getCreateDate());
    }

    @Test
    public void delete_role () throws Exception {
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
    public void delete_role_in_background () throws Exception {
        NCMBRole role = new NCMBRole("testRole");
        role.setObjectId("dummyRoleId");
        role.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasks();

        Assert.assertNull(role.getRoleName());
        Assert.assertNull(role.getObjectId());
    }
}
