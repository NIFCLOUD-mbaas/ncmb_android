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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Random;

/**
 * GCM push notification receive class
 */
public class NCMBGcmListenerService extends GcmListenerService {

    //<meta-data>
    static final String OPEN_PUSH_START_ACTIVITY_KEY = "openPushStartActivity";
    static final String SMALL_ICON_KEY = "smallIcon";
    static final String SMALL_ICON_COLOR_KEY = "smallIconColor";
    static final String NOTIFICATION_OVERLAP_KEY = "notificationOverlap";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data);
    }

    private void sendNotification(Bundle pushData) {

        //サイレントプッシュ
        if ((!pushData.containsKey("message")) && (!pushData.containsKey("title"))) {
            return;
        }

        NotificationCompat.Builder notificationBuilder = notificationSettings(pushData);

        /*
         * 通知重複設定
         * 0:常に最新の通知のみ表示
         * 1:最新以外の通知も複数表示
         */
        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        boolean containsKey = appInfo.metaData.containsKey(NOTIFICATION_OVERLAP_KEY);
        int overlap = appInfo.metaData.getInt(NOTIFICATION_OVERLAP_KEY);

        //デフォルト複数表示
        int notificationId = new Random().nextInt();

        if (overlap == 0 && containsKey) {
            //最新のみ表示
            notificationId = 0;
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public NotificationCompat.Builder notificationSettings(Bundle pushData) {
        //AndroidManifestから情報を取得
        ApplicationInfo appInfo = null;
        Class startClass = null;
        String applicationName = null;
        String activityName = null;
        String packageName = null;
        int channelIcon = 0;
        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            applicationName = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getPackageName(), 0)).toString();
            activityName = appInfo.packageName + appInfo.metaData.getString(OPEN_PUSH_START_ACTIVITY_KEY);
            packageName = appInfo.packageName;

            /*
            プッシュデータにチャネルが指定されているかつ、チャネルファイルが登録されている場合は、
            通知タップ起動時のactivityNameをファイル内指定のactivityNameactivityNameに更新する
            */
            String channel = pushData.getString("com.nifty.Channel");
            if (channel != null) {
                File channelDirectory = new File(this.getDir(NCMBLocalFile.FOLDER_NAME, Context.MODE_PRIVATE), NCMBInstallation.CHANNELS_FOLDER_NAME);
                File channelFile = new File(channelDirectory, channel);
                if (channelFile.exists()) {
                    JSONObject json = new JSONObject();
                    try {
                        json = NCMBLocalFile.readFile(channelFile);
                    } catch (NCMBException e) {
                        Log.e("Error", e.toString());
                    }
                    if (json.has("activityClass")) {
                        activityName = json.getString("activityClass");
                    }
                    if (json.has("icon")) {
                        channelIcon = json.getInt("icon");
                    }
                    //v1→v2時のみTrue. v2でチャネル登録した場合には設定されない
                    if (json.has("activityPackage")) {
                        packageName = json.getString("activityPackage");
                    }
                }
            }
            //通知起動時のActivityクラスを作成
            startClass = Class.forName(activityName);
        } catch (PackageManager.NameNotFoundException | ClassNotFoundException | JSONException e) {
            throw new IllegalArgumentException(e);
        }

        //通知エリアに表示されるプッシュ通知をタップした際に起動するアクティビティ画面を設定する
        Intent intent = new Intent(this, startClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ComponentName componentName =
                new ComponentName(packageName, activityName);
        intent.setComponent(componentName);
        intent.putExtras(pushData);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //pushDataから情報を取得
        String message = "";
        String title = "";
        if (pushData.getString("title") != null) {
            title = pushData.getString("title");
        } else {
            //titleの設定が無い場合はアプリ名をセットする
            title = applicationName;
        }
        if (pushData.getString("message") != null) {
            message = pushData.getString("message");
        }

        //SmallIconを設定。manifestsにユーザー指定の設定が無い場合はアプリアイコンを設定する
        int userSmallIcon = appInfo.metaData.getInt(SMALL_ICON_KEY);
        int icon;
        if (channelIcon != 0) {
            //チャネル毎にアイコン設定がされている場合
            icon = channelIcon;
        } else if (userSmallIcon != 0) {
            //manifestsにアイコン設定がされている場合
            icon = userSmallIcon;
        } else {
            //それ以外はアプリのアイコンを設定する
            icon = appInfo.icon;
        }
        //SmallIconカラーを設定
        int smallIconColor = appInfo.metaData.getInt(SMALL_ICON_COLOR_KEY);

        //Notification作成
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NCMBNotificationUtils.getDefaultChannel())
                .setSmallIcon(icon)//通知エリアのアイコン設定
                .setColor(smallIconColor) //通知エリアのアイコンカラー設定
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)//通知をタップしたら自動で削除する
                .setSound(defaultSoundUri)//端末のデフォルトサウンド
                .setContentIntent(pendingIntent);//通知をタップした際に起動するActivity

        return notificationBuilder;
    }
}
