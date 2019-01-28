package com.nifcloud.mbaas.core.model;

import com.google.gson.annotations.SerializedName;

public class NotificationChannel {
    @SerializedName("notification")
    private NotificationSetting notificationSetting;

    public NotificationSetting getNotificationSetting() {
        return notificationSetting;
    }

    public void setNotificationSetting(NotificationSetting notificationSetting) {
        this.notificationSetting = notificationSetting;
    }

}
