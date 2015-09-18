package com.nifty.cloud.mb.pushtest;

import android.os.AsyncTask;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBDialogPushConfiguration;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBInstallation;
import com.nifty.cloud.mb.core.NCMBPush;
import com.nifty.cloud.mb.core.NCMBQuery;

import org.json.JSONArray;

import java.util.Date;
import java.util.Set;

/**
 * テストケースクラス
 */
public class TestCase {

    // テスト成功結果
    static Exception error = null;
    static Boolean TestCompletion = false;


    //GCMより取得したregistrationIdがmBaaSに保存する
    public static void registrationId() {
        //installationの作成
        final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
        //GCMからRegistrationIdを取得
        installation.getRegistrationIdInBackground("senderId", new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    //成功
                    try {
                        //mBaaSに端末情報を保存
                        installation.save();
                    } catch (NCMBException saveError) {
                        //保存失敗
                        error = saveError;
                    }
                } else {
                    //ID取得失敗
                    error = e;
                }
                TestCompletion = true;
            }
        });

    }


    //プッシュ通知を送信する
    public static void sendPush() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理
                NCMBPush push = new NCMBPush();
                push.setTitle("SendPush");
                push.setMessage("プッシュ通知送信");
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //5分後にプッシュ通知を送信
    public static void sendPush_anHourLater() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                NCMBPush push = new NCMBPush();
                push.setTitle("SendPush");
                push.setMessage("1時間後に通知");
                Date date = new Date();
                date.setTime(date.getTime() + 60 * 60 * 1 * 1000);//1時間後に設定
                push.setDeliveryTime(date);
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //ダイアログプッシュ(標準)を送信する
    public static void dialogPush() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理
                //標準的なダイアログを表示するタイプ
                MyCustomService.dialogPushConfiguration.setDisplayType(NCMBDialogPushConfiguration.DIALOG_DISPLAY_DIALOG);

                NCMBPush push = new NCMBPush();
                push.setTitle("DialogPush");
                push.setMessage("ダイアログプッシュ_標準");
                push.setDialog(true);
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //ダイアログプッシュ(dialog_none)を送信する
    public static void dialogPush_none() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理
                //標準的なダイアログを表示するタイプ
                MyCustomService.dialogPushConfiguration.setDisplayType(NCMBDialogPushConfiguration.DIALOG_DISPLAY_NONE);

                NCMBPush push = new NCMBPush();
                push.setTitle("DialogPush_None");
                push.setMessage("ダイアログプッシュ_表示なし");
                push.setDialog(true);
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //ダイアログプッシュ(カスタム_背景)を送信する
    public static void dialogPush_background() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理

                //背景画像を設定するタイプ
                MyCustomService.dialogPushConfiguration.setDisplayType(NCMBDialogPushConfiguration.DIALOG_DISPLAY_BACKGROUND);

                NCMBPush push = new NCMBPush();
                push.setTitle("DialogPush_background");
                push.setMessage("ダイアログプッシュ_背景画像");
                push.setDialog(true);
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //ダイアログプッシュ(カスタム_レイアウト)を送信する
    public static void dialogPush_originalLayout() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理
                //背景画像を設定するタイプ
                MyCustomService.dialogPushConfiguration.setDisplayType(NCMBDialogPushConfiguration.DIALOG_DISPLAY_ORIGINAL);

                NCMBPush push = new NCMBPush();
                push.setTitle("DialogPush_originalLayout");
                push.setMessage("ダイアログプッシュ_オリジナルレイアウト");
                push.setDialog(true);
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //リッチプッシュを送信する
    public static void richPush() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理
                NCMBPush push = new NCMBPush();
                push.setTitle("richPush");
                push.setMessage("リッチプッシュ");
                push.setRichUrl("https://www.google.co.jp/");
                try {
                    push.send();
                } catch (NCMBException e) {
                    error = e;
                }
                TestCompletion = true;
                return null;
            }
        }.execute();
    }

    //チャネルを登録する
    public static void channels_save() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    NCMBInstallation.subscribe("Ch1", ChannelActivity.class, R.drawable.ch1);
                    NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
                    JSONArray channels = new JSONArray();
                    channels.put("Ch1");
                    installation.setChannels(channels);
                    installation.save();
                } catch (Exception e) {
                    error = e;
                }
                TestCompletion = true;
                return error;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result == null) {
                    Toast.makeText(MainActivity.context, "Ch1を登録",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }.execute(null, null, null);
    }

    //チャネルを取得する
    public static void channels_get() {
        new AsyncTask<Void, Void, Set<String>>() {
            @Override
            protected Set<String> doInBackground(Void[] params) {
                Set<String> set = null;
                try {
                    set = NCMBInstallation.getSubscriptions();
                } catch (Exception e) {
                    error = e;
                }
                TestCompletion = true;
                return set;
            }

            @Override
            protected void onPostExecute(Set<String> result) {
                Toast.makeText(MainActivity.context, "現在のチャネルを取得 : " + result.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    //チャネルを削除する
    public static void channels_delete() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void[] params) {
                try {
                    NCMBInstallation.unsubscribe("Ch1");
                    NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
                    JSONArray channels = new JSONArray();
                    installation.setChannels(channels);
                    installation.save();
                } catch (Exception e) {
                    error = e;
                }
                TestCompletion = true;
                return error;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result == null) {
                    Toast.makeText(MainActivity.context, "Ch1を削除",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    //登録したチャネルにプッシュ通知を送信する
    public static void channels_send() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void[] params) {
                try {
                    NCMBPush push = new NCMBPush();
                    NCMBQuery<NCMBInstallation> query = new NCMBQuery<>("installation");
                    query.whereEqualTo("channels", "Ch1");
                    push.setSearchCondition(query);
                    push.setTitle("channels_send");
                    push.setTitle("チャネル送信");
                    push.send();
                } catch (Exception e) {
                    error = e;
                }
                TestCompletion = true;
                return error;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result == null) {
                    Toast.makeText(MainActivity.context, "Ch1に対してプッシュを送信",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    //登録端末のdeviceTokenを取得する
    public static void currentInstallation() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void[] params) {
                String deviceToken = null;
                try {
                    NCMBInstallation currentInstallation = NCMBInstallation.getCurrentInstallation();
                    deviceToken = currentInstallation.getDeviceToken();
                } catch (Exception e) {
                    error = e;
                }
                TestCompletion = true;
                return deviceToken;
            }

            @Override
            protected void onPostExecute(String deviceToken) {
                Toast.makeText(MainActivity.context, "登録中の端末:\n" + deviceToken, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }
}



