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
import org.robolectric.annotation.Config;


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBConnectionTest {

    private MockWebServer mServer;

    @Before
    public void setup() throws Exception {

        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
    }

    @After
    public void teardown() {

    }

/*** Test Case ***/

    /**
     * - 内容：GET通信が成功することを確認する
     * - 結果：ステータスコード200及びresponseDataが返却されること
     */
    @Test
    public void connectionGetMethodReturn200() throws Exception {
        //モックサーバーURL設定
        String url = mServer.getUrl("/2013-09-01/classes/TestClass/7FrmPTBKSNtVjajm").toString();

        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest(url, Constants.HTTP_METHOD_GET, null, null, "sessionToken", "appKey", "clientKey");

        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse response = null;
        int statusCode = 0;
        NCMBException error = null;
        try {
            response = connection.sendRequest();
            statusCode = response.statusCode;
        } catch (NCMBException e) {
            error = e;
        }

        Assert.assertNull(error);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.statusCode);
        Assert.assertNotNull(response.responseData);

        //ステータスコード200及びresponseDataが返却されるか確認
        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("7FrmPTBKSNtVjajm", response.responseData.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.responseData.getString("createDate"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.responseData.getString("updateDate"));
        Assert.assertEquals("value", response.responseData.getString("key"));
    }

    /**
     * - 内容：POST通信が成功することを確認する
     * - 結果：ステータスコード201及びresponseDataが返却されること
     */
    @Test
    public void connectionPostMethodReturn201() throws Exception {
        //モックサーバーURL設定
        String url = mServer.getUrl("/2013-09-01/classes/TestClass").toString();

        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest(url,
                Constants.HTTP_METHOD_POST,
                "{'key':'value'}",
                null,
                "sessionToken",
                "appKey",
                "clientKey"
        );

        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse response = null;
        int statusCode = 0;
        NCMBException error = null;
        try {
            response = connection.sendRequest();
            statusCode = response.statusCode;
        } catch (NCMBException e) {
            error = e;
        }

        //ステータスコード201及びresponseDataが返却されるか確認
        Assert.assertNull(error);
        Assert.assertEquals(201, statusCode);
        Assert.assertEquals("7FrmPTBKSNtVjajm", response.responseData.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.responseData.getString("createDate"));
    }

    /**
     * - 内容：PUT通信が成功することを確認する
     * - 結果：ステータスコード200及びresponseDataが返却されること
     */
    @Test
    public void connectionPutMethodReturn200() throws Exception {
        //モックサーバーURL設定
        String url = mServer.getUrl("/2013-09-01/classes/TestClass/7FrmPTBKSNtVjajm").toString();

        //NCMBRequest作成
        NCMBRequest request = null;
        NCMBException error = null;
        try {
            request = new NCMBRequest(url, Constants.HTTP_METHOD_PUT, "content", null, "sessionToken", "appKey", "clientKey");
        } catch (NCMBException e) {
            error = e;
        }

        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse response = null;
        try {
            response = connection.sendRequest();
        } catch (NCMBException e) {
            error = e;
        }

        //ステータスコード200及びresponseDataが返却されるか確認
        //Assert.assertEquals(200, statusCode);
        Assert.assertNull(error);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", response.responseData.getString("updateDate"));
    }

    /**
     * - 内容：DELETE通信が成功することを確認する
     * - 結果：ステータスコード200及びresponseDataが返却されること
     */
    @Test
    public void requestDeleteMethodReturn200() throws Exception {
        //モックサーバーURL設定
        String url = mServer.getUrl("/2013-09-01/classes/TestClass/7FrmPTBKSNtVjajm").toString();

        //NCMBRequest作成
        NCMBRequest request = null;
        NCMBException error = null;
        try {
            request = new NCMBRequest(url, Constants.HTTP_METHOD_DELETE, null, null, "sessionToken", "appKey", "clientKey");
        } catch (NCMBException e) {
            error = e;
        }

        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse response = null;

        try {
            response = connection.sendRequest();
        } catch (NCMBException e) {
            error = e;
        }

        //ステータスコード200及びresponseDataが返却されるか確認
        Assert.assertNull(error);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertNull(response.responseData);

    }

    /**
     * - 内容：許可されていない操作をリクエストする
     * - 結果：NCMBExceptionにエラーコードE403003が返却されること
     */
    @Test
    public void connectionOperationForbidden() throws Exception {
        //許可されていないAPIリクエストのパスを設定
        String url = mServer.getUrl("/2013-09-01/classes/user").toString();

        //NCMBRequest作成
        NCMBRequest request = null;
        NCMBException error = null;
        try {
            request = new NCMBRequest(url, Constants.HTTP_METHOD_POST, "content", null, "sessionToken", "appKey", "clientKey");
        } catch (NCMBException e) {
            System.out.println("CreateRequest error");
            error = e;
        }


        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse res = null;
        try {
            res = connection.sendRequest();
        } catch (NCMBException e) {
            System.out.println("Connection error");
            e.printStackTrace();
            error = e;
        }

        Assert.assertEquals(NCMBException.OPERATION_FORBIDDEN, error.getCode());
    }

    /**
     * - 内容：不正なパスを設定し通信が失敗することを確認する
     * - 結果：NCMBExceptionにエラーコードE404002及びエラーメッセージが返却されること
     */
    @Test
    public void connectionInvalidPathReturn404() throws JSONException {
        //モックサーバーURLに不正なパスを設定
        String url = mServer.getUrl("/2013-09-01/xxxxx").toString();

        //NCMBRequest作成
        NCMBRequest request = null;
        NCMBException error = null;
        try {
            request = new NCMBRequest(url, Constants.HTTP_METHOD_POST, "content", null, "sessionToken", "appKey", "clientKey");
        } catch (NCMBException e) {
            error = e;
        }


        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse res = null;
        try {
            res = connection.sendRequest();
        } catch (NCMBException e) {
            error = e;
        }

        //エラーコードE404002及びエラーメッセージが返却されるか確認
        Assert.assertEquals(NCMBException.SERVICE_NOT_FOUND, error.getCode());
    }

    @Test
    public void requestSearchAPI() {
        //モックサーバーURL設定
        String url = mServer.getUrl("/2013-09-01/classes/TestClass").toString();

        //NCMBRequest作成
        NCMBRequest request = null;
        NCMBException error = null;
        try {
            request = new NCMBRequest(url, Constants.HTTP_METHOD_GET, null, new JSONObject("{'where':{'key':'value'}, 'limit':1}"), "sessionToken", "appKey", "clientKey");
        } catch (NCMBException e) {
            error = e;
        } catch (JSONException e) {
            error = new NCMBException(NCMBException.GENERIC_ERROR, "Invalid json query");
        }

        //NCMBURLConnectionを作成
        NCMBConnection connection = new NCMBConnection(request);

        //通信
        NCMBResponse response = null;
        String assertObjectId = null;
        try {
            response = connection.sendRequest();
            JSONArray results = response.responseData.getJSONArray("results");
            JSONObject firstObj = results.getJSONObject(0);
            assertObjectId = firstObj.getString("objectId");
        } catch (NCMBException e) {
            error = e;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ステータスコード200及びresponseDataが返却されるか確認
        Assert.assertNull(error);
        Assert.assertEquals(200, response.statusCode);
        Assert.assertEquals("8FgKqFlH8dZRDrBJ", assertObjectId);


    }

    /**
     * - 内容：timeoutの設定が出来るかを確認する
     * - 結果：タイムアウト時間5000ミリ秒が取得出来ること
     */
    @Test
    public void connection_timeout() throws Exception {
        //check set&get
        NCMB.setTimeout(5000);
        Assert.assertEquals(5000, NCMB.getTimeout());
    }

}
