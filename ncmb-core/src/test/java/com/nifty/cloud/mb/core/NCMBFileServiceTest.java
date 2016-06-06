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

import java.util.List;

@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBFileServiceTest {
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

    /*** Test Case NCMBFileService ***/

    /**
     * - 内容：saveFileInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void saveFileInBackground() throws Exception {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);

        byte[] data = "Hello,NCMB".getBytes();
        fileService.saveFileInBackground("Sample.txt", data, new JSONObject(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：updateFileInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void updateFileInBackground() throws Exception {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);

        fileService.updateFileInBackground("Sample.txt", new JSONObject(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：deleteFileInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void deleteFileInBackground() throws Exception {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);

        fileService.deleteFileInBackground("Sample.txt", new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：fetchFileInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void fetchFileInBackground() throws Exception {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);

        fileService.fetchFileInBackground("Sample.txt", new FetchFileCallback() {
            @Override
            public void done(byte[] data, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }

    /**
     * - 内容：searchFileInBackgroundのコールバックが実行されることを確認する
     * - 結果：コールバックが実行されること
     */
    @Test
    public void searchFileInBackground() throws Exception {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);

        fileService.searchFileInBackground(new JSONObject(), new SearchFileCallback() {
            @Override
            public void done(List<NCMBFile> files, NCMBException e) {
                callbackFlag = true;
            }
        });
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(callbackFlag);
    }
}
