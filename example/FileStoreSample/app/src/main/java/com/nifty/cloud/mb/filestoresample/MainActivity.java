package com.nifty.cloud.mb.filestoresample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.FetchFileCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBAcl;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;
import com.nifty.cloud.mb.core.NCMBQuery;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static final String INTENT_RESULT = "result";
    static final String TEXT_FILENAME = "Hello.txt";
    static final String TEXT_FILENAME_JP = "テスト.txt";
    static final String IMAGE_FILENAME = "ic_launcher.png";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初期化
        NCMB.initialize(
                this.getApplicationContext(),
                "YOUR_APPLICATION_KEY",
                "YOUR_CLIENT_KEY");
        intent = new Intent(this, ResultActivity.class);
    }

    /**
     * 同期POSTボタン処理
     * 読み込み:不可,書き込み:可でテキストファイルをアップロード
     *
     * @param v view
     */
    public void onPOSTClicked(View v) {
        Toast.makeText(this, "同期POST実行", Toast.LENGTH_SHORT).show();

        //ACL 読み込み:不可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(false);
        acl.setPublicWriteAccess(true);

        //テキストアップロード
        byte[] data = "Hello,NCMB".getBytes();
        NCMBFile file = new NCMBFile(TEXT_FILENAME, data, acl);

        //通信
        String result;
        try {
            file.save();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 非同期POSTボタン処理
     * 読み込み:不可,書き込み:可で画像ファイルをアップロード
     *
     * @param v view
     */
    public void onAsyncPOSTClicked(View v) {
        Toast.makeText(this, "非同期POST実行", Toast.LENGTH_SHORT).show();

        //画像アップロード
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, byteArrayStream);
        byte[] data = byteArrayStream.toByteArray();

        //ACL 読み込み:不可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(false);
        acl.setPublicWriteAccess(true);

        //通信
        final NCMBFile file = new NCMBFile(IMAGE_FILENAME, data, acl);
        file.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                String result;
                if (e != null) {
                    result = createFailureString(e);
                } else {
                    result = createSuccessString(file);
                }

                //結果
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 同期PUTボタン処理
     * 読み込み:可,書き込み:可でテキストファイルを更新
     *
     * @param v view
     */
    public void onPUTClicked(View v) {
        Toast.makeText(this, "同期PUT実行", Toast.LENGTH_SHORT).show();

        //ACL 読み込み:可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);

        //テキストACL更新
        NCMBFile file = new NCMBFile(TEXT_FILENAME, acl);

        //通信
        String result;
        try {
            file.update();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 非同期PUTボタン処理
     * 読み込み:可,書き込み:可で画像ファイルを更新
     *
     * @param v view
     */
    public void onAsyncPUTClicked(View v) {
        Toast.makeText(this, "非同期PUT実行", Toast.LENGTH_SHORT).show();

        //ACL 読み込み:可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);

        //画像ACL更新
        final NCMBFile file = new NCMBFile(IMAGE_FILENAME, acl);

        //通信
        file.updateInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                String result;
                if (e != null) {
                    result = createFailureString(e);
                } else {
                    result = createSuccessString(file);
                }

                //結果
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 同期DELETEボタン処理
     * テキストファイルを削除
     *
     * @param v view
     */
    public void onDELETEClicked(View v) {
        Toast.makeText(this, "同期DELETE実行", Toast.LENGTH_SHORT).show();
        //テキスト削除
        NCMBFile file = new NCMBFile(TEXT_FILENAME);

        //通信
        String result;
        try {
            file.delete();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 非同期DELETEボタン処理
     * 画像ファイルを削除
     *
     * @param v view
     */
    public void onAsyncDELETEClicked(View v) {
        Toast.makeText(this, "非同期DELETE実行", Toast.LENGTH_SHORT).show();
        //画像削除
        final NCMBFile file = new NCMBFile(IMAGE_FILENAME);

        //通信
        file.deleteInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                String result;
                if (e != null) {
                    result = createFailureString(e);
                } else {
                    result = createSuccessString(file);
                }

                //結果
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 同期GETボタン処理
     * 読み込み:可の場合にテキストファイルをダウンロード
     *
     * @param v view
     */
    public void onGETClicked(View v) {
        Toast.makeText(this, "同期GET実行", Toast.LENGTH_SHORT).show();
        //テキストダウンロード
        NCMBFile file = new NCMBFile(TEXT_FILENAME);

        //通信
        String result;
        try {
            file.fetch();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 非同期GETボタン処理
     * 読み込み:可の場合に画像ファイルをダウンロード
     *
     * @param v view
     */
    public void onAsyncGETClicked(View v) {
        Toast.makeText(this, "非同期GET実行", Toast.LENGTH_SHORT).show();
        //画像ダウンロード
        final NCMBFile file = new NCMBFile(IMAGE_FILENAME);

        //通信
        file.fetchInBackground(new FetchFileCallback() {
            @Override
            public void done(byte[] data, NCMBException e) {
                String result;
                if (e != null) {
                    result = createFailureString(e);
                } else {
                    result = createSuccessString(file);
                }

                //結果
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * 非同期GET(クエリ)ボタン処理
     * 読み込み:可のファイルデータを全検索
     *
     * @param v view
     */
    public void onGETQueryClicked(View v) {
        Toast.makeText(this, "同期GET(Query)実行", Toast.LENGTH_SHORT).show();
        //全検索
        NCMBQuery<NCMBFile> query = new NCMBQuery<>("file");

        //通信
        String result;
        try {
            List<NCMBFile> list = query.find();
            result = createSuccessStringFromList(list);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 非同期GET(クエリ)ボタン処理
     * 読み込み:可の場合に画像ファイルデータを取得
     *
     * @param v view
     */
    public void onAsyncGETQueryClicked(View v) {
        Toast.makeText(this, "非同期GET(Query)実行", Toast.LENGTH_SHORT).show();
        //画像ファイル名を指定して検索
        NCMBQuery<NCMBFile> query = new NCMBQuery<>("file");
        query.whereEqualTo("fileName", IMAGE_FILENAME);

        //通信
        query.findInBackground(new FindCallback<NCMBFile>() {
            @Override
            public void done(List<NCMBFile> results, NCMBException e) {
                String result;
                if (e != null) {
                    result = createFailureString(e);
                } else {
                    result = createSuccessStringFromList(results);
                }

                //結果
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }


    /**
     * 日本語ファイル名のアップロード
     *
     * @param v view
     */
    public void onPOST_JP_Clicked(View v) {
        //ACL 読み込み:不可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(false);
        acl.setPublicWriteAccess(true);


        //テキストアップロード
        byte[] data = "日本語名ファイルアップロード".getBytes();
        NCMBFile file = new NCMBFile(TEXT_FILENAME_JP, data, acl);

        //通信
        String result;
        try {
            file.save();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }


    /**
     * 日本語ファイル名の更新
     *
     * @param v view
     */
    public void onPUT_JP_Clicked(View v) {
        //ACL 読み込み:可 , 書き込み:可
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        //テキスト更新
        NCMBFile file = new NCMBFile(TEXT_FILENAME_JP, acl);

        //通信
        String result;
        try {
            file.update();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * 日本語ファイル名の削除
     *
     * @param v view
     */
    public void onDELETE_JP_Clicked(View v) {
        //テキスト削除
        NCMBFile file = new NCMBFile(TEXT_FILENAME_JP);

        //通信
        String result;
        try {
            file.delete();
            result = createSuccessString(file);
        } catch (NCMBException e) {
            result = createFailureString(e);
        }

        //結果
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    String createSuccessString(NCMBFile file) {
        //成功時結果画面に出力する文字列の作成
        StringBuilder sb = new StringBuilder("【Success】\n");
        sb.append("FileName : ").append(file.getFileName()).append("\n");
        sb.append("FileData :").append(file.getFileData()).append("\n");
        try {
            sb.append("ACL : ").append(file.getAcl().toJson()).append("\n");
        } catch (JSONException | NullPointerException error) {
            sb.append("ACL : ").append("null").append("\n");
        }
        sb.append("CreateDate : ").append(file.getCreateDate()).append("\n");
        sb.append("UpdateDate : ").append(file.getUpdateDate()).append("\n");
        return sb.toString();
    }

    String createFailureString(NCMBException error) {
        //失敗時結果画面に出力する文字列の作成
        StringBuilder sb = new StringBuilder("【Failure】\n");
        if (error.getCode() != null) {
            sb.append("StatusCode : ").append(error.getCode()).append("\n");
        }
        if (error.getMessage() != null) {
            sb.append("Message : ").append(error.getMessage()).append("\n");
        }
        return sb.toString();
    }

    String createSuccessStringFromList(List<NCMBFile> list) {
        //listから成功時結果画面に出力する文字列の作成。クエリ検索後に実施
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            //【Success】を件数に置換
            sb.append(createSuccessString(list.get(i)));
            Pattern p = Pattern.compile("【Success】");
            Matcher m = p.matcher(sb);
            String number = String.valueOf(i + 1);
            sb = new StringBuilder(m.replaceFirst("・" + number + "件目"));
        }

        //先頭に挿入
        sb.insert(0, "【Success】\n 取得件数 : " + list.size() + "\n");
        return sb.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
