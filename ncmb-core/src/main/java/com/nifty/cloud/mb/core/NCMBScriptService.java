package com.nifty.cloud.mb.core;

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

import static com.nifty.cloud.mb.core.NCMBScript.MethodType;

/**
 * Service class for script api
 */
public class NCMBScriptService extends NCMBService {

    /**
     * execute api path
     */
    public static final String SERVICE_PATH = "logic";

    /**
     * script end point
     */
    public static final String DEFAULT_SCRIPT_DOMAIN_URL = "https://logic.mb.cloud.nifty.com";

    /**
     * script API version
     */
    public static final String DEFAULT_SCRIPT_API_VERSION = "2015-08-03";

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
        mContext.baseUrl = DEFAULT_SCRIPT_DOMAIN_URL + "/" + DEFAULT_SCRIPT_API_VERSION + "/";
    }

    /**
     * execute script to Nifty cloud mobile backend
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param params     ContentData or queryString
     * @return Result to script
     * @throws NCMBException
     */
    public byte[] execute(String scriptName, MethodType method, byte[] params) throws NCMBException {
        byte[] responseByte = null;
        String urlStr = mContext.baseUrl + mServicePath + scriptName;
        String type = "";
        String content = null;
        JSONObject queryParam = null;
        try {
            switch (method) {
                case POST:
                    type = NCMBRequest.HTTP_METHOD_POST;
                    if (params != null) {
                        content = new String(params, "UTF-8");
                    }
                    break;
                case PUT:
                    type = NCMBRequest.HTTP_METHOD_PUT;
                    if (params != null) {
                        content = new String(params, "UTF-8");
                    }
                    break;
                case GET:
                    type = NCMBRequest.HTTP_METHOD_GET;
                    if (params != null) {
                        queryParam = new JSONObject(new String(params, "UTF-8"));
                    }
                    break;
                case DELETE:
                    type = NCMBRequest.HTTP_METHOD_DELETE;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid methodType");
            }

            if (mContext.sessionToken == null) {
                mContext.sessionToken = NCMBUser.getSessionToken();
            }
            String sessionToken = mContext.sessionToken;
            String applicationKey = mContext.applicationKey;
            String clientKey = mContext.clientKey;

            NCMBRequest request = new NCMBRequest(urlStr, type, content, queryParam, sessionToken, applicationKey, clientKey);

            URL url = request.getUrl();
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(request.getMethod());
            // リクエストヘッダー設定
            for (String requestKey : request.getAllRequestProperties().keySet()) {
                urlConnection.setRequestProperty(requestKey, request.getRequestProperty(requestKey));
            }

            // コンテントデータ設定
            if (urlConnection.getRequestMethod().equals("POST")
                    || urlConnection.getRequestMethod().equals("PUT")) {
                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(request.getContent());
                writer.flush();
                writer.close();
            }

            // 通信
            urlConnection.connect();

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

                if (sb.length() > 0) {
                    JSONObject responseData = new JSONObject(new String(sb));
                    String statusCode = "";
                    if (responseData.has("status")) {
                        statusCode = responseData.getString("status");
                    } else if (responseData.has("code")) {
                        statusCode = responseData.getString("code");
                    }
                    throw new NCMBException(statusCode, responseData.getString("error"));
                }
            }
        } catch (IOException | JSONException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
        return responseByte;
    }
}

