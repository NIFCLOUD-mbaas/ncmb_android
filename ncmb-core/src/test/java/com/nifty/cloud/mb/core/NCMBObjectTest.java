package com.nifty.cloud.mb.core;

//import junit.framework.Assert;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONException;
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
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * NCMBObjectTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBObjectTest {

    private MockWebServer mServer;
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
        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("SaveObjectTest");
        obj.put("key", "value");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("save Background is failed.");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

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

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

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
    public void save_object_with_remove() {
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
        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("updateTestObjectId");
        obj.put("updateKey", "updateValue");
        obj.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("update object error");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

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

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

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
            NCMBObject obj = new NCMBObject("TestClass", new JSONObject("{\"acl\":{\"*\":{\"read\":true, \"write\":true}}}"));
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
    public void fetch_object() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetch();
        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));

        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    @Test
    public void fetchObject_non_object_id() {

        NCMBObject obj = new NCMBObject("testClass");
        try {
            obj.fetch();
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
        }
    }

    @Test
    public void fetchObject_non_exist_object() {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("NonExistObject");
        try {
            obj.fetch();
        } catch (NCMBException e) {
            Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.getCode());
        }
    }

    @Test
    public void fetch_object_in_background() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("getTestObjectId");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {

            @Override
            public void done(NCMBObject object, NCMBException e) {
                object.getString("key");
                if (e != null) {
                    Assert.fail("get object raise exception:" + e.getMessage());
                } else {
                    Assert.assertEquals("7FrmPTBKSNtVjajm", object.getObjectId());
                    Assert.assertEquals("value", object.getString("key"));

                    SimpleDateFormat df = NCMBDateFormat.getIso8601();

                    try {
                        Assert.assertTrue(object.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                        Assert.assertTrue(object.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

        Assert.assertEquals("7FrmPTBKSNtVjajm", obj.getObjectId());
        Assert.assertEquals("value", obj.getString("key"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Assert.assertTrue(obj.getCreateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));
        Assert.assertTrue(obj.getUpdateDate().equals(df.parse("2014-06-03T11:28:30.348Z")));

        JSONAssert.assertEquals("{}", obj.getAcl().toJson().toString(), false);
    }

    @Test
    public void fetch_object_non_object_id() throws Exception {

        NCMBObject obj = new NCMBObject("TestClass");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {
            @Override
            public void done(NCMBObject object, NCMBException e) {
                if (e == null) {
                    Assert.fail("get object method should raise exception:");
                } else {
                    Assert.assertEquals(NCMBException.GENERIC_ERROR, e.getCode());
                }
            }
        });
    }

    @Test
    public void fetch_object_non_exist_object() throws Exception {
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("NonExistObject");
        obj.fetchInBackground(new FetchCallback<NCMBObject>() {
            @Override
            public void done(NCMBObject object, NCMBException e) {
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
    public void delete_object_without_object_id() {

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
        Assert.assertFalse(callbackFlag);
        NCMBObject obj = new NCMBObject("TestClass");
        obj.setObjectId("deleteTestObjectId");
        obj.deleteObjectInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e != null) {
                    Assert.fail("delete object method should not raise exception:");
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void delete_object_in_background_without_object_id() {

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
    public void delete_object_in_background_non_exist_object_id() {
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
    public void add_operation() throws Exception {
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.addToList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(new JSONObject("{\"__op\":\"Add\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }

    @Test
    public void add_unique_operation() throws Exception {
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.addUniqueToList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                new JSONObject("{\"__op\":\"AddUnique\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }

    @Test
    public void remove_operation() throws Exception {
        NCMBObject obj = new NCMBObject("testClass", new JSONObject("{\"list\":[\"value1\",\"value2\"]}"));
        obj.setObjectId("testObjectId");
        obj.removeFromList("list", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(new JSONObject("{\"__op\":\"Remove\",\"objects\":[\"value1\",\"value2\"]}"),
                obj.mFields.getJSONObject("list"),
                true);
    }


    @Test
    public void add_relation_to_object() throws Exception {
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
    public void remove_relation_operation() throws Exception {
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
