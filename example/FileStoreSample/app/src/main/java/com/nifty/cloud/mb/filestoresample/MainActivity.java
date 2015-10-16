package com.nifty.cloud.mb.filestoresample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBAcl;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NCMB.initialize(
                this,
                "YOUR_APPLICATION_KEY",
                "YOUR_CLIENT_KEY"
        );

        Button uploadButton = (Button)findViewById(R.id.uploadButton);

        // リスナーをボタンに登録
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mobile backendのファイルストアに画像を保存
                try {
                    /*
                    NCMBObject obj = new NCMBObject("TestClass");
                    obj.put("key", "value");
                    obj.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null) {
                                Log.d("Error", e.getMessage());
                            }
                        }
                    });
                    */

                    InputStream istream = getResources().getAssets().open("img_mb.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(istream);

                    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayStream);
                    byteArrayStream.flush();
                    byte[] byteArray = byteArrayStream.toByteArray();
                    byteArrayStream.close();

                    NCMBFile file = new NCMBFile("test.txt", byteArray, new NCMBAcl());
                    file.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e != null){
                                Log.d("Error", e.getMessage());
                            }
                        }
                    });

                } catch (IOException e) {

                //} catch (NCMBException e){
                    e.printStackTrace();
                }

            }
        });
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
