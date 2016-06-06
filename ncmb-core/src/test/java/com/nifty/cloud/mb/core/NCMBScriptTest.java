package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONObject;
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
import java.util.HashMap;

/**
 * NCMBScriptTest
 */
@RunWith(CustomRobolectricTestRunner.class)
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

    /**
     * - 内容：executeが成功することを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_and_return_byte() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        byte[] result = null;
        try {
            result = script.execute(null, null, null);
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals("hello", new String(result, "UTF-8"));
    }

    /**
     * - 内容：executeが失敗することを確認する
     * - 結果：エラーが発生すること
     */
    @Test
    public void script_execute_and_return_error() {
        NCMBScript script = new NCMBScript("errorTestScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        try {
            script.execute(null, null, null);
        } catch (NCMBException e) {
            Assert.assertEquals(e.getCode(), "404");
        }
    }

    /**
     * - 内容：executeInBackgroundが成功することを確認する
     * - 結果：エラーが発生しないこと
     */
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
                    try {
                        Assert.assertEquals("hello,tarou", new String(result, "UTF-8"));
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

    /**
     * - 内容：executeInBackgroundが失敗することを確認する
     * - 結果：エラーが発生すること
     */
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

    /**
     * - 内容：bodyが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_content() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.POST, mScriptUrl);
        JSONObject body = new JSONObject("{name:tarou}");
        script.executeInBackground(null, body, null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    try {
                        Assert.assertEquals("hello,tarou", new String(result, "UTF-8"));
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

    /**
     * - 内容：headerが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_header() throws Exception {
        NCMBScript script = new NCMBScript("testScript.js", NCMBScript.MethodType.GET, mScriptUrl);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("key", "value");
        script.executeInBackground(header, null, null, new ExecuteScriptCallback() {
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

    /**
     * - 内容：MethodにPOSTが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_POST() throws Exception {
        NCMBScript script = new NCMBScript("testScript_POST.js", NCMBScript.MethodType.POST, mScriptUrl);
        JSONObject body = new JSONObject();
        body.put("message", "hello,tarou");
        script.executeInBackground(null, body, null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    try {
                        Assert.assertEquals("hello,tarou", new String(result, "UTF-8"));
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

    /**
     * - 内容：MethodにPUTが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_PUT() throws Exception {
        NCMBScript script = new NCMBScript("testScript_PUT.js", NCMBScript.MethodType.PUT, mScriptUrl);
        JSONObject body = new JSONObject();
        body.put("message", "hello,tarou");
        script.executeInBackground(null, body, null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    try {
                        Assert.assertEquals("hello,tarou", new String(result, "UTF-8"));
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

    /**
     * - 内容：MethodにGETが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_GET() throws Exception {
        NCMBScript script = new NCMBScript("testScript_GET.js", NCMBScript.MethodType.GET, mScriptUrl);
        JSONObject query = new JSONObject("{name:tarou}");
        script.executeInBackground(null, null, query, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                } else {
                    try {
                        Assert.assertEquals("hello,tarou", new String(result, "UTF-8"));
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

    /**
     * - 内容：MethodにDELETEが指定出来ることを確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void script_execute_DELETE() throws Exception {
        NCMBScript script = new NCMBScript("testScript_DELETE.js", NCMBScript.MethodType.DELETE, mScriptUrl);
        script.executeInBackground(null, null, null, new ExecuteScriptCallback() {
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
