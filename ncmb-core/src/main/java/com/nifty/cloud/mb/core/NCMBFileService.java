package com.nifty.cloud.mb.core;

import org.json.JSONObject;

/**
 * NCMBFileService class
 */
public class NCMBFileService extends NCMBService{

    /** service path for API category */
    public static final String SERVICE_PATH = "files/";

    /**
     * Inner class for callback
     */
    abstract class FileServiceCallback extends ServiceCallback {
        /** constructors */
        FileServiceCallback(NCMBFileService service, ExecuteServiceCallback callback) {
            super(service, (CallbackBase)callback);
        }

        protected NCMBFileService getFileService() {
            return (NCMBFileService)mService;
        }
    }


    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    NCMBFileService(NCMBContext context) {
        super(context);
        mServicePath = SERVICE_PATH;
    }

    public void saveFileInBackground(String fileName, byte[] fileData, JSONObject aclJson, ExecuteServiceCallback callback){
        if (!validateFileName(fileName)){
            callback.done(null, new NCMBException(NCMBException.GENERIC_ERROR, "fileName is must not be null or empty"));
        }

        String url = mContext.baseUrl + mServicePath + fileName;

        try {
            sendRequestFileAsync(url, NCMBRequest.HTTP_METHOD_POST, fileData, aclJson, new FileServiceCallback(this, callback) {
                @Override
                public void handleResponse(NCMBResponse response) {

                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(response.responseData, null);
                    }
                }

                @Override
                public void handleError(NCMBException e) {
                    ExecuteServiceCallback callback = (ExecuteServiceCallback) mCallback;
                    if (callback != null) {
                        callback.done(null, e);
                    }
                }
            });
        } catch (NCMBException e) {
            //Exception handling for NCMBRequest
            if (callback != null) {
                callback.done(null, e);
            }
        }
    }

    private boolean validateFileName (String fileName){
        if (fileName == null || fileName.isEmpty()){
            return false;
        }
        return true;
    }
}
