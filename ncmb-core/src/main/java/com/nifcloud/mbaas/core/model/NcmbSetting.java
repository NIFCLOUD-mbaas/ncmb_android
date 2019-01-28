package com.nifcloud.mbaas.core.model;

import com.google.gson.annotations.SerializedName;

public class NcmbSetting {
    @SerializedName("ncmb")
    private NotificationChannel notificationChannel;

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

}
