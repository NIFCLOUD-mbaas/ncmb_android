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
package com.nifcloud.mbaas.core.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CategoryDeserializer implements JsonDeserializer<Category> {


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Category category = new Category();

        //Set default id
        category.setId(null);
        JsonElement id = jsonObject.get("id");
        if (id != null) {
            category.setId(id.getAsString());
        }

        //Set default name
        category.setName("");
        JsonElement name = jsonObject.get("name");
        if (name != null) {
            category.setName(name.getAsString());
        }

        //Set default description
        category.setDescription("");
        JsonElement description = jsonObject.get("description");
        if (description != null) {
            category.setDescription(description.getAsString());
        }

        //Set default importance
        category.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
        JsonElement importance = jsonObject.get("importance");
        if (importance != null) {
            String code = importance.getAsString();

            if (getInterger(code) != null && isImportanceNum(getInterger(code))) {
                category.setImportance(getInterger(code));
            }
        }

        //Set default enableLights
        category.setEnableLights(true);
        JsonElement enableLights = jsonObject.get("enableLights");
        if (enableLights != null) {
            String code = enableLights.getAsString();

            if (code.equalsIgnoreCase("false")) {
                category.setEnableLights(false);
            }
        }

        // Set default enableVibration
        category.setEnableVibration(true);
        JsonElement enableVibration = jsonObject.get("enableVibration");
        if (enableVibration != null) {
            String code = enableVibration.getAsString();
            if (code.equalsIgnoreCase("false")) {
                category.setEnableVibration(false);
            }
        }

        // Set default lightColor
        category.setLightColor(Color.GREEN);
        JsonElement lightColor = jsonObject.get("lightColor");
        if (lightColor != null) {
            String code = lightColor.getAsString();

            if (getInterger(code) != null) {
                category.setLightColor(getInterger(code));
            }
        }

        // Set default lockscreenVisibility
        category.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        JsonElement lockscreenVisibility = jsonObject.get("lockscreenVisibility");
        if (lockscreenVisibility != null) {
            String code = lockscreenVisibility.getAsString();
            if (getInterger(code) != null && isLockScreenVisibility(getInterger(code))) {
                category.setLockscreenVisibility(getInterger(code));
            }
        }

        return category;
    }

    private Integer getInterger(String val) {
        Integer result = null;
        try {
            result = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Check importance is in range
     *
     * @param importance value of importance has get from object
     * @return boolean
     */
    private boolean isImportanceNum(int importance) {
        return (importance >= 0 && importance <= 5) || (importance == -1000);
    }

    /**
     * Check lockscreenVisibility is in range
     * @param lockscreenVisibility value for lock screen Visibility
     * @return boolean
     */
    private boolean isLockScreenVisibility(int lockscreenVisibility) {
        return (lockscreenVisibility >= -1 && lockscreenVisibility <= 1);
    }
}
