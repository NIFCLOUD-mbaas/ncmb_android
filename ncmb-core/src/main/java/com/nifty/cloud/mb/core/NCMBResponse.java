package com.nifty.cloud.mb.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * NCMBResponse contains response data from NIFTY Cloud mobile backend
 */
public class NCMBResponse {
    /** http status for success */
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_CREATED = 201;

    //通信結果文字列
    public JSONObject responseData = null;
    //通信結果ステータスコード
    public int statusCode = 0;

    /** mobile backendへのAPIリクエスト結果から取得したエラーコード */
    public String mbStatus = null;
    public String mbErrorMessage = null;

    /**
     *  API response
     * @param in InputStream
     * @param responseCode statusCode
     * @param responseHeaders responseHeaders
     * @throws NCMBException exception sdk internal or NIFTY Cloud mobile backend
     */
    public NCMBResponse(InputStream in, int responseCode, Map<String, List<String>> responseHeaders) throws NCMBException {
        statusCode = responseCode;
        String contentType = responseHeaders.get("Content-Type").get(0);
        if (contentType.equals("application/json") || contentType.equals("application/json;charset=UTF-8")) {
            // Set response json data
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    sb.append(line);

                }
                br.close();

                if (sb.length() > 0) {
                    responseData = new JSONObject(new String(sb));
                }

                if (statusCode != HttpURLConnection.HTTP_CREATED &&
                        statusCode != HttpURLConnection.HTTP_OK) {
                    mbStatus = responseData.getString("code");
                    mbErrorMessage = responseData.getString("error");
                    //throw new NCMBException(mbStatus, responseData.getString("error"));
                }
            } catch (IOException | JSONException e) {
                throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
            }
        } else {

            //Set response file data
        }

        //Checking invalid sessionToken
        invalidSessionToken(mbStatus);

        //TODO:Checking response signature
    }

    /**
     * check invalid sessionToken
     * automatic logout when 'E404001' error
     * @param code statusCode
     */
    void invalidSessionToken(String code) {
        if (NCMBException.INVALID_AUTH_HEADER.equals(code)) {
            NCMBUserService.clearCurrentUser();
        }
    }


        /*
        HttpEntity httpEntity = null;
        try {
            // 通信結果の取得
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpEntity = response.getEntity();
            httpEntity.writeTo(outputStream);

            //TODO:レスポンスシグネチャ検証を実装する
            //if(NCMB.responseValidationFlag){

            this.responseData = outputStream.toString();
            //レスポンスが空の時はnullに置き換える。DELETE等
            if (this.responseData.length() == 0) {
                this.responseData = null;
            }
        } catch (IOException e) {
            //通信エラー
            throw new NCMBException(e);
        } catch (IllegalArgumentException e) {
            throw new NCMBException(e);
        }

        // HTTP応答コードをチェック
        this.statusCode = response.getStatusLine().getStatusCode();

        //mBaaSエラー
        if (this.statusCode == HttpStatus.SC_OK || this.statusCode == HttpStatus.SC_CREATED) {
            //TODO:キャッシュキーが指定されていれば通信結果をキャッシュに保存する
        } else {
            try {
                JSONObject res = new JSONObject(this.responseData);
                String code = null;
                if (res.has("code")) {
                    code = res.getString("code");
                    //TODO:セッショントークン検証を実装する
                    //checkInvalidSessionToken(code);
                }
                throw new NCMBException(code, this.responseData);
            } catch (JSONException e) {
                throw new NCMBException(e);
            }
        }
//        switch (this.statusCode) {
//            // 通信成功
//            //case HttpStatus.SC_OK:
//            case HttpStatus.SC_CREATED:
//                //TODO:キャッシュキーが指定されていれば通信結果をキャッシュに保存する
//                break;
//            // 通信失敗
//            default:
//                try {
//                    JSONObject res = new JSONObject(this.responseData);
//                    String code = null;
//                    if (res.has("code")) {
//                        code = res.getString("code");
//                        //TODO:セッショントークン検証を実装する
//                        //checkInvalidSessionToken(code);
//                    }
//                    throw new NCMBException(code, this.responseData);
//                } catch (JSONException e) {
//                    throw new NCMBException(e);
//                }
//        }
*/
//    }
}
