package com.nifty.cloud.mb.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBPushTest {

    private MockWebServer mServer;
    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {
        //create mocServer
        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();

        //initialize
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey",
                mServer.getUrl("/").toString(),
                null);

        //log stream
        ShadowLog.stream = System.out;

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @After
    public void teardown() throws Exception {
        //mServer.shutdown();
    }


    /**
     * - 内容：Constructorが成功することを確認する
     * - 結果：Constructorにパラメーターが正しく設定されている事
     */
    @Test
    public void Constructor() throws Exception {
        //set data
        JSONObject params = new JSONObject();
        params.put("title", "タイトル");
        params.put("message", "メッセージ");

        //create push argument
        NCMBPush push = new NCMBPush(params);
        Assert.assertEquals("タイトル", push.getTitle());
        Assert.assertEquals("メッセージ", push.getMessage());

        //create push
        push = new NCMBPush();
        Assert.assertNull(push.getTitle());
        Assert.assertNull(push.getMessage());

    }

    //region send

    /**
     * - 内容：send(POST)が成功することを確認する
     * - 結果：同期でプッシュの送信が出来る事
     */
    @Test
    public void send_post() throws Exception {
        //post
        NCMBException error = null;
        NCMBPush push = new NCMBPush();
        try {
            push.send();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());
    }

    /**
     * - 内容：send(PUT)が成功することを確認する
     * - 結果：同期でプッシュの更新が出来る事
     */
    @Test
    public void send_put() throws Exception {
        NCMBException error = null;
        Date date = new Date();
        date.setTime(date.getTime() + 60 * 60 * 24 * 1000);

        //post
        NCMBPush push = new NCMBPush();
        try {
            push.setTitle("title");
            push.setMessage("message");
            push.setDeliveryTime(date);//配信日時1日後
            push.send();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNull(error);
        Assert.assertEquals("title", push.getTitle());
        Assert.assertEquals("message", push.getMessage());
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());

        //put
        try {
            push.setTitle("title_update");
            push.setMessage("message_update");
            push.send();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals("title_update", push.getTitle());
        Assert.assertEquals("message_update", push.getMessage());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate());
    }

    /**
     * - 内容：sendInBackground(POST)が成功することを確認する
     * - 結果：非同期でプッシュの送信が出来る事
     */
    @Test
    public void sendInBackground_post() throws Exception {
        Assert.assertFalse(callbackFlag);
        //post
        NCMBPush push = new NCMBPush();
        push.setMessage("message");
        push.setTitle("title");
        push.sendInBackground(new DoneCallback() {

            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

        //check
        Assert.assertEquals("message", push.getMessage());
        Assert.assertEquals("title", push.getTitle());
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());
    }

    /**
     * - 内容：sendInBackground(PUT)が成功することを確認する
     * - 結果：非同期でプッシュの送信が出来る事
     */
    @Test
    public void sendInBackground_put() throws Exception {
        Assert.assertFalse(callbackFlag);
        //post
        NCMBPush push = new NCMBPush();
        push.setMessage("message1");
        push.setTitle("title1");
        push.send();

        //check
        Assert.assertEquals("message1", push.getMessage());
        Assert.assertEquals("title1", push.getTitle());
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());

        //put
        push.setMessage("message_update");
        push.setTitle("title_update");
        push.sendInBackground(new DoneCallback() {

            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

        //check
        Assert.assertEquals("message_update", push.getMessage());
        Assert.assertEquals("title_update", push.getTitle());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate());
    }

    /**
     * - 内容：sendInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でプッシュの送信が出来る事
     */
    @Test
    public void sendInBackground_none_callback() throws Exception {
        //post
        NCMBPush push = new NCMBPush();
        push.setMessage("message");
        push.setTitle("title");
        push.sendInBackground();

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();


        //check
        Assert.assertEquals("message", push.getMessage());
        Assert.assertEquals("title", push.getTitle());
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getUpdateDate());
    }

    /**
     * - 内容：即時配信フラグと配信時刻が同時に設定出来ない事を確認する
     * - 結果：エラーが出る事
     */
    @Test
    public void send_error_sameTime() throws Exception {
        //post
        NCMBException error = null;
        JSONObject params = new JSONObject();
        params.put("immediateDeliveryFlag", true);
        params.put("deliveryTime", new Date());
        NCMBPush push = new NCMBPush(params);
        try {
            push.send();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNotNull(error);
        Assert.assertEquals("'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time.", error.getMessage());
    }

    /**
     * - 内容：setSearchConditionで設定してプッシュ送信が出来るか確認する
     * - 結果：エラーが発生せずにsetSearchConditionの値が取得出来る事
     */
    @Test
    public void setSearchCondition() throws Exception {
        //post
        NCMBPush push = new NCMBPush();
        NCMBQuery<NCMBInstallation> query = new NCMBQuery<>("installation");
        query.whereGreaterThan("score", 80);
        push.setSearchCondition(query);

        NCMBException error = null;
        try {
            push.send();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals(80, push.getSearchCondition().getJSONObject("score").getInt("$gt"));
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getUpdateDate());
    }
    //endregion

    //region fetch

    /**
     * - 内容：fetchが成功することを確認する
     * - 結果：同期でプッシュ情報が取得出来る事
     */
    @Test
    public void fetch() throws Exception {
        //get
        NCMBException error = null;
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        try {
            push.fetch();
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        checkGetResponse(push);
    }

    /**
     * - 内容：fetchInBackgroundが成功することを確認する
     * - 結果：非同期でプッシュ情報が取得出来る事
     */
    @Test
    public void fetchInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        //get
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.fetchInBackground(new FetchCallback<NCMBPush>() {
            @Override
            public void done(NCMBPush fetchedPush, NCMBException e) {
                Assert.assertNull(e);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

        //check
        checkGetResponse(push);
    }

    /**
     * - 内容：fetchInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でプッシュ情報が取得出来る事
     */
    @Test
    public void fetchInBackground_none_callback() throws Exception {
        //get
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.fetchInBackground();

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        //check
        checkGetResponse(push);
    }

    /**
     * - 内容：fetchInBackgroundが成功することを確認する
     * - 結果：非同期でプッシュ通知が取得できること
     */
    @Test
    public void fetchInBackground_with_callback() throws Exception {
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.fetchInBackground(new FetchCallback<NCMBPush>() {
            @Override
            public void done(NCMBPush fetchedPush, NCMBException e) {
                try {
                    checkGetResponse(fetchedPush);
                } catch (Exception e1) {
                    Assert.fail(e1.getMessage());
                }
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

    }

    /**
     * - 内容：objectId未設定でfetch出来ない事を確認する
     * - 結果：エラーが出る事
     */
    @Test
    public void fetch_error_notSetObjectId() throws Exception {
        //get
        NCMBException error = null;
        NCMBPush push = new NCMBPush();
        try {
            push.fetch();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals("pushId is must not be null.", error.getMessage());
    }

    //endregion

    //region fetch

    /**
     * - 内容：deleteが成功することを確認する
     * - 結果：同期でプッシュ情報が削除出来る事
     */
    @Test
    public void delete() throws Exception {
        //delete
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        try {
            push.delete();
        } catch (NCMBException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertNull(push.getObjectId());
    }

    /**
     * - 内容：fetchInBackgroundが成功することを確認する
     * - 結果：非同期でプッシュ情報が削除出来る事
     */
    @Test
    public void deleteInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        //delete
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.deleteInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

        //check
        Assert.assertNull(push.getObjectId());
    }

    /**
     * - 内容：deleteInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でプッシュ情報が削除出来る事
     */
    @Test
    public void deleteInBackground_none_callback() throws Exception {
        //delete
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.deleteInBackground();

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();


        //check
        Assert.assertNull(push.getObjectId());
    }

    /**
     * - 内容：objectId未設定でdelete出来ない事を確認する
     * - 結果：エラーが出る事
     */
    @Test
    public void delete_error_notSetObjectId() throws Exception {
        //delete
        NCMBException error = null;
        NCMBPush push = new NCMBPush();
        try {
            push.delete();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals("pushId is must not be null.", error.getMessage());
    }
    //endregion


    //region setter/getter

    /**
     * - 内容：Instanceの値が正しく取得出来るかを確認する
     * - 結果：値が正しく取得されていること
     */
    @Test
    public void checkInstanceGet() throws Exception {
        //set Instance data
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        NCMBPush fetchedPush = pushService.fetchPush("7FrmPTBKSNtVjajm");
        NCMBPush push = new NCMBPush(fetchedPush.mFields);

        //check get
        checkGetResponse(push);
    }

    /**
     * - 内容：Instanceの値が正しく設定出来るかを確認する
     * - 結果：値が正しく設定されていること
     */
    @Test
    public void checkInstanceSet() throws Exception {
        //set Instance data
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.setTitle("title");
        push.setMessage("message");
        push.setSound("default");
        push.setRichUrl("http://www.yahoo.co.jp/");
        push.setCategory("Category");
        push.setAction("com.sample.NCMBReceiver");
        push.setBadgeSetting(0);
        push.setTarget(new JSONArray("[android,ios]"));
        NCMBQuery<NCMBInstallation> query = new NCMBQuery<>("installation");
        query.whereEqualTo("channels", "Ch1");
        push.setSearchCondition(query);
        push.setUserSettingValue(new JSONObject("{score:100}"));
        push.setBadgeIncrementFlag(true);
        push.setContentAvailable(true);
        push.setDialog(true);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse("2015-09-10");
        push.setDeliveryTime(date);
        push.setDeliveryExpirationDate(date);
        push.setDeliveryExpirationTime("10 day");
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        push.setAcl(acl);

        //check String
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        Assert.assertEquals("title", push.getTitle());
        Assert.assertEquals("message", push.getMessage());
        Assert.assertEquals("default", push.getSound());
        Assert.assertEquals("http://www.yahoo.co.jp/", push.getRichUrl());
        Assert.assertEquals("Category", push.getCategory());
        Assert.assertEquals("10 day", push.getDeliveryExpirationTime());
        Assert.assertEquals("com.sample.NCMBReceiver", push.getAction());

        //check Integer
        Assert.assertEquals(0, push.getBadgeSetting());
        Assert.assertEquals(0, push.getDeliveryPlanNumber());
        Assert.assertEquals(0, push.getDeliveryNumber());
        Assert.assertEquals(0, push.getStatus());

        //check JSONArray
        Assert.assertEquals(new JSONArray("[android,ios]"), push.getTarget());

        //check JSONObject
        Assert.assertEquals(new JSONObject("{channels:Ch1}").getString("channels"), push.getSearchCondition().getString("channels"));
        JSONObject userSettingValue = push.getUserSettingValue();
        Assert.assertEquals(new JSONObject("{score:100}").getInt("score"), userSettingValue.getInt("score"));
        Assert.assertNull(push.getError());

        //check boolean
        Assert.assertEquals(Boolean.TRUE, push.getBadgeIncrementFlag());
        Assert.assertEquals(Boolean.TRUE, push.getContentAvailable());
        Assert.assertEquals(Boolean.TRUE, push.getDialog());

        //check Date
        Date resultDate = format.parse("2015-09-10T15:00:00.000Z");
        Assert.assertEquals(resultDate, push.getDeliveryTime());
        Assert.assertEquals(null, push.getCreateDate());
        Assert.assertEquals(null, push.getUpdateDate());
        Assert.assertEquals(resultDate, push.getDeliveryExpirationDate());

        //check Acl
        NCMBAcl resultAcl = new NCMBAcl(new JSONObject("{*:{read:true,write:true}}"));
        Assert.assertEquals(resultAcl.getPublicReadAccess(), push.getAcl().getPublicReadAccess());
        Assert.assertEquals(resultAcl.getPublicWriteAccess(), push.getAcl().getPublicWriteAccess());
    }

    /**
     * - 内容：Instanceの値が正しく削除出来るかを確認する
     * - 結果：値が正しく削除されていること
     */
    @Test
    public void checkInstanceRemove() throws Exception {
        //set Instance data
        NCMBPush push = new NCMBPush();
        push.setObjectId("7FrmPTBKSNtVjajm");
        push.setTitle("title");
        push.setMessage("message");
        push.setSound("default");
        push.setRichUrl("http://www.yahoo.co.jp/");
        push.setCategory("Category");
        push.setAction("com.sample.NCMBReceiver");
        push.setBadgeSetting(0);
        push.setTarget(new JSONArray("[android,ios]"));
        NCMBQuery<NCMBInstallation> query = new NCMBQuery<>("installation");
        query.whereEqualTo("channels", "Ch1");
        push.setSearchCondition(query);
        push.setUserSettingValue(new JSONObject("{score:100}"));
        push.setBadgeIncrementFlag(true);
        push.setContentAvailable(true);
        push.setDialog(true);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse("2015-09-10");
        push.setDeliveryTime(date);
        push.setDeliveryExpirationDate(date);
        push.setDeliveryExpirationTime("10 day");
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        push.setAcl(acl);

        //check contains instance data
        Assert.assertTrue(push.containsKey("objectId"));
        Assert.assertTrue(push.containsKey("title"));
        Assert.assertTrue(push.containsKey("message"));
        Assert.assertTrue(push.containsKey("sound"));
        Assert.assertTrue(push.containsKey("richUrl"));
        Assert.assertTrue(push.containsKey("category"));
        Assert.assertTrue(push.containsKey("action"));
        Assert.assertTrue(push.containsKey("badgeSetting"));
        Assert.assertTrue(push.containsKey("target"));
        Assert.assertTrue(push.containsKey("searchCondition"));
        Assert.assertTrue(push.containsKey("userSettingValue"));
        Assert.assertTrue(push.containsKey("badgeIncrementFlag"));
        Assert.assertTrue(push.containsKey("contentAvailable"));
        Assert.assertTrue(push.containsKey("dialog"));
        Assert.assertTrue(push.containsKey("deliveryTime"));
        Assert.assertTrue(push.containsKey("deliveryExpirationDate"));
        Assert.assertTrue(push.containsKey("deliveryExpirationTime"));
        Assert.assertTrue(push.containsKey("acl"));

        //remove instance data
        push.remove("objectId");
        push.remove("title");
        push.remove("message");
        push.remove("sound");
        push.remove("richUrl");
        push.remove("category");
        push.remove("action");
        push.remove("badgeSetting");
        push.remove("target");
        push.remove("searchCondition");
        push.remove("userSettingValue");
        push.remove("badgeIncrementFlag");
        push.remove("contentAvailable");
        push.remove("dialog");
        push.remove("deliveryTime");
        push.remove("deliveryExpirationDate");
        push.remove("deliveryExpirationTime");
        push.remove("acl");

        //check contains instance data
        Assert.assertNull(push.getObjectId());
        Assert.assertNull(push.getTitle());
        Assert.assertNull(push.getMessage());
        Assert.assertNull(push.getSound());
        Assert.assertNull(push.getRichUrl());
        Assert.assertNull(push.getCategory());
        Assert.assertNull(push.getAction());
        Assert.assertEquals(0, push.getBadgeSetting());
        Assert.assertNull(push.getTarget());
        Assert.assertNull(push.getSearchCondition());
        Assert.assertNull(push.getUserSettingValue());
        Assert.assertNull(push.getBadgeIncrementFlag());
        Assert.assertNull(push.getContentAvailable());
        Assert.assertNull(push.getDialog());
        Assert.assertNull(push.getDeliveryTime());
        Assert.assertNull(push.getDeliveryExpirationDate());
        Assert.assertNull(push.getDeliveryExpirationTime());
        Assert.assertNull(push.getAcl());
    }

    //endregion

    @Test
    public void NCMBGcmReceiverSavedRecentPushNotoficationId() {

        String testPushId = "testPushId";

        NCMBGcmReceiver receiver = new NCMBGcmReceiver();
        Intent intent = new Intent(ShadowApplication.getInstance().getApplicationContext(), NCMBGcmListenerService.class);
        intent.putExtra("com.nifty.PushId", testPushId);
        receiver.onReceive(ShadowApplication.getInstance().getApplicationContext(), intent);


        SharedPreferences sp = RuntimeEnvironment.application.getSharedPreferences("ncmbPushId", Context.MODE_PRIVATE);
        Assert.assertEquals(testPushId, sp.getString("recentPushId", ""));
    }

    void checkGetResponse(NCMBPush push) throws Exception {
        //check String
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId());
        Assert.assertEquals("title", push.getTitle());
        Assert.assertEquals("message", push.getMessage());
        Assert.assertEquals("default", push.getSound());
        Assert.assertEquals("http://www.yahoo.co.jp/", push.getRichUrl());
        Assert.assertEquals("Category", push.getCategory());
        Assert.assertEquals("10 day", push.getDeliveryExpirationTime());
        Assert.assertEquals("com.sample.NCMBReceiver", push.getAction());

        //check Integer
        Assert.assertEquals(0, push.getBadgeSetting());
        Assert.assertEquals(3, push.getDeliveryPlanNumber());
        Assert.assertEquals(0, push.getDeliveryNumber());
        Assert.assertEquals(0, push.getStatus());

        //check JSONArray
        Assert.assertEquals(new JSONArray("[android,ios]"), push.getTarget());

        //check JSONObject
        Assert.assertEquals(0, push.getSearchCondition().length());
        JSONObject userSettingValue = push.getUserSettingValue();
        Assert.assertEquals(new JSONObject("{score:100}").getInt("score"), userSettingValue.getInt("score"));
        Assert.assertNull(push.getError());

        //check boolean
        Assert.assertEquals(Boolean.TRUE, push.getBadgeIncrementFlag());
        Assert.assertEquals(Boolean.FALSE, push.getContentAvailable());
        Assert.assertEquals(Boolean.TRUE, push.getDialog());

        //check Date
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.JAPAN);
        format.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Date resultDate = format.parse("2015-07-09T15:10:00.000");
        Assert.assertEquals(resultDate, push.getDeliveryTime());
        resultDate = format.parse("2015-07-09T06:08:45.668");
        Assert.assertEquals(resultDate, push.getCreateDate());
        resultDate = format.parse("2015-07-09T06:08:45.669");
        Assert.assertEquals(resultDate, push.getUpdateDate());
        Assert.assertNull(push.getDeliveryExpirationDate());

        //check Acl
        NCMBAcl resultAcl = new NCMBAcl(new JSONObject("{*:{read:true,write:true}}"));
        Assert.assertEquals(resultAcl.getPublicReadAccess(), push.getAcl().getPublicReadAccess());
        Assert.assertEquals(resultAcl.getPublicWriteAccess(), push.getAcl().getPublicWriteAccess());
    }
}
