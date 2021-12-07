/*
 * Copyright 2017-2018 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.os.AsyncTask;
import android.webkit.MimeTypeMap;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * NCMBConnection is a class that communicates with NIFCLOUD mobile backend
 */
public class NCMBConnection {

    //time out millisecond from NIFCLOUD mobile backend
    static int sConnectionTimeout = 10000;
    private int mConnectionTimeout = 10000;

    //API request object
    private NCMBRequest ncmbRequest = null;

    //Callback after request api
    private RequestApiCallback mCallback;

    /**
     * setting callback for api request
     *
     * @param callback callback for api request
     */
    public void setCallbackListener(RequestApiCallback callback) {
        mCallback = callback;
    }

    /**
     * Constructor with NCMBRequest
     *
     * @param request API request object
     */
    public NCMBConnection(NCMBRequest request) {
        this.ncmbRequest = request;
        setConnectionTimeout(sConnectionTimeout);
    }

    /**
     * Request NIFCLOUD mobile backed api synchronously
     *
     * @return result object from NIFCLOUD mobile backend
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    public NCMBResponse sendRequest() throws NCMBException {

        NCMBResponse res = null;
        try {
            ExecutorService exec = Executors.newSingleThreadExecutor();

            Future<NCMBResponse> future = exec.submit(new Callable<NCMBResponse>() {
                @Override
                public NCMBResponse call() throws NCMBException {
                    HttpURLConnection urlConnection = null;
                    NCMBResponse res = null;

                    try {
                        URL url = ncmbRequest.getUrl();
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod(ncmbRequest.getMethod());
                        //Set HTTP request header
                        for (String requestKey : ncmbRequest.getAllRequestProperties().keySet()) {
                            urlConnection.setRequestProperty(requestKey, ncmbRequest.getRequestProperty(requestKey));
                        }

                        //Check request method
                        if (urlConnection.getRequestMethod().equals("POST") ||
                                urlConnection.getRequestMethod().equals("PUT")) {

                            //enable post data option
                            urlConnection.setDoOutput(true);

                            if (urlConnection.getRequestProperty("Content-Type").equals(NCMBRequest.HEADER_CONTENT_TYPE_JSON)) {
                                //Sending json data
                                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                                writer.write(ncmbRequest.getContent());
                                writer.flush();
                                writer.close();
                            } else if (urlConnection.getRequestProperty("Content-Type").equals(NCMBRequest.HEADER_CONTENT_TYPE_FILE)) {

                                //Sending file data
                                final String boundary = Long.toString(System.currentTimeMillis());
                                final String lineEnd = "\r\n";
                                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());

                                //upload data
                                out.writeBytes("--" + boundary + lineEnd);
                                out.writeBytes("Content-Disposition: form-data; name=file; filename=" + URLEncoder.encode(ncmbRequest.getFileName(), "UTF-8") + lineEnd);
                                out.writeBytes("Content-Type: " + createMimeType(ncmbRequest.getFileName()) + lineEnd);
                                out.writeBytes(lineEnd);
                                for (int i = 0; i < ncmbRequest.getFileData().length; i++) {
                                    out.writeByte(ncmbRequest.getFileData()[i]);
                                }
                                out.writeBytes(lineEnd);

                                //ACLのみ対応
                                if (!ncmbRequest.getContent().isEmpty() && !ncmbRequest.getContent().equals("{}")) {
                                    out.writeBytes("--" + boundary + lineEnd);
                                    out.writeBytes("Content-Disposition: form-data; name=acl; filename=acl" + lineEnd);
                                    out.writeBytes(lineEnd);
                                    out.writeBytes(ncmbRequest.getContent());
                                    out.writeBytes(lineEnd);
                                }

                                out.writeBytes("--" + boundary + "--" + lineEnd);
                                out.flush();
                                out.close();
                            }

                        }
                        urlConnection.connect();

                        //Read response data
                        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED ||
                                urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            res = new NCMBResponse(urlConnection.getInputStream(), urlConnection.getResponseCode(), urlConnection.getHeaderFields());
                        } else {
                            res = new NCMBResponse(urlConnection.getErrorStream(), urlConnection.getResponseCode(), urlConnection.getHeaderFields());
                        }

                        // response signature check
                        responseSignatureCheck(urlConnection, res, ncmbRequest);


                    } catch (IOException e) {
                        throw new NCMBException(NCMBException.AUTH_FAILURE, e.getMessage());
                    } finally {
                        //Disconnect HTTPURLConnection
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                    return res;
                }

            });

            res = future.get(this.mConnectionTimeout, TimeUnit.MILLISECONDS);
            if (res.statusCode != HttpURLConnection.HTTP_CREATED &&
                    res.statusCode != HttpURLConnection.HTTP_OK) {
                throw new NCMBException(res.mbStatus, res.mbErrorMessage);
            }
            return res;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new NCMBException(e);
        }
    }

    // レスポンスシグネチャが正常か判定
    void responseSignatureCheck(URLConnection urlConnection, NCMBResponse res, NCMBRequest req) throws NCMBException {
        String responseSignature = urlConnection.getHeaderField("X-NCMB-Response-Signature");
        if (NCMB.getResponseValidation() && responseSignature != null && !responseSignature.isEmpty()) {
            String hashData = "";
            if (res.responseByte != null) {
                // file data
                String hexadecimal = asHex(res.responseByte);
                hashData = req.getSignatureHashData() + "\n" + hexadecimal;
            } else if(res.responseDataString != null){
                // json data
                hashData = req.getSignatureHashData() + "\n" + res.responseData.toString();
            }else {
                // delete,logout API
                hashData = req.getSignatureHashData();
            }

            String newSignature = req.createSignature(hashData, req.getClientKey());
            if (!newSignature.equals(responseSignature)) {
                throw new NCMBException(NCMBException.INVALID_RESPONSE_SIGNATURE, "Authentication error by response signature incorrect.");
            }
        }
    }

    // バイト配列を16進数文字列に変換
    String asHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Request NIFCLOUD mobile backend api asynchronously
     *
     * @param callback execute callback after api request
     */
    public void sendRequestAsynchronously(RequestApiCallback callback) {
        setCallbackListener(callback);
        StaticAsyncTask task = new StaticAsyncTask(this);
        task.execute();

    }

    private String createMimeType(String fileName) {
        //fileの拡張子毎のmimeTypeを作成
        String mimeType = null;
        if (fileName.lastIndexOf(".") != -1) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    private static class StaticAsyncTask extends AsyncTask<Void, Void, Void> {

        private NCMBConnection connection = null;
        NCMBResponse res = null;
        NCMBException error = null;

        StaticAsyncTask(NCMBConnection connection) {
            this.connection = connection;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(connection != null){
                try {
                    res = connection.sendRequest();
                } catch (NCMBException e) {
                    error = e;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {

            if(connection != null && connection.mCallback != null){
                connection.mCallback.done(res, error);
            }
        }
    }

    protected void setConnectionTimeout(int timeout){
        this.mConnectionTimeout = timeout;
    }
}
