package com.nifty.cloud.mb.googlesample;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBGoogleParameters;
import com.nifty.cloud.mb.core.NCMBUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN";
    private static final String ACCOUNT_TYPE_GOOGLE = "com.google";
    private static final String AUTH_SCOPE = "oauth2:profile email";
    private static final int REQUEST_SIGN_IN = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NCMB.initialize(
                this,
                "YOUR_APPLICATION_KEY",
                "YOUR_CLIENT_KEY"
        );

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGoogleToken();
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.GET_ACCOUNTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "ACCESS_GET_ACCOUNTS is granted!");
                } else {
                    Log.d(TAG, "ACCESS_GET_ACCOUNTS is denied!");
                }
            }
        }
    }

    private void getGoogleToken() {

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String> () {
            @Override
            protected String doInBackground(String... accounts) {

                String scopes = AUTH_SCOPE;
                String token = null;
                String id = null;
                try {
                    // Get google account
                    AccountManager accountManager = AccountManager.get(getApplicationContext());
                    Account[] getAccounts = accountManager.getAccountsByType(ACCOUNT_TYPE_GOOGLE);
                    String accountName = getAccounts[0].name;

                    id = GoogleAuthUtil.getAccountId(getApplicationContext(), accountName);
                    token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
                    Log.d(TAG, "id: " + id);
                    Log.d(TAG, "token: " + token);

                    NCMBGoogleParameters parameters = new NCMBGoogleParameters(
                            id,
                            token
                    );

                    NCMBUser.loginWith(parameters);

                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), REQUEST_SIGN_IN);
                } catch (IOException | GoogleAuthException | ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, e.getMessage());
                } catch (NCMBException e) {
                    e.printStackTrace();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };

        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN && resultCode == RESULT_OK) {
            getGoogleToken();
        }
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
