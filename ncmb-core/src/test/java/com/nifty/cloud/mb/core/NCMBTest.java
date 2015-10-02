package com.nifty.cloud.mb.core;

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
        Assert.assertEquals("appKey",NCMB.sCurrentContext.applicationKey);
        Assert.assertEquals("cliKey", NCMB.sCurrentContext.clientKey);
        Assert.assertEquals("https://mb.api.cloud.nifty.com/2013-09-01/", NCMB.sCurrentContext.baseUrl);
    }

    @Test
    public void initialize_with_apiKey_and_url() {
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                        "appKey",
                        "cliKey",
                        mServer.getUrl("/").toString(),
                        null);
        Assert.assertEquals("appKey",NCMB.sCurrentContext.applicationKey);
        Assert.assertEquals("cliKey", NCMB.sCurrentContext.clientKey);
        Assert.assertEquals(mServer.getUrl("/" + NCMB.DEFAULT_API_VERSION + "/").toString(), NCMB.sCurrentContext.baseUrl);
    }

    @Test
    public void initialize_with_apiKey_and_metaData() {
        PowerMockito.spy(NCMB.class);
        when(NCMB.getMetadata(RuntimeEnvironment.application.getApplicationContext() ,NCMB.METADATA_PREFIX + "DOMAIN_URL")).thenReturn("http://sample.com/");
        when(NCMB.getMetadata(RuntimeEnvironment.application.getApplicationContext() ,NCMB.METADATA_PREFIX + "API_VERSION")).thenReturn("2015-07-23");
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey");
        PowerMockito.verifyStatic();
        Assert.assertEquals("appKey", NCMB.sCurrentContext.applicationKey);
        Assert.assertEquals("cliKey", NCMB.sCurrentContext.clientKey);
        Assert.assertEquals("http://sample.com/2015-07-23/", NCMB.sCurrentContext.baseUrl);
    }
}
