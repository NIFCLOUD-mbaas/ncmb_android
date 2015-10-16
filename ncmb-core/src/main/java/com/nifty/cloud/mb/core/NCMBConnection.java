package com.nifty.cloud.mb.core;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NCMBConnection {

    //サーバ接続のタイムアウト時間
    private static final int CONNECTION_TIMEOUT = 10000;

    //リクエスト
    private NCMBRequest ncmbRequest = null;

    /**
     * コールバック用のリスナー
     */
    private RequestApiCallback mCallback;

    /**
     * コールバックの設定を行う
     */
    public void setCallbackListener(RequestApiCallback callback){
        mCallback = callback;
    }

    /**
     * コンストラクタ
     *
     * @param request リクエスト
     */
    public NCMBConnection(NCMBRequest request) {
        this.ncmbRequest = request;
    }

    /**
     * 同期通信
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
                                //TODO:file data upload

                                final String twoHyphens = "--";
                                final String boundary =  "-------"+ Long.toString(System.currentTimeMillis());
                                final String lineEnd = "\r\n";

                                //urlConnection.setRequestProperty("Connection", "Keep-Alive");
                                //urlConnection.setRequestProperty("Content-Type", "multipart/form-data;");

                                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                                writer.write(boundary + lineEnd);

                                //writer.write(lineEnd);
                                String fileStr = "";


                                //filedata
                                //writer.write("Content-Type:"+ NCMBRequest.HEADER_CONTENT_TYPE_FILE + lineEnd);
                                /*
                                writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"" + lineEnd);
                                writer.write("Content-Type: text/plain" + lineEnd);
                                writer.write(lineEnd);
                                writer.write("test file...");
                                writer.write(lineEnd);
                                */

                                fileStr += boundary + lineEnd;
                                fileStr += "Content-Disposition: form-data; name=file; filename=test.txt" + lineEnd;
                                fileStr += "Content-Type: text/plain" + lineEnd;
                                fileStr += lineEnd;
                                fileStr += "test file...";
                                fileStr += lineEnd;
                                fileStr += boundary + twoHyphens + lineEnd;

                                //writer.write("Content-Transfer-Encoding: binary" + lineEnd);
                                //writer.write(new String(ncmbRequest.getFileData(), "UTF-8"));

                                /*
                                //acl
                                writer.write(boundary + lineEnd);
                                writer.write("Content-Disposition: form-data; name=\"acl\"" + lineEnd);
                                writer.write("Content-Type: application/json" + lineEnd);
                                writer.write(ncmbRequest.getContent());
                                */

                                //writer.write(boundary + twoHyphens + lineEnd);
                                Log.d("Error", fileStr);
                                writer.write(fileStr);
                                writer.flush();
                                writer.close();
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

                    } catch (IOException e) {

                        throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
                    } finally {
                        //Disconnect HTTPURLConnection
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }

                    return res;
                    //return null;
                }

            });

            res = future.get(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            if (res.statusCode != HttpURLConnection.HTTP_CREATED &&
                    res.statusCode != HttpURLConnection.HTTP_OK) {
                throw new NCMBException(res.mbStatus, res.mbErrorMessage);
            }
            return res;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new NCMBException(NCMBException.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * 非同期通信
     */
    public void sendRequestAsynchronously(RequestApiCallback callback) {
        setCallbackListener(callback);
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {

            NCMBResponse res = null;
            NCMBException error = null;

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    res = sendRequest();
                } catch (NCMBException e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                mCallback.done(res, error);
            }
        }.execute();

    }
}


