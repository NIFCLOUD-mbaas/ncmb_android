## Android SDK for NIF Cloud mobile backend

[![Build Status](https://travis-ci.org/NIFCloud-mbaas/ncmb_android.svg?branch=master)](https://travis-ci.org/NIFCloud-mbaas/ncmb_android)

## ダウンロード

Githubリリースページの NCMB.x.x.x.zip ボタンからダウンロードしてください。

## jarの作成

リリースページからダウンロードせずにjarを作成する場合は、<br>
以下のコマンドをプロジェクトのルートディレクトリで実行してください。

```
./gradlew clean makeJar
```

`ncmb-core/release`にNCMB.jarが作成されます。

Android Studioから作成する場合は、<br>
Gradle projectsタブの `ncmb-core > Tasks > other > makejar` を実行することで作成されます。

## 依存ライブラリ

このSDKでは、以下のライブラリを使用しています。

- Gson

プッシュ通知機能を利用する場合には以下のライブラリを設定する必要があります。<br>
事前にSDK Managerでのインストールが必要です。

- Android Support Library
- Google Play Services SDK


## 動作環境

本SDKは、Android 4.x ～ 8.0 にて動作確認を行っております。

## インストール

Android Studioでプロジェクトを開き、以下の手順でSDKをインストールしてください。

1. app/libsフォルダにNCMB.jarをコピーします
2. app/build.gradleファイルに以下を追加します

```
dependencies {
    compile 'com.google.code.gson:gson:2.3.1'
    compile files('libs/NCMB.jar')
}
```

## クイックスタート

* AndroidManifest.xmlの編集

&lt;application&gt;タグの直前に以下のpermissionを追加します。

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* 初期化

ActivityのonCreateメソッド内に以下を記載します。

```
NCMB.initialize(this,"APP_KEY","CLIENT_KEY");
```

* オブジェクトの保存

NCMB.initializeの下に以下を記載します。

```
NCMBObject obj = new NCMBObject("TestObject");
obj.put("message", "Hello, NCMB!");
obj.saveInBackground(new DoneCallback() {
    @Override
    public void done(NCMBException e) {
        if(e == null){
            //保存成功
        }else {
            //保存失敗
        }
    }
});
```

## ライセンス

本SDKのライセンスについては、LICENSEファイルをご覧ください。

## 参考URL集

- [ニフクラ mobile backend](http://mb.cloud.nifty.com)
- [ドキュメント](http://mb.cloud.nifty.com/doc)
- [ユーザーコミュニティ](https://github.com/NIFCloud-mbaas/UserCommunity)
