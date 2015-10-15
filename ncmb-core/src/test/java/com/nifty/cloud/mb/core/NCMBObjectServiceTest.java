package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * NCMBObjectServiceTest
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "app/src/main/AndroidManifest.xml", emulateSdk = 18)
public class NCMBObjectServiceTest {

    private MockWebServer mServer;
    private JSONObject response;
    private ArrayList<NCMBObject> searchResult;

    @Before
    public void setup() throws Exception{
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);

        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();

        NCMB.initialize(Robolectric.application.getApplicationContext(),
                "appKey",
                "cliKey",
                mServer.getUrl("/").toString(),
                null);
    }

    @After
    public void teardown() {
        response = null;
    }



    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveObjectToNcmb_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject response = objServ.saveObject("TestClass", new JSONObject("{\"key\":\"value\"}"));

        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
    }

    @Test
    public void saveObjectToNcmbInBackground_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.saveObjectInBackground(
                "TestClass",
                new JSONObject("{\"key\":\"value\"}"),
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                    }
                }
        );

        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
    }

    @Test
    public void getObject_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        response = objServ.fetchObject(
                "TestClass",
                "getTestObjectId"
        );

        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("updateDate"));
        Assert.assertEquals("{}", response.getString("acl"));
    }

    @Test
    public void getObjectInBackground_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.fetchObjectInBackground(
                "TestClass",
                "getTestObjectId",
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                    }
                }
        );

        Assert.assertEquals("7FrmPTBKSNtVjajm", response.getString("objectId"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("createDate"));
        Assert.assertEquals("2014-06-03T11:28:30.348Z", response.getString("updateDate"));
        Assert.assertEquals("{}", response.getString("acl"));
    }

    @Test
    public void updateObject_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        response = objServ.updateObject(
                "TestClass",
                "updateTestObjectId",
                new JSONObject("{\"updateKey\":\"updateValue\"}")
        );

        Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
    }

    @Test
    public void updateObjectInBackground_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.updateObjectInBackground(
                "TestClass",
                "updateTestObjectId",
                new JSONObject("{\"updateKey\":\"updateValue\"}"),
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                    }
                }
        );

        Assert.assertEquals("2014-06-04T11:28:30.348Z", response.getString("updateDate"));
    }

    @Test
    public void deleteObject_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        response = objServ.deleteObject(
                "TestClass",
                "deleteTestObjectId"
        );

        Assert.assertNull(response);
    }

    @Test
    public void deleteObjectInBackground_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.deleteObjectInBackground(
                "TestClass",
                "deleteTestObjectId",
                new ExecuteServiceCallback() {
                    @Override
                    public void done(JSONObject json, NCMBException e) {
                        response = json;
                    }
                }
        );

        Assert.assertNull(response);
    }

    @Test
    public void searchObject_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject conditions = new JSONObject("{\"where\":{\"key\":\"value\"},\"limit\":1}");
        ArrayList<NCMBObject> result = (ArrayList<NCMBObject>)objServ.searchObject("TestClass", conditions);
        Assert.assertEquals("8FgKqFlH8dZRDrBJ", result.get(0).getObjectId());
        
    }

    @Test
    public void searchObjectInBackground_valid_class () throws Exception {
        NCMBObjectService objServ = (NCMBObjectService)NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONObject conditions = new JSONObject("{\"where\":{\"key\":\"value\"},\"limit\":1}");
        objServ.searchObjectInBackground("TestClass", conditions, new SearchObjectCallback() {
            @Override
            public void done(List result, NCMBException e) {
                searchResult = (ArrayList<NCMBObject>)result;
            }
        });

        Assert.assertEquals("8FgKqFlH8dZRDrBJ", searchResult.get(0).getObjectId());
    }

    @Test
    public void saveAllObject_valid_class() throws Exception {

        //first
        JSONObject firstObject = new JSONObject();
        firstObject.put("method", "PUT");
        firstObject.put("path", "2013-09-01/classes/food/firstDummyObjectId");
        firstObject.put("body", new JSONObject("{name:tomato,type:vegetable}"));

        //second
        JSONObject secondObject = new JSONObject();
        secondObject.put("method", "PUT");
        secondObject.put("path", "2013-09-01/classes/food/secondDummyObjectId");
        secondObject.put("body", new JSONObject("{name:apple,type:fruit}"));

        //third
        JSONObject thirdObject = new JSONObject();
        thirdObject.put("method", "DELETE");
        thirdObject.put("path", "2013-09-01/classes/food/thirdDummyObjectId");
        thirdObject.put("body", new JSONObject());

        //connect
        JSONArray objects = new JSONArray();
        objects.put(firstObject);
        objects.put(secondObject);
        objects.put(thirdObject);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONArray responseArray = objServ.saveAllObject(objects);

        //check
        Assert.assertTrue(responseArray.getJSONObject(0).has("success"));
        Assert.assertEquals("2015-09-25T04:14:25.333Z", responseArray.getJSONObject(0).getJSONObject("success").getString("updateDate"));
        Assert.assertTrue(responseArray.getJSONObject(1).has("success"));
        Assert.assertEquals("2015-09-25T04:14:25.555Z", responseArray.getJSONObject(1).getJSONObject("success").getString("updateDate"));
        Assert.assertTrue(responseArray.getJSONObject(2).has("success"));
        Assert.assertTrue(responseArray.getJSONObject(2).isNull("success"));
    }

    @Test
    public void saveAllObjectInBackground_valid_class() throws Exception {

        //first
        JSONObject firstObject = new JSONObject();
        firstObject.put("method", "PUT");
        firstObject.put("path", "2013-09-01/classes/food/firstDummyObjectId");
        firstObject.put("body", new JSONObject("{name:tomato,type:vegetable}"));

        //second
        JSONObject secondObject = new JSONObject();
        secondObject.put("method", "PUT");
        secondObject.put("path", "2013-09-01/classes/food/secondDummyObjectId");
        secondObject.put("body", new JSONObject("{name:apple,type:fruit}"));

        //third
        JSONObject thirdObject = new JSONObject();
        thirdObject.put("method", "DELETE");
        thirdObject.put("path", "2013-09-01/classes/food/thirdDummyObjectId");
        thirdObject.put("body", new JSONObject());

        //connect
        JSONArray objects = new JSONArray();
        objects.put(firstObject);
        objects.put(secondObject);
        objects.put(thirdObject);
        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        objServ.saveAllObjectInBackground(objects, new BatchCallback() {
            @Override
            public void done(JSONArray objects, NCMBException e) {
                //check
                try {
                    Assert.assertTrue(objects.getJSONObject(0).has("success"));
                    Assert.assertEquals("2015-09-25T04:14:25.333Z", objects.getJSONObject(0).getJSONObject("success").getString("updateDate"));
                    Assert.assertTrue(objects.getJSONObject(1).has("success"));
                    Assert.assertEquals("2015-09-25T04:14:25.555Z", objects.getJSONObject(1).getJSONObject("success").getString("updateDate"));
                    Assert.assertTrue(objects.getJSONObject(2).has("success"));
                    Assert.assertTrue(objects.getJSONObject(2).isNull("success"));
                } catch (JSONException error) {
                    Assert.fail(error.getMessage());
                }
            }
        });
    }

    @Test
    public void saveAllObject_error() throws Exception {

        //first
        JSONObject firstObject = new JSONObject();
        firstObject.put("method", "PUT");
        firstObject.put("path", "2013-09-01/classes/food/firstDummyObjectId");
        firstObject.put("body", new JSONObject("{name:tomato,type:vegetable}"));
        //second
        JSONObject secondObject = new JSONObject();
        secondObject.put("method", "PUT");
        secondObject.put("path", "2013-09-01/classes/food/secondDummyObjectId");
        secondObject.put("body", new JSONObject("{name:apple,type:fruit}"));
        //third
        JSONObject thirdObject = new JSONObject();
        thirdObject.put("method", "DELETE");
        thirdObject.put("path", "2013-09-01/classes/food/thirdDummyObjectId_error");
        thirdObject.put("body", new JSONObject());

        JSONArray objects = new JSONArray();
        objects.put(firstObject);
        objects.put(secondObject);
        objects.put(thirdObject);

        NCMBObjectService objServ = (NCMBObjectService) NCMB.factory(NCMB.ServiceType.OBJECT);
        JSONArray responseArray = objServ.saveAllObject(objects);
        Assert.assertTrue(responseArray.getJSONObject(0).has("success"));
        Assert.assertEquals("2015-09-25T04:14:25.333Z", responseArray.getJSONObject(0).getJSONObject("success").getString("updateDate"));
        Assert.assertTrue(responseArray.getJSONObject(1).has("success"));
        Assert.assertEquals("2015-09-25T04:14:25.555Z", responseArray.getJSONObject(1).getJSONObject("success").getString("updateDate"));
        Assert.assertTrue(responseArray.getJSONObject(2).has("error"));
        Assert.assertEquals("E404001", responseArray.getJSONObject(2).getJSONObject("error").getString("code"));
        Assert.assertEquals("No data available.", responseArray.getJSONObject(2).getJSONObject("error").getString("error"));
    }
}
