package com.nifcloud.mbaas.core.model;

import com.google.gson.annotations.SerializedName;

public class Ncmb {
    @SerializedName("notification")
    private Notification notification;

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "Ncmb{" +
                "notification=" + notification +
                '}';
    }
}
