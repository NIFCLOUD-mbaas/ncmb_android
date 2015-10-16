package com.nifty.cloud.mb.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NCMBFile class
 */
public class NCMBFile {

    private String mFileName;

    private byte[] mFileData;

    private NCMBAcl mAcl;

    public NCMBFile(String fileName, byte[] fileData, NCMBAcl acl) {
        this.mFileName = fileName;
        this.mFileData = fileData;
        this.mAcl = acl;
    }

    public void saveInBackground(final DoneCallback callback) {

        //byte[] multipartFormData = createMultipartFormData();
        JSONObject aclJson = null;
        try {
            aclJson = mAcl.toJson();
        } catch (JSONException e) {
            if (callback != null) {
                callback.done(new NCMBException(NCMBException.INVALID_JSON, e.getMessage()));
            }
        }

        NCMBFileService fileServ = (NCMBFileService)NCMB.factory(NCMB.ServiceType.FILE);
        fileServ.saveFileInBackground(mFileName, mFileData, aclJson, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    if (callback != null) {
                        callback.done(null);
                    }
                }
            }
        });
    }
}
