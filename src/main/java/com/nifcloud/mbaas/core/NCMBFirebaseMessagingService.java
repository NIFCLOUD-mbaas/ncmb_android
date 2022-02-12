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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.util.Patterns;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

/**
 * FCM push notification receive class
 */
public class NCMBFirebaseMessagingService extends FirebaseMessagingService {
    //<meta-data>
    static final String OPEN_PUSH_START_ACTIVITY_KEY = "openPushStartActivity";
    static final String SMALL_ICON_KEY = "smallIcon";
    static final String SMALL_ICON_COLOR_KEY = "smallIconColor";
    static final String NOTIFICATION_OVERLAP_KEY = "notificationOverlap";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *
     * @param token InstanceID token
     */
    @Override
    public void onNewToken(String token) {
        if(NCMBApplicationController.getApplicationState() != null){
            NCMBInstallationUtils.updateToken(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null && remoteMessage.getData() != null) {
            SharedPreferences recentPushIdPref = this.getSharedPreferences("ncmbPushId", Context.MODE_PRIVATE);
            String recentPushId = recentPushIdPref.getString("recentPushId", "");
            String currentPushId = remoteMessage.getData().get("com.nifcloud.mbaas.PushId");
            // Skip duplicated message
            if (!recentPushId.equals(currentPushId)) {
                SharedPreferences.Editor editor = recentPushIdPref.edit();
                editor.putString("recentPushId", currentPushId);
                editor.apply();
                super.onMessageReceived(remoteMessage);
                Bundle bundle = getBundleFromRemoteMessage(remoteMessage);
                sendNotification(bundle);
            }
        }
    }

    protected Bundle getBundleFromRemoteMessage(RemoteMessage remoteMessage) {
        Bundle bundle = new Bundle();
        Map<String, String> data = remoteMessage.getData();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
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
            String channel = pushData.getString("com.nifcloud.mbaas.Channel");
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


        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_IMMUTABLE);

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

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NCMBNotificationUtils.getDefaultChannel());
        //SmallIconカラーを設定
        int smallIconColor = appInfo.metaData.getInt(SMALL_ICON_COLOR_KEY);

        //Notification作成
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final String dataKey = "com.nifcloud.mbaas.Data";

        if (pushData.getString(dataKey) != null) {
            String imageUrl = null;
            Bitmap bitmap = null;
            try {
                JSONObject userSettingJson = new JSONObject((String) pushData.get(dataKey));
                if (userSettingJson.get("imgUrl") != null) {
                    imageUrl = userSettingJson.get("imgUrl").toString();
                }
            } catch (JSONException e) {}

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                bitmap = getBitmapFromURL(imageUrl);
            }
            if (bitmap != null) {
                settingBigNotification(bitmap, notificationBuilder, icon, smallIconColor, title, message, pendingIntent, defaultSoundUri);
            } else {
                settingSmallNotification(notificationBuilder, icon, smallIconColor, title, message, pendingIntent, defaultSoundUri);
            }
        } else {
            settingSmallNotification(notificationBuilder, icon, smallIconColor, title, message, pendingIntent, defaultSoundUri);
        }

        return notificationBuilder;
    }

    private void settingBigNotification(Bitmap bitmap, NotificationCompat.Builder notificationBuilder, int icon, int smallIconColor, String title, String message, PendingIntent pendingIntent, Uri defaultSoundUri) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(message);
        bigPictureStyle.bigPicture(bitmap).bigLargeIcon(null);

        notificationBuilder.setSmallIcon(icon)//通知エリアのアイコン設定
                .setColor(smallIconColor) //通知エリアのアイコンカラー設定
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(bigPictureStyle)
                .setAutoCancel(true)//通知をタップしたら自動で削除する
                .setSound(defaultSoundUri)//端末のデフォルトサウンド
                .setContentIntent(pendingIntent);//通知をタップした際に起動するActivity
    }

    private void settingSmallNotification(NotificationCompat.Builder notificationBuilder, int icon, int smallIconColor, String title, String message, PendingIntent pendingIntent, Uri defaultSoundUri) {
        notificationBuilder.setSmallIcon(icon)//通知エリアのアイコン設定
                .setColor(smallIconColor) //通知エリアのアイコンカラー設定
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title))
                .setAutoCancel(true)//通知をタップしたら自動で削除する
                .setSound(defaultSoundUri)//端末のデフォルトサウンド
                .setContentIntent(pendingIntent);//通知をタップした際に起動するActivity
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}

