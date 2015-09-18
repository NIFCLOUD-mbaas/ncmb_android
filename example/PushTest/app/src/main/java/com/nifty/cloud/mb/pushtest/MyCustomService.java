package com.nifty.cloud.mb.pushtest;

import android.os.Bundle;
import android.util.Log;

import com.nifty.cloud.mb.core.NCMBDialogPushConfiguration;
import com.nifty.cloud.mb.core.NCMBGcmListenerService;
import com.nifty.cloud.mb.core.NCMBPush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * 独自受信クラス
 */
public class MyCustomService extends NCMBGcmListenerService {
    //NCMBDialogPushConfigurationクラスのインスタンスを作成
    static NCMBDialogPushConfiguration dialogPushConfiguration = new NCMBDialogPushConfiguration();

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String action = data.getString("action");
        String channel = data.getString("com.nifty.Channel");
        Log.d("tag", "action:" + action);
        Log.d("tag", "channel:" + channel);
        if (data.containsKey("com.nifty.Data")) {
            try {
                JSONObject json = new JSONObject(data.getString("com.nifty.Data"));
                Iterator keys = json.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = json.getString(key);
                    Log.d("tag", "key: " + key);
                    Log.d("tag", "value: " + value);
                }
            } catch (JSONException e) {
                //エラー処理
            }
        }

        NCMBPush.dialogPushHandler(this, data, dialogPushConfiguration);

        //デフォルトの通知を実行
        super.onMessageReceived(from, data);
    }
}
