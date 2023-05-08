/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.text.SimpleDateFormat;

/**
 * NCMBAnonymousTest class
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE, shadows = {ShadowNCMBUser.class})
public class NCMBAnonymousTest {

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

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @Test
    public void updateCurrentUser() throws Exception {
        NCMBUser currentUser = NCMBUser.currentUser;

        Assert.assertNotNull(currentUser);

        currentUser.setUserName("updateUserName");
        currentUser.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNotNull(e);
                Assert.assertEquals(NCMBUser.getCurrentUser().getUserName(), "updateUserName");
				Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
            }
        });
    }

    @Test
    public void createNewUser() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");

        user.save();
        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @Test
    public void createNewUserBySignUp() throws Exception {
        NCMBUser user = new NCMBUser();
        user.setUserName("Ncmb Tarou");
        user.setPassword("Ncmbtarou");
        user.signUpInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
                    Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
                    Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
                }
            }
        });

    }

    @Test
    public void deleteAnonymousUser() throws Exception {
        Assert.assertNotNull(NCMBUser.currentUser);
        NCMBUser.currentUser.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNotNull(e);
                Assert.assertNull(NCMBUser.currentUser);
            }
        });
    }

    @Test
    public void createDataStore() throws Exception {
        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(null);

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @Test
    public void updateDataStore() throws Exception {
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
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @Test
    public void deleteDataStore() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        try {
            obj.setObjectId("deleteTestObjectId");
            obj.deleteObject();
        } catch (NCMBException e) {
            Assert.fail("exception raised:" + e.getMessage());
        }

        Assert.assertTrue(NCMBUser.getCurrentUser().isLinkedWith("anonymous"));
        Assert.assertEquals("dummySessionToken", NCMB.getCurrentContext().sessionToken);
        Assert.assertEquals("dummyObjectId", NCMBUser.getCurrentUser().getObjectId());
    }

    @After
    public void teardown() {

    }
}
