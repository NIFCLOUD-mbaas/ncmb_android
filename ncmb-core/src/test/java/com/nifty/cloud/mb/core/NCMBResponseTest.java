package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import junit.framework.Assert;

import org.apache.maven.artifact.ant.shaded.FileUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * NCMBResponse自動化テストクラス
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class NCMBResponseTest {
    private MockWebServer mServer;

    //URLごとにレスポンスを定義するdispatcherを作成
    final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getMethod().equals(Constants.HTTP_METHOD_GET) && request.getPath().equals("/2013-09-01/classes/TestClass")){
                return new MockResponse().setHeader("X-NCMB-Response-Signature","tLTbS3aMV7PT2N8Qy38UZoNjySRFHmJJ3tEPS1J2SS0=")
                                         .setHeader("Content-Type","application/json")
                                         .setResponseCode(201)
                                         .setBody(readJsonResponse("valid_get_response.json"));
            }
            return new MockResponse().setResponseCode(404).setBody(readJsonResponse("valid_error_response.json"));
        }
    };

    /*** Utilities ***/

    public String readJsonResponse(String file_name) {
        File file = new File("src/test/assets/json/"+file_name);
        String json = null;
        try {
            json = FileUtils.fileRead(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Before
    public void setup() throws Exception {

        mServer = new MockWebServer();
        mServer.setDispatcher(dispatcher);
        mServer.start();
    }

    @After
    public void teardown() {

    }

    /*** Test Case ***/

    /**
     * - 内容：プロパティに値が正しく設定されているかを確認する
     * - 結果：値が正しく設定されていること
     */
    @Test
    public void responsePropertyCheck() throws Exception{
        URL url = mServer.getUrl("/2013-09-01/classes/TestClass");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        NCMBResponse response = new NCMBResponse(urlConnection.getInputStream(),
                                                 urlConnection.getResponseCode(),
                                                 urlConnection.getHeaderFields());

        //プロパティの値を取得
        int statusCode = response.statusCode;
        JSONObject result = response.responseData;

        //値が設定されているか確認
        Assert.assertEquals(201, statusCode);
        Assert.assertNotNull(result);
        Assert.assertEquals("7FrmPTBKSNtVjajm", result.getString("objectId"));
    }

    /**
     * - 内容：レスポンスシグネチャが正しく比較されているかを確認する
     * - 結果：レスポンスシグネチャが正しく比較されること
     */
    @Test
    public void responseSignatureCheck() throws Exception{
        //レスポンスシグネチャ検証未実装
    }

}
