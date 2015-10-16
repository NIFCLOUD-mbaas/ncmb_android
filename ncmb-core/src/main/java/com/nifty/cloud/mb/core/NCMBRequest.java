package com.nifty.cloud.mb.core;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
    private byte[] fileData = null;
    private String contentType = null;
    // endregion

    //region getter

    /**
     * urlの取得
     *
     * @return url
     */
    public URL getUrl() {
        return this.url;
    }

    /**
     * HTTPメソッドの取得
     *
     * @return HTTPメソッド
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * コンテントデータの取得
     *
     * @return コンテントデータ
     */
    public String getContent() {
        return this.content;
    }

    /**
     * クエリ文字列の取得
     *
     * @return 検索条件
     */
    public JSONObject getQueryString() {
        return this.queryParam;
    }

    /**
     * セッショントークンの取得
     *
     * @return セッショントークン
     */
    public String getSessionToken() {
        return this.sessionToken;
    }

    /**
     * アプリケーションキーの取得
     *
     * @return アプリケーションキー
     */
    public String getApplicationKey() {
        return this.applicationKey;
    }

    /**
     * クライアントキーの取得
     *
     * @return クライアントキー
     */
    public String getClientKey() {
        return this.clientKey;
    }

    /**
     * File送信データの取得
     *
     * @return File送信データ
     */
    public byte[] getFileData() {
        return this.fileData;
    }

    /**
     * Fileコンテントタイプの取得
     *
     * @return Fileコンテントタイプ
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * タイムスタンプの取得
     *
     * @return タイムスタンプ
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    public String getRequestProperty(String key){
        return this.requestProperties.get(key);
    }

    public HashMap<String, String> getAllRequestProperties(){
        return this.requestProperties;
    }

    //endregion

    //region Constructor

    /**
     * コンストラクタ
     *
     * @param url            URL
     * @param method         HTTPメソッド
     * @param content        コンテントデータ
     * @param queryParam    検索条件
     * @param sessionToken   セッショントークン
     * @param applicationKey アプリケーションキー
     * @param clientKey      クライアントキー
     */
    public NCMBRequest(String url, String method, String content, JSONObject queryParam, String sessionToken, String applicationKey, String clientKey) throws NCMBException {
        this(url,method,content,null,HEADER_CONTENT_TYPE_JSON,queryParam,sessionToken,applicationKey,clientKey,null);
    }


    /**
     * file用コンストラクタ
     *
     * @param url            URL
     * @param method         HTTPメソッド
     * @param fileData       POSTデータ
     * @param aclJson        ACLデータ
     * @param sessionToken   セッショントークン
     * @param applicationKey アプリケーションキー
     * @param clientKey      クライアントキー
     */
    public NCMBRequest(String url, String method, byte[] fileData, JSONObject aclJson, String sessionToken, String applicationKey, String clientKey) throws NCMBException{
        this(url,method,aclJson.toString(),fileData,HEADER_CONTENT_TYPE_FILE,null,sessionToken,applicationKey,clientKey,null);
    }

    /**
     * コンストラクタ
     *
     * @param url            URL
     * @param method         HTTPメソッド
     * @param content        コンテントデータ
     * @param queryParam    検索条件
     * @param sessionToken   セッショントークン
     * @param applicationKey アプリケーションキー
     * @param clientKey      クライアントキー
     * @param timestamp      タイムスタンプ
     */
    public NCMBRequest(String url,
                       String method,
                       String content,
                       byte[] fileData,
                       String contentType,
                       JSONObject queryParam,
                       String sessionToken,
                       String applicationKey,
                       String clientKey,
                       String timestamp
    ) throws NCMBException {
        //必須プロパティ設定
        //ToDo:nullチェックメソッドがAPI19からだったので修正する
        //this.url = Objects.requireNonNull(url, "url must not be null.");
        //this.method = Objects.requireNonNull(method, "method must not be null.");
        //this.applicationKey = Objects.requireNonNull(applicationKey, "applicationKey must not be null.");
        //this.clientKey = Objects.requireNonNull(clientKey, "clientKey must not be null.");

        this.method = method;
        this.applicationKey = applicationKey;
        this.clientKey = clientKey;

        //無効値チェック
        invalidCheck(url, method, content, queryParam, sessionToken, applicationKey, clientKey);
        //その他プロパティ設定
        this.content = content;
        this.queryParam = queryParam;
        this.sessionToken = sessionToken;
        this.timestamp = timestamp;

        this.contentType = contentType;
        this.fileData = fileData;


        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
        String query = "";

        List<String> parameterList = new ArrayList<String>();
        if (queryParam != null && this.queryParam.length() > 0) {
            try {
                query = query + "?";//検索条件 連結
                Iterator<?> keys = queryParam.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String param = key + "=" + URLEncoder.encode(queryParam.get(key).toString(), "UTF-8");
                    parameterList.add(param);//シグネチャ生成で使用
                    query = query + param;
                    if (keys.hasNext()) {
                        query = query + "&";//検索条件 区切り
                    }

                }
                this.url = new URL(this.url.toString() + query);
            } catch (UnsupportedEncodingException | JSONException | MalformedURLException e) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
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
            if(this.timestamp == null){
                //timestamp引数なしコンストラクタの場合は現在時刻で生成する
                SimpleDateFormat df = NCMBDateFormat.getIso8601();
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                this.timestamp = URLEncoder.encode(df.format(ts), "UTF-8");
            }
            this.requestProperties.put(HEADER_TIMESTAMP, this.timestamp);
            // シグネチャ生成/設定
            String signatureHashData = createSignatureHashData(this.url.getPath(), parameterList);
            String signature = createSignature(signatureHashData, this.clientKey);
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
    }

    private void invalidCheck(String url, String method, String content, JSONObject queryString, String sessionToken, String applicationKey, String clientKey) {
        //オブジェクト作成、更新時のクエリ文字列は不正。コンテントは必ず設定する
        if (method.equals("POST") || method.equals("PUT")) {
            if (queryString != null && queryString.length() > 0) {
                throw new IllegalArgumentException("Can not set the queryString in the POST or PUT.");
            }
            //オブジェクト取得、検索時のコンテントは不正。クエリ文字列は取得時のnullを許容する
        } else if (method.equals("GET")) {
            if (content != null && content.length() > 0) {
                throw new IllegalArgumentException("Can not set the content in the GET.");
            }
            //オブジェクト削除時のコンテント及びクエリ文字列は不正
        } else if (method.equals("DELETE")) {
            if (content != null && content.length() > 0 || queryString != null && queryString.length() > 0) {
                throw new IllegalArgumentException("Can not set the queryString&content in the DELETE.");
            }
        }
    }

    //endregion

    // region Method

    // シグネチャ文字列の生成
    private String createSignature(String data, String key) {
        Log.d(null, data);
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
