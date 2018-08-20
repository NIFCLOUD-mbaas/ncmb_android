/*
 * Copyright 2017-2018 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBFileTest {
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

    /*** Test Case NCMBFile ***/

    /**
     * - 内容：saveInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void saveInBackground() throws Exception {
        byte[] data = "Hello,NCMB".getBytes();
        NCMBFile file = new NCMBFile("Sample.txt", data, new NCMBAcl());
        file.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：updateInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void updateInBackground() throws Exception {
        NCMBFile file = new NCMBFile("Sample.txt", new NCMBAcl());
        file.updateInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：deleteInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void deleteInBackground() throws Exception {
        NCMBFile file = new NCMBFile("Sample.txt");
        file.deleteInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：fetchInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void fetchInBackground() throws Exception {
        NCMBFile file = new NCMBFile("Sample.txt");
        file.fetchInBackground(new FetchFileCallback() {
            @Override
            public void done(byte[] data, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }
}
