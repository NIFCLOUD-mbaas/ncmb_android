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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.text.SimpleDateFormat;

/**
 * NCMBUserTest class
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBUserTest {
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
        NCMBUser.currentUser = null;
        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
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
    public void sign_up () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Nifty Tarou");
        user.setPassword("niftytarou");

        user.signUp();

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Nifty Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void sign_up_in_background () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Nifty Tarou");
        user.setPassword("niftytarou");

        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Nifty Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void login () throws Exception {
        NCMBUser user = NCMBUser.login("Nifty Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Nifty Tarou", user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void login_in_background () throws Exception {
        NCMBUser.loginInBackground("Nifty Tarou", "dummyPassword", new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        NCMBUser user = NCMBUser.getCurrentUser();

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Nifty Tarou", user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void login_with_facebook_account () throws Exception {

        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "facebookDummyId",
                "facebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );
        NCMBUser user = NCMBUser.loginWith(facebookParams);
        Assert.assertEquals(user.getObjectId(), "dummyObjectId");
        Assert.assertEquals(facebookParams.userId, user.getAuthData("facebook").getString("id"));
        Assert.assertEquals(facebookParams.accessToken, user.getAuthData("facebook").getString("access_token"));

        Assert.assertEquals(df.format(facebookParams.expirationDate), user.getAuthData("facebook").getJSONObject("expiration_date").getString("iso"));
        Assert.assertNotNull(NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void login_with_invalid_facebook_account () throws Exception {

        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "invalidFacebookDummyId",
                "invalidFacebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );
        NCMBUser user = null;
        try {
            user = NCMBUser.loginWith(facebookParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
            Assert.assertNull(user);
            Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        }
    }

    @Test
    public void login_with_facebook_in_background () throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "facebookDummyId",
                "facebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser.loginInBackgroundWith(facebookParams, new LoginCallback(){
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        NCMBUser user = NCMBUser.getCurrentUser();
        Assert.assertEquals(user.getObjectId(), "dummyObjectId");
        Assert.assertEquals(facebookParams.userId, user.getAuthData("facebook").getString("id"));
        Assert.assertEquals(facebookParams.accessToken, user.getAuthData("facebook").getString("access_token"));

        Assert.assertEquals(df.format(facebookParams.expirationDate), user.getAuthData("facebook").getJSONObject("expiration_date").getString("iso"));
        Assert.assertNotNull(NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void login_with_invalid_facebook_in_background () throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "invalidFacebookDummyId",
                "invalidFacebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser.loginInBackgroundWith(facebookParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
    }

    @Test
    public void logout () throws Exception {
        NCMBUser.logout();

        NCMBUser user = NCMBUser.getCurrentUser();

        Assert.assertNull(user.getObjectId());
        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);

    }

    @Test
    public void logout_in_background () throws Exception {
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
            }
        });
    }

    @Test
    public void update () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.put("key", "value");
        user.save();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), user.getUpdateDate());
    }

    @Test
    public void update_in_background () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.put("key", "value");
        user.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), user.getUpdateDate());
    }

    @Test
    public void fetch () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.fetch();

        Assert.assertEquals("Nifty Tarou", user.getUserName());
    }

    @Test
    public void fetch_in_background () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("Nifty Tarou", user.getUserName());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertEquals("Nifty Tarou", user.getUserName());
    }

    @Test
    public void delete_current_user () throws Exception {
        NCMBUser.login("Nifty Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = NCMBUser.getCurrentUser();
        user.deleteObject();

        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        Assert.assertNull(NCMB.sCurrentContext.userId);

    }

    @Test
    public void delete_in_bakcground () throws Exception {
        NCMBUser.login("Nifty Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = NCMBUser.getCurrentUser();
        user.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        Assert.assertNull(NCMB.sCurrentContext.userId);
    }

    @Test
    public void delete_not_current_user () throws Exception {
        NCMBUser.login("Nifty Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = new NCMBUser();
        user.setObjectId("notCurrentUserId");
        user.deleteObject();

        Assert.assertNull(user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("dummyObjectId", NCMB.sCurrentContext.userId);
    }

    @Test
    public void delete_in_background_not_current_user () throws Exception {
        NCMBUser.login("Nifty Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = new NCMBUser();
        user.setObjectId("notCurrentUserId");
        user.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertNull(user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("dummyObjectId", NCMB.sCurrentContext.userId);
    }

    @Test
    public void mUpdateKeys_property_check () throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Nifty Tarou");
        user.setPassword("dummyPassword");
        user.setMailAddress("test.foo@nifty.com");

        Assert.assertEquals("Nifty Tarou", user.mFields.getString("userName"));
        Assert.assertEquals("dummyPassword", user.mFields.getString("password"));
        Assert.assertEquals("test.foo@nifty.com", user.mFields.getString("mailAddress"));

        Assert.assertTrue(user.mUpdateKeys.contains("userName"));
        Assert.assertTrue(user.mUpdateKeys.contains("password"));
        Assert.assertTrue(user.mUpdateKeys.contains("mailAddress"));
    }

}
