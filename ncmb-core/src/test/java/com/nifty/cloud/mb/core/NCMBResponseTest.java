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

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * NCMBResponse自動化テストクラス
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBResponseTest {
    private MockWebServer mServer;

    //URLごとにレスポンスを定義するdispatcherを作成
    final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (request.getMethod().equals(Constants.HTTP_METHOD_GET) && request.getPath().equals("/2013-09-01/classes/TestClass")) {
                return new MockResponse().setHeader("X-NCMB-Response-Signature", "tLTbS3aMV7PT2N8Qy38UZoNjySRFHmJJ3tEPS1J2SS0=")
                        .setHeader("Content-Type", "application/json")
                        .setResponseCode(201)
                        .setBody(readJsonResponse("valid_get_response.json"));
            }
            return new MockResponse().setResponseCode(404).setBody(readJsonResponse("valid_error_response.json"));
        }
    };

    /***
     * Utilities
     ***/

    public String readJsonResponse(String file_name) {
        File file = new File("src/test/assets/json/" + file_name);
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

    /*** Test Case ***/

    /**
     * - 内容：プロパティに値が正しく設定されているかを確認する
     * - 結果：値が正しく設定されていること
     */
    @Test
    public void responsePropertyCheck() throws Exception {
        URL url = mServer.getUrl("/2013-09-01/classes/TestClass/7FrmPTBKSNtVjajm");

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        NCMBResponse response = new NCMBResponse(urlConnection.getInputStream(),
                urlConnection.getResponseCode(),
                urlConnection.getHeaderFields());

        //プロパティの値を取得
        int statusCode = response.statusCode;
        JSONObject result = response.responseData;

        //値が設定されているか確認
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(result);
        Assert.assertEquals("7FrmPTBKSNtVjajm", result.getString("objectId"));
    }

    /**
     * - 内容：レスポンスシグネチャが正しく比較されているかを確認する
     * - 結果：レスポンスシグネチャが正しく比較されること
     */
    @Test
    public void responseSignatureCheck() throws Exception {
        NCMB.enableResponseValidation(true);

        // 検証用のAPIキーでレスポンスシグネチャを確認.該当アプリは削除済
        NCMBRequest request = new NCMBRequest(
                "https://mb.api.cloud.nifty.com/2013-09-01/classes/ResponseSignatureTest",
                Constants.HTTP_METHOD_POST,
                "{\"key\":\"value\"}",
                null,
                null,
                null,
                null,
                null,
                "6e58668eb431b6cef540116a18ac269eeee83383acd61cdb08101e2531cdeece",
                "9714b04345bcdad2d609d3b9ba9bd89c7bed612189d4e6a923c70bf87da60e76",
                "2016-05-27T04%3A46%3A09.376Z");

        URL url = mServer.getUrl("/2013-09-01/classes/ResponseSignatureTest");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write("{\"key\":\"value\"}");
        writer.flush();
        writer.close();
        urlConnection.connect();
        NCMBResponse response = new NCMBResponse(urlConnection.getInputStream(),
                urlConnection.getResponseCode(),
                urlConnection.getHeaderFields());

        try {
            NCMBConnection connection = new NCMBConnection(request);
            connection.responseSignatureCheck(urlConnection, response, request);
        } catch (NCMBException error) {
            Assert.fail(error.getMessage());
        }

    }

    /**
     * - 内容：不正データでレスポンスシグネチャの検証が行われた場合にエラーが返却されることを確認する
     * - 結果：エラーが返却されること
     */
    @Test
    public void responseSignatureCheck_error() throws Exception {
        NCMB.enableResponseValidation(true);

        NCMBRequest request = new NCMBRequest(
                "https://mb.api.cloud.nifty.com/2013-09-01/classes/ResponseSignatureTest",
                Constants.HTTP_METHOD_POST,
                "{\"key\":\"value\"}",
                null,
                null,
                null,
                null,
                null,
                "dummy_application_key",
                "dummy_client_key",
                "2016-05-27T04%3A46%3A09.376Z");

        URL url = mServer.getUrl("/2013-09-01/classes/ResponseSignatureTest");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write("{\"key\":\"value\"}");
        writer.flush();
        writer.close();
        urlConnection.connect();
        NCMBResponse response = new NCMBResponse(urlConnection.getInputStream(),
                urlConnection.getResponseCode(),
                urlConnection.getHeaderFields());

        try {
            NCMBConnection connection = new NCMBConnection(request);
            connection.responseSignatureCheck(urlConnection, response, request);
            Assert.fail();
        } catch (NCMBException error) {
            Assert.assertEquals(NCMBException.INVALID_RESPONSE_SIGNATURE, error.getCode());
            Assert.assertEquals("Authentication error by response signature incorrect.", error.getMessage());
        }

    }


}
