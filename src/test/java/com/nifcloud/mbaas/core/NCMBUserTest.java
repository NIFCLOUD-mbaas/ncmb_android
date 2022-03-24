/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
package com.nifcloud.mbaas.core;

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
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * NCMBUserTest class
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE, shadows = {ShadowNCMBUser.class})
public class NCMBUserTest {

    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {

        MockWebServer mServer = new MockWebServer();
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
    public void sign_up() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");

        user.signUp();

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void sign_up_add_own_field() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.put("testField", "test");

        user.signUp();

        Assert.assertEquals("test", user.mFields.getString("testField"));
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }


    @Test
    public void sign_up_in_background() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");

        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void sign_up_add_own_field_in_background() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.put("testField", "test");

        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("test", user.mFields.getString("testField"));
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void requestAuthenticationMail() throws Exception {
        try {
            NCMBUser.requestAuthenticationMail("sample@example.com");
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        }
    }

    @Test
    public void requestAuthenticationMailInBackground() throws Exception {
        NCMBUser.requestAuthenticationMailInBackground("sample@example.com", new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void requestPasswordResetSynchronously() throws Exception {
        try {
            NCMBUser.requestPasswordReset("sample@example.com");
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        }
    }

    @Test
    public void requestPasswordResetInBackground() throws Exception {
        NCMBUser.requestPasswordResetInBackground("sample@example.com", new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void login() throws Exception {
        NCMBUser user = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void login_in_background() throws Exception {
        NCMBUser.loginInBackground("Ncmb Tarou", "dummyPassword", new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertEquals("dummyObjectId", user.getObjectId());
                Assert.assertEquals("Ncmb Tarou", user.getUserName());
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        NCMBUser user = NCMBUser.getCurrentUser();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals("Ncmb Tarou", user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void loginWithMailAddress() throws Exception {
        try {
            NCMBUser user = NCMBUser.loginWithMailAddress("sample@example.com", "dummyPassword");
            Assert.assertEquals("dummyObjectId", user.getObjectId());
            Assert.assertEquals("Ncmb Tarou", user.getUserName());
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        }

        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    @Test
    public void loginWithMailAddressInBackground() throws Exception {
        NCMBUser.loginWithMailAddressInBackground("sample@example.com", "dummyPassword", new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    @Test
    public void loginWithAnonymous() throws Exception {
        Assert.assertFalse(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        try {
            NCMBUser user = NCMBUser.loginWithAnonymous();
            Assert.assertEquals("dummyObjectId", user.getObjectId());
        } catch (Exception error) {
            Assert.fail(error.getMessage());
        }

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @Test
    public void loginWithAnonymousInBackground() throws Exception {

        Assert.assertFalse(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        NCMBUser.loginWithAnonymousInBackground(new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @Test
    public void login_with_facebook_account() throws Exception {

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
        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("facebook"));
    }

    @Test
    public void login_with_invalid_facebook_account() throws Exception {

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
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
        }

        Assert.assertNull(user);
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void login_with_facebook_in_background() throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "facebookDummyId",
                "facebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser.loginInBackgroundWith(facebookParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        NCMBUser user = NCMBUser.getCurrentUser();
        Assert.assertEquals(user.getObjectId(), "dummyObjectId");
        Assert.assertEquals(facebookParams.userId, user.getAuthData("facebook").getString("id"));
        Assert.assertEquals(facebookParams.accessToken, user.getAuthData("facebook").getString("access_token"));

        Assert.assertEquals(df.format(facebookParams.expirationDate), user.getAuthData("facebook").getJSONObject("expiration_date").getString("iso"));
        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("facebook"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_with_empty_facebook_auth_data() throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                null,
                "invalidFacebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser.loginWith(facebookParams);
    }

    @Test
    public void login_with_invalid_facebook_in_background() throws Exception {
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
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void login_with_twitter_account() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "twitterDummyId",
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );
        NCMBUser user = NCMBUser.loginWith(twitterParams);
        Assert.assertEquals(user.getObjectId(), "dummyObjectId");
        Assert.assertEquals(twitterParams.userId, user.getAuthData("twitter").getString("id"));
        Assert.assertEquals(twitterParams.screenName, user.getAuthData("twitter").getString("screen_name"));
        Assert.assertEquals(twitterParams.consumerKey, user.getAuthData("twitter").getString("oauth_consumer_key"));
        Assert.assertEquals(twitterParams.accessToken, user.getAuthData("twitter").getString("oauth_token"));
        Assert.assertEquals(twitterParams.accessTokenSecret, user.getAuthData("twitter").getString("oauth_token_secret"));

        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("twitter"));
    }

    @Test
    public void login_with_invalid_twitter_account() throws Exception {

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "invalidTwitterDummyId",
                "invalidTwitterDummyScreenName",
                "invalidTwitterDummyConsumerKey",
                "invalidTwitterDummyConsumerSecret",
                "invalidTwitterDummyOauthToken",
                "invalidTwitterDummyOauthSecret"
        );
        NCMBUser user = null;
        try {
            user = NCMBUser.loginWith(twitterParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
        }
        Assert.assertNull(user);
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }


    @Test
    public void login_with_twitter_in_background() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "twitterDummyId",
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );

        NCMBUser.loginInBackgroundWith(twitterParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        NCMBUser user = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", user.getObjectId());

        Assert.assertEquals(twitterParams.userId, user.getAuthData("twitter").getString("id"));
        Assert.assertEquals(twitterParams.screenName, user.getAuthData("twitter").getString("screen_name"));
        Assert.assertEquals(twitterParams.consumerKey, user.getAuthData("twitter").getString("oauth_consumer_key"));
        Assert.assertEquals(twitterParams.accessToken, user.getAuthData("twitter").getString("oauth_token"));
        Assert.assertEquals(twitterParams.accessTokenSecret, user.getAuthData("twitter").getString("oauth_token_secret"));

        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("twitter"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_with_empty_twitter_auth_data() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                null,
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );

        NCMBUser.loginWith(twitterParams);
    }

    @Test
    public void login_with_invalid_twitter_in_background() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "invalidTwitterDummyId",
                "invalidTwitterDummyScreenName",
                "invalidTwitterDummyConsumerKey",
                "invalidTwitterDummyConsumerSecret",
                "invalidTwitterDummyOauthToken",
                "invalidTwitterDummyOauthSecret"
        );

        NCMBUser.loginInBackgroundWith(twitterParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void login_with_google_account() throws Exception {

        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );
        NCMBUser user = NCMBUser.loginWith(googleParams);
        Assert.assertEquals(user.getObjectId(), "dummyObjectId");
        Assert.assertEquals(googleParams.userId, user.getAuthData("google").getString("id"));
        Assert.assertEquals(googleParams.accessToken, user.getAuthData("google").getString("access_token"));

        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("google"));
    }

    @Test
    public void login_with_invalid_google_account() throws Exception {

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "invalidGoogleDummyId",
                "invalidGoogleDummyAccessToken"
        );
        NCMBUser user = null;
        try {
            user = NCMBUser.loginWith(googleParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
        }

        Assert.assertNull(user);
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void login_with_google_in_background() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );

        NCMBUser.loginInBackgroundWith(googleParams, new LoginCallback() {
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
        Assert.assertEquals(googleParams.userId, user.getAuthData("google").getString("id"));
        Assert.assertEquals(googleParams.accessToken, user.getAuthData("google").getString("access_token"));
        Assert.assertNotNull(NCMB.getCurrentContext().sessionToken);

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("google"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void login_with_empty_google_auth_data() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                null,
                "invalidGoogleDummyAccessToken"
        );

        NCMBUser.loginWith(googleParams);
    }

    @Test
    public void login_with_invalid_google_in_background() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "invalidGoogleDummyId",
                "invalidGoogleDummyAccessToken"
        );

        NCMBUser.loginInBackgroundWith(googleParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
    }

    @Test
    public void logout() throws Exception {
        NCMBUser.logout();

        NCMBUser user = NCMBUser.getCurrentUser();

        Assert.assertNull(user.getObjectId());
        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

    }

    @Test
    public void logout_in_background() throws Exception {
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void update() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.put("key", "value");
        user.save();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), user.getUpdateDate());
    }

    @Test
    public void update_in_background() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.put("key", "value");
        user.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), user.getUpdateDate());
    }

    @Test
    public void link_facebook_auth_data() throws Exception {

        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "facebookDummyId",
                "facebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(facebookParams);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(user.isLinkedWith("facebook"));
    }

    @Test
    public void link_facebook_auth_data_in_background() throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "facebookDummyId",
                "facebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(facebookParams, new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertTrue(user.isLinkedWith("facebook"));
    }

    @Test
    public void link_invalid_facebook_auth_data() throws Exception {

        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "invalidFacebookDummyId",
                "invalidFacebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(facebookParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
        }

        Assert.assertFalse(user.isLinkedWith("facebook"));
    }

    @Test
    public void link_invalid_facebook_auth_data_in_background() throws Exception {
        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        NCMBFacebookParameters facebookParams = new NCMBFacebookParameters(
                "invalidFacebookDummyId",
                "invalidFacebookDummyAccessToken",
                df.parse("2016-06-07T01:02:03.004Z")
        );


        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(facebookParams, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertFalse(user.isLinkedWith("facebook"));
    }

    @Test
    public void link_twitter_auth_data() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "twitterDummyId",
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(twitterParams);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(user.isLinkedWith("twitter"));
    }

    @Test
    public void link_twitter_auth_data_in_background() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "twitterDummyId",
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(twitterParams, new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertTrue(user.isLinkedWith("twitter"));
    }

    @Test
    public void link_invalid_twitter_auth_data() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "invalidTwitterDummyId",
                "invalidTwitterDummyScreenName",
                "invalidTwitterDummyConsumerKey",
                "invalidTwitterDummyConsumerSecret",
                "invalidTwitterDummyOauthToken",
                "invalidTwitterDummyOauthSecret"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(twitterParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
        }

        Assert.assertFalse(user.isLinkedWith("twitter"));
    }

    @Test
    public void link_invalid_twitter_auth_data_in_background() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "invalidTwitterDummyId",
                "invalidTwitterDummyScreenName",
                "invalidTwitterDummyConsumerKey",
                "invalidTwitterDummyConsumerSecret",
                "invalidTwitterDummyOauthToken",
                "invalidTwitterDummyOauthSecret"
        );


        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(twitterParams, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertFalse(user.isLinkedWith("twitter"));
    }

    @Test
    public void link_google_auth_data() throws Exception {

        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(googleParams);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(user.isLinkedWith("google"));
    }

    @Test
    public void link_google_auth_data_in_background() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(googleParams, new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertTrue(user.isLinkedWith("google"));
    }

    @Test
    public void link_invalid_google_auth_data() throws Exception {

        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "invalidGoogleDummyId",
                "invalidGoogleDummyAccessToken"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        try {
            user.linkWith(googleParams);
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
        }

        Assert.assertFalse(user.isLinkedWith("google"));
    }

    @Test
    public void link_invalid_google_auth_data_in_background() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "invalidGoogleDummyId",
                "invalidGoogleDummyAccessToken"
        );

        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");

        user.linkInBackgroundWith(googleParams, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertFalse(user.isLinkedWith("google"));
    }

    @Test
    public void login_with_twitter_and_link_google_auth_data() throws Exception {

        NCMBTwitterParameters twitterParams = new NCMBTwitterParameters(
                "twitterDummyId",
                "twitterDummyScreenName",
                "twitterDummyConsumerKey",
                "twitterDummyConsumerSecret",
                "twitterDummyOauthToken",
                "twitterDummyOauthSecret"
        );
        NCMBUser user = null;
        try {
            user = NCMBUser.loginWith(twitterParams);
            user.setObjectId("dummyUserId");

            NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                    "googleDummyId",
                    "googleDummyAccessToken"
            );

            user.linkWith(googleParams);
            Assert.assertTrue(user.isLinkedWith("twitter"));
            Assert.assertTrue(user.isLinkedWith("google"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void unlink_authentication_data() throws Exception {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );
        NCMBUser user = NCMBUser.loginWith(googleParams);
        Assert.assertTrue(user.isLinkedWith("google"));

        user.unlink("google");

        Assert.assertFalse(user.isLinkedWith("google"));
    }

    @Test
    public void unlink_authentication_data_in_background() throws NCMBException {
        NCMBGoogleParameters googleParams = new NCMBGoogleParameters(
                "googleDummyId",
                "googleDummyAccessToken"
        );
        NCMBUser user = NCMBUser.loginWith(googleParams);
        Assert.assertTrue(user.isLinkedWith("google"));

        user.unlinkInBackground("google", new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertFalse(user.isLinkedWith("google"));
    }

    /**
     * - 内容：ログイン状態が取得できる事を確認する
     * - 結果：ログイン中はtrueが返却される事
     */
    @Test
    public void isAuthenticated_true() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.loginByName("Ncmb Tarou", "dummyPassword");
        Assert.assertEquals("Ncmb Tarou", user.getUserName());

        //check isAuthenticated
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertTrue(currentUser.isAuthenticated());
    }

    /**
     * - 内容：ログアウト状態が取得できる事を確認する
     * - 結果：ログアウト中はfalseが返却される事
     */
    @Test
    public void isAuthenticated_false() throws Exception {
        //check isAuthenticated
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertFalse(currentUser.isAuthenticated());
    }

    @Test
    public void fetch() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.fetch();

        Assert.assertEquals("Ncmb Tarou", user.getUserName());
    }

    @Test
    public void fetch_in_background() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setObjectId("dummyUserId");
        user.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("Ncmb Tarou", user.getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("Ncmb Tarou", user.getUserName());
    }

    @Test
    public void delete_current_user() throws Exception {
        NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = NCMBUser.getCurrentUser();
        user.deleteObject();

        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
        Assert.assertNull(NCMB.getCurrentContext().userId);

    }

    @Test
    public void delete_in_bakcground() throws Exception {
        NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = NCMBUser.getCurrentUser();
        user.deleteObjectInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(user.getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
        Assert.assertNull(NCMB.getCurrentContext().userId);
    }

    @Test
    public void delete_not_current_user() throws Exception {
        NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = new NCMBUser();
        user.setObjectId("notCurrentUserId");
        user.deleteObject();

        Assert.assertNull(user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMB.getCurrentContext().userId);
    }

    @Test
    public void delete_in_background_not_current_user() throws Exception {
        NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = new NCMBUser();
        user.setObjectId("notCurrentUserId");
        user.deleteObjectInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMB.getCurrentContext().userId);
    }

    @Test
    public void mUpdateKeys_property_check() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("dummyPassword");
        user.setMailAddress("test.foo@Ncmb.com");

        Assert.assertEquals("Ncmb Tarou", user.mFields.getString("userName"));
        Assert.assertEquals("dummyPassword", user.mFields.getString("password"));
        Assert.assertEquals("test.foo@Ncmb.com", user.mFields.getString("mailAddress"));

        Assert.assertTrue(user.mUpdateKeys.contains("userName"));
        Assert.assertTrue(user.mUpdateKeys.contains("password"));
        Assert.assertTrue(user.mUpdateKeys.contains("mailAddress"));
    }

    /**
     * - 内容：トークンがエラー場合、CurrentUserのGET通信を確認する。
     * - 結果：ステータスコード401及び「Error Json」が返却されること
     */
    @Test
    public void fetch_currentUser_when_token_error() throws Exception {
        NCMBUser user = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        NCMB.getCurrentContext().sessionToken = null;
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                    Assert.assertEquals("OAuth authentication error.", e.getMessage());
                }
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
        
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：トークンがエラー場合、DataStoreのGET通信を確認する。
     * - 結果：ステータスコード401及び「Error Json」が返却されること
     */
    @Test
    public void fetch_dataStore_when_token_error() throws Exception {
        NCMBUser user = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        NCMB.getCurrentContext().sessionToken = null;

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getObjectExpiredToken");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {

            @Override
            public void done(NCMBObject object, NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                    Assert.assertEquals("OAuth authentication error.", e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：トークンがエラー場合、UserのGET通信を確認する。
     * - 結果：ステータスコード401及び「Error Json」が返却されること
     */
    @Test
    public void fetch_user_when_token_error() throws Exception {
        NCMBUser user = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        NCMB.getCurrentContext().sessionToken = null;

        NCMBUser userFetch = new NCMBUser();
        userFetch.setObjectId("dummyAllowUserId");
        userFetch.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.OAUTH_FAILURE, e.getCode());
                    Assert.assertEquals("OAuth authentication error.", e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：ログイン後、存在しないCurrentUserのGET通信を確認する。
     * - 結果：ステータスコード404及び「Error Json」が返却されること
     */
    @Test
    public void fetch_current_user_non_exist_after_login() throws Exception {
        NCMBUser user = NCMBUser.login("NcmbCurrentUser", "dummyPassword");

        Assert.assertEquals("dummyCurrentUserId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbCurrentUser", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);

        NCMBUser userFetch = NCMBUser.getCurrentUser();
        userFetch.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
                    Assert.assertEquals("No data available.", e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyCurrentUserId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbCurrentUser", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：ログイン後、存在しないオブジェクトのGET通信を確認する。
     * - 結果：ステータスコード404及び「Error Json」が返却されること
     */
    @Test
    public void fetch_object_non_exist_after_login() throws Exception {
        NCMBUser user = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);

        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("NonExistObject");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {
            @Override
            public void done(NCMBObject object, NCMBException e) {
                if (e == null) {
                    Assert.fail("get object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
                    Assert.assertEquals("No data available.", e.getMessage());
                }
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：ログイン後、存在しないユーザーのGET通信を確認する。
     * - 結果：ステータスコード404及び「Error Json」が返却されること
     */
    @Test
    public void fetch_user_non_exist_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);

        NCMBUser userFetch = new NCMBUser();
        userFetch.setObjectId("dummyNotfoundUserId");
        userFetch.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
                    Assert.assertEquals("No data available.", e.getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
    }

    /**
     * - 内容：ログイン後、CurrentUserのGET通信を確認する。
     * - 結果：ステータスコード200及び「User data Json」が返却されること
     */
    @Test
    public void fetch_currentUser_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("dummyUserLoginId", user.getObjectId());
                    Assert.assertEquals("NcmbToTestAfterLogin", user.getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：ログイン後、CurrentUserのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_currentUser_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.put("key", "value");
        currentUser.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("value", NCMBUser.getCurrentUser().mFields.get("key"));
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), NCMBUser.getCurrentUser().getUpdateDate());
    }

    /**
     * - 内容：ログイン後、CurrentUserのPOST通信を確認する。
     * - 結果：ステータスコード409及び「Error Json」が返却されること
     */
    @Test
    public void add_currentUser_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.setObjectId(null);
        currentUser.setPassword("dummyPassword");
        currentUser.setAcl(null);

        currentUser.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DUPLICATE_VALUE, e.getCode());
                    Assert.assertEquals("NcmbToTestAfterLogin is duplication.", e.getMessage());
                }
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2015-06-07T01:02:03.004Z"), NCMBUser.getCurrentUser().getUpdateDate());

    }

    /**
     * - 内容：ログイン後、DatastoreのGET通信を確認する。
     * - 結果：ステータスコード200及び「Object data Json」が返却されること
     */
    @Test
    public void fetch_data_store_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {

            @Override
            public void done(NCMBObject object, NCMBException e) {
                object.getString("key");
                if (e != null) {
                    Assert.fail("get object raise exception:" + e.getMessage());
                } else {
                    Assert.assertEquals("7FrmPTBKSNtVjajm", object.getObjectId());
                    Assert.assertEquals("value", object.getString("key"));
                    try {
                        SimpleDateFormat df = NCMBDateFormat.getIso8601();
                        Assert.assertTrue(object.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                        Assert.assertTrue(object.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                    } catch (Exception error) {
                        Assert.fail(error.getMessage());
                    }
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    /**
     * - 内容：ログイン後、DatastoreのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_data_store_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.put("updateKey", "updateValue");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("update object error");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }

    /**
     * - 内容：ログイン後、DatastoreのPOST通信を確認する。
     * - 結果：ステータスコード201及び「Object data Json」が返却されること
     */
    @Test
    public void add_data_store_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertEquals(0, obj.mUpdateKeys.size());

    }

    /**
     * - 内容：ログイン後、DatastoreのDELETE通信を確認する。
     * - 結果：ステータスコード200及び「」が返却されること
     */
    @Test
    public void delete_data_store_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("deleteTestObjectId");
        obj.deleteObjectInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：ログイン後、AllowUserのGET通信を確認する。
     * - 結果：ステータスコード200及び「User data Json」が返却されること
     */
    @Test
    public void fetch_allow_user_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBUser> query = NCMBUser.getQuery();
        query.whereEqualTo("userName", "Ncmb Tarou");
        query.findInBackground(new FindCallback<NCMBUser>() {
            @Override
            public void done(List<NCMBUser> results, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("Ncmb Tarou", results.get(0).getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

    }

    /**
     * - 内容：ログイン後、AllowUserのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_allow_user_after_login() throws Exception {
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser userUpdate = new NCMBUser();
        userUpdate.setObjectId("dummyUserId");
        userUpdate.put("key", "value");
        userUpdate.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), userUpdate.getUpdateDate());
    }

    /**
     * - 内容：再ログイン後、CurrentUserのGET通信を確認する。
     * - 結果：ステータスコード200及び「User data Json」が返却されること
     */
    @Test
    public void fetch_currentUser_after_login_again() throws Exception {

        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.fetchInBackground(new FetchCallback<NCMBUser>() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("NcmbToTestAfterLogin", user.getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：再ログイン後、CurrentUserのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_currentUser_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.put("key", "value");
        currentUser.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), currentUser.getUpdateDate());
        Assert.assertEquals("value", NCMBUser.getCurrentUser().mFields.get("key"));
    }

    /**
     * - 内容：再ログイン後、CurrentUserのPOST通信を確認する。
     * - 結果：ステータスコード409及び「Error Json」が返却されること
     */
    @Test
    public void add_currentUser_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.setObjectId(null);
        currentUser.setPassword("dummyPassword");
        currentUser.setAcl(null);

        currentUser.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("get user method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DUPLICATE_VALUE, e.getCode());
                    Assert.assertEquals("NcmbToTestAfterLogin is duplication.", e.getMessage());
                }
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2015-06-07T01:02:03.004Z"), currentUser.getUpdateDate());
    }

    /**
     * - 内容：再ログイン後、CurrentUserのDELETE通信を確認する。
     * - 結果：ステータスコード200が返却されること
     */
    @Test
    public void delete_current_user_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser currentUser = NCMBUser.getCurrentUser();
        currentUser.deleteObject();

        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);
        Assert.assertNull(NCMB.getCurrentContext().userId);

    }

    /**
     * - 内容：再ログイン後、DataStoreのGET通信を確認する。
     * - 結果：ステータスコード200及び「Object data Json」が返却されること
     */
    @Test
    public void fetch_data_store_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {

            @Override
            public void done(NCMBObject object, NCMBException e) {
                object.getString("key");
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals("7FrmPTBKSNtVjajm", object.getObjectId());
                    Assert.assertEquals("value", object.getString("key"));

                    SimpleDateFormat df = NCMBDateFormat.getIso8601();

                    try {
                        Assert.assertTrue(object.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                        Assert.assertTrue(object.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                    } catch (ParseException | NCMBException error) {
                        Assert.fail(error.getMessage());
                    }
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    /**
     * - 内容：再ログイン後、DataStoreのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_data_store_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.put("updateKey", "updateValue");
        obj.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }

    /**
     * - 内容：再ログイン後、DataStoreのPOST通信を確認する。
     * - 結果：ステータスコード201及び「Object data Json」が返却されること
     */
    @Test
    public void add_data_store_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertEquals(0, obj.mUpdateKeys.size());
    }

    /**
     * - 内容：再ログイン後、DataStoreのDELETE通信を確認する。
     * - 結果：ステータスコード200が返却されること
     */
    @Test
    public void delete_data_store_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("deleteTestObjectId");
        obj.deleteObjectInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：再ログイン後、AllowUserのGET通信を確認する。
     * - 結果：ステータスコード200及び「User data Json」が返却されること
     */
    @Test
    public void fetch_allow_user_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBUser> query = NCMBUser.getQuery();
        query.whereEqualTo("userName", "Ncmb Tarou");
        query.findInBackground(new FindCallback<NCMBUser>() {
            @Override
            public void done(List<NCMBUser> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {
                    Assert.assertEquals("Ncmb Tarou", results.get(0).getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：再ログイン後、AllowUserのPUT通信を確認する。
     * - 結果：ステータスコード200及び「updateDate data Json」が返却されること
     */
    @Test
    public void update_allow_user_after_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("NcmbToTestAfterLogin", "dummyPassword");

        Assert.assertEquals("dummyUserLoginId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("NcmbToTestAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);

        NCMBUser userUpdate = new NCMBUser();
        userUpdate.setObjectId("dummyUserId");
        userUpdate.put("key", "value");
        userUpdate.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), userUpdate.getUpdateDate());
        Assert.assertEquals("dummyUserId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("dummySessionTokenUserLogin", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：再ログイン後、CurrentUserではないもののDELETE通信を確認する。
     * - 結果：ステータスコード200が返却されること
     */
    @Test
    public void delete_not_current_user_login_again() throws Exception {
        // Login first time
        NCMBUser loginUser = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        // Logout
        NCMBUser.logout();

        Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
        Assert.assertNull(NCMBUser.getCurrentUser().getUserName());
        Assert.assertNull(NCMB.getCurrentContext().sessionToken);

        // Login second time
        NCMBUser loginUser1 = NCMBUser.login("Ncmb Tarou", "dummyPassword");

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());

        NCMBUser user = new NCMBUser();
        user.setObjectId("notCurrentUserId");
        user.deleteObject();

        Assert.assertNull(user.getUserName());
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMB.getCurrentContext().userId);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、Saveを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：セッショントークンが変更されない
     */
    @Test
    public void login_and_save() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.save();

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、SaveInBackgroundを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：セッショントークンが変更されない
     */
    @Test
    public void login_and_save_inbackground() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、SignUpを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：セッショントークンが変更されない
     */
    @Test
    public void login_and_signup() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.signUp();

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、SignUpInBackgroundを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：セッショントークンが変更されない
     */
    @Test
    public void login_and_signupInbackground() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.signUpInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、ログインしてSignUpしてSaveを行ってからのセッショントークン。
     *
     * - 結果：セッショントークンが存在して、SignUpと適応する。
     */
    @Test
    public void login_signup_after_save() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.save();
        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user2 = new NCMBUser();
        user2.setUserName("Ncmb Tarou");
        user2.setPassword("Ncmbtarou");
        user2.signUp();
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、ログインしてSaveしてSignUpを行ってからのセッショントークン。
     *
     * - 結果：セッショントークンが存在して、SignUpと適応する。
     */
    @Test
    public void login_save_after_sigup() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.signUp();
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);

        NCMBUser user2 = new NCMBUser();
        user2.setUserName("Ncmb Tarou");
        user2.setPassword("Ncmbtarou");
        user2.save();
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、ログインしてSaveしてSignUpInBackgroundを行ってからのセッショントークン。
     *
     * - 結果：セッショントークンが存在して、SignUpInBackgroundと適応する。
     */
    @Test
    public void login_signup_in_background_after_save() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.save();
        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user2 = new NCMBUser();
        user2.setUserName("Ncmb Tarou");
        user2.setPassword("Ncmbtarou");
        user2.signUpInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、ログインしてSignUpInBackgroundしてSaveを行ってからのセッショントークン。
     *
     * - 結果：セッショントークンが存在して、SignUpInBackgroundと適応する。
     */
    @Test
    public void login_save_after_signup_in_background() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.signUpInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);

        NCMBUser user2 = new NCMBUser();
        user2.setUserName("Ncmb Tarou");
        user2.setPassword("Ncmbtarou");
        user2.save();
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、ログインしてSaveInBackgroundしてSignUpを行ってからのセッショントークン。
     * 
     * - 結果：セッショントークンが存在して、SignUpと適応する。
     */
    @Test
    public void login_signup_after_save_in_background() throws Exception {
        NCMBUser.login("saveAfterLogin", "saveAfterLogin");

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.saveInBackground(new DoneCallback() {
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
        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("dummyObjectIdSave", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("saveAfterLogin", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionTokenSave", NCMB.getCurrentContext().sessionToken);

        NCMBUser user2 = new NCMBUser();
        user2.setUserName("Ncmb Tarou");
        user2.setPassword("Ncmbtarou");
        user2.signUp();

        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
        Assert.assertEquals("Ncmb Tarou", NCMBUser.getCurrentUser().getUserName());
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
    }
}
