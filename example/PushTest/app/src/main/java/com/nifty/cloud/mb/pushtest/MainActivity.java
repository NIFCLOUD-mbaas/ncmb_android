package com.nifty.cloud.mb.pushtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBPush;

/**
 * テスト一覧画面クラス
 */
public class MainActivity extends ActionBarActivity {
    ListView lv;
    Intent intent;

    //テストケース
    private static final String REGISTRATIONID = "RegistrationId";
    private static final String SENDPUSH = "SendPush";
    private static final String SENDPUSH_AN_HOUR_LATER = "SendPush_AnHourLater";
    private static final String DIALOGPUSH = "DialogPush";
    private static final String DIALOGPUSH_NONE = "DialogPush_None";
    private static final String DIALOGPUSH_BACKGROUND = "DialogPush_Background";
    private static final String DIALOGPUSH_ORIGINAL_LAYOUT = "DialogPush_OriginalLayout";
    private static final String RICHPUSH = "RichPush";
    private static final String CHANNELS_SAVE = "Channels_Save";
    private static final String CHANNELS_GET = "Channels_Get";
    private static final String CHANNELS_DERETE = "Channels_Delete";
    private static final String CHANNELS_SEND = "Channels_Send";
    private static final String CURRENT_INSTALLATION = "CurrentInstallation";


    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mBaaS初期化
        NCMB.initialize(this,
                "applicationKey",
                "clientKey");

        //トースト表示用コンテキス保持
        context = this;

        //テスト結果画面へのインテント作成
        intent = new Intent(this, ResultActivity.class);

        //テスト項目のリストを作成
        final String[] testCase = {
                REGISTRATIONID, SENDPUSH, SENDPUSH_AN_HOUR_LATER,DIALOGPUSH, DIALOGPUSH_NONE, DIALOGPUSH_BACKGROUND,
                DIALOGPUSH_ORIGINAL_LAYOUT, RICHPUSH, CHANNELS_SAVE, CHANNELS_GET, CHANNELS_DERETE,
                CHANNELS_SEND, CURRENT_INSTALLATION};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_expandable_list_item_1,
                testCase);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        //リスト項目がクリック時のリスナー登録
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //テスト項目名取得
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);

                //テスト結果の初期化
                TestCase.error = null;
                TestCase.TestCompletion = false;

                switch (item) {
                    case REGISTRATIONID:
                        TestCase.registrationId();
                        break;
                    case SENDPUSH:
                        TestCase.sendPush();
                        break;
                    case SENDPUSH_AN_HOUR_LATER:
                        TestCase.sendPush_anHourLater();
                        break;
                    case DIALOGPUSH:
                        TestCase.dialogPush();
                        break;
                    case DIALOGPUSH_NONE:
                        TestCase.dialogPush_none();
                        break;
                    case DIALOGPUSH_BACKGROUND:
                        TestCase.dialogPush_background();
                        break;
                    case DIALOGPUSH_ORIGINAL_LAYOUT:
                        TestCase.dialogPush_originalLayout();
                        break;
                    case RICHPUSH:
                        TestCase.richPush();
                        break;
                    case CHANNELS_SAVE:
                        TestCase.channels_save();
                        break;
                    case CHANNELS_GET:
                        TestCase.channels_get();
                        break;
                    case CHANNELS_DERETE:
                        TestCase.channels_delete();
                        break;
                    case CHANNELS_SEND:
                        TestCase.channels_send();
                        break;
                    case CURRENT_INSTALLATION:
                        TestCase.currentInstallation();
                        break;
                }
                startActivity(intent);
            }
        });

        //開封通知登録
        NCMBPush.trackAppOpened(getIntent());
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

    @Override
    public void onResume() {
        super.onResume();
        NCMBPush.richPushHandler(this, getIntent());
        //リッチプッシュを再表示させたくない場合はintentからURLを削除
        getIntent().removeExtra("com.nifty.RichUrl");
    }
}
