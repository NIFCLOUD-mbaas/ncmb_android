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
    /** Base folder name */
    static final String FOLDER_NAME = "NCMB";

    static File create(String fileName){
        return new File(NCMB.getCurrentContext().context.getDir(FOLDER_NAME, Context.MODE_PRIVATE), fileName);
    }
    /**
     * Writing to a local file
     * @param writeFile write file instance
     * @param fileData local file data
     */
    static void writeFile(File writeFile , JSONObject fileData){
        checkNCMBContext();
        try {
            FileOutputStream out = new FileOutputStream(writeFile);
            out.write(fileData.toString().getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //空白のファイル作成を防ぐ
            deleteFileSizeZero(writeFile);
        }
    }

    /**
     * Reading to a local file
     * @param readFile read file instance
     * @return file data
     * @throws NCMBException
     */
    static JSONObject readFile(File readFile) throws NCMBException {
        checkNCMBContext();
        JSONObject json = new JSONObject();
        try {
            BufferedReader br = new BufferedReader(new FileReader(readFile));
            String information = br.readLine();
            br.close();
            if(null != information){ 
               json = new JSONObject(information);
            }
        } catch (Exception e) {
            throw new NCMBException(e);
        }
        return json;
    }

    /**
     * Delete the local file
     * @param deleteFile delete file instance
     */
    static void deleteFile(File deleteFile){
        checkNCMBContext();
        deleteFile.delete();
    }

    /**
     * null check in NCMBContext
     */
    static void checkNCMBContext(){
        if (NCMB.getCurrentContext() == null) {
            throw new RuntimeException("Please run the　NCMB.initialize.");
        }
        if (NCMB.getCurrentContext().context == null) {
            throw new RuntimeException("NCMB.initialize context may not be null.");
        }
    }

    /**
     * delete from local file of size zero
     * @param writeFile write file instance
     */
    static void deleteFileSizeZero(File writeFile) {
        if (null != writeFile && writeFile.exists() && 0 == writeFile.length()) {
            //サイズが0の場合は削除する
            writeFile.delete();
        }
    }
}
