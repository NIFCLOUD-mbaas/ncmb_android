package com.nifcloud.mbaas.core.model;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("importance")
    private int importance = 3;
    @SerializedName("description")
    private String description;
    @SerializedName("enableLights")
    private boolean enableLights = true;
    @SerializedName("enableVibration")
    private boolean enableVibration = true;
    @SerializedName("lightColor")
    private int lightColor = Color.GREEN;
    @SerializedName("lockscreenVisibility")
    private int lockscreenVisibility = 0;

    public Category() {
    }

    public Category(String id, String name, int importance, String description, boolean enableLights, boolean enableVibration, int lightColor, int lockscreenVisibility) {
        this.id = id;
        this.name = name;
        this.importance = importance;
        this.description = description;
        this.enableLights = enableLights;
        this.enableVibration = enableVibration;
        this.lightColor = lightColor;
        this.lockscreenVisibility = lockscreenVisibility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if (null == name) {
            name = getId();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImportance() {
        if (!isImportanceNum(importance)) {
            importance = 3;
        }
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getDescription() {
        if (null == description) {
            description = new String("");
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnableLights() {
        return enableLights;
    }

    public void setEnableLights(boolean enableLights) {
        this.enableLights = enableLights;
    }

    public boolean isEnableVibration() {
        return enableVibration;
    }

    public void setEnableVibration(boolean enableVibration) {
        this.enableVibration = enableVibration;
    }

    public int getLightColor() {
        return lightColor;
    }

    public void setLightColor(int lightColor) {
        this.lightColor = lightColor;
    }

    public int getLockscreenVisibility() {
        if (lockscreenVisibility < -1 || lockscreenVisibility > 1) {
            lockscreenVisibility = 0;
        }
        return lockscreenVisibility;
    }

    public void setLockscreenVisibility(int lockscreenVisibility) {
        this.lockscreenVisibility = lockscreenVisibility;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", importance=" + importance +
                ", description='" + description + '\'' +
                ", enableLights=" + enableLights +
                ", enableVibration=" + enableVibration +
                ", lightColor=" + lightColor +
                ", lockscreenVisibility=" + lockscreenVisibility +
                '}';
    }

    /**
     * Check importance is in range
     * @param importance
     * @return boolean
     */
    private boolean isImportanceNum(int importance) {
        if ((importance >= 0 && importance <= 5) || (importance == -1000)) {
            return true;
        }
        return false;
    }

}
