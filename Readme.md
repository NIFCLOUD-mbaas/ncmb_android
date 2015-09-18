## Android SDK for NiftyCloud mobile backend

## ダウンロード

Githubリリースページの NCMB.x.x.x.zip ボタンからダウンロードしてください。


## インストール

Android Studioでプロジェクトを開き、以下の手順でSDKをインストールしてください。

1. app/libsフォルダにNCMB.jarをコピーします
2. app/build.gradleファイルに以下を追加します

```
dependencies {
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
## 依存ライブラリ（任意）

プッシュ通知機能を利用する場合には以下のライブラリを設定する必要があります。

| 機能名           | 必要なライブラリ |
|:---:             | :---:            |
| プッシュ通知機能 | Google Play Services SDK |

## 動作環境

本SDKは、Android 4.x / 5.xにて動作確認を行っております。

## ライセンス

本SDKのライセンスについては、LICENSEファイルをご覧ください。

## 参考URL集

- [ニフティクラウド mobile backend](http://mb.cloud.nifty.com)
- [ドキュメント](http://mb.cloud.nifty.com/doc)
- [ユーザーコミュニティ](https://github.com/NIFTYCloud-mbaas/UserCommunity)
