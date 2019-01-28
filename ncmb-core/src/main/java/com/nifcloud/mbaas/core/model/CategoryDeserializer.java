package com.nifcloud.mbaas.core.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CategoryDeserializer implements JsonDeserializer<Category> {


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
     * @param lockscreenVisibility
     * @return boolean
     */
    private boolean isLockScreenVisibility(int lockscreenVisibility) {
        return (lockscreenVisibility >= -1 && lockscreenVisibility <= 1);
    }
}
