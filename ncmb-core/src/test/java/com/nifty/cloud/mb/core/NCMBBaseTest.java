package com.nifty.cloud.mb.core;

import android.location.Location;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * NCMBBaseTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBBaseTest {

    private MockWebServer mServer;

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
    public void put_string_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("string", "value");

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("value", ((JSONObject) f.get(obj)).getString("string"));
    }

    @Test
    public void put_boolean_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("boolean", true);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertTrue(((JSONObject) f.get(obj)).getBoolean("boolean"));
    }

    @Test
    public void put_int_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("int", 303);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals(303, ((JSONObject) f.get(obj)).getInt("int"));
    }

    @Test
    public void put_long_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("long", 9223372036854775807L);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals(9223372036854775807L, ((JSONObject) f.get(obj)).getLong("long"));
    }

    @Test
    public void put_float_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        float val = 2013.0F;
        obj.put("float", val);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals((double) val, ((JSONObject) f.get(obj)).getDouble("float"));
    }

    @Test
    public void put_double_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("double", 2013.0901);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals(2013.0901, ((JSONObject) f.get(obj)).getDouble("double"));
    }

    @Test
    public void put_date_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");

        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        Date assertDate = df.parse("2015-07-29T10:10:10.000Z");
        obj.put("date", assertDate);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Date dateVaalue = df.parse(((JSONObject) f.get(obj)).getJSONObject("date").getString("iso"));
        Assert.assertTrue(assertDate.equals(dateVaalue));
    }

    @Test
    public void put_geolocation_value() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");

        Location geo = new Location("test");
        geo.setLatitude(10.01);
        geo.setLongitude(10.01);
        obj.put("geo", geo);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals(geo.getLatitude(), ((JSONObject) f.get(obj)).getJSONObject("geo").getDouble("latitude"));
        Assert.assertEquals(geo.getLongitude(), ((JSONObject) f.get(obj)).getJSONObject("geo").getDouble("longitude"));
    }

    @Test
    public void put_json_object() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");

        JSONObject object = new JSONObject("{'objKey':'objValue'}");
        obj.put("object", object);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        JSONAssert.assertEquals(object, ((JSONObject) f.get(obj)).getJSONObject("object"), false);
    }

    @Test
    public void put_json_array() throws Exception {
        NCMBBase obj = new NCMBBase("dataTypeClass");

        JSONArray array = new JSONArray("['value1','value2']");
        obj.put("array", array);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        JSONAssert.assertEquals(array, ((JSONObject) f.get(obj)).getJSONArray("array"), false);
    }

    @Test
    public void set_object_id() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass");
        baseObj.setObjectId("testObjectId");

        Class c = baseObj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("testObjectId", ((JSONObject) f.get(baseObj)).getString("objectId"));
    }

    @Test
    public void get_object_id() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"objectId\":\"testObjectId\"}"));

        Assert.assertEquals("testObjectId", baseObj.getObjectId());
    }

    @Test
    public void set_create_date() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass");
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        baseObj.setCreateDate(df.parse("2015-07-29T10:10:10.000Z"));

        Class c = baseObj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("2015-07-29T10:10:10.000Z", ((JSONObject) f.get(baseObj)).getString("createDate"));
    }

    @Test
    public void get_create_date() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"createDate\":\"2015-07-29T10:10:10.000Z\"}"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        Assert.assertEquals(df.parse("2015-07-29T10:10:10.000Z"), baseObj.getCreateDate());
    }

    @Test
    public void set_update_date() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass");
        SimpleDateFormat df = NCMBDateFormat.getIso8601();
        baseObj.setUpdateDate(df.parse("2015-07-29T10:10:10.000Z"));

        Class c = baseObj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("2015-07-29T10:10:10.000Z", ((JSONObject) f.get(baseObj)).getString("updateDate"));
    }

    @Test
    public void get_update_date() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"updateDate\":\"2015-07-29T10:10:10.000Z\"}"));

        SimpleDateFormat df = NCMBDateFormat.getIso8601();

        Assert.assertEquals(df.parse("2015-07-29T10:10:10.000Z"), baseObj.getUpdateDate());
    }

    @Test
    public void set_acl() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass");
        NCMBAcl acl = new NCMBAcl(new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"));
        baseObj.setAcl(acl);

        Class c = baseObj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        JSONAssert.assertEquals(acl.toJson(), ((JSONObject) f.get(baseObj)).getJSONObject("acl"), false);
    }

    @Test
    public void get_acl() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"acl\":{\"*\":{\"read\":true, \"write\":true}}}"));

        JSONAssert.assertEquals(
                new JSONObject("{\"*\":{\"read\":true, \"write\":true}}"),
                baseObj.getJSONObject("acl"),
                false
        );

    }


    @Test
    public void put_list_object() throws Exception {
        List listData = new ArrayList();
        listData.add("value1");
        listData.add("value2");

        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("list", listData);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("value1", ((JSONObject) f.get(obj)).getJSONArray("list").get(0));
        Assert.assertEquals("value2", ((JSONObject) f.get(obj)).getJSONArray("list").get(1));
    }

    @Test
    public void put_map_object() throws Exception {
        Map mapData = new HashMap();
        mapData.put("key1", "value1");
        mapData.put("key2", "value2");

        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("map", mapData);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        Assert.assertEquals("value1", ((JSONObject) f.get(obj)).getJSONObject("map").getString("key1"));
        Assert.assertEquals("value2", ((JSONObject) f.get(obj)).getJSONObject("map").getString("key2"));
    }

    @Test
    public void put_pointer_object() throws Exception {

        NCMBObject pointerObj = new NCMBObject("pointerClass");
        pointerObj.setObjectId("testObjectId");

        NCMBBase obj = new NCMBBase("dataTypeClass");
        obj.put("pointer", pointerObj);

        Class c = obj.getClass();
        Field f = c.getDeclaredField("mFields");
        f.setAccessible(true);

        JSONAssert.assertEquals(
                "{\"__type\":\"Pointer\",\"className\":\"pointerClass\",\"objectId\":\"testObjectId\"}",
                ((JSONObject) f.get(obj)).getJSONObject("pointer"),
                false
        );
        //Assert.assertEquals("value1", ((NCMBFields) f.get(obj)).getJSONObject("map").getString("key1"));
    }

    @Test
    public void get_string_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'string':'value'}"));
        Assert.assertEquals("value", obj.getString("string"));
    }

    @Test
    public void get_string_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getString("nonExistKey"));
    }

    @Test
    public void get_boolean_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'boolean':true}"));
        Assert.assertEquals(true, obj.getBoolean("boolean"));
    }

    @Test
    public void get_boolean_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertFalse(obj.getBoolean("nonExistKey"));
    }

    @Test
    public void get_int_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'int':10}"));
        Assert.assertEquals(10, obj.getInt("int"));
    }

    @Test
    public void get_int_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertEquals(0, obj.getInt("nonExistKey"));
    }

    @Test
    public void get_long_value() throws Exception {
        JSONObject json = new JSONObject();
        json.put("long", 9223372036854775807L);
        NCMBBase obj = new NCMBBase("testClass", json);
        Assert.assertEquals(9223372036854775807L, obj.getLong("long"));

    }

    @Test
    public void get_long_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertEquals(0, obj.getLong("nonExistKey"));
    }

    @Test
    public void get_double_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'double':2013.0901}"));
        Assert.assertEquals(2013.0901, obj.getDouble("double"));
    }

    @Test
    public void get_double_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertEquals(0.0, obj.getDouble("nonExistKey"));
    }

    @Test
    public void get_date_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{\"date\":{\"__type\":\"Date\",\"iso\":\"2015-07-29T10:10:10.000Z\"}}"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(new SimpleTimeZone(0, "UTC"));
        Assert.assertTrue(df.parse("2015-07-29 10:10:10").equals(obj.getDate("date")));
    }

    @Test
    public void get_date_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getDate("nonExistKey"));
    }

    @Test
    public void get_geolocation_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'location':{'__type':'geo', 'latitude':10.01,'longitude':10.01}}"));
        Assert.assertEquals(10.01, obj.getGeolocation("location").getLatitude());
        Assert.assertEquals(10.01, obj.getGeolocation("location").getLongitude());
    }

    @Test
    public void get_geolocation_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getGeolocation("nonExistKey"));
    }

    @Test
    public void get_json_object_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'jsonObject':{'key1':'value1','key2':'value2'}}"));
        JSONAssert.assertEquals(new JSONObject("{'key1':'value1','key2':'value2'}"), obj.getJSONObject("jsonObject"), false);
    }

    @Test
    public void get_json_object_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getJSONObject("nonExistKey"));
    }

    @Test
    public void get_json_array_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'jsonArray':['value1','value2']}"));
        JSONAssert.assertEquals(new JSONArray("['value1','value2']"), obj.getJSONArray("jsonArray"), false);
    }

    @Test
    public void get_json_array_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getJSONArray("nonExistKey"));
    }

    @Test
    public void get_list_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'list':['value1','value2']}"));
        List listValue = obj.getList("list");
        Assert.assertEquals("value1", listValue.get(0));
        Assert.assertEquals("value2", listValue.get(1));
    }

    @Test
    public void get_list_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getList("nonExistKey"));
    }

    @Test
    public void get_map_value() throws Exception {
        NCMBBase obj = new NCMBBase("testClass", new JSONObject("{'map':{'key1':'value1','key2':'value2'}}"));
        Map mapValue = obj.getMap("map");
        Assert.assertEquals("value1", mapValue.get("key1"));
        Assert.assertEquals("value2", mapValue.get("key2"));
    }

    @Test
    public void get_map_from_non_exist_key() throws Exception {
        NCMBBase obj = new NCMBBase("testClass");
        Assert.assertNull(obj.getMap("nonExistKey"));
    }

    @Test
    public void remove() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"key\":\"value\"}"));

        Assert.assertTrue(baseObj.containsKey("key"));

        baseObj.remove("key");

        Assert.assertTrue(baseObj.mFields.isNull("key"));
        Assert.assertFalse(baseObj.containsKey("key"));
        Assert.assertTrue(baseObj.mUpdateKeys.contains("key"));

    }

    @Test
    public void contains() throws Exception {
        NCMBBase baseObj = new NCMBBase("testClass", new JSONObject("{\"key\":\"value\"}"));
        Assert.assertTrue(baseObj.containsKey("key"));
    }


}
