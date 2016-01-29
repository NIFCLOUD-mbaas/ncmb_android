package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBScriptTest {
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

    @Test
    public void script_execute_and_return_byte() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        byte[] result = null;
        try {
            result = script.execute(null, null, null);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
        String expected = "hello";
        Assert.assertTrue(Arrays.equals(result, expected.getBytes()));
    }

    @Test
    public void script_execute_and_return_error() {
        NCMBScript script = new NCMBScript("errorTestScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        try {
            script.execute(null, null, null);
        } catch (NCMBException e) {
            Assert.assertEquals(e.getCode(), "404");
        }
    }

    @Test
    public void script_execute_asynchronously() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        JSONObject query = new JSONObject("{name:tarou}");
        script.executeInBackground(null, null, query, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    String expected = "hello,tarou";
                    Assert.assertTrue(Arrays.equals(result, expected.getBytes()));
                }
                mCallbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(mCallbackFlag);
    }

    @Test
    public void script_execute_asynchronously_and_return_error() {
        NCMBScript script = new NCMBScript("errorTestScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        script.executeInBackground(null, null, null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e == null) {
                    Assert.fail();
                } else {
                    Assert.assertEquals(e.getCode(), "404");
                }
                mCallbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(mCallbackFlag);
    }
}
