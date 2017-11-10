/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
package com.nifty.cloud.mb.core;

import android.os.Build;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * NCMBRequest class is used to config api request
 */
public class NCMBRequest {

    // region Constant
    // HTTP method "GET"
    static final String HTTP_METHOD_GET = "GET";
    // HTTP method "POST"
    static final String HTTP_METHOD_POST = "POST";
    // HTTP method "PUT"
    static final String HTTP_METHOD_PUT = "PUT";
    // HTTP method "DELETE"
    static final String HTTP_METHOD_DELETE = "DELETE";
    //アプリケーションキー
    static final String HEADER_APPLICATION_KEY = "X-NCMB-Application-Key";
    //シグネチャ
    static final String HEADER_SIGNATURE = "X-NCMB-Signature";
    //タイムスタンプ
    static final String HEADER_TIMESTAMP = "X-NCMB-Timestamp";
    //Access-Control-Allow-Origin
    static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    //アプリセッショントークン
    static final String HEADER_APPS_SESSION_TOKEN = "X-NCMB-Apps-Session-Token";
    //コンテントタイプ
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    //JSON形式のコンテントタイプの値
    static final String HEADER_CONTENT_TYPE_JSON = "application/json";
    //ファイル形式のコンテントタイプの値
    static final String HEADER_CONTENT_TYPE_FILE = "multipart/form-data";
    //SDKVersionのキー
    static final String HEADER_SDK_VERSION = "X-NCMB-SDK-Version";
    //OSVersionのキー
    static final String HEADER_OS_VERSION = "X-NCMB-OS-Version";
    //シグネチャメソッドのキー
    private static final String SIGNATURE_METHOD_KEY = "SignatureMethod";
    //シグネチャメソッドの値
    private static final String SIGNATURE_METHOD_VALUE = "HmacSHA256";
    //シグネチャバージョンのキー
    private static final String SIGNATURE_VERSION_KEY = "SignatureVersion";
    // シグネチャバージョンの値
    private static final String SIGNATURE_VERSION_VALUE = "2";
    // endregion

    // region property
    /** APIリクエストを行うURL */
    private URL url = null;

    /** APIリクエストのHTTPメソッド */
    private String method = "";

    private String content = "";
    private JSONObject queryParam = new JSONObject();
    private String sessionToken = "";
    private String applicationKey = "";
    private String clientKey = "";
    private String timestamp = "";

    /** リクエストヘッダーのリスト */
    private HashMap<String, String> requestProperties = new HashMap<String, String>();

    // リクエスト生成用(File)
    private String fileName = "";
    private byte[] fileData = null;
    private String contentType = null;

    // レスポンスシグネチャ計算ハッシュデータ用
    private String signatureHashData = null;
    // endregion

    //region getter

    /**
     * Get url
     *
     * @return url
     */
    public URL getUrl() {
        return this.url;
    }

    /**
     * Get HTTPMethod
     *
     * @return HTTPMethod
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Get contentData
     *
     * @return contentData
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get queryParams
     *
     * @return queryParams
     */
    public JSONObject getQueryString() {
        return this.queryParam;
    }

    /**
     * Get sessionToken
     *
     * @return sessionToken
     */
    public String getSessionToken() {
        return this.sessionToken;
    }

    /**
     * Get applicationKey
     *
     * @return applicationKey
     */
    public String getApplicationKey() {
        return this.applicationKey;
    }

    /**
     * Get clientKey
     *
     * @return clientKey
     */
    public String getClientKey() {
        return this.clientKey;
    }

    /**
     * Get file data
     *
     * @return file data
     */
    public byte[] getFileData() {
        return this.fileData;
    }

    /**
     * Get fileName
     *
     * @return fileName
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Get contentType
     *
     * @return contentType
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Get signatureHashData
     *
     * @return signatureHashData
     */
    public String getSignatureHashData() {
        return this.signatureHashData;
    }


    /**
     * Get timestamp
     *
     * @return timestamp
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    public String getRequestProperty(String key) {
        return this.requestProperties.get(key);
    }

    public HashMap<String, String> getAllRequestProperties() {
        return this.requestProperties;
    }

    //endregion

    //region Constructor

    /**
     * Constructor
     *
     * @param url            URL
     * @param method         HTTPMethod
     * @param content        contentData
     * @param queryParam     queryJSON
     * @param sessionToken   sessionToken
     * @param applicationKey applicationKey
     * @param clientKey      clientKey
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBRequest(String url, String method, String content, JSONObject queryParam, String sessionToken, String applicationKey, String clientKey) throws NCMBException {
        this(url, method, content, null,null, HEADER_CONTENT_TYPE_JSON, queryParam, sessionToken, applicationKey, clientKey, null);
    }

    /**
     * constructor for fileStore
     *
     * @param url            URL
     * @param method         HTTPMethod
     * @param fileName       fileName
     * @param fileData       fileData
     * @param aclJson        contentData
     * @param sessionToken   sessionToken
     * @param applicationKey applicationKey
     * @param clientKey      clientKey
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBRequest(String url, String method, String fileName, byte[] fileData, JSONObject aclJson, String sessionToken, String applicationKey, String clientKey) throws NCMBException {
        this(url, method, aclJson.toString(),fileName, fileData, HEADER_CONTENT_TYPE_FILE, null, sessionToken, applicationKey, clientKey, null);
    }

    /**
     * コンストラクタ
     *
     * @param url            URL
     * @param method         HTTPMethod
     * @param content        contentData
     * @param fileName       fileName
     * @param fileData       fileData
     * @param contentType    content-type
     * @param queryParam     queryJSON
     * @param sessionToken   sessionToken
     * @param applicationKey applicationKey
     * @param clientKey      clientKey
     * @param timestamp      timestamp
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public NCMBRequest(String url, String method, String content,String fileName, byte[] fileData, String contentType, JSONObject queryParam, String sessionToken, String applicationKey, String clientKey, String timestamp) throws NCMBException {
        this.method = method;
        this.applicationKey = applicationKey;
        this.clientKey = clientKey;

        //その他プロパティ設定
        this.content = content;
        this.queryParam = queryParam;
        this.sessionToken = sessionToken;
        this.timestamp = timestamp;
        this.contentType = contentType;
        this.fileName = fileName;
        this.fileData = fileData;


        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new NCMBException(NCMBException.INVALID_FORMAT, e.getMessage());
        }
        String query = "";

        List<String> parameterList = new ArrayList<String>();
        if (queryParam != null && this.queryParam.length() > 0) {
            try {
                query = query + "?";//検索条件 連結
                Iterator<?> keys = queryParam.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    //String value = queryParam.get(key).toString();
                    //Log.v("tag", "KEY:" + key + " VALUE:" + value);
                    String param = key + "=" + URLEncoder.encode(queryParam.get(key).toString(), "UTF-8");
                    if (NCMBRequest.HTTP_METHOD_GET.equals(method)) {
                        parameterList.add(param);//シグネチャ生成で使用
                    }
                    query = query + param;
                    if (keys.hasNext()) {
                        query = query + "&";//検索条件 区切り
                    }

                }
                this.url = new URL(this.url.toString() + query);
            } catch (UnsupportedEncodingException | JSONException | MalformedURLException e) {
                throw new NCMBException(e);
            }
        }


        //createHttpRequestで生成したRequestのヘッダ設定
        //addHeader();
        // コンテンツタイプ設定
        if (this.contentType != null && this.contentType.length() > 0) {
            this.requestProperties.put(HEADER_CONTENT_TYPE, contentType);
            //this.httpRequest.addHeader(HEADER_CONTENT_TYPE, contentType);
        } else {
            this.requestProperties.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_JSON);
        }
        // アプリケーションキー設定
        this.requestProperties.put(HEADER_APPLICATION_KEY, this.applicationKey);

        try {
            // タイムスタンプ生成/設定
            if (this.timestamp == null) {
                //timestamp引数なしコンストラクタの場合は現在時刻で生成する
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                df.setTimeZone(new SimpleTimeZone(0, "GMT"));
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                this.timestamp = URLEncoder.encode(df.format(ts), "UTF-8");
            }
            this.requestProperties.put(HEADER_TIMESTAMP, this.timestamp);
            // シグネチャ生成/設定
            this.signatureHashData = createSignatureHashData(this.url.getPath(), parameterList);
            String signature = createSignature(this.signatureHashData, this.clientKey);
            this.requestProperties.put(HEADER_SIGNATURE, signature);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        // Access-Control-Allow-Origin設定
        this.requestProperties.put(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        // セッショントークン設定
        if (this.sessionToken != null && this.sessionToken.length() > 0) {
            this.requestProperties.put(HEADER_APPS_SESSION_TOKEN, this.sessionToken);
        }

        // 独自UserAgent設定
        this.requestProperties.put(HEADER_SDK_VERSION, "android-" + NCMB.SDK_VERSION);
        String osVersion = Build.VERSION.RELEASE;
        this.requestProperties.put(HEADER_OS_VERSION, "android-" + osVersion);

        //createHttpRequestで生成したRequestのコンテント設定
        //addContent();
        this.content = content;

    }
    //endregion

    // region Method

    // シグネチャ文字列の生成
    String createSignature(String data, String key) {
        String result = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), SIGNATURE_METHOD_VALUE);

            Mac mac = Mac.getInstance(SIGNATURE_METHOD_VALUE);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

            result = Base64.encodeToString(rawHmac, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalArgumentException("signature");
        }
        return result;
    }

    //シグネチャのためハッシュ化するデータの生成
    private String createSignatureHashData(String path, List<String> parameterList) {

        // シグネチャメソッド
        parameterList.add(SIGNATURE_METHOD_KEY + "=" + SIGNATURE_METHOD_VALUE);
        // シグネチャバージョン
        parameterList.add(SIGNATURE_VERSION_KEY + "=" + SIGNATURE_VERSION_VALUE);
        // アプリケーションキー
        parameterList.add(HEADER_APPLICATION_KEY + "=" + this.applicationKey);
        // タイムスタンプ
        parameterList.add(HEADER_TIMESTAMP + "=" + this.timestamp);
        // 自然昇順でソート
        Collections.sort(parameterList);

        // ハッシュかするデータの生成
        StringBuilder data = new StringBuilder();
        // リクエストメソッド
        data.append(this.method).append("\n");
        // FQDN
        data.append(this.url.getHost()).append("\n");
        // APIパス
        data.append(path).append("\n");

        // パラメーター
        Iterator<?> it = parameterList.iterator();
        while (it.hasNext()) {
            data.append(it.next());
            if (it.hasNext()) {
                data.append("&");// 最後以外を「&」で区切る
            }
        }
        return data.toString();
    }
    //endregion
}
