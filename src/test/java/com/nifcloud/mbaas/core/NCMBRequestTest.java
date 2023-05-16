/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifcloud.mbaas.core;

import android.os.Build;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.util.ReflectionHelpers;

/**
 * NCMBRequest自動化テストクラス
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBRequestTest {

    @Before
    public void setup() throws Exception {
        ShadowLog.stream = System.out;
    }

    /*** Test Case ***/

    /**
     * - 内容：プロパティに値が正しく設定されているかを確認する
     * - 結果：値が正しく設定されていること
     */
    @Test
    public void requestPropertyCheck() throws Exception {
        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest(
                "https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass",
                Constants.HTTP_METHOD_POST,
                "content",
                null,
                null,
                null,
                null,
                "sessionToken",
                "applicationKey",
                "clientKey",
                "2015-06-12T02:59:52.367Z");

        //プロパティ取得
        String url = request.getUrl().toString();
        String type = request.getMethod();
        String content = request.getContent();
        JSONObject query = request.getQueryString();
        String sessionToken = request.getSessionToken();
        String applicationKey = request.getApplicationKey();
        String clientKey = request.getClientKey();
        String timeStamp = request.getTimestamp();

        //値が正しく設定されているか確認
        Assert.assertEquals("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass", url);
        Assert.assertEquals(Constants.HTTP_METHOD_POST, type);
        Assert.assertEquals("content", content);
        Assert.assertNull(query);
        Assert.assertEquals("sessionToken", sessionToken);
        Assert.assertEquals("applicationKey", applicationKey);
        Assert.assertEquals("clientKey", clientKey);
        Assert.assertEquals("2015-06-12T02:59:52.367Z", timeStamp);
    }

    /**
     * - 内容：ヘッダーが正しく設定されているかを確認する
     * - 結果：ヘッダーが正しく設定されること
     */
    @Test
    public void requestHeaderCheck() throws Exception {

        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass",
                Constants.HTTP_METHOD_GET,
                null,
                null,
                null,
                null,
                new JSONObject("{\"where\":{\"testKey\":\"testValue\"}}"),
                null,
                "6145f91061916580c742f806bab67649d10f45920246ff459404c46f00ff3e56",
                "1343d198b510a0315db1c03f3aa0e32418b7a743f8e4b47cbff670601345cf75",
                "2013-12-02T02:44:35.452Z");

        //ヘッダーのKeyを確認
        Assert.assertEquals("6145f91061916580c742f806bab67649d10f45920246ff459404c46f00ff3e56",
                request.getRequestProperty("X-NCMB-Application-Key"));

//        Assert.assertTrue(request.getRequestProperty("X-NCMB-Signature"));
//        Assert.assertTrue(headers.containsKey("X-NCMB-Timestamp"));
//        Assert.assertTrue(headers.containsKey("X-NCMB-Apps-Session-Token"));
//        //ヘッダーのValueを確認
//        Assert.assertEquals("appKey",headers.get("X-NCMB-Application-Key").toString());
//        Assert.assertEquals("sessionToken",headers.get("X-NCMB-Apps-Session-Token").toString());
//        Assert.assertNotNull(headers.get("X-NCMB-Signature"));
//        Assert.assertNotNull(headers.get("X-NCMB-Timestamp"));
    }

    /**
     * - 内容：シグネチャが正しく生成されているかを確認する
     * - 結果：ドキュメントのシグネチャと一致すること(https://mbaas.nifcloud.com/doc/current/rest/common/signature.html)
     */
    @Test
    public void requestSignatureCheck() throws Exception {
        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass",
                Constants.HTTP_METHOD_GET,
                null,
                null,
                null,
                null,
                new JSONObject("{\"where\":{\"testKey\":\"testValue\"}}"),
                null,
                "6145f91061916580c742f806bab67649d10f45920246ff459404c46f00ff3e56",
                "1343d198b510a0315db1c03f3aa0e32418b7a743f8e4b47cbff670601345cf75",
                "2013-12-02T02:44:35.452Z");

        //シグネチャが正しく生成されているか確認

        Assert.assertEquals("AltGkQgXurEV7u0qMd+87ud7BKuueldoCjaMgVc9Bes=", request.getRequestProperty("X-NCMB-Signature"));
    }

    /**
     * - 内容：独自UserAgentが正しく設定されているかを確認する
     * - 結果：独自UserAgentが正しく設定されること
     */
    @Test
    public void userAgentCheck() throws Exception {

        //OSのversionを設定
        ReflectionHelpers.setStaticField(Build.VERSION.class, "RELEASE", "4.1");

        //NCMBRequest作成
        NCMBRequest request = new NCMBRequest("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass",
                Constants.HTTP_METHOD_POST,
                "{}",
                new JSONObject(),
                null,
                "applicationKey",
                "clientKey");

        //独自UserAgentの値を確認
        Assert.assertEquals("android-" + NCMB.SDK_VERSION, request.getRequestProperty("X-NCMB-SDK-Version"));
        Assert.assertEquals("android-4.1", request.getRequestProperty("X-NCMB-OS-Version"));
    }
}

