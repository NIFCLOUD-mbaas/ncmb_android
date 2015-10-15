package com.nifty.cloud.mb.core;

//import junit.framework.Assert;

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
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * NCMBObjectTest
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "app/src/main/AndroidManifest.xml", emulateSdk = 18)
public class NCMBObjectTest {

    private MockWebServer mServer;

    @Before
    public void setup() throws Exception {
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

    }

    /**
     * TestCase
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void save_object_valid_class() throws Exception {

        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");

        NCMBException error = null;
        try {
            obj.save();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNull(error);
        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertEquals(0, obj.mUpdateKeys.size());
    }

    @Test
    public void save_object_asynchronously_valid_class() throws Exception {

        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
            }
        });

        Robolectric.runBackgroundTasks();

        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertEquals(0, obj.mUpdateKeys.size());
    }

    @Test
    public void save_object_asynchronously_none_callback() throws NCMBException {

        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(null);

        Robolectric.runBackgroundTasks();

        Assert.assertNotNull(obj);
        Assert.assertEquals("7FrmPTBKSNtVjajm9", obj.getObjectId());
    }

    @Test
    public void save_object_default_class() throws NCMBException {

        NCMBObject obj = new NCMBObject("user");
        obj.put("key", "value");
        NCMBException error = null;
        try {
            obj.save();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertEquals(NCMBException.OPERATION_FORBIDDEN, error.getCode());
    }

    @Test
    public void save_object_asynchronously_default_class() throws NCMBException {
        NCMBObject obj = new NCMBObject("user");
        obj.put("key", "value");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("this operation should be raise exception");
                } else {
                    Assert.assertEquals(NCMBException.OPERATION_FORBIDDEN, e.getCode());
                }
            }
        });
    }

    @Test
    public void save_object_with_acl() {
        try {
            NCMBObject obj = new NCMBObject("TestClass");
            obj.put("key", "value");
            NCMBAcl acl = new NCMBAcl(new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"));
            obj.setAcl(acl);
            obj.save();

            Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            Assert.assertEquals(df.parse("2015-09-14T11:28:30.348Z"), obj.getCreateDate());
            JSONAssert.assertEquals(obj.getAcl().toJson(), acl.toJson(), false);
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void save_object_with_acl_parameter() {
        try {
            NCMBObject obj = new NCMBObject("TestClass", new JSONObject("{\"acl\":{\"*\":{\"read\":true, \"write\":true}}}"));
            obj.put("key", "value");
            obj.save();

            Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            Assert.assertEquals(df.parse("2015-09-14T11:28:30.348Z"), obj.getCreateDate());
            JSONAssert.assertEquals(obj.getAcl().toJson(), new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"), false);
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void save_object_with_remove () {
        try {
            NCMBObject obj = new NCMBObject("TestClass", new JSONObject("{\"key\":\"value\"}"));
            obj.setObjectId("testObjectId");
            obj.remove("key");
            obj.save();

            Assert.assertFalse(obj.containsKey("key"));
            SimpleDateFormat df = NCMBDateFormat.getIso8601();

            Assert.assertEquals(df.parse("2015-09-16T11:28:30.348Z"), obj.getUpdateDate());
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void update_object_with_update_value() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.put("updateKey", "updateValue");
        obj.save();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }

    @Test
    public void update_object_non_update_value() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.save();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }


    @Test
    public void update_object_in_background_with_update_value() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.put("updateKey", "updateValue");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("update object error");
                }
            }
        });

        Robolectric.runBackgroundTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }

    @Test
    public void update_object_in_background_non_update_value() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("update object error");
                }
            }
        });

        Robolectric.runBackgroundTasks();

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-04T11:28:30.348Z")));
    }

    @Test
    public void update_object_with_acl() {
        try {
            NCMBObject obj = new NCMBObject("TestClass");
            obj.setObjectId("updateWithAclTestObjectId");
            obj.put("updateKey", "updateValue");
            NCMBAcl acl = new NCMBAcl(new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"));
            obj.setAcl(acl);
            obj.save();

            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            Assert.assertEquals(df.parse("2015-09-14T11:28:30.348Z"), obj.getUpdateDate());
            JSONAssert.assertEquals(obj.getAcl().toJson(), acl.toJson(), false);
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void update_object_with_acl_parameter_dont_update_acl() {
        try {
            NCMBObject obj = new NCMBObject("TestClass",new JSONObject("{\"acl\":{\"*\":{\"read\":true, \"write\":true}}}"));
            obj.setObjectId("updateTestObjectId");
            obj.put("updateKey", "updateValue");
            obj.save();

            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), obj.getUpdateDate());
            JSONAssert.assertEquals(obj.getAcl().toJson(), new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"), false);
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void update_object_only_update_key() {
        try {
            NCMBObject obj = new NCMBObject("TestClass");
            obj.setObjectId("updateTestObjectId");
            obj.put("updateKey", "updateValue");
            NCMBAcl acl = new NCMBAcl(new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"));
            obj.setAclFromInternal(acl);
            obj.save();

            SimpleDateFormat df = NCMBDateFormat.getIso8601();
            Assert.assertEquals(df.parse("2014-06-04T11:28:30.348Z"), obj.getUpdateDate());
            JSONAssert.assertEquals(obj.getAcl().toJson(), acl.toJson(), false);
        } catch (NCMBException | JSONException | ParseException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void saveAll_push() throws Exception {
        //first role
        NCMBPush firstPush = new NCMBPush();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sf.parse("2020-01-01 00:00:00");
        firstPush.setDeliveryTime(date);
        firstPush.setTitle("firstTitle");
        firstPush.setMessage("firstMessage");

        //second role
        NCMBPush secondPush = new NCMBPush();
        secondPush.setObjectId("dummyObjectId");
        secondPush.setDeliveryTime(date);
        secondPush.setTitle("secondTitle");
        secondPush.setMessage("secondMessage");

        //connect
        List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstPush);
        objects.add(secondPush);
        JSONArray response = NCMBObject.saveAll(objects);

        //first role check
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

        //second role check
        Assert.assertEquals("dummyObjectId", objects.get(1).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.555Z"), objects.get(1).getUpdateDate());

        //response check
        Assert.assertTrue(response.getJSONObject(0).has("success"));
        Assert.assertTrue(response.getJSONObject(1).has("success"));
    }

    @Test
    public void saveAll_installation() throws Exception {
        //first installation
        NCMBInstallation firstInstallation = new NCMBInstallation();
        firstInstallation.setDeviceToken("firstInstallation");
        firstInstallation.setDeviceType("android");
        firstInstallation.put("name", "tomato");
        firstInstallation.put("type", "vegetable");

        //second installation
        NCMBInstallation secondInstallation = new NCMBInstallation();
        secondInstallation.setObjectId("dummyObjectId");
        secondInstallation.setDeviceToken("secondInstallation");
        secondInstallation.setDeviceType("ios");
        secondInstallation.put("name", "apple");
        secondInstallation.put("type", "fruit");

        //connect
        List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstInstallation);
        objects.add(secondInstallation);
        JSONArray response = NCMBObject.saveAll(objects);

        //first installation check;
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

        //second installation check
        Assert.assertEquals("dummyObjectId", objects.get(1).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.555Z"), objects.get(1).getUpdateDate());

        //response check
        Assert.assertTrue(response.getJSONObject(0).has("success"));
        Assert.assertTrue(response.getJSONObject(1).has("success"));
    }

    @Test
    public void saveAll_role() throws Exception {
        //first role
        NCMBRole firstRole = new NCMBRole("firstRole");
        firstRole.put("name", "tomato");
        firstRole.put("type", "vegetable");

        //second role
        NCMBRole secondRole = new NCMBRole("secondRole");
        secondRole.setObjectId("dummyObjectId");
        secondRole.put("name", "apple");
        secondRole.put("type", "fruit");

        //connect
        List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstRole);
        objects.add(secondRole);
        JSONArray response = NCMBObject.saveAll(objects);

        //first push check
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

        //second push check
        Assert.assertEquals("dummyObjectId", objects.get(1).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.555Z"), objects.get(1).getUpdateDate());

        //response check
        Assert.assertTrue(response.getJSONObject(0).has("success"));
        Assert.assertTrue(response.getJSONObject(1).has("success"));
    }

    @Test
    public void saveAll_object() throws Exception {
        //first object POST
        NCMBObject firstObject = new NCMBObject("food");
        firstObject.put("name", "tomato");
        firstObject.put("type", "vegetable");

        //second object PUT
        NCMBObject secondObject = new NCMBObject("food");
        secondObject.setObjectId("dummyObjectId");
        secondObject.put("name", "apple");
        secondObject.put("type", "fruit");

        //connect
        List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstObject);
        objects.add(secondObject);
        JSONArray response = NCMBObject.saveAll(objects);

        //first object check
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

        //second object check
        Assert.assertEquals("dummyObjectId", objects.get(1).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.555Z"), objects.get(1).getUpdateDate());

        //response check
        Assert.assertTrue(response.getJSONObject(0).has("success"));
        Assert.assertTrue(response.getJSONObject(1).has("success"));
    }

    @Test
    public void saveAll_object_error() throws Exception {
        //first object POST
        NCMBObject firstObject = new NCMBObject("food");
        firstObject.put("name", "tomato");
        firstObject.put("type", "vegetable");

        //second object PUT
        NCMBObject secondObject = new NCMBObject("food");
        secondObject.setObjectId("errorObjectId");
        secondObject.put("name", "apple");
        secondObject.put("type", "fruit");

        //connect check
        List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstObject);
        objects.add(secondObject);
        JSONArray response = NCMBObject.saveAll(objects);

        //first object check
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
        Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

        //response check
        Assert.assertTrue(response.getJSONObject(0).has("success"));
        Assert.assertTrue(response.getJSONObject(1).has("error"));
        Assert.assertEquals("E404001", response.getJSONObject(1).getJSONObject("error").getString("code"));
        Assert.assertEquals("No data available.", response.getJSONObject(1).getJSONObject("error").getString("error"));
    }

    @Test
    public void saveAllInBackground_object() throws Exception {
        //first object POST
        NCMBObject firstObject = new NCMBObject("food");
        firstObject.put("name", "tomato");
        firstObject.put("type", "vegetable");

        //second object PUT
        NCMBObject secondObject = new NCMBObject("food");
        secondObject.setObjectId("dummyObjectId");
        secondObject.put("name", "apple");
        secondObject.put("type", "fruit");

        //connect
        final List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstObject);
        objects.add(secondObject);
        NCMBObject.saveAllInBackground(objects, new BatchCallback() {
            @Override
            public void done(JSONArray responseArray, NCMBException e) {
                try {
                    //first object check
                    SimpleDateFormat df = NCMBDateFormat.getIso8601();
                    Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
                    Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
                    Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

                    //second object check
                    Assert.assertEquals("dummyObjectId", objects.get(1).getObjectId());
                    Assert.assertEquals(df.parse("2015-09-25T04:14:25.555Z"), objects.get(1).getUpdateDate());

                    //response check
                    Assert.assertTrue(responseArray.getJSONObject(0).has("success"));
                    Assert.assertTrue(responseArray.getJSONObject(1).has("success"));
                } catch (JSONException | ParseException error) {
                    Assert.fail(error.getMessage());
                }
            }
        });
    }

    @Test
    public void saveAllInBackground_object_error() throws Exception {
        //first object POST
        NCMBObject firstObject = new NCMBObject("food");
        firstObject.put("name", "tomato");
        firstObject.put("type", "vegetable");

        //second object PUT
        NCMBObject secondObject = new NCMBObject("food");
        secondObject.setObjectId("errorObjectId");
        secondObject.put("name", "apple");
        secondObject.put("type", "fruit");

        //connect
        final List<NCMBBase> objects = new ArrayList<>();
        objects.add(firstObject);
        objects.add(secondObject);
        NCMBObject.saveAllInBackground(objects, new BatchCallback() {
            @Override
            public void done(JSONArray responseArray, NCMBException e) {
                try {
                    //first object check
                    SimpleDateFormat df = NCMBDateFormat.getIso8601();
                    Assert.assertEquals("7FrmPTBKSNtVjajm", objects.get(0).getObjectId());
                    Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getCreateDate());
                    Assert.assertEquals(df.parse("2015-09-25T04:14:25.333Z"), objects.get(0).getUpdateDate());

                    //response check
                    Assert.assertTrue(responseArray.getJSONObject(0).has("success"));
                    Assert.assertTrue(responseArray.getJSONObject(1).has("error"));
                    Assert.assertEquals("E404001", responseArray.getJSONObject(1).getJSONObject("error").getString("code"));
                    Assert.assertEquals("No data available.", responseArray.getJSONObject(1).getJSONObject("error").getString("error"));
                } catch (JSONException | ParseException error) {
                    Assert.fail(error.getMessage());
                }
            }
        });
    }

    @Test
    public void fetch_object() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetchObject();
        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));

        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    @Test
    public void fetchObject_non_object_id () {

        NCMBObject obj = new NCMBObject("testClass");
        try {
            obj.fetchObject();
        } catch (NCMBException e){
            Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
        }
    }

    @Test
    public void fetchObject_non_exist_object () {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("NonExistObject");
        try {
            obj.fetchObject();
        } catch (NCMBException e){
            Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
        }
    }

    @Test
    public void fetch_object_in_background() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetchObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("get object raise exception:" + e.getMessage());
                }
            }
        });

        Robolectric.runBackgroundTasks();

        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));

        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    @Test
    public void fetch_object_non_object_id () throws Exception {

        NCMBObject obj = new NCMBObject("TestClass");
        obj.fetchObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("get object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
                }
            }
        });
    }

    @Test
    public void fetch_object_non_exist_object () throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("NonExistObject");
        obj.fetchObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("get object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
                }
            }
        });
    }

    @Test
    public void delete_object() {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("deleteTestObjectId");
        try {
            obj.deleteObject();
        } catch (NCMBException e) {
            Assert.fail("exception raised:" + e.getMessage());
        }
    }

    @Test
    public void delete_object_without_object_id () {

        NCMBObject obj = new NCMBObject("TestClass");
        try {
            obj.deleteObject();
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
        }
    }

    @Test
    public void delete_object_non_exist_object() {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("nonExistId");
        try {
            obj.deleteObject();
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
        }
    }

    @Test
    public void delete_object_in_background() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("deleteTestObjectId");
        obj.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("delete object method should not raise exception:");
                }
            }
        });
    }

    @Test
    public void delete_object_in_background_without_object_id () {

        NCMBObject obj = new NCMBObject("TestClass");
        obj.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("delete object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
                }
            }
        });
    }

    @Test
    public void delete_object_in_background_non_exist_object_id () {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("nonExistId");
        obj.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    Assert.fail("delete object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
                }
            }
        });
    }


    @Test
    public void increment_operation() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass", new JSONObject("{\"incrementKey\":0}"));
        obj.setObjectId("testObjectId");
        obj.increment("incrementKey", 1);

        JSONAssert.assertEquals(new JSONObject("{\"__op\":\"Increment\",\"amount\":1}"),
                obj.mFields.getJSONObject("incrementKey"),
                true);

        obj.save();
        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
    }

    @Test
    public void add_operation() throws Exception{
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.addToList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(new JSONObject("{\"__op\":\"Add\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }

    @Test
    public void add_unique_operation() throws Exception{
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.addUniqueToList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                new JSONObject("{\"__op\":\"AddUnique\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }

    @Test
    public void remove_operation() throws Exception{
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.removeFromList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(new JSONObject("{\"__op\":\"Remove\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }


    @Test
    public void add_relation_to_object () throws Exception {
        NCMBObject pointerObj = new NCMBObject("pointerClass");
        pointerObj.setObjectId("testObjectId");

        NCMBObject obj = new NCMBObject("dataTypeClass");
        obj.put("relation", NCMBRelation.addRelation(Arrays.asList(pointerObj)));

        JSONAssert.assertEquals(
                "{\"__op\":\"AddRelation\",\"objects\":[{\"__type\":\"Pointer\",\"className\":\"pointerClass\",\"objectId\":\"testObjectId\"}]}",
                obj.mFields.getJSONObject("relation"),
                true
        );
    }

    @Test
    public void remove_relation_operation() throws Exception{
        NCMBObject pointerObj = new NCMBObject("pointerClass");
        pointerObj.setObjectId("testObjectId");

        NCMBObject obj = new NCMBObject("dataTypeClass");

        obj.put("relation", NCMBRelation.removeRelation(Arrays.asList(pointerObj)));


        JSONAssert.assertEquals(
                "{\"__op\":\"RemoveRelation\",\"objects\":[{\"__type\":\"Pointer\",\"className\":\"pointerClass\",\"objectId\":\"testObjectId\"}]}",
                obj.mFields.getJSONObject("relation"),
                true
        );
    }


}
