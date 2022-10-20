## Android SDK for NIFCLOUD mobile backend

[![Build Status](https://travis-ci.org/NIFCLOUD-mbaas/ncmb_android.svg?branch=master)](https://travis-ci.org/NIFCLOUD-mbaas/ncmb_android)


## 依存ライブラリ

このSDKでは、以下のライブラリを使用しています。

- Gson

プッシュ通知機能を利用する場合には以下のライブラリを設定する必要があります。<br>
事前にSDK Managerでのインストールが必要です。

- Android Support Library
- Google Play Services SDK


## 動作環境

本SDKは、以下の環境にて動作確認を行っております。
- Android 8.x ～ 13.x
- Android Studio 4.x ～ 2021.3.1
(※2022年10月時点)

## テクニカルサポート窓口対応バージョン

テクニカルサポート窓口では、1年半以内にリリースされたSDKに対してのみサポート対応させていただきます。<br>
定期的なバージョンのアップデートにご協力ください。<br>
※なお、mobile backend にて大規模な改修が行われた際は、1年半以内のSDKであっても対応出来ない場合がございます。<br>
その際は[informationブログ](https://mbaas.nifcloud.com/info/)にてお知らせいたします。予めご了承ください。

- v3.0.5 ～ (※2022年2月時点)

## インストール

Android Studioでプロジェクトを開き、以下の手順でSDKをインストールしてください。

- v4.1.0以降の場合  

    app/build.gradleに以下を追加します。  
    ```
    dependencies{
        implementation 'com.nifcloud.mbaas:ncmb_android:4.1.0'
    }
    ```

    ※v4.1.0からは依存関係が含まれておりますので以前必要だったGsonライブラリの設定は必要ありません。

- v4.0.3以前の場合  

    1. Githubリリースページの NCMB.x.x.x.zip ボタンからNCMB.jarをダウンロードします。  
    2. app/libsフォルダにNCMB.jarをコピーします。
    3. app/build.gradleファイルに以下を追加します。

    ```
    dependencies {
        implementation 'com.google.code.gson:gson:2.3.1'
        api files('libs/NCMB.jar')
    }
    ```

## クイックスタート

* AndroidManifest.xmlの編集

&lt;application&gt;タグの直前に以下のpermissionを追加します。

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* 利用するライブラリの宣言

Activityの冒頭に利用するライブラリを追記します。

```
import com.nifcloud.mbaas.core.NCMB;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBObject;
import com.nifcloud.mbaas.core.DoneCallback;
```

* 初期化

ActivityのonCreateメソッド内に以下を記載します。

```
NCMB.initialize(this,"APP_KEY","CLIENT_KEY");
```

* オブジェクトの保存

NCMB.initializeの下に以下を記載します。

```
// クラスのNCMBObjectを作成
NCMBObject obj = new NCMBObject("TestClass");
// オブジェクトの値を設定
try {
    obj.put("message", "Hello, NCMB!");
} catch (NCMBException e) {
    e.printStackTrace();
}
// データストアへの登録
obj.saveInBackground(new DoneCallback() {
    @Override
    public void done(NCMBException e) {
        if(e != null){
            //保存に失敗した場合の処理

        }else {
            //保存に成功した場合の処理

        }
    }
});
```

## ライセンス

本SDKのライセンスについては、LICENSEファイルをご覧ください。

## 参考URL集

- [ニフクラ mobile backend](https://mbaas.nifcloud.com/)
- [SDKの詳細な使い方](https://mbaas.nifcloud.com/doc/current/)
- [サンプル＆チュートリアル](https://mbaas.nifcloud.com/doc/current/tutorial/tutorial_android.html)
- [ユーザーコミュニティ](https://github.com/NIFCLOUD-mbaas/UserCommunity)
