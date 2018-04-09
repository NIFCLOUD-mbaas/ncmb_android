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

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

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

import java.io.File;
import java.io.FileOutputStream;

/**
 * NCMBLocalFileTest class
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE, shadows = {ShadowNCMBUser.class})
public class NCMBLocalFileTest {

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
    public void create() throws Exception {
        String fileName = "Test";
        File file = NCMBLocalFile.create(fileName);
        Assert.assertEquals(fileName, file.getName());
    }

    @Test
    public void writeFile() throws Exception {
        String jsonString = "{\"key\":\"value\"}";

        File file = NCMBLocalFile.create("Test");
        Assert.assertFalse(file.exists());
        NCMBLocalFile.writeFile(file, new JSONObject(jsonString));
        Assert.assertTrue(file.exists());
    }

    @Test
    public void readFile() throws Exception {
        String jsonString = "{\"key\":\"value\"}";
        File file = NCMBLocalFile.create("Test");
        NCMBLocalFile.writeFile(file, new JSONObject(jsonString));

        Assert.assertEquals(jsonString, NCMBLocalFile.readFile(file).toString());
    }

    @Test
    public void readFile_0byte() throws Exception {
        // set 0byte local file
        File file = NCMBLocalFile.create("Test");
        FileOutputStream out = new FileOutputStream(file);
        out.write("".getBytes("UTF-8"));
        out.close();

        Assert.assertEquals("{}", NCMBLocalFile.readFile(file).toString());
    }

    @Test
    public void deleteFile() throws Exception {
        String jsonString = "{\"key\":\"value\"}";
        File file = NCMBLocalFile.create("Test");

        NCMBLocalFile.writeFile(file, new JSONObject(jsonString));
        Assert.assertTrue(file.exists());

        NCMBLocalFile.deleteFile(file);
        Assert.assertFalse(file.exists());
    }


}
