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

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.nifcloud.mbaas.core.model.Category;
import com.nifcloud.mbaas.core.model.NcmbSetting;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The NCMBNotificationUtils Class contains register channel and get channel method
 */
public class NCMBNotificationUtils extends ContextWrapper {
    private NotificationManager mManager;
    // デフォルトチャンネルID
    private static final String DEFAULT_CHANNEL_ID = "com.nifcloud.mbaas.push.channel";
    // デフォルトチャンネル名
    private static final String DEFAULT_CHANNEL_NAME = "Push Channel";
    private static final int DEFAULT_IMPORTANCE = 3;
    // デフォルトチャンネル説明
    private static final String DEFAULT_CHANNEL_DES = "push notification channel";
    private static final boolean DEFAULT_ENABLE_LIGHTS = true;
    private static final boolean DEFAULT_ENABLE_VIBRATION = true;
    private static final int DEFAULT_LIGHT_COLOR = Color.GREEN;
    private static final int DEFAULT_LOCK_SCREEN_VISIBILITY = 0;

    public NCMBNotificationUtils(Context base) {
        super(base);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void settingDefaultChannels() {

        NcmbSetting ncmbSetting = getNcmbSetting("ncmbsettings.json", getBaseContext());
        if (ncmbSetting != null) {
            settingChannel(ncmbSetting.getNcmb().getNotification().getCategories());
        } else {
            Category category = new Category();
            category.setId(DEFAULT_CHANNEL_ID);
            category.setName(DEFAULT_CHANNEL_NAME);
            category.setImportance(DEFAULT_IMPORTANCE);
            category.setDescription(DEFAULT_CHANNEL_DES);
            category.setEnableLights(DEFAULT_ENABLE_LIGHTS);
            category.setEnableVibration(DEFAULT_ENABLE_VIBRATION);
            category.setLightColor(DEFAULT_LIGHT_COLOR);
            category.setLockscreenVisibility(DEFAULT_LOCK_SCREEN_VISIBILITY);
            settingChannel(category);
        }
    }

    /**
     * チャンネルを作成
     * @param category this category using to registration the notification channel.
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void settingChannel(Category category) {
        NotificationChannel androidChannel = new NotificationChannel(category.getId(),
                category.getName(), category.getImportance());
        androidChannel.setDescription(category.getDescription());
        androidChannel.enableLights(category.isEnableLights());
        androidChannel.enableVibration(category.isEnableVibration());
        androidChannel.setLightColor(category.getLightColor());
        androidChannel.setLockscreenVisibility(category.getLockscreenVisibility());

        getManager().createNotificationChannel(androidChannel);
    }

    /**
     * Setting and register for a list category
     * @param categories registration notifications channel by list categories.
     */
    private void settingChannel(List<Category> categories) {
        for (Category category : categories) {
            settingChannel(category);
        }
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public static String getDefaultChannel() {
        return DEFAULT_CHANNEL_ID;
    }

    /**
     * load Json file from assets to string
     * @param fileName the name of file to read.
     * @param context the context of app.
     * @return String
     */
    private String loadJSONFromAsset(String fileName, Context context) {
        String json = null;
        if (!fileName.isEmpty()) {
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return json;
    }

    /**
     * Check file name exist in assets folder
     * @param filename the name of file to read.
     * @param context the context of app.
     * @return boolean
     */
    private boolean isExistFileInAssets(String filename, Context context) {
        boolean result = false;
        if (!filename.isEmpty()) {
            try {
                result = Arrays.asList(context.getAssets().list("")).contains(filename);
            } catch (IOException e) {
                return false;
            }
        }
        return result;
    }

    /**
     * Validate NcmbSetting class
     * @param ncmbSetting The object after parse from json.
     * @return NcmbSetting Class
     */
    private NcmbSetting validateNcmbSetting(NcmbSetting ncmbSetting) {
        if (ncmbSetting != null
                && ncmbSetting.getNcmb() != null
                && ncmbSetting.getNcmb().getNotification() != null
                && ncmbSetting.getNcmb().getNotification().getCategories() != null) {

            List<Category> categories = ncmbSetting.getNcmb().getNotification().getCategories();
            List<Category> categoryList = new ArrayList<>();
            List<String> ids = new ArrayList<>();

            NcmbSetting result = ncmbSetting;
            for (Category category : categories) {
                if (category.getId() != null) {
                    if (!ids.contains(category.getId())) {
                        ids.add(category.getId());
                        categoryList.add(category);
                    }
                } else {
                    result = null;
                    break;
                }
            }
            if (result != null) {
                result.getNcmb().getNotification().setCategories(categoryList);
            }
            return result;
        }
        return null;
    }

    /**
     * Parse Json object to NcmbSetting class
     * @param fileName the name of file to read.
     * @param context the context of app.
     * @return NcmbSetting
     */
    private NcmbSetting parseNcmbSettingFromAsset(String fileName, Context context) {
        NcmbSetting ncmbSetting = null;
        if (isExistFileInAssets(fileName, context)) {

            Gson gson = new Gson();

            String json = loadJSONFromAsset(fileName, context);
            if (json != null) {
                try {
                    ncmbSetting = gson.fromJson(json, NcmbSetting.class);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return ncmbSetting;
    }

    /**
     * Get NcmbSetting after validate value.
     * @param fileName the name of file to read.
     * @param context the context of app.
     * @return NcmbSetting
     */
    private NcmbSetting getNcmbSetting(String fileName, Context context) {
        return validateNcmbSetting(parseNcmbSettingFromAsset(fileName, context));
    }
}