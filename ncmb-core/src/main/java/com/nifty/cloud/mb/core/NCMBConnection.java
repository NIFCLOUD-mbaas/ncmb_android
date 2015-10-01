package com.nifty.cloud.mb.core;

import android.os.AsyncTask;

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

/**
 * NCMBConnection is a class that communicates with NIFTY Cloud mobile backend
 */
public class NCMBConnection {

    //time out millisecond from NIFTY Cloud mobile backend
    static int sConnectionTimeout = 10000;

    //API request object
    private NCMBRequest ncmbRequest = null;

    //Callback after request api
    private RequestApiCallback mCallback;

    /**
     * setting callback for api request
     * @param callback callback for api request
     */
    public void setCallbackListener(RequestApiCallback callback){
        mCallback = callback;
    }

    /**
     * Constructor with NCMBRequest
     * @param request API request object
     */
    public NCMBConnection(NCMBRequest request) {
        this.ncmbRequest = request;
    }

    /**
     * Request NIFTY Cloud mobile backed api synchronously
     * @return result object from NIFTY Cloud mobile backend
     * @throws NCMBException exception from NIFTY Cloud mobile backend
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

                            if (urlConnection.getRequestProperty("Content-Type").equals("application/json")) {

                                //Sending json data
                                DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                                writer.write(ncmbRequest.getContent());
                                writer.flush();
                                writer.close();
                            } else if (urlConnection.getRequestProperty("Content-Type").equals("multipart-formdata")) {

                                //Sending file data
                                //TODO:file data upload
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

            res = future.get(sConnectionTimeout, TimeUnit.MILLISECONDS);
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
     * Request NIFTY Cloud mobile backend api asynchronously
     * @param callback execute callback after api request
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


