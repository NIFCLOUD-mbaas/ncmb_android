/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class NCMBInstallationUtils {

    private static final String THIS_DEVICE_NOT_SUPPORTED_MESSAGE = 
        "This device is not supported google-play-services-APK.";

    protected static void saveInstallation(Map<String,String> installationCustomFields) {
        DeviceTokenCallbackQueue.getInstance().beginSaveInstallation();

        try {
            //端末にAPKがインストールされていない場合は処理を終了
            if (!checkPlayServices(NCMB.getCurrentContext().context)) {
                DeviceTokenCallbackQueue.getInstance().execQueue(null,new NCMBException(new IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)));
                return;
            }

            final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
            if(installationCustomFields != null){
                for (Entry<String,String> entry : installationCustomFields.entrySet()) {
                    if(entry.getKey() != null && entry.getValue() != null){
                        installation.put(entry.getKey(),entry.getValue());
                    }
                }
            }

            installation.getDeviceTokenInternalProcess(new TokenCallback() {
                @Override
                public void done(final String token, NCMBException e) {
                    if (e == null) {
                        installation.setDeviceToken(token);
                        //端末情報をデータストアに登録
                        installation.saveInBackground(new DoneCallback() {
                            @Override
                            public void done(NCMBException saveErr) {
                                if (saveErr == null) {
                                    //保存成功
                                    DeviceTokenCallbackQueue.getInstance().execQueue(token,null);
                                } else if (NCMBException.DUPLICATE_VALUE.equals(saveErr.getCode())) {
                                    //保存失敗 : registrationID重複
                                    updateInstallation(installation);
                                } else if (NCMBException.DATA_NOT_FOUND.equals(saveErr.getCode())) {
                                    //保存失敗 : 端末情報の該当データがない
                                    reRegistInstallation(installation);
                                } else {
                                    DeviceTokenCallbackQueue.getInstance().execQueue(token,saveErr);
                                }
                            }
                        });
                    }else{
                        DeviceTokenCallbackQueue.getInstance().execQueue(token,e);
                    }
                }
            });
        } catch (NoClassDefFoundError e) {
            Log.i("INFO", "For Push Notification function, you must be install Google Play Services in SDK Manager and add the FCM dependency. More information: https://mbaas.nifcloud.com/doc/current/push/basic_usage_android.html");
            DeviceTokenCallbackQueue.getInstance().execQueue(null,new NCMBException(new IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)));
        } catch (Exception e) {
            Log.e("Error", e.toString());
            DeviceTokenCallbackQueue.getInstance().execQueue(null,new NCMBException(new IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)));
        }
    }

    /**
     * 端末にGooglePlay開発者サービスがインストールされているか確認
     * インストールされていな場合はエラーを返す
     *
     * @param context
     * @return bool
     */
    protected static boolean checkPlayServices(Context context) throws Exception {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            throw new IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE);
        }
        return true;
    }

    private static void updateInstallation(final NCMBInstallation installation) {

        //installationクラスを検索するクエリの作成
        NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();

        //同じRegistration IDをdeviceTokenフィールドに持つ端末情報を検索する
        query.whereEqualTo("deviceToken", installation.getLocalDeviceToken());

        //データストアの検索を実行
        query.findInBackground(new FindCallback<NCMBInstallation>() {
            @Override
            public void done(List<NCMBInstallation> results, NCMBException e) {

                if (results != null && results.size() > 0) {
                    //検索された端末情報のobjectIdを設定
                    try {
                        installation.setObjectId(results.get(0).getObjectId());
                    } catch (NCMBException searchErr) {
                        Log.e("Error", searchErr.toString());
                    }

                    //端末情報を更新する
                    installation.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            DeviceTokenCallbackQueue.getInstance().execQueue(installation.getLocalDeviceToken(), null);
                        }
                    });
                }
            }
        });
    }

    private static void reRegistInstallation(final NCMBInstallation installation) {

        //objectIdと作成日・更新日のみ削除し、installationが再生成されるようにする
        try {
            installation.setObjectId(null);
            installation.remove("createDate");
            installation.remove("updateDate");
            //データストアの端末情報の再登録を実行
            installation.saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    DeviceTokenCallbackQueue.getInstance().execQueue(installation.getLocalDeviceToken(),null);
                }
            });
        } catch (NCMBException e) {
            Log.e("Error", e.toString());
        }
    }

    static void updateToken(String token){
        NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
        String localToken = installation.getLocalDeviceToken();
        if(installation.getObjectId() != null && installation.getLocalDeviceToken() != token){
            try {
                installation.setDeviceToken(token);
                installation.save();
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
        }
    }
}
