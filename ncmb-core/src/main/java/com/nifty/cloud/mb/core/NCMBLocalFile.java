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

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * LocalFile class
 */
class NCMBLocalFile {
    /**
     * Base folder name
     */
    static final String FOLDER_NAME = "NCMB";

    static File create(String fileName) {
        return new File(NCMB.getCurrentContext().context.getDir(FOLDER_NAME, Context.MODE_PRIVATE), fileName);
    }

    /**
     * Writing to a local file
     *
     * @param writeFile write file instance
     * @param fileData  local file data
     */
    static void writeFile(File writeFile, JSONObject fileData) throws NCMBException {
        checkNCMBContext();
        try {
            FileOutputStream out = new FileOutputStream(writeFile);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            throw new NCMBException(e);
        }
    }

    /**
     * Reading to a local file
     *
     * @param readFile read file instance
     * @return file data. if file data is empty, return empty json
     */
    static JSONObject readFile(File readFile) throws NCMBException {
        checkNCMBContext();
        JSONObject json = new JSONObject();
        try {
            BufferedReader br = new BufferedReader(new FileReader(readFile));
            String information = br.readLine();
            if (information == null) {
                return json;
            }
            br.close();
            json = new JSONObject(information);
        } catch (IOException | JSONException | NullPointerException e) {
            throw new NCMBException(e);
        }
        return json;
    }

    /**
     * Delete the local file
     *
     * @param deleteFile delete file instance
     */
    static void deleteFile(File deleteFile) {
        checkNCMBContext();
        deleteFile.delete();
    }

    /**
     * null check in NCMBContext
     */
    static void checkNCMBContext() {
        if (NCMB.getCurrentContext() == null) {
            throw new RuntimeException("Please run the　NCMB.initialize.");
        }
        if (NCMB.getCurrentContext().context == null) {
            throw new RuntimeException("NCMB.initialize context may not be null.");
        }
    }
}
