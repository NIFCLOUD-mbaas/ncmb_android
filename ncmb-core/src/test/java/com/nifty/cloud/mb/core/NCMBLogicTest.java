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
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBLogicTest {
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
    public void script_execute_and_return_byte () throws Exception {
        NCMBLogic logic = new NCMBLogic("testLogic.js");
        byte[] result;
        try {
            result = logic.execute();
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
        String expected = "hello";
        Assert.assertTrue(result.equals(expected.getBytes()));
    }

    @Test
    public void script_execute_and_return_error () {
        NCMBLogic logic = new NCMBLogic("errorTestLogic.js");
        try {
            logic.execute();
        } catch (NCMBException e) {
            Assert.assertEquals(e.getCode(), NCMBException.DATA_NOT_FOUND);
        }
    }

    @Test
    public void script_execute_asynchronously () {
        NCMBLogic logic = new NCMBLogic("testLogic.js");
        logic.executeInBackground(new ExecuteScriptCallback (){
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    String expected = "hello";
                    Assert.assertTrue(result.equals(expected.getBytes()));
                }
            }
        });
    }

    @Test
    public void script_execute_asynchronously_and_return_error () {
        NCMBLogic logic = new NCMBLogic("errorTestLogic.js");
        logic.executeInBackground(new ExecuteScriptCallback (){
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e == null) {
                    Assert.fail(e.getMessage());
                } else {
                    Assert.assertEquals(e.getCode(), NCMBException.DATA_NOT_FOUND);
                }
            }
        });
    }
}
=