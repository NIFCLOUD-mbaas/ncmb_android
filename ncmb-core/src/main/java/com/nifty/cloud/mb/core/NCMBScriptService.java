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

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.nifty.cloud.mb.core.NCMBScript.MethodType;

/**
 * Service class for script api
 */
public class NCMBScriptService extends NCMBService {

    /**
     * execute api path
     */
    public static final String SERVICE_PATH = "script";

    /**
     * script end point
     */
    public static final String DEFAULT_SCRIPT_DOMAIN_URL = "https://script.mb.api.cloud.nifty.com";

    /**
     * script API version
     */
    public static final String DEFAULT_SCRIPT_API_VERSION = "2015-09-01";

    /**
     * Inner class for callback
     */
    abstract class ScriptServiceCallback extends ServiceCallback {

        /**
         * constructors
         */
        ScriptServiceCallback(NCMBScriptService service, ExecuteScriptCallback callback) {
            super(service, (CallbackBase) callback);
        }

        protected NCMBScriptService getScriptService() {
            return (NCMBScriptService) mService;
        }
    }

    /**
     * Constructor
     *
     * @param context Service context
     */
    NCMBScriptService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    /**
     * execute script to NIF Cloud mobile backend
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param header     header data
     * @param body       content data
     * @param query      query params
     * @param baseUrl    script base url
     * @return Result to script
     * @throws NCMBException
     */
    public byte[] executeScript(String scriptName, MethodType method, Map<String, String> header, JSONObject body, JSONObject query, String baseUrl) throws NCMBException {

        String scriptUrl;
        if (baseUrl != null && baseUrl.length() > 0) {
            scriptUrl = baseUrl + "/" + scriptName;
        } else {
            scriptUrl = DEFAULT_SCRIPT_DOMAIN_URL + "/" + DEFAULT_SCRIPT_API_VERSION + "/" + mServicePath + "/" + scriptName;
        }

        byte[] responseByte = null;
        HttpURLConnection urlConnection = null;
        String type;
        try {
            switch (method) {
                case POST:
                    type = NCMBRequest.HTTP_METHOD_POST;
                    break;
                case PUT:
                    type = NCMBRequest.HTTP_METHOD_PUT;
                    break;
                case GET:
                    type = NCMBRequest.HTTP_METHOD_GET;
                    break;
                case DELETE:
                    type = NCMBRequest.HTTP_METHOD_DELETE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid methodType");
            }

            String content = null;
            if (body != null) {
                content = body.toString();
            }

            if (mContext.sessionToken == null) {
                mContext.sessionToken = NCMBUser.getSessionToken();
            }
            String sessionToken = mContext.sessionToken;
            String applicationKey = mContext.applicationKey;
            String clientKey = mContext.clientKey;
            NCMBRequest request = new NCMBRequest(scriptUrl, type, content, query, sessionToken, applicationKey, clientKey);

            // query連結済みURLでコネクション作成
            URL url = request.getUrl();
            urlConnection = (HttpURLConnection) url.openConnection();

            // メソッド設定
            urlConnection.setRequestMethod(request.getMethod());

            // NCMB定義のヘッダー設定
            for (String requestKey : request.getAllRequestProperties().keySet()) {
                urlConnection.setRequestProperty(requestKey, request.getRequestProperty(requestKey));
            }

            // User定義のヘッダー設定
            if (header != null && !header.isEmpty()) {
                for (String requestKey : header.keySet()) {
                    urlConnection.setRequestProperty(requestKey, header.get(requestKey));
                }
            }

            // body設定
            if (request.getContent() != null) {
                urlConnection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(request.getContent());
                writer.flush();
                writer.close();
            }

            // 通信
            urlConnection.connect();

            // 判定
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 成功
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[32768];
                int nRead;
                while ((nRead = urlConnection.getInputStream().read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                responseByte = buffer.toByteArray();
            } else {
                // 失敗
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                String statusCode = String.valueOf(urlConnection.getResponseCode());
                String message = sb.toString();
                if (message.length() > 0 && isJSONString(message)) {
                    JSONObject responseData = new JSONObject(message);
                    if (responseData.has("status")) {
                        statusCode = responseData.getString("status");
                    } else if (responseData.has("code")) {
                        statusCode = responseData.getString("code");
                    }
                    if (responseData.has("error")) {
                        message = responseData.getString("error");
                    }
                }
                throw new NCMBException(statusCode, message);
            }
        } catch (IOException | JSONException e) {
            throw new NCMBException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return responseByte;
    }

    /**
     * execute script to NIF Cloud mobile backend in background thread
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param header     header
     * @param body       content data
     * @param query      query params
     * @param baseUrl    script base url
     * @param callback   callback for after script execute
     */
    public void executeScriptInBackground(final String scriptName, final MethodType method, final Map<String, String> header, final JSONObject body, final JSONObject query, final String baseUrl, final ExecuteScriptCallback callback) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            byte[] res = null;
            NCMBException error = null;

            @Override
            protected Void doInBackground(Void... param) {

                try {
                    res = executeScript(scriptName, method, header, body, query, baseUrl);
                } catch (NCMBException e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                callback.done(res, error);
            }
        }.execute();
    }

    boolean isJSONString(String str){
        try {
            new JSONObject(str);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}

