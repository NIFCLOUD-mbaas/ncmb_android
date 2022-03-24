/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.app.Activity;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * NCMBInstallation is used to retrieve and upload the installation data
 */
public class NCMBInstallation extends NCMBObject {

    /**
     * currentInstallation fileName
     */
    static final String INSTALLATION_FILENAME = "currentInstallation";
    /**
     * channels folder Name
     */
    static final String CHANNELS_FOLDER_NAME = "channels";

    /**
     * request code
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * cannot get device token message
     */
    private static final String CANNOT_GET_DEVICE_TOKEN_MESSAGE = 
        "Can not get device token, please check your google-service.json";
    /**
     * push device
     */
    static NCMBInstallation currentInstallation = null;

    static final List<String> ignoreKeys = Arrays.asList(
            "objectId", "applicationName", "appVersion", "badge", "channels", "deviceToken",
            "deviceType", "sdkVersion", "timeZone", "createDate", "updateDate", "acl", "pushType"
    );

    //region getter

    /**
     * Get application name
     *
     * @return application name
     */
    public String getApplicationName() {
        try {
            if (mFields.isNull("applicationName")) {
                return null;
            }

            return mFields.getString("applicationName");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get application version
     *
     * @return application Version
     */
    public String getAppVersion() {
        try {
            if (mFields.isNull("appVersion")) {
                return null;
            }

            return mFields.getString("appVersion");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get badge count
     *
     * @return badge count
     */
    public int getBadge() {
        try {
            if (mFields.isNull("badge")) {
                return 0;
            }

            return mFields.getInt("badge");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get channels
     *
     * @return channels
     */
    public JSONArray getChannels() {
        try {
            if (mFields.isNull("channels")) {
                return null;
            }

            return mFields.getJSONArray("channels");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get device type
     *
     * @return device type
     */
    public String getDeviceType() {
        try {
            if (mFields.isNull("deviceType")) {
                return null;
            }

            return mFields.getString("deviceType");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get device token
     *
     * @deprecated replaced by {@link #getDeviceTokenInBackground}
     * @return device token
     *
     */
    public String getDeviceToken() {
        try {
            if (mFields.isNull("deviceToken")) {
                return null;
            }
            return mFields.getString("deviceToken");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get device token
     *
     * @param callback TokenCallback
     */
    public void getDeviceTokenInBackground(final TokenCallback callback) {
        if (DeviceTokenCallbackQueue.getInstance().isDuringSaveInstallation()) {
            DeviceTokenCallbackQueue.getInstance().addQueue(callback);
            return;
        }
        if (FirebaseApp.getApps(NCMB.getCurrentContext().context).isEmpty()) {
            callback.done(null, new NCMBException(new IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)));
            return;
        }
        String deviceToken = getLocalDeviceToken();
        if (deviceToken != null) {
            callback.done(getLocalDeviceToken(), null);
            return;
        }
        getDeviceTokenInternalProcess(callback);
    }

    /**
     * Get device token (Internal Process)
     *
     * @param callback TokenCallback
     */
    void getDeviceTokenInternalProcess(final TokenCallback callback) {
        if (!FirebaseApp.getApps(NCMB.getCurrentContext().context).isEmpty()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                // Get new FCM registration token
                                String token = task.getResult();
                                callback.done(token, null);

                            } else {
                                callback.done(null, new NCMBException(new IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)));
                            }
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            callback.done(null, new NCMBException(new IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)));
                        }
                    });
        } else {
            callback.done(null, new NCMBException(new IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)));
        }
    }

    /**
     * Get device token
     *
     * @return device token
     *
     */
    protected String getLocalDeviceToken() {
        try {
            if (mFields.isNull("deviceToken")) {
                return null;
            }
            return mFields.getString("deviceToken");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get SDK version
     *
     * @return SDK version
     */
    public String getSDKVersion() {
        try {
            if (mFields.isNull("sdkVersion")) {
                return null;
            }

            return mFields.getString("sdkVersion");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get timezone
     *
     * @return timezone
     */
    public String getTimeZone() {
        try {
            if (mFields.isNull("timeZone")) {
                return null;
            }

            return mFields.getString("timeZone");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get custom field value
     *
     * @param name field name
     * @return field value
     */
    public Object getValue(String name) {
        try {
            if (mFields.isNull(name)) {
                return null;
            }
            return mFields.get(name);
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    //endregion

    //region setter

    /**
     * Set application name
     *
     * @param value applicationName
     */
    public void setApplicationName(String value) {
        try {
            mFields.put("applicationName", value);
            mUpdateKeys.add("applicationName");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set application version
     * ReadOnly field
     *
     * @param value appVersion
     */
    void setAppVersion(String value) {
        try {
            mFields.put("appVersion", value);
            mUpdateKeys.add("appVersion");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set badge count
     *
     * @param value applicationName
     */
    public void setBadge(int value) {
        try {
            mFields.put("badge", value);
            mUpdateKeys.add("badge");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set channels
     *
     * @param value channels
     */
    public void setChannels(JSONArray value) {
        try {
            mFields.put("channels", value);
            mUpdateKeys.add("channels");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set device type
     * ReadOnly field
     *
     * @param value device type
     */
    void setDeviceType(String value) {
        try {
            mFields.put("deviceType", value);
            mUpdateKeys.add("deviceType");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set device token
     *
     * @param value device token
     */
    public void setDeviceToken(String value) {
        try {
            mFields.put("deviceToken", value);
            mUpdateKeys.add("deviceToken");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set SDK version
     * ReadOnly field
     *
     * @param value SDKversion
     */
    void setSDKVersion(String value) {
        try {
            mFields.put("sdkVersion", value);
            mUpdateKeys.add("sdkVersion");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set timezone
     * ReadOnly field
     *
     * @param value timezone
     */
    void setTimeZone(String value) {
        try {
            mFields.put("timeZone", value);
            mUpdateKeys.add("timeZone");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    //endregion

    /**
     * Get current installation object
     *
     * @return NCMBInstallation object that is created from data that is saved to local file.<br>
     * If local file is not available, it returns empty NCMBInstallation object
     */
    public static NCMBInstallation getCurrentInstallation() {
        //null check
        NCMBLocalFile.checkNCMBContext();
        try {
            //create currentInstallation
            if (currentInstallation == null) {
                currentInstallation = new NCMBInstallation();
                //ローカルファイルに配信端末情報があれば取得、なければ新規作成
                File currentInstallationFile = NCMBLocalFile.create(INSTALLATION_FILENAME);
                if (currentInstallationFile.exists()) {
                    //ローカルファイルから端末情報を取得
                    JSONObject localData = NCMBLocalFile.readFile(currentInstallationFile);
                    currentInstallation = new NCMBInstallation(localData);
                }
            }
        } catch (Exception error) {
            Log.e("Error", error.toString());
        }
        return currentInstallation;
    }

    /**
     * Create query for installation class
     *
     * @return NCMBQuery for installation class
     */
    public static NCMBQuery<NCMBInstallation> getQuery() {
        return new NCMBQuery<>("installation");
    }

    /**
     * Constructor
     */
    public NCMBInstallation() {
        super("installation");
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Constructor from JSON
     *
     * @param params params source JSON
     * @throws NCMBException
     */
    NCMBInstallation(JSONObject params) {
        super("installation", params);
        mIgnoreKeys = ignoreKeys;
    }

    // region channel

    /**
     * set the channel
     *
     * @param channel  channel
     * @param activity Activity Class
     */
    public static void subscribe(String channel, Class<? extends Activity> activity) {
        subscribe(channel, activity, 0);
    }

    /**
     * set the channel
     * please use after the instalation registration
     *
     * @param channelName channel
     * @param icon        icon
     * @param activity    Activity Class
     */
    public static void subscribe(String channelName, Class<? extends Activity> activity, int icon) {
        if (channelName == null) {
            throw new IllegalArgumentException("channel may not be null.");
        } else if (activity == null) {
            throw new IllegalArgumentException("activity may not be null.");
        }

        //端末未登録の場合は処理を実行しない
        if (NCMBInstallation.getCurrentInstallation().getObjectId() == null) {
            return;
        }

        // NCMB/channels フォルダの取得
        File channelDir = NCMBLocalFile.create(CHANNELS_FOLDER_NAME);
        if (!channelDir.exists()) {
            //フォルダが存在しない場合は新規作成
            channelDir.mkdir();
        }

        // 書き込みデータ作成
        JSONObject localData = new JSONObject();
        try {
            localData.put("activityClass", activity.getName());
            localData.put("icon", icon);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // NCMB/channels/channelName 作成
        File writeFile = new File(channelDir, channelName);
        try {
            NCMBLocalFile.writeFile(writeFile, localData);
        } catch (NCMBException e) {
            throw new RuntimeException();
        }
    }

    /**
     * remove the channel
     * please use after the instalation registration
     *
     * @param channelName channel
     */
    public static void unsubscribe(String channelName) {
        if (channelName == null) {
            throw new IllegalArgumentException("channel may not be null.");
        }

        //端末未登録の場合は処理を実行しない
        if (NCMBInstallation.getCurrentInstallation().getObjectId() == null) {
            return;
        }

        // NCMB/channels フォルダの取得
        File channelDir = NCMBLocalFile.create(CHANNELS_FOLDER_NAME);
        if (!channelDir.exists()) {
            return;
        }

        // 引数のチャネル名ファイルを削除
        File channelFile = new File(channelDir, channelName);
        NCMBLocalFile.deleteFile(channelFile);
    }

    /**
     * get the channels
     *
     * @return chaanel
     */
    public static Set<String> getSubscriptions() {
        HashSet<String> channelSet = new HashSet<>();

        // NCMB/channels フォルダの取得
        File channelDir = NCMBLocalFile.create(CHANNELS_FOLDER_NAME);

        // フォルダに保存されているチャネル名一覧を取得しHashに設定する
        String channelNames[] = channelDir.list();
        if (channelNames != null) {
            Collections.addAll(channelSet, channelNames);
        }
        return channelSet;
    }

    // endregion

    //region save

    /**
     * Save installation object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    public void save() throws NCMBException {
        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        JSONObject responseData;
        if (getObjectId() == null) {
            //new create
            responseData = installationService.createInstallation(getLocalDeviceToken(), mFields);
        } else {
            //update
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            responseData = installationService.updateInstallation(getObjectId(), updateJson);
        }
        setLocalData(responseData);
        mUpdateKeys.clear();
    }

    /**
     * Save installation object inBackground
     * none callback
     */
    public void saveInBackground() {
        saveInBackground(null);
    }

    /**
     * Save installation object inBackground
     *
     * @param callback DoneCallback
     */
    public void saveInBackground(final DoneCallback callback) {
        //callback
        ExecuteServiceCallback exeCallback = new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject responseData, NCMBException error) {
                if (error == null) {
                    //instance set data
                    try {
                        setLocalData(responseData);
                    } catch (NCMBException e) {
                        error = e;
                    }
                    mUpdateKeys.clear();
                }
                if (callback != null) {
                    callback.done(error);
                }
            }
        };

        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        if (getObjectId() == null) {
            //new create
            installationService.createInstallationInBackground(getLocalDeviceToken(), mFields, exeCallback);
        } else {
            //update
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            installationService.updateInstallationInBackground(getObjectId(), updateJson, exeCallback);
        }
    }

    // endregion

    //region fetch

    /**
     * Get installation object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Override
    public void fetch() throws NCMBException {
        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        NCMBInstallation installation = installationService.fetchInstallation(getObjectId());
        mFields = installation.mFields;
    }

    /**
     * Get installation object inBackground
     *
     * @param callback DoneCallback
     */
    @Override
    public void fetchInBackground(final FetchCallback callback) {
        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.fetchInstallationInBackground(getObjectId(), new FetchCallback<NCMBInstallation>() {
            @Override
            public void done(NCMBInstallation installation, NCMBException e) {
                NCMBException error = null;
                if (e != null) {
                    error = e;
                } else {
                    mFields = installation.mFields;
                }
                if (callback != null) {
                    callback.done(installation, error);
                }
            }
        });
    }

    //endregion

    //region delete

    /**
     * Delete installation object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    public void delete() throws NCMBException {
        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.deleteInstallation(getObjectId());
        mFields = new JSONObject();
        mUpdateKeys.clear();
    }

    /**
     * Delete installation object inBackground
     * none callback
     */
    public void deleteInBackground() {
        deleteInBackground(null);
    }

    /**
     * Delete installation object inBackground
     *
     * @param callback DoneCallback
     */
    public void deleteInBackground(final DoneCallback callback) {
        //connect
        NCMBInstallationService installationService = (NCMBInstallationService) NCMB.factory(NCMB.ServiceType.INSTALLATION);
        installationService.deleteInstallationInBackground(getObjectId(), new DoneCallback() {
            @Override
            public void done(NCMBException error) {
                if (error == null) {
                    mFields = new JSONObject();
                    mUpdateKeys.clear();
                }
                if (callback != null) {
                    callback.done(error);
                }
            }
        });
    }

    //endregion

    void setLocalData(JSONObject data) throws NCMBException {
        try {
            //新規作成時
            if (data.has("createDate") && !data.has("updateDate")) {
                data.put("updateDate", data.getString("createDate"));
            }

            for (Iterator<String> keys = data.keys(); keys.hasNext(); ) {
                String key = keys.next();
                mFields.put(key, data.get(key));
            }
        } catch (JSONException e) {
            throw new NCMBException(NCMBException.INVALID_JSON, e.getMessage());
        }
    }

    JSONObject getLocalData() throws NCMBException {
        JSONObject localData = mFields;
        try {
            if (getObjectId() != null) {
                localData.put("objectId", getObjectId());
            }
            if (getApplicationName() != null) {
                localData.put("applicationName", getApplicationName());
            }
            if (getAppVersion() != null) {
                localData.put("appVersion", getAppVersion());
            }
            if (getBadge() != 0) {
                localData.put("badge", getBadge());
            }
            if (getChannels() != null) {
                localData.put("channels", getChannels());
            }
            if (getDeviceType() != null) {
                localData.put("deviceType", getDeviceType());
            }
            if (getLocalDeviceToken() != null) {
                localData.put("deviceToken", getLocalDeviceToken());
            }
            if (getSDKVersion() != null) {
                localData.put("sdkVersion", getSDKVersion());
            }
            if (getTimeZone() != null) {
                localData.put("timeZone", getTimeZone());
            }
            DateFormat format = NCMBDateFormat.getIso8601();
            if (getCreateDate() != null) {
                String createDateStr = format.format(getCreateDate());
                localData.put("createDate", createDateStr);
            }
            if (getUpdateDate() != null) {
                String updateDateStr = format.format(getUpdateDate());
                localData.put("updateDate", updateDateStr);
            }
            if (getAcl() != null) {
                localData.put("acl", getAcl());
            }
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
        return localData;
    }
}
