package com.nifty.cloud.mb.core;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class CustomGcmListenerService extends NCMBGcmListenerService{
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //super.onMessageReceived(from, data);

        SharedPreferences recentPushIdPref = getSharedPreferences("ncmbPushId", Context.MODE_PRIVATE);
        String recentPushId = recentPushIdPref.getString("recentPushId", "");
        String currentPushId = data.getString("com.nifty.PushId");
        if (recentPushId.equals(currentPushId)) {
            stopSelf();
        } else {
            SharedPreferences.Editor editor = recentPushIdPref.edit();
            editor.putString("recentPushId", "updatedPushId");
            editor.commit();

        }
    }
}
