package com.nifty.cloud.mb.core;

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

import java.io.UnsupportedEncodingException;

/**
 * NCMBScriptServiceTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBScriptServiceTest {
    private MockWebServer mServer;
    private boolean mCallbackFlag;
    private String mScriptUrl;

    @Before
    public void setup() throws Exception {
        ShadowLog.stream = System.out;

        mServer = new MockWebServer();

        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
        mScriptUrl = mServer.getUrl("/").toString() + "2015-09-01/script";
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey",
                null,
                null);

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        mCallbackFlag = false;
    }

    @After
    public void teardown() {

    }

    /**
     * - 内容：executeScriptが成功することを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void executeScript() throws Exception {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        byte[] result = scriptService.executeScript(
                "testScript.js",
                NCMBScript.MethodType.GET,
                null,
                null,
                null,
                mScriptUrl);

        Assert.assertEquals("hello", new String(result, "UTF-8"));
    }


    /**
     * - 内容：executeScriptInBackgroundが成功することを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void executeScriptInBackground() throws Exception {
        NCMBScriptService scriptService = (NCMBScriptService) NCMB.factory(NCMB.ServiceType.SCRIPT);
        scriptService.executeScriptInBackground(
                "testScript.js",
                NCMBScript.MethodType.GET,
                null,
                null,
                null,
                mScriptUrl,
                new ExecuteScriptCallback() {
                    @Override
                    public void done(byte[] result, NCMBException e) {
                        if (e != null) {
                            Assert.fail(e.getMessage());
                        } else {
                            try {
                                Assert.assertEquals("hello", new String(result, "UTF-8"));
                            } catch (UnsupportedEncodingException error) {
                                Assert.fail(error.getMessage());
                            }
                        }
                        mCallbackFlag = true;
                    }
                });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(mCallbackFlag);
    }

}
