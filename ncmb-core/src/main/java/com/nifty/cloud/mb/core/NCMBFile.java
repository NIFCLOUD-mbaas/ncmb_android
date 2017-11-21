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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * NCMBFile class.<br>
 * NCMBFile can not add any field.<br>
 * Information about the field names that can be set , refer to the following reference .<br>
 * @see <a target="_blank" href="http://mb.cloud.nifty.com/doc/current/rest/filestore/fileRegistration.html">NIF Cloud mobile backned API Reference(Japanese)</a>
 */
public class NCMBFile extends NCMBBase {

    static final List<String> ignoreKeys = Arrays.asList("fileName", "fileData", "mimeType", "fileSize", "createDate", "updateDate", "acl");

    /**
     * Set fileName
     *
     * @param fileName fileName
     */

    public void setFileName(String fileName) {
        try {
            mFields.put("fileName", fileName);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get fileName
     *
     * @return fileName
     */
    public String getFileName() {
        try {
            if (mFields.isNull("fileName")) {
                return null;
            }
            return mFields.getString("fileName");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set fileData
     *
     * @param data fileData
     */
    public void setFileData(byte[] data) {
        try {
            mFields.put("fileData", data);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get fileData
     *
     * @return fileData
     */
    public byte[] getFileData() {
        try {
            if (mFields.isNull("fileData")) {
                return null;
            }
            return (byte[]) mFields.get("fileData");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Create query for file class
     * @return NCMBQuery for file class
     */
    public static NCMBQuery<NCMBFile> getQuery() {
        return new NCMBQuery<>("file");
    }

    /**
     * Constructor
     */
    public NCMBFile() {
        this(null, null, null);
    }

    /**
     * Constructor with fileName
     *
     * @param fileName file name
     */
    public NCMBFile(String fileName) {
        this(fileName, null, null);
    }

    /**
     * Constructor with fileName and fileACL
     *
     * @param fileName file name
     * @param acl      file acl
     */
    public NCMBFile(String fileName, NCMBAcl acl) {
        this(fileName, null, acl);
    }

    /**
     * Constructor with fileName and fileData and fileACL
     *
     * @param fileName file name
     * @param fileData file data
     * @param acl      file acl
     */
    public NCMBFile(String fileName, byte[] fileData, NCMBAcl acl) {
        super("files");
        setFileName(fileName);
        setFileData(fileData);
        setAcl(acl);
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Upload file to file store
     *
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    public void save() throws NCMBException {
        JSONObject aclJson = createAclJSON();
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        JSONObject res = fileService.saveFile(getFileName(), getFileData(), aclJson);
        setLocalData(res);
    }

    /**
     * Upload file to file store asynchronously
     *
     * @param callback callback after file save
     */
    public void saveInBackground(final DoneCallback callback) {
        JSONObject aclJson = new JSONObject();
        try {
            aclJson = createAclJSON();
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(error);
            }
        }

        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        fileService.saveFileInBackground(getFileName(), getFileData(), aclJson, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    try {
                        setLocalData(jsonData);
                    } catch (NCMBException error) {
                        if (callback != null) {
                            callback.done(error);
                        }
                    }
                    if (callback != null) {
                        callback.done(null);
                    }
                }
            }
        });
    }

    /**
     * Update file to file store
     *
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    public void update() throws NCMBException {
        JSONObject aclJson = createAclJSON();
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        JSONObject res = fileService.updateFile(getFileName(), aclJson);
        setLocalData(res);
    }

    /**
     * Update file to file store asynchronously
     *
     * @param callback callback after file update
     */
    public void updateInBackground(final DoneCallback callback) {
        JSONObject aclJson = new JSONObject();
        try {
            aclJson = createAclJSON();
        } catch (NCMBException error) {
            if (callback != null) {
                callback.done(error);
            }
        }

        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        fileService.updateFileInBackground(getFileName(), aclJson, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject jsonData, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(e);
                    }
                } else {
                    try {
                        setLocalData(jsonData);
                    } catch (NCMBException error) {
                        if (callback != null) {
                            callback.done(error);
                        }
                    }
                    if (callback != null) {
                        callback.done(null);
                    }
                }
            }
        });
    }

    /**
     * Delete file from file store
     *
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    public void delete() throws NCMBException {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        fileService.deleteFile(getFileName());
    }

    /**
     * Delete file from file store asynchronously
     *
     * @param callback callback after file delete
     */
    public void deleteInBackground(final DoneCallback callback) {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        fileService.deleteFileInBackground(getFileName(), new ExecuteServiceCallback() {
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

    /**
     * Get fileData from file store
     * @return byte data obtained from filestore
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    public byte[] fetch() throws NCMBException {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        byte[] data = fileService.fetchFile(getFileName());
        setFileData(data);
        return getFileData();
    }

    /**
     * Get file from file store asynchronously
     *
     * @param callback callback after file get
     */
    public void fetchInBackground(final FetchFileCallback callback) {
        NCMBFileService fileService = (NCMBFileService) NCMB.factory(NCMB.ServiceType.FILE);
        fileService.fetchFileInBackground(getFileName(), new FetchFileCallback() {
            @Override
            public void done(byte[] data, NCMBException e) {
                if (e != null) {
                    if (callback != null) {
                        callback.done(null, e);
                    }
                } else {
                    setFileData(data);
                    if (callback != null) {
                        callback.done(getFileData(), null);
                    }
                }
            }
        });
    }

    private JSONObject createAclJSON() throws NCMBException {
        JSONObject aclJson = null;
        try {
            if (getAcl() == null) {
                aclJson = new NCMBAcl().toJson();
            } else {
                aclJson = getAcl().toJson();
            }
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, "Invalid acl");
        }
        return aclJson;
    }

    void setLocalData(JSONObject res) throws NCMBException {
        if (res != null) {
            try {
                //新規作成時
                if (res.has("createDate") && !res.has("updateDate")) {
                    res.put("updateDate", res.getString("createDate"));
                }
                for (Iterator<String> keys = res.keys(); keys.hasNext(); ) {
                    String key = keys.next();
                    mFields.put(key, res.get(key));
                }
            } catch (JSONException e) {
                throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
            }
        }
    }
}
