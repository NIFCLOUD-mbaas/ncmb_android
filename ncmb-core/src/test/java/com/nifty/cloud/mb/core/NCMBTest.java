package com.nifty.cloud.mb.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.Mockito.when;


@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*", "org.junit.*" })
@PrepareForTest(NCMB.class)
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private MockWebServer mServer;

    @Before
    public void setup() throws Exception {
        //Robolectric.getFakeHttpLayer().interceptHttpRequests(false);

        mServer = new MockWebServer();
        mServer.start();
    }

    @After
    public void teardown() {

    }

    @Test
    public void initialize_with_apiKey() {
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),"appKey","cliKey");
        Assert.assertEquals("appKey",NCMB.getCurrentContext().applicationKey);
        Assert.assertEquals("cliKey", NCMB.getCurrentContext().clientKey);
        Assert.assertEquals("https://mb.api.cloud.nifty.com/2013-09-01/", NCMB.getCurrentContext().baseUrl);
    }

    @Test
    public void initialize_with_apiKey_and_url() {
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                        "appKey",
                        "cliKey",
                        mServer.getUrl("/").toString(),
                        null);
        Assert.assertEquals("appKey",NCMB.getCurrentContext().applicationKey);
        Assert.assertEquals("cliKey", NCMB.getCurrentContext().clientKey);
        Assert.assertEquals(mServer.getUrl("/" + NCMB.DEFAULT_API_VERSION + "/").toString(), NCMB.getCurrentContext().baseUrl);
    }

    @Test
    public void initialize_with_apiKey_and_metaData() {
        PowerMockito.spy(NCMB.class);
        when(NCMB.getMetadata(RuntimeEnvironment.application.getApplicationContext() ,NCMB.METADATA_PREFIX + "DOMAIN_URL")).thenReturn("http://sample.com/");
        when(NCMB.getMetadata(RuntimeEnvironment.application.getApplicationContext() ,NCMB.METADATA_PREFIX + "API_VERSION")).thenReturn("2015-07-23");
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey");

        Assert.assertEquals("appKey", NCMB.getCurrentContext().applicationKey);
        Assert.assertEquals("cliKey", NCMB.getCurrentContext().clientKey);
        Assert.assertEquals("http://sample.com/2015-07-23/", NCMB.getCurrentContext().baseUrl);
    }

    @Test
    public void initialize_with_process_killing() {
        // アプリが起動している場合をモック(getApplicationStateにアプリ起動時の状態を設定)
        PowerMockito.spy(NCMBApplicationController.class);
        NCMBApplicationController controller = new NCMBApplicationController();// モックの戻り値
        controller.onCreate();
        PowerMockito.doReturn(controller).when(NCMBApplicationController.class);
        NCMBApplicationController.getApplicationState();// staticメソッドをモック

        // 初期化
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(), "appKey", "cliKey");

        // Preferencesに書き込まれているか確認
        SharedPreferences preferences = controller.getApplicationContext().getSharedPreferences("NCMB", Context.MODE_PRIVATE);
        Assert.assertEquals("appKey", preferences.getString("applicationKey", ""));
        Assert.assertEquals("cliKey", preferences.getString("clientKey", ""));
        Assert.assertEquals("https://mb.api.cloud.nifty.com/2013-09-01/", preferences.getString("apiBaseUrl", ""));


        // GCがstaticを解放した場合やプロセスが破棄された場合をモック(sCurrentContextがnullになる)
        Field modifiersField = null;
        try {
            modifiersField = NCMB.class.getDeclaredField("sCurrentContext");
            modifiersField.setAccessible(true);
            modifiersField.set(null, null);// モックの戻り値
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Assert.fail(e.getMessage());
        }

        // nullの状態の場合でもPreferencesを元にsCurrentContextが生成されるか確認
        Assert.assertEquals(NCMB.getCurrentContext().applicationKey, preferences.getString("applicationKey", ""));
        Assert.assertEquals(NCMB.getCurrentContext().clientKey, preferences.getString("clientKey", ""));
        Assert.assertEquals(NCMB.getCurrentContext().baseUrl, preferences.getString("apiBaseUrl", ""));
    }

    @Test
    public void enableResponseValidation() throws IOException {
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(), "applicationKey", "clientKey");

        SharedPreferences preferences = NCMB.getCurrentContext().context.getApplicationContext().getSharedPreferences("NCMB", Context.MODE_PRIVATE);
        Assert.assertEquals(false, preferences.getBoolean("responseValidation", false));

        NCMB.enableResponseValidation(true);
        Assert.assertEquals(true, preferences.getBoolean("responseValidation", false));
    }
}
