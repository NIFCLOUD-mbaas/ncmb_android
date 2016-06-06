package com.nifty.cloud.mb.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SimpleTimeZone;

/**
 * Test for NCMBInstallationServiceTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class NCMBInstallationServiceTest {

    private MockWebServer mServer;
    static final String PACKAGE_NAME = "com.nifty.cloud.mb.core";
    static final String APP_VERSION = "1.0";
    static final String APP_NAME = "ncmb-core";
    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {

        FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);

        //set application information
        RobolectricPackageManager rpm = (RobolectricPackageManager) RuntimeEnvironment.application.getPackageManager();
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = PACKAGE_NAME;
        packageInfo.versionName = APP_VERSION;
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = PACKAGE_NAME;
        packageInfo.applicationInfo.name = APP_NAME;
        rpm.addPackage(packageInfo);
        RuntimeEnvironment.setRobolectricPackageManager(rpm);

        //setup mocServer
        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
        String mockServerUrl = mServer.getUrl("/").toString();

        //initialization
        NCMB.initialize(RuntimeEnvironment.application,
                "applicationKey",
                "clientKey",
                mockServerUrl,
                null);


        Assert.assertEquals(
                NCMB.getCurrentContext().context.getApplicationInfo().name,
                APP_NAME
        );

        MockitoAnnotations.initMocks(this);

        ShadowLog.stream = System.out;

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @After
    public void teardown() throws Exception {
        NCMBInstallationService.clearCurrentInstallation();
        mServer.shutdown();
    }

    /**
     * - 内容：createInstallationが成功する事を確認する
     * - 結果：objectIdを含むJSONObjectが返却される事
     */
    @Test
    public void createInstallation() throws Exception {
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", new JSONObject());

        //checkAssert
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));
    }

    /**
     * - 内容：createInstallationInBackgroundが成功する事を確認する
     * - 結果：objectIdを含むJSONObjectが返却される事
     */
    @Test
    public void createInstallationInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.createInstallationInBackground("xxxxxxxxxxxxxxxxxxx", new JSONObject(), new ExecuteServiceCallback() {
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
     * - 内容：updateInstallationが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void updateInstallation() throws Exception {
        Exception error = null;
        JSONObject json = null;
        try {
            NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
            JSONObject updateData = new JSONObject();
            updateData.put("key", "value_update");
            json = installationService.updateInstallation("7FrmPTBKSNtVjajm", updateData);
        } catch (Exception e) {
            error = e;
        }

        //checkAssert
        Assert.assertNull(error);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
    }

    /**
     * - 内容：updateInstallationInBackgroundが成功する事を確認する
     * - 結果：updateDateを含むJSONObjectが返却される事
     */
    @Test
    public void updateInstallationInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject json = new JSONObject();
        json.put("key", "value_update");
        installationService.updateInstallationInBackground("7FrmPTBKSNtVjajm", json, new ExecuteServiceCallback() {
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
     * - 内容：deleteInstallationが成功する事を確認する
     * - 結果：エラーが発生しない事
     */
    @Test
    public void deleteInstallation() throws Exception {
        Exception error = null;
        try {
            NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
            installationService.deleteInstallation("7FrmPTBKSNtVjajm");
        } catch (Exception e) {
            error = e;
        }

        //checkAssert
        Assert.assertNull(error);
    }

    /**
     * - 内容：deleteInstallationInBackgroundが成功する事を確認する
     * - 結果：エラーが発生しない事
     */
    @Test
    public void deleteInstallationInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.deleteInstallationInBackground("7FrmPTBKSNtVjajm", new DoneCallback() {
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
     * - 内容：getInstallationが成功する事を確認する
     * - 結果：objectId,valueを含むJSONObjectが返却される事
     */
    @Test
    public void fetchInstallation() throws Exception {
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        NCMBInstallation installation = installationService.fetchInstallation("7FrmPTBKSNtVjajm");

        //checkAssert
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getString("objectId"));
        Assert.assertEquals("value", installation.getString("key"));
    }

    /**
     * - 内容：getInstallationInBackgroundが成功する事を確認する
     * - 結果：objectId,valueを含むJSONObjectが返却される事
     */
    @Test
    public void fetchInstallationInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.fetchInstallationInBackground("7FrmPTBKSNtVjajm", new FetchCallback<NCMBInstallation>() {
            @Override
            public void done(NCMBInstallation installation, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getString("objectId"));
                Assert.assertEquals("value", installation.getString("key"));

                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：searchInstallationが成功する事を確認する
     * - 結果：deviceTypeがandroidの情報を含むJSONObjectが返却される事
     */
    @Test
    public void searchInstallation() throws Exception {
        //search condition
        JSONObject query = new JSONObject();
        query.put("where", new JSONObject("{deviceType:android}"));
        query.put("limit", 2);

        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        List results = installationService.searchInstallation(query);

        //checkAssert
        for (int i = 0; i < results.size(); i++) {
            Assert.assertEquals("android", ((NCMBInstallation) results.get(i)).getDeviceType());
        }
    }

    /**
     * - 内容：searchInstallationInBackgroundが成功する事を確認する
     * - 結果：deviceTypeがandroidの情報を含むJSONObjectが返却される事
     */
    @Test
    public void searchInstallationInBackground() throws Exception {
        Assert.assertFalse(callbackFlag);
        //search condition
        JSONObject query = new JSONObject();
        query.put("where", new JSONObject("{deviceType:android}"));
        query.put("limit", 2);

        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.searchInstallationInBackground(query, new SearchInstallationCallback() {
            public void done(ArrayList<NCMBInstallation> results, NCMBException e) {
                //checkAssert
                Assert.assertNull(e);
                for (int i = 0; i < results.size(); i++) {
                    Assert.assertEquals("android", results.get(i).getDeviceType());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：プロパティに値が正しく設定されているかを確認する
     * - 結果：値が正しく設定されている事
     */
    @Test
    public void installationPropertyCheck() throws Exception {
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        NCMBInstallation installation = installationService.fetchInstallation("7FrmPTBKSNtVjajm");

        //checkAssert
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals(APP_NAME, installation.getApplicationName());
        Assert.assertEquals(APP_VERSION, installation.getAppVersion());
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        Assert.assertEquals("android", installation.getDeviceType());
        Assert.assertEquals(NCMB.SDK_VERSION, installation.getSDKVersion());
        Assert.assertEquals("Asia/Tokyo", installation.getTimeZone());
        Assert.assertEquals(0, installation.getBadge());

        //checkList
        JSONArray resultList = new JSONArray("[Ch1]");
        Assert.assertEquals(resultList, installation.getChannels());

        //checkDate
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.JAPAN);
        format.setTimeZone(new SimpleTimeZone(0, "GMT"));
        Date resultDate = format.parse("2014-06-03T11:28:30.348Z");
        Assert.assertEquals(resultDate, installation.getCreateDate());
        Assert.assertEquals(resultDate, installation.getUpdateDate());

        //check custom filed
        Assert.assertEquals("value", installation.getValue("key"));
    }

    /**
     * - 内容：チャネルを登録出来るか確認する
     * - 結果：Activityデータとiconデータが登録されていること
     */
    @Test
    public void subscribe() throws Exception {
        //create currentInstallation
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", new JSONObject());
        Assert.assertEquals("7FrmPTBKSNtVjajm", NCMBInstallation.currentInstallation.getObjectId());

        //set file data
        String channel = "Ch1";
        Activity activity = new Activity();
        NCMBInstallation.subscribe(channel, activity.getClass(), android.R.drawable.sym_def_app_icon);

        //get file data
        File dir = new File(NCMB.getCurrentContext().context.getDir("NCMB", Context.MODE_PRIVATE), "channels");
        File file = new File(dir, channel);
        JSONObject json = NCMBLocalFile.readFile(file);

        //check
        Assert.assertEquals("android.app.Activity", json.getString("activityClass"));
        Assert.assertEquals(android.R.drawable.sym_def_app_icon, json.getInt("icon"));
    }

    /**
     * - 内容：登録したチャネルが取得出来るか確認する
     * - 結果：チャネル名が取得されること
     */
    @Test
    public void getSubscriptions() throws Exception {
        //create currentInstallation
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", new JSONObject());
        Assert.assertEquals("7FrmPTBKSNtVjajm", NCMBInstallation.currentInstallation.getObjectId());

        //set file data
        String channel = "Ch1";
        Activity activity = new Activity();
        NCMBInstallation.subscribe(channel, activity.getClass(), android.R.drawable.sym_def_app_icon);

        //get file data
        Set<String> set = NCMBInstallation.getSubscriptions();

        //check
        Assert.assertTrue(set.contains(channel));
    }

    /**
     * - 内容：登録したチャネルが削除出来るか確認する
     * - 結果：getSubscriptionsで空のオブジェクトが取得出来ること
     */
    @Test
    public void unsubscribe() throws Exception {

        //set file data
        String channel = "Ch1";
        Activity activity = new Activity();
        NCMBInstallation.subscribe(channel, activity.getClass(), android.R.drawable.sym_def_app_icon);

        //delete file data
        NCMBInstallation.unsubscribe(channel);

        //check
        Assert.assertEquals(0, NCMBInstallation.getSubscriptions().size());
    }

    /**
     * - 内容：currentInstallationの作成が成功する事を確認する
     * - 結果：currentInstallationにobjectId,key,createDateの値が含まれている事
     */
    @Test
    public void currentInstallation_POST() throws Exception {
        //connect post
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject params = new JSONObject();
        params.put("key", "value");
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", params);
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", json.getString("createDate"));

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("7FrmPTBKSNtVjajm", currentInstallation.getObjectId());
        Assert.assertEquals("value", currentInstallation.getValue("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", currentInstallation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2014-06-03T11:28:30.348Z");
        Assert.assertEquals(resultDate, currentInstallation.getCreateDate());
    }

    /**
     * - 内容：currentInstallationの更新が成功する事を確認する
     * - 結果：currentInstallationのkeyの値が更新され、updateDateの値が含まれている事
     */
    @Test
    public void currentInstallation_PUT() throws Exception {
        //connect post
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject params = new JSONObject();
        params.put("key", "value");
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", params);
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("value", currentInstallation.getValue("key"));

        //connect put
        JSONObject update = new JSONObject();
        update.put("key", "value_update");
        json = installationService.updateInstallation(currentInstallation.getObjectId(), update);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));

        //check currentInstallation
        currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("7FrmPTBKSNtVjajm", currentInstallation.getObjectId());
        Assert.assertEquals("value_update", currentInstallation.getValue("key"));
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2014-06-04T11:28:30.348Z");
        Assert.assertEquals(resultDate, currentInstallation.getUpdateDate());
    }

    /**
     * - 内容：currentInstallationの削除が成功する事を確認する
     * - 結果：currentInstallationの値が削除されている事
     */
    @Test
    public void currentInstallation_DELETE() throws Exception {
        //connect post
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject params = new JSONObject();
        params.put("key", "value");
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", params);
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("value", currentInstallation.getValue("key"));

        //connect delete
        installationService.deleteInstallation(currentInstallation.getObjectId());

        //check currentInstallation
        currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertNull(currentInstallation.getObjectId());
    }

    /**
     * - 内容：currentInstallationの自動削除が成功する事を確認する
     * - 結果：currentInstallationの値が削除されている事
     */
    @Test
    public void currentInstallation_AutoDelete_PUT() throws Exception {
        //connect post
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject params = new JSONObject();
        params.put("error", "test");
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", params);
        Assert.assertEquals("errorObjectId", json.getString("objectId"));

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", currentInstallation.getDeviceToken());

        //connect auto delete
        NCMBException error = null;
        try {
            installationService.updateInstallation("errorObjectId", null);
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(NCMBException.DATA_NOT_FOUND, error.getCode());

        //check currentInstallation
        currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertNull(currentInstallation.getObjectId());
    }

    /**
     * - 内容：currentInstallationの自動削除が成功する事を確認する
     * - 結果：currentInstallationの値が削除されている事
     */
    @Test
    public void currentInstallation_AutoDelete_DELETE() throws Exception {
        //connect post
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject params = new JSONObject();
        params.put("error", "test");
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", params);
        Assert.assertEquals("errorObjectId", json.getString("objectId"));

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", currentInstallation.getDeviceToken());

        //connect auto delete inBackground
        installationService.deleteInstallationInBackground("errorObjectId", new DoneCallback() {

            @Override
            public void done(NCMBException e) {
                Assert.assertNotNull(e);
                Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());

                //check currentInstallation
                NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
                Assert.assertNull(currentInstallation.getObjectId());
            }
        });
    }

    /**
     * - 内容：currentInstallationのファイルが生成されている事を確認する
     * - 結果：objectId,key,createDateの値を含むファイルが作成されている事
     */
    @Test
    public void currentInstallation_newLocalFile() throws Exception {
        //create currentInstallation
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", new JSONObject("{key:value}"));
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", json.getString("createDate"));

        //check new create localFile
        File localFile = new File(NCMB.getCurrentContext().context.getDir("NCMB", Context.MODE_PRIVATE), "currentInstallation");
        if (!localFile.exists()) {
            Assert.fail("currentInstallationFile is not created.");
        }

        //check localFile data
        JSONObject localData = new JSONObject();
        try {
            BufferedReader br = new BufferedReader(new FileReader(localFile));
            String information = br.readLine();
            br.close();
            localData = new JSONObject(information);
        } catch (IOException | JSONException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals("7FrmPTBKSNtVjajm", localData.getString("objectId"));
        Assert.assertEquals("value", localData.getString("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", localData.getString("deviceToken"));
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals("2014-06-03T11:28:30.348Z", localData.getString("createDate"));
    }

    /**
     * - 内容：v1時のパスで生成されたCurrentInstallation情報がv2で正しく取得されている事を確認する
     * - 結果：objectId,key,createDate,sdkVersionの値を含むcurrentInstallationが取得されている事
     */
    @Test
    public void currentInstallation_v1_From_v2() throws Exception {
        //create currentInstallation data
        JSONObject localFileData = new JSONObject();
        localFileData.put("appVersion", "1.0");
        localFileData.put("deviceToken", "dummyDeviceToken");
        localFileData.put("objectId", "non-update-value-id");
        localFileData.put("key", "value");
        localFileData.put("applicationName", "AndroidSDK_v1");
        localFileData.put("classname", "installation");
        localFileData.put("channels", new JSONArray("[Ch2]"));
        localFileData.put("timeZone", "Asia\\/Tokyo");
        localFileData.put("createDate", "2015-09-10T02:24:03.597Z");
        localFileData.put("updateDate", "2015-09-11T02:24:03.597Z");
        localFileData.put("sdkVersion", "1.5.0");

        //create currentInstallation from v1 path
        File localFile = new File(NCMB.getCurrentContext().context.getDir("NCMB", Context.MODE_PRIVATE), "currentInstallation");
        try {
            FileOutputStream out = new FileOutputStream(localFile);
            out.write(localFileData.toString().getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //check currentInstallation
        NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("1.5.0", currentInstallation.getSDKVersion());
        Assert.assertEquals("non-update-value-id", currentInstallation.getObjectId());
        Assert.assertEquals("value", currentInstallation.getString("key"));
        Assert.assertEquals("dummyDeviceToken", currentInstallation.getDeviceToken());
        Assert.assertEquals("1.0", currentInstallation.getAppVersion());
        Assert.assertEquals("AndroidSDK_v1", currentInstallation.getApplicationName());
        Assert.assertEquals(new JSONArray("[Ch2]"), currentInstallation.getChannels());
        Assert.assertEquals("Asia\\/Tokyo", currentInstallation.getTimeZone());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultCreateDate = format.parse("2015-09-10T02:24:03.597Z");
        Assert.assertEquals(resultCreateDate, currentInstallation.getCreateDate());
        Date resultUpdateDate = format.parse("2015-09-11T02:24:03.597Z");
        Assert.assertEquals(resultUpdateDate, currentInstallation.getUpdateDate());

        //check upDate currentInstallation
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject updateJson = installationService.updateInstallation(currentInstallation.getObjectId(), null);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", updateJson.getString("updateDate"));
        currentInstallation = NCMBInstallation.getCurrentInstallation();
        Assert.assertEquals("2.2.2", currentInstallation.getSDKVersion());
    }

    /*
     * - 内容：paramsにnullを指定してもJSONが返却される事を確認する
     * - 結果：objectId,updateDateが返却される事
     */
    @Test
    public void argumentNullCheckForParams() throws Exception {
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);

        //POST params check
        JSONObject json = installationService.createInstallation("xxxxxxxxxxxxxxxxxxx", null);
        Assert.assertEquals("7FrmPTBKSNtVjajm", json.getString("objectId"));

        //PUT params check
        json = installationService.updateInstallation("non-update-value-id", null);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", json.getString("updateDate"));
    }

    /**
     * - 内容：idにnullを指定した場合エラーが発生する事を確認する
     * - 結果：エラーが返却される事
     */
    @Test
    public void argumentNullCheckForId() throws Exception {
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        NCMBException error = null;

        //POST registrationId check
        try {
            installationService.createInstallation(null, new JSONObject());
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);

        //PUT installationId check
        try {
            installationService.updateInstallation(null, new JSONObject());
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}
