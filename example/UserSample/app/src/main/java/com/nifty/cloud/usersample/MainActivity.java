package com.nifty.cloud.usersample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.LoginCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    static final String INTENT_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初期化
        NCMB.initialize(
                this,
                "YOUR_APPLICATION_KEY",
                "YOUR_CLIENT_KEY");

        intent = new Intent(this, ResultActivity.class);
    }

    /**
     * on signUp button clicked
     *
     * @param v view
     */
    public void onSignUpClicked(View v) {
        String result;
        try {
            NCMBUser user = new NCMBUser();
            user.setUserName("TestUser");
            user.setPassword("TestPassword");
            user.signUp();
            result = createSuccessString(user);
            Toast.makeText(this, "新規登録成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "新規登録失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on login button clicked
     *
     * @param v view
     */
    public void onLoginClicked(View v) {
        String result;
        try {
            NCMBUser user = NCMBUser.login("TestUser", "TestPassword");
            result = createSuccessString(user);
            Toast.makeText(this, "ログイン成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "ログイン失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on logout button clicked
     *
     * @param v view
     */
    public void onLogoutClicked(View v) {
        String result;
        try {
            NCMBUser.logout();
            NCMBUser user = NCMBUser.getCurrentUser();
            result = createSuccessString(user);
            Toast.makeText(this, "ログアウト成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "ログアウト失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on currentUser button clicked
     *
     * @param v view
     */
    public void onCurrentUserClicked(View v) {
        String result;
        try {
            NCMBUser user = NCMBUser.getCurrentUser();
            result = createSuccessString(user);
            Toast.makeText(this, "ログインユーザー : " + user.getUserName(), Toast.LENGTH_SHORT).show();
        } catch (NCMBException e) {
            Toast.makeText(this, "ログインユーザーの取得に失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(e);
        }
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on user delete button clicked
     *
     * @param v view
     */
    public void onUserDeleteClicked(View v) {
        String result;
        try {
            NCMBUser user = new NCMBUser();
            user.setObjectId(NCMBUser.getCurrentUser().getObjectId());
            user.deleteObject();
            result = createSuccessString(user);
            Toast.makeText(this, "削除成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "削除失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on mail signUp button clicked
     *
     * @param v view
     */
    public void onMailSignUpClicked(View v) {
        String result;
        try {
            NCMBUser.requestAuthenticationMail("TestMailAddress");
            result = "指定したアドレスに招待メールを送信しました。\n受信したメールから会員登録を行ってください。";
            Toast.makeText(this, "登録メール送信成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "登録メール送信失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on mail signUp in background button clicked
     *
     * @param v view
     */
    public void onMailSignUpInBackgroundClicked(View v) {
        final Context context = this;
        NCMBUser.requestAuthenticationMailInBackground("TestMailAddress", new DoneCallback() {
            @Override
            public void done(NCMBException error) {
                String result;
                if (error == null) {
                    Toast.makeText(context, "登録メール送信成功", Toast.LENGTH_SHORT).show();
                    result = "指定したアドレスに招待メールを送信しました。\n受信したメールから会員登録を行ってください。";
                } else {
                    Toast.makeText(context, "登録メール送信失敗", Toast.LENGTH_SHORT).show();
                    result = createFailedString(error);
                }
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * on mail login button clicked
     *
     * @param v view
     */
    public void onMailLoginClicked(View v) {
        String result;
        try {
            NCMBUser user = NCMBUser.loginWithMailAddress("TestMailAddress", "TestPassWord");
            result = createSuccessString(user);
            Toast.makeText(this, "メールログイン成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "メールログイン失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on mail login in background button clicked
     *
     * @param v view
     */
    public void onMailLoginInBackgroundClicked(View v) {
        final Context context = this;
        NCMBUser.loginWithMailAddressInBackground("TestMailAddress", "TestPassWord", new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException error) {
                String result;
                if (error == null) {
                    try {
                        result = MainActivity.createSuccessString(user);
                        Toast.makeText(context, "メールログイン成功", Toast.LENGTH_SHORT).show();
                    } catch (NCMBException e) {
                        result = createFailedString(e);
                        Toast.makeText(context, "メールログイン失敗", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "メールログイン失敗", Toast.LENGTH_SHORT).show();
                    result = createFailedString(error);
                }
                intent.putExtra(INTENT_RESULT, result);
                startActivityForResult(intent, 0);
            }
        });
    }

    static String createSuccessString(NCMBUser user) throws NCMBException {
        String successString;

        successString = "【Success】\n";
        successString += "ID : " + user.getObjectId() + "\n";
        successString += "UserName : " + user.getUserName() + "\n";
        successString += "MailAddress : " + user.getMailAddress() + "\n";
        successString += "SessionToken : " + user.getString("sessionToken") + "\n";

        return successString;
    }

    String createFailedString(NCMBException error) {
        return "【Failed】\n" +
                "StatusCode : " + error.getCode() + "\n" +
                "Message : " + error.getMessage();
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
