package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBScriptTest {
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
    }

    @After
    public void teardown() {

    }

    @Test
    public void script_execute_and_return_byte() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.GET);

        byte[] result = null;
        try {
            result = script.execute(null);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
        String expected = "hello";
        Assert.assertTrue(Arrays.equals(result, expected.getBytes()));
    }

    @Test
    public void script_execute_and_return_error() {
        NCMBScript script = new NCMBScript("errorTestScript.js",NCMBScript.MethodType.GET);
        try {
            script.execute(null);
        } catch (NCMBException e) {
            Assert.assertEquals(e.getCode(), NCMBException.DATA_NOT_FOUND);
        }
    }

    @Test
    public void script_execute_asynchronously() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js",NCMBScript.MethodType.GET);
        JSONObject query = new JSONObject("{name:tarou}");
        byte[] params = query.toString().getBytes();
        script.executeInBackground(params, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    String expected = "hello, tarou";
                    Assert.assertTrue(Arrays.equals(result, expected.getBytes()));
                }
            }
        });
    }

    @Test
    public void script_execute_asynchronously_and_return_error() {
        NCMBScript script = new NCMBScript("errorTestScript.js",NCMBScript.MethodType.GET);
        script.executeInBackground(null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e == null) {
                    Assert.fail();
                } else {
                    Assert.assertEquals(e.getCode(), NCMBException.DATA_NOT_FOUND);
                }
            }
        });
    }
}
