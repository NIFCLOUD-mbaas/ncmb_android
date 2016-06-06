package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayList;
import java.util.List;

/**
 * NCMBObjectServiceTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBObjectServiceTest {

    private MockWebServer mServer;
    private JSONObject response;
    private ArrayList<NCMBObject> searchResult;
    private boolean callbackFlag;

    @Before
    public void setup() throws Exception {

        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();

        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),
                "appKey",
                "cliKey",
                mServer.getUrl("/").toString(),
                null);

        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();

        callbackFlag = false;
    }

    @After
    public void teardown() {
        response = null;
    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveObjectToNcmb_valid_class() throws Exception {
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject response = objServ.saveObject("TestClass", new JSONObject("{\"key\":\"value\"}"));

        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
    }

    @Test
    public void saveObjectToNcmbInBackground_valid_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.saveObjectInBackground(
                "TestClass",
                new JSONObject("{\"key\":\"value\"}"),
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                        callbackFlag = true;
                    }
                }
        );


        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
    }

    @Test
    public void fetchObject_valid_class() throws Exception {
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        NCMBObject obj = objServ.fetchObject(
                "TestClass",
                "getTestObjectId"
        );

        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", obj.getString("createDate"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", obj.getString("updateDate"));
        Assert.assertEquals("{}", obj.getString("acl"));
    }

    @Test
    public void fetchObjectInBackground_valid_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.fetchObjectInBackground(
                "TestClass",
                "getTestObjectId",
                new FetchCallback<NCMBObject>() {
                    @Override
                    public void done(NCMBObject object, NCMBException e) {
                        Assert.assertEquals("7FrmPTBKSNtVjajm", object.getString("objectId"));
                        Assert.assertEquals("2014-06-03T11:28:30.348Z", object.getString("createDate"));
                        Assert.assertEquals("2014-06-03T11:28:30.348Z", object.getString("updateDate"));
                        Assert.assertEquals("{}", object.getString("acl"));

                        callbackFlag = true;
                    }
                }
        );

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void updateObject_valid_class() throws Exception {
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        response = objServ.updateObject(
                "TestClass",
                "updateTestObjectId",
                new JSONObject("{\"updateKey\":\"updateValue\"}")
        );

        Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
    }

    @Test
    public void updateObjectInBackground_valid_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.updateObjectInBackground(
                "TestClass",
                "updateTestObjectId",
                new JSONObject("{\"updateKey\":\"updateValue\"}"),
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                        callbackFlag = true;
                    }
                }
        );

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
        Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
    }

    @Test
    public void deleteObject_valid_class() throws Exception {
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        response = objServ.deleteObject(
                "TestClass",
                "deleteTestObjectId"
        );

        Assert.assertNull(response);
    }

    @Test
    public void deleteObjectInBackground_valid_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.deleteObjectInBackground(
                "TestClass",
                "deleteTestObjectId",
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                        callbackFlag = true;
                    }
                }
        );

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
        Assert.assertNull(response);
    }

    @Test
    public void searchObject_valid_class() throws Exception {
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject conditions = new JSONObject("{\"where\":{\"key\":\"value\"},\"limit\":1}");
        ArrayList<NCMBObject> result = (ArrayList<NCMBObject>) objServ.searchObject("TestClass", conditions);
        Assert.assertEquals("8FgKqFlH8dZRDrBJ", result.get(0).getObjectId());

    }

    @Test
    public void searchObjectInBackground_valid_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject conditions = new JSONObject("{\"where\":{\"key\":\"value\"},\"limit\":1}");
        objServ.searchObjectInBackground("TestClass", conditions, new SearchObjectCallback() {
            @Override
            public void done(List result, NCMBException e) {
                searchResult = (ArrayList<NCMBObject>) result;
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
        Assert.assertEquals("8FgKqFlH8dZRDrBJ", searchResult.get(0).getObjectId());
    }

}
