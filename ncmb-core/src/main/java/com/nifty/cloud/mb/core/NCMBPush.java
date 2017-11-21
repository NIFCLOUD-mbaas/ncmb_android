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
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * NCMBPush is used to retrieve and send the push notification.<br>
 * NCMBPush can not add any field.<br>
 * Information about the field names that can be set , refer to the following reference .<br>
 * @see <a target="_blank" href="http://mb.cloud.nifty.com/doc/current/rest/push/pushRegistration.html">NIF Cloud mobile backned API Reference(Japanese)</a>
 */
public class NCMBPush extends NCMBBase {

    private static final String MATCH_URL_REGEX =
            "^(https?)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";

    static final List<String> ignoreKeys = Arrays.asList(
            "objectId", "deliveryTime", "target",
            "searchCondition", "message", "userSettingValue",
            "deliveryExpirationDate", "deliveryExpirationTime", "deliveryPlanNumber",
            "deliveryNumber", "status", "error",
            "action", "badgeIncrementFlag", "sound",
            "contentAvailable", "title", "dialog",
            "richUrl", "badgeSetting", "category",
            "acl", "createDate", "updateDate");

    // region getter

    /**
     * Get delivery date
     *
     * @return Date delivery date
     */
    public Date getDeliveryTime() {
        try {
            if (mFields.isNull("deliveryTime")) {
                return null;
            }
            DateFormat format = NCMBDateFormat.getIso8601();
            return format.parse(mFields.getJSONObject("deliveryTime").getString("iso"));
        } catch (JSONException | ParseException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get target device
     *
     * @return JSONArray target device
     */
    public JSONArray getTarget() {
        try {
            if (mFields.isNull("target")) {
                return null;
            }
            return mFields.getJSONArray("target");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }


    /**
     * Get search condition
     *
     * @return JSONObject search condition
     */
    public JSONObject getSearchCondition() {
        try {
            if (mFields.isNull("searchCondition")) {
                return null;
            }
            return mFields.getJSONObject("searchCondition");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get push message
     *
     * @return String push message
     */
    public String getMessage() {
        try {
            if (mFields.isNull("message")) {
                return null;
            }
            return mFields.getString("message");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get user setting value
     *
     * @return JSONObject user setting value
     */
    public JSONObject getUserSettingValue() {
        try {
            if (mFields.isNull("userSettingValue")) {
                return null;
            }
            return mFields.getJSONObject("userSettingValue");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery expiration date
     *
     * @return Date delivery expiration date
     */
    public Date getDeliveryExpirationDate() {
        try {
            if (mFields.isNull("deliveryExpirationDate")) {
                return null;
            }
            DateFormat format = NCMBDateFormat.getIso8601();
            return format.parse(mFields.getJSONObject("deliveryExpirationDate").getString("iso"));
        } catch (JSONException | ParseException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery expiration time
     *
     * @return Date delivery expiration time
     */
    public String getDeliveryExpirationTime() {
        try {
            if (mFields.isNull("deliveryExpirationTime")) {
                return null;
            }
            return mFields.getString("deliveryExpirationTime");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery plan number
     *
     * @return int delivery plan number
     */
    public int getDeliveryPlanNumber() {
        try {
            if (mFields.isNull("deliveryPlanNumber")) {
                return 0;
            }
            return mFields.getInt("deliveryPlanNumber");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery number
     *
     * @return int delivery number
     */
    public int getDeliveryNumber() {
        try {
            if (mFields.isNull("deliveryNumber")) {
                return 0;
            }
            return mFields.getInt("deliveryNumber");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery status
     *
     * @return int delivery status
     */
    public int getStatus() {
        try {
            if (mFields.isNull("status")) {
                return 0;
            }
            return mFields.getInt("status");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get delivery error
     *
     * @return JSONObject delivery error
     */
    public JSONObject getError() {
        try {
            if (mFields.isNull("error")) {
                return null;
            }
            return mFields.getJSONObject("error");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get action
     *
     * @return String action
     */
    public String getAction() {
        try {
            if (mFields.isNull("action")) {
                return null;
            }
            return mFields.getString("action");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get badge increment flag
     *
     * @return Boolean badge increment flag
     */
    public Boolean getBadgeIncrementFlag() {
        try {
            if (mFields.isNull("badgeIncrementFlag")) {
                return null;
            }
            return mFields.getBoolean("badgeIncrementFlag");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get sound
     *
     * @return String sound
     */
    public String getSound() {
        try {
            if (mFields.isNull("sound")) {
                return null;
            }
            return mFields.getString("sound");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get content available flag
     *
     * @return Boolean content available flag
     */
    public Boolean getContentAvailable() {
        try {
            if (mFields.isNull("contentAvailable")) {
                return null;
            }
            return mFields.getBoolean("contentAvailable");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get push title
     *
     * @return String get push title
     */
    public String getTitle() {
        try {
            if (mFields.isNull("title")) {
                return null;
            }
            return mFields.getString("title");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get dialog flag
     *
     * @return Boolean dialog flag
     */
    public Boolean getDialog() {
        try {
            if (mFields.isNull("dialog")) {
                return null;
            }
            return mFields.getBoolean("dialog");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get richUrl
     *
     * @return String richUrl
     */
    public String getRichUrl() {
        try {
            if (mFields.isNull("richUrl")) {
                return null;
            }
            return mFields.getString("richUrl");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get badge setting count
     *
     * @return Integer badge setting count
     */
    public int getBadgeSetting() {
        try {
            if (mFields.isNull("badgeSetting")) {
                return 0;
            }
            return mFields.getInt("badgeSetting");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Get category
     *
     * @return String category
     */
    public String getCategory() {
        try {
            if (mFields.isNull("category")) {
                return null;
            }
            return mFields.getString("category");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    // endregion

    // region setter

    /**
     * Set delivery date
     *
     * @param value deliveryTime
     */
    public void setDeliveryTime(Date value) {
        try {
            //mBaaSの日付型文字列に変換して設定
            mFields.put("deliveryTime", createIsoDate(value));
            mUpdateKeys.add("deliveryTime");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set target device
     *
     * @param value deliveryTarget os
     */
    public void setTarget(JSONArray value) {
        try {
            mFields.put("target", value);
            mUpdateKeys.add("target");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set search condition
     *
     * @param query NCMBQuery for installation search
     */
    public void setSearchCondition(NCMBQuery<NCMBInstallation> query) {
        try {
            JSONObject whereConditions = query.getConditions();
            JSONObject value = new JSONObject();
            if (whereConditions.has("where")) {
                value = whereConditions.getJSONObject("where");
            }
            mFields.put("searchCondition", value);
            mUpdateKeys.add("searchCondition");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set push message
     *
     * @param value message
     */
    public void setMessage(String value) {
        try {
            mFields.put("message", value);
            mUpdateKeys.add("message");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set user setting value
     *
     * @param value user setting value
     */
    public void setUserSettingValue(JSONObject value) {
        try {
            mFields.put("userSettingValue", value);
            mUpdateKeys.add("userSettingValue");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set delivery expiration date
     *
     * @param value delivery expiration date
     */
    public void setDeliveryExpirationDate(Date value) {
        try {
            //mBaaSの日付型に変換して設定
            mFields.put("deliveryExpirationDate", createIsoDate(value));
            mUpdateKeys.add("deliveryExpirationDate");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set delivery expiration time
     *
     * @param value delivery expiration date
     */
    public void setDeliveryExpirationTime(String value) {
        try {
            mFields.put("deliveryExpirationTime", value);
            mUpdateKeys.add("deliveryExpirationTime");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set action
     *
     * @param value action
     */
    public void setAction(String value) {
        try {
            mFields.put("action", value);
            mUpdateKeys.add("action");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set badge increment flag
     *
     * @param value badge increment flag
     */
    public void setBadgeIncrementFlag(Boolean value) {
        try {
            mFields.put("badgeIncrementFlag", value);
            mUpdateKeys.add("badgeIncrementFlag");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set sound
     *
     * @param value sound
     */
    public void setSound(String value) {
        try {
            mFields.put("sound", value);
            mUpdateKeys.add("sound");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set content available flag
     *
     * @param value content available flag
     */
    public void setContentAvailable(Boolean value) {
        try {
            mFields.put("contentAvailable", value);
            mUpdateKeys.add("contentAvailable");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set push title
     *
     * @param value title
     */
    public void setTitle(String value) {
        try {
            mFields.put("title", value);
            mUpdateKeys.add("title");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set dialog flag
     *
     * @param value dialog flag
     */
    public void setDialog(Boolean value) {
        try {
            mFields.put("dialog", value);
            mUpdateKeys.add("dialog");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set richUrl
     *
     * @param value richUrl
     */
    public void setRichUrl(String value) {
        try {
            mFields.put("richUrl", value);
            mUpdateKeys.add("richUrl");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set badge setting count
     *
     * @param value badge setting count
     */
    public void setBadgeSetting(Integer value) {
        try {
            mFields.put("badgeSetting", value);
            mUpdateKeys.add("badgeSetting");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    /**
     * Set category
     *
     * @param value category
     */
    public void setCategory(String value) {
        try {
            mFields.put("category", value);
            mUpdateKeys.add("category");
        } catch (JSONException error) {
            throw new IllegalArgumentException(error.getMessage());
        }
    }

    //endregion


    /**
     * Create query for push class
     * @return NCMBQuery for push class
     */
    public static NCMBQuery<NCMBPush> getQuery() {
        return new NCMBQuery<>("push");
    }

    /**
     * Constructor
     */
    public NCMBPush() {
        super("push");
        mIgnoreKeys = ignoreKeys;
    }

    /**
     * Constructor
     *
     * @param params input parameters
     * @throws NCMBException
     */
    NCMBPush(JSONObject params){
        super("push", params);
        mIgnoreKeys = ignoreKeys;
    }

    // region send

    /**
     * Send push object
     *
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public void send() throws NCMBException {
        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        JSONObject responseData;
        if (getObjectId() == null) {
            //new create
            responseData = pushService.sendPush(mFields);
        } else {
            //update
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            responseData = pushService.updatePush(getObjectId(), updateJson);
        }
        setLocalData(responseData);
        mUpdateKeys.clear();
    }

    /**
     * Send push object inBackground
     * none callback
     */
    public void sendInBackground() {
        sendInBackground(null);
    }

    /**
     * Send push object inBackground
     *
     * @param callback DoneCallback
     */
    public void sendInBackground(final DoneCallback callback) {

        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);

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
                }
                mUpdateKeys.clear();
                if (callback != null) {
                    callback.done(error);
                }
            }
        };

        if (getObjectId() == null) {
            //new create
            pushService.sendPushInBackground(mFields, exeCallback);
        } else {
            //update
            JSONObject updateJson = null;
            try {
                updateJson = createUpdateJsonData();
            } catch (JSONException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
            pushService.updatePushInBackground(getObjectId(), updateJson, exeCallback);
        }
    }

    //endregion

    //region fetch

    /**
     * Get push object
     *
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public void fetch() throws NCMBException {
        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        NCMBPush push = pushService.fetchPush(getObjectId());
        //afterFetch
        setLocalData(push.mFields);
    }

    /**
     * Get push object inBackground
     * none callback
     */
    public void fetchInBackground() {
        fetchInBackground(null);
    }

    /**
     * Get push object inBackground
     *
     * @param callback DoneCallback
     */
    public void fetchInBackground(final FetchCallback callback) {
        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.fetchPushInBackground(getObjectId(), new FetchCallback<NCMBPush>() {
            @Override
            public void done(NCMBPush push, NCMBException e) {
                NCMBException error = null;
                if (e != null) {
                    error = e;
                } else {
                    //instance set data
                    try {
                        setLocalData(push.mFields);
                    } catch (NCMBException ncmbError) {
                        error = ncmbError;
                    }
                }
                if (callback != null) {
                    callback.done(push, error);
                }
            }
        });
    }

    //endregion

    //region delete

    /**
     * Delete push object
     *
     * @throws NCMBException exception sdk internal or NIF Cloud mobile backend
     */
    public void delete() throws NCMBException {
        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.deletePush(getObjectId());
        //instance data clear
        mFields = new JSONObject();
        mUpdateKeys.clear();
    }

    /**
     * Delete push object inBackground
     * none callback
     */
    public void deleteInBackground() {
        deleteInBackground(null);
    }

    /**
     * Delete push object inBackground
     *
     * @param callback DoneCallback
     */
    public void deleteInBackground(final DoneCallback callback) {
        //connect
        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.deletePushInBackground(getObjectId(), new DoneCallback() {
            @Override
            public void done(NCMBException error) {
                if (error == null) {
                    //instance data clear
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

    // region RichPush

    /**
     * If it contains the URL in the payload data, it will display the webview
     *
     * @param context context
     * @param intent  URL
     */
    public static void richPushHandler(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        // URLチェック
        String url = intent.getStringExtra("com.nifty.RichUrl");
        if (url == null) {
            return;
        }
        // URLのバリデーションチェック
        if (!url.matches(MATCH_URL_REGEX)) {
            return;
        }

        // ダイアログ表示
        final NCMBRichPush dialog = new NCMBRichPush(context, url);
        dialog.show();
    }
    // endregion

    /**
     * Open push registration in background
     *
     * @param intent ActivityIntent
     */
    public static void trackAppOpened(Intent intent) {

        if (intent == null) {
            return;
        }

        String pushId = intent.getStringExtra("com.nifty.PushId");
        if (pushId == null) {
            return;
        }

        NCMBPushService pushService = (NCMBPushService) NCMB.factory(NCMB.ServiceType.PUSH);
        pushService.sendPushReceiptStatusInBackground(pushId, null);
    }


    // region internal method

    /**
     * Set data to instance
     *
     * @param data json params
     */
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

    /**
     * create a date of mBaaS correspondence
     *
     * @param value iso value
     * @return JSONObject
     * @throws JSONException
     */
    JSONObject createIsoDate(Date value) throws JSONException {
        JSONObject dateJson = new JSONObject();
        dateJson.put("iso", NCMBDateFormat.getIso8601().format(value));
        dateJson.put("__type", "Date");
        return dateJson;
    }

    // endregion

    // region dialogPush

    /**
     * If it contains the dialog in the payload data, it will display the dialog
     *
     * @param context                 context
     * @param bundle                  pushData
     * @param dialogPushConfiguration push settings
     */
    public static void dialogPushHandler(Context context, Bundle bundle, NCMBDialogPushConfiguration dialogPushConfiguration) {
        if (!bundle.containsKey("com.nifty.Dialog")) {
            //dialogが有効になっていない場合
            return;
        }

        if (dialogPushConfiguration.getDisplayType() == NCMBDialogPushConfiguration.DIALOG_DISPLAY_NONE) {
            //ダイアログ設定クラスの表示形式が"表示しない"(DIALOG_DISPLAY_NONE)場合
            return;
        }

        ApplicationInfo appInfo;
        String activityName = "";
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            activityName = appInfo.packageName + appInfo.metaData.getString(NCMBGcmListenerService.OPEN_PUSH_START_ACTIVITY_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //NCMBDialogActivityクラスを呼び出す
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(context.getApplicationContext(), NCMBDialogActivity.class);
        intent.putExtra("com.nifty.OriginalData", bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(NCMBDialogActivity.INTENT_EXTRA_THEME, android.R.style.Theme_Wallpaper_NoTitleBar);
        intent.putExtra(NCMBDialogActivity.INTENT_EXTRA_LAUNCH_CLASS, activityName);
        intent.putExtra(NCMBDialogActivity.INTENT_EXTRA_SUBJECT, bundle.getString("title"));
        intent.putExtra(NCMBDialogActivity.INTENT_EXTRA_MESSAGE, bundle.getString("message"));
        intent.putExtra(NCMBDialogActivity.INTENT_EXTRA_DISPLAYTYPE, dialogPushConfiguration.getDisplayType());
        context.getApplicationContext().startActivity(intent);
    }

    // endregion
}
