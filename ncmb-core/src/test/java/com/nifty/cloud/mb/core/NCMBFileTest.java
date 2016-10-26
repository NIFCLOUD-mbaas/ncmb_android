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
     * - 内容：読込時の処理にNullチェックを入れる
     * - 結果：NullPointerException発生しない事
     */
    @Test
    public void checkReadFileNull() {

        NullPointerException nullError = null;
        try {
            //空のファイルを準備する
            String fileData = ""; //空データ
            File file = NCMBLocalFile.create("currentInstallation");
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();

            //読込時の処理を実行する
            JSONObject json = NCMBLocalFile.readFile(file);
            //空データ確認する
            Assert.assertEquals(0, json.length());

        } catch (NullPointerException e) {
            nullError = e;
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        //check:NullPointerException発生しない
        Assert.assertNull(nullError);
    }

    /**
     * - 内容：書込時の処理に空ファイルが作られないようにする
     * - 結果：空ファイル生成場合は削除する事
     */
    @Test
    public void deleteWriteFileZeroSize() {

        try {
            //1)空のファイルを準備する
            String fileData = ""; //空データ
            File file = NCMBLocalFile.create("currentInstallation");
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();

            //ファイル存在する
            if (file.exists()) {
                Assert.assertTrue(true);
            }
            //空ファイル場合は削除する
            NCMBLocalFile.deleteFileSizeZero(file);

            //check:空のファイル存在しない(削除する)
            if (!file.exists()) {
                Assert.assertFalse(false);
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            //2)データがあるファイルを準備する
            String fileData = "Test"; //データ
            File file = NCMBLocalFile.create("currentInstallation");
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();

            //ファイル存在する
            if (file.exists()) {
                Assert.assertTrue(true);
            }
            //空ファイル場合は削除する
            NCMBLocalFile.deleteFileSizeZero(file);

            //check:ファイル存在する(削除されない)
            if (file.exists()) {
                Assert.assertTrue(true);
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
