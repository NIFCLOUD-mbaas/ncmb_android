package com.nifty.cloud.mb.pushtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 結果表示画面クラス
 */
public class ResultActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //ダイアログ表示
        final ProgressDialog dialog = ProgressDialog.show(ResultActivity.this,
                "Please wait",
                "Test run...",
                true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //非同期処理

                while (!TestCase.TestCompletion){
                    //...テスト結果待ち
                }
                dialog.dismiss();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //メインスレッド処理

                //結果表示
                TextView resultCategory = (TextView) findViewById(R.id.result_category);
                resultCategory.setText("Result : ");
                TextView resultMassage = (TextView) findViewById(R.id.result_massage);

                if (TestCase.error == null) {
                    resultMassage.setText("Success");
                    resultMassage.setTextColor(Color.BLUE);

                } else {
                    resultMassage.setText("Failure");
                    resultMassage.setTextColor(Color.RED);

                    TextView errorCategory = (TextView) findViewById(R.id.error_category);
                    errorCategory.setText("Error : ");
                    TextView errorMassage = (TextView) findViewById(R.id.error_massage);
                    errorMassage.setText(TestCase.error.toString());
                    errorMassage.setTextColor(Color.RED);
                }
            }
        };
        //AsyncTask実行
        task.execute(null, null, null);
    }

//    //結果画面でリッチプッシュを表示する場合はコメントアウトを外す
//    @Override
//    public void onResume() {
//        super.onResume();
//        NCMBPush.richPushHandler(this, getIntent());
//    }
}
