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

import java.io.File;
import java.io.FileOutputStream;

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

    /**
     * - 内容：エラーの場合NCMBExceptionをスローする
     * - 結果：NCMBExceptionがスロー出来る事
     */
    @Test
    public void readFile_NCMBException() throws Exception {
        //post
        NCMBException error = null;
        try {
            //空のファイルのみを準備する
            String fileData = "";
            File file = NCMBLocalFile.create("currentInstallation");
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();
            //getCurrentInstallation実施()
            NCMBInstallation current = NCMBInstallation.getCurrentInstallation();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNotNull(error);
        Assert.assertEquals("java.lang.NullPointerException",  error.getMessage());
    }

}
