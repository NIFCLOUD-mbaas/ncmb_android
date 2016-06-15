package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
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

import java.util.ArrayList;
import java.util.List;

@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBPushServiceTest {
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

    /*** Test Case NCMBPushService ***/

    /**
     * - 内容：sendPushが成功する事を確認する
     * - 結果：objectIdを含むJSONObjectが返却される事
     */
    @Test
    public void sendPush() throws Exception {
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        JSONObject params = new JSONObject();
        params.put("title", "title");
        params.put("message", "message");
        params.put("target", new JSONArray("[android]"));
        JSONObject json = pushService.sendPush(params);

        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));
    }

    /**
     * - 内容：sendPushInBackgroundが成功する事を確認する
     * - 結果：objectIdを含むJSONObjectが返却される事
     */
    @Test
    public void sendPushInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.sendPushInBackground(new JSONObject(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                try {
                    Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));
                } catch (JSONException error) {
                    Assert.assertNull(error);
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：updatePushが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void updatePush() throws Exception {
        Exception error = null;
        JSONObject json = null;
        try {
            NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
            JSONObject params = new JSONObject();
            params.put("title", "title_update");
            params.put("message", "message_update");
            json = pushService.updatePush("7FrmPTBKSNtVjajm", params);
        } catch (NCMBException e) {
            error = e;
        }

        Assert.assertNull(error);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
    }

    /**
     * - 内容：updatePushInBackgroundが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void updatePushInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        JSONObject params = new JSONObject();
        params.put("title", "title_update");
        params.put("message", "message_update");
        pushService.updatePushInBackground("7FrmPTBKSNtVjajm", params, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.assertNull(error);
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：deletePushが成功する事を確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void deletePush() throws Exception {
        Exception error = null;
        try {
            NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
            pushService.deletePush("7FrmPTBKSNtVjajm");
        } catch (NCMBException e) {
            error = e;
        }

        Assert.assertNull(error);
    }

    /**
     * - 内容：deletePushInBackgroundが成功する事を確認する
     * - 結果：エラーが発生しないこと
     */
    @Test
    public void deletePushInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.deletePushInBackground("7FrmPTBKSNtVjajm", new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：getPushが成功する事を確認する
     * - 結果：objectIdを含むJSONObjectが返却される事
     */
    @Test
    public void getPush() throws Exception {
        Exception error = null;
        JSONObject json = null;
        try {
            NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
            NCMBPush push = pushService.fetchPush("7FrmPTBKSNtVjajm");

            Assert.assertNull(error);
            Assert.assertEquals("7FrmPTBKSNtVjajm", push.getString("objectId"));
            Assert.assertEquals("http://www.yahoo.co.jp/", push.getString("richUrl"));
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * - 内容：getPushInBackgroundが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void getPushInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.fetchPushInBackground("7FrmPTBKSNtVjajm", new FetchCallback<NCMBPush>() {
            @Override
            public void done(NCMBPush push, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                Assert.assertEquals("7FrmPTBKSNtVjajm", push.getString("objectId"));
                Assert.assertEquals("http://www.yahoo.co.jp/", push.getString("richUrl"));

                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：searchPushが成功する事を確認する
     * - 結果：targetがandroidの情報を含むJSONObjectが返却される事
     */
    @Test
    public void searchPush() throws Exception {
        NCMBPushService pushService = new NCMBPushService(NCMB.getCurrentContext());
        //Search condition
        JSONObject query = new JSONObject();
        query.put("where", new JSONObject("{target:[android]}"));
        query.put("limit", 2);

        List results = pushService.searchPush(query);

        //checkAssert
        Assert.assertEquals(2, results.size());
        for (int i = 0; i < results.size(); i++) {
            Assert.assertEquals(new JSONArray("[android]"), ((NCMBPush) results.get(i)).getTarget());
        }
    }

    /**
     * - 内容：searchPushInBackgroundが成功する事を確認する
     * - 結果：targetがandroidの情報を含むJSONObjectが返却される事
     */
    @Test
    public void searchPushInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = new NCMBPushService(NCMB.getCurrentContext());
        //Search condition
        JSONObject query = new JSONObject();
        query.put("where", new JSONObject("{target:[android]}"));
        query.put("limit", 2);

        pushService.searchPushInBackground(query, new SearchPushCallback() {
            @Override
            public void done(ArrayList<NCMBPush> results, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                Assert.assertEquals(2, results.size());
                for (int i = 0; i < results.size(); i++) {
                    try {
                        Assert.assertEquals(new JSONArray("[android]"), results.get(i).getTarget());
                    } catch (JSONException error) {
                        Assert.fail(error.getMessage());
                    }
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：sendPushReceiptStatusBackgroundが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void sendPushReceiptStatusBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.sendPushReceiptStatusInBackground("7FrmPTBKSNtVjajm", new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                try {
                    Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
                } catch (JSONException error) {
                    Assert.assertNull(error);
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }
}
