package com.nifty.cloud.mb.core;

import android.location.Location;

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
import org.skyscreamer.jsonassert.JSONAssert;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

/**
 * NCMBQueryTest
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBQueryTest {
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
    public void set_condition_equal_to() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereEqualTo("key", "value");

        JSONAssert.assertEquals(query.getConditions(), new JSONObject("{\"where\":{\"key\":\"value\"}}"), true);
    }

    @Test
    public void set_condition_not_equal_to() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereNotEqualTo("key", "value");

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$ne\":\"value\"}}}"),
                true
        );
    }

    @Test
    public void set_condition_less_than() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereLessThan("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$lt\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_greater_than() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereGreaterThan("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$gt\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_less_than_equal() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereLessThanOrEqualTo("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$lte\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_greater_than_equal() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereGreaterThanOrEqualTo("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$gte\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_greater_than_and_less_than() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereGreaterThan("key", 0);
        query.whereLessThan("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$gt\":0,\"$lt\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_greater_than_and_not_equal() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereGreaterThan("key", 0);
        query.whereNotEqualTo("key", 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$gt\":0,\"$ne\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_contained_in() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereContainedIn("key", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$in\":[\"value1\",\"value2\"]}}}"),
                true
        );
    }

    @Test
    public void set_condition_not_contained_in() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereNotContainedIn("key", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$nin\":[\"value1\",\"value2\"]}}}"),
                true
        );
    }

    @Test
    public void set_condition_exists() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereExists("key");

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$exists\":true}}}"),
                true
        );
    }

    @Test
    public void set_condition_does_not_exists() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereDoesNotExists("key");

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$exists\":false}}}"),
                true
        );
    }

    @Test
    public void set_condition_contained_in_array() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereContainedInArray("key", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$inArray\":[\"value1\",\"value2\"]}}}"),
                true
        );
    }

    @Test
    public void set_condition_not_contained_in_array() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereNotContainedInArray("key", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$ninArray\":[\"value1\",\"value2\"]}}}"),
                true
        );
    }

    @Test
    public void set_condition_contains_all_in_array() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereContainsAll("key", Arrays.asList("value1", "value2"));

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":{\"$all\":[\"value1\",\"value2\"]}}}"),
                true
        );
    }

    @Test
    public void set_condition_or_queries() throws Exception {
        NCMBQuery<NCMBObject> queryA = new NCMBQuery<>("TestClass");
        queryA.whereEqualTo("keyA", "valueA");

        NCMBQuery<NCMBObject> queryB = new NCMBQuery<>("TestClass");
        queryB.whereEqualTo("keyB", "valueB");

        NCMBQuery orQuery = new NCMBQuery<>("TestClass");
        orQuery.or(Arrays.asList(queryA, queryB));

        JSONAssert.assertEquals(
                orQuery.getConditions(),
                new JSONObject("{\"where\":{\"$or\":[{\"keyA\":\"valueA\"},{\"keyB\":\"valueB\"}]}}"),
                true
        );
    }

    @Test
    public void set_condition_matches_key_in_query() throws Exception {
        NCMBQuery<NCMBObject> subQuery = new NCMBQuery<>("SubClass");
        subQuery.whereLessThan("countNumber", 10);

        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereMatchesKeyInQuery("subKey", "number", subQuery);

        JSONObject selectQueryJson = new JSONObject();
        selectQueryJson.put("subKey", new JSONObject("{\"$select\":{\"query\":{\"where\":{\"countNumber\":{\"$lt\":10}},\"className\":\"SubClass\"},\"key\":\"number\"}}"));

        JSONObject expectJson = new JSONObject();
        expectJson.put("where", selectQueryJson);

        JSONAssert.assertEquals(
                query.getConditions(),
                expectJson,
                true
        );
    }

    @Test
    public void set_condition_matches_query() throws Exception {
        NCMBQuery<NCMBObject> subQuery = new NCMBQuery<>("SubClass");
        subQuery.whereEqualTo("key", "value");

        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereMatchesQuery("subKey", subQuery);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"subKey\":{\"$inQuery\":{\"where\":{\"key\":\"value\"},\"className\":\"SubClass\"}}}}"),
                true
        );
    }

    @Test
    public void set_condition_related_to() throws Exception {
        NCMBObject parentObj = new NCMBObject("TestClass");
        parentObj.setObjectId("testObjectId");

        NCMBQuery<NCMBObject> subQuery = new NCMBQuery<>("SubClass");
        subQuery.whereRelatedTo(parentObj, "pointer");

        JSONAssert.assertEquals(
                subQuery.getConditions(),
                new JSONObject("{\"where\":{\"$relatedTo\":{\"object\":{\"__type\":\"Pointer\",\"className\":\"TestClass\",\"objectId\":\"testObjectId\"},\"key\":\"pointer\"}}}"),
                true
        );
    }

    @Test
    public void set_condition_with_in_geo_box() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        Location southwest = new Location("sdk-test");
        southwest.setLatitude(30.0);
        southwest.setLongitude(30.0);

        Location northeast = new Location("sdk-test");
        northeast.setLatitude(60.0);
        northeast.setLongitude(60.0);

        query.whereWithinGeoBox("location", southwest, northeast);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"location\":{\"$within\":{\"$box\":[{\"__type\":\"GeoPoint\",\"latitude\":30.0,\"longitude\":30.0},{\"__type\":\"GeoPoint\",\"latitude\":60.0,\"longitude\":60.0}]}}}}"),
                true
        );
    }

    @Test
    public void set_condition_within_kilometers() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        Location point = new Location("sdk-test");
        point.setLatitude(30.0);
        point.setLongitude(30.0);

        query.whereWithinKilometers("location", point, 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"location\":{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":30.0,\"longitude\":30.0},\"$maxDistanceInKilometers\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_within_miles() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        Location point = new Location("sdk-test");
        point.setLatitude(30.0);
        point.setLongitude(30.0);

        query.whereWithinMiles("location", point, 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"location\":{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":30.0,\"longitude\":30.0},\"$maxDistanceInMiles\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_within_radians() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        Location point = new Location("sdk-test");
        point.setLatitude(30.0);
        point.setLongitude(30.0);

        query.whereWithinRadians("location", point, 10);

        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"location\":{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":30.0,\"longitude\":30.0},\"$maxDistanceInRadians\":10}}}"),
                true
        );
    }

    @Test
    public void set_condition_limit() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.setLimit(3);
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"limit\":3}"),
                true
        );
    }

    @Test
    public void set_condition_skip() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.setSkip(3);
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"skip\":3}"),
                true
        );
    }

    @Test
    public void set_condition_include() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.setIncludeKey("pointer");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"include\":\"pointer\"}"),
                true
        );
    }

    @Test
    public void set_condition_ascending() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.addOrderByAscending("testKey");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"order\":\"testKey\"}"),
                true
        );
    }

    @Test
    public void set_condition_descending() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.addOrderByDescending("testKey");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"order\":\"-testKey\"}"),
                true
        );
    }

    @Test
    public void set_condition_multiple_order() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.addOrderByAscending("ascendingKey");
        query.addOrderByDescending("descendingKey");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"order\":\"ascendingKey,-descendingKey\"}"),
                true
        );
    }

    @Test
    public void set_condition_delete_order() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.addOrderByAscending("ascendingKey");
        query.addOrderByDescending("descendingKey");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"order\":\"ascendingKey,-descendingKey\"}"),
                true
        );

        query.deleteOrder("ascendingKey");
        JSONAssert.assertEquals(
                query.getConditions(),
                new JSONObject("{\"where\":{\"key\":\"value\"}, \"order\":\"-descendingKey\"}"),
                true
        );
    }


    @Test
    public void count_search_result() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        int result = query.count();
        Assert.assertEquals(result, 50);
    }

    @Test
    public void count_search_result_in_background() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

        query.whereEqualTo("key", "value");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int result, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals(result, 50);
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void count_installation() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("installation");

        query.whereEqualTo("deviceType", "android");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int result, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals(result, 50);
                }
            }
        });
    }

    @Test
    public void find_with_operand() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereEqualTo("key", "value");
        query.setLimit(50);
        query.setSkip(3);
        query.setIncludeKey("pointerKey");
        query.addOrderByAscending("ascendingKey");
        query.addOrderByDescending("descendingKey");

        List<NCMBObject> result = query.find();

        Assert.assertEquals("8FgKqFlH8dZRDrBJ", result.get(0).getObjectId());
    }

    @Test
    public void find_valid_class() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        List<NCMBObject> result = query.find();

        Assert.assertEquals("8FgKqFlH8dZRDrBJ", result.get(0).getObjectId());
    }

    @Test
    public void check_searchCondition_data_type() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.whereEqualTo("stringKey", "string");
        query.whereEqualTo("intKey", 10);
        query.whereEqualTo("longKey", 10000000000000000L);
        query.whereEqualTo("floatKey", 1.23F);
        query.whereEqualTo("doubleKey", 1.23);
        query.whereEqualTo("boolKey", true);
        query.whereEqualTo("arrayKey", Arrays.asList("array"));
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        query.whereEqualTo("mapKey", map);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS", Locale.JAPAN);
        df.setTimeZone(new SimpleTimeZone(0, "UTC"));
        query.whereEqualTo("dateKey", df.parse("2016/01/26-00:00:00.000"));
        //位置情報は別途位置情報検索でテスト済み

        List<NCMBObject> result = query.find();
        Assert.assertEquals("8FgKqFlH8dZRDrBJ", result.get(0).getObjectId());
    }

    @Test
    public void find_include_object() throws Exception {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.setIncludeKey("post.author");
        List<NCMBObject> result = query.find();

        Assert.assertEquals("aaaaa", result.get(0).getObjectId());

        NCMBObject post = result.get(0).getIncludeObject("post");
        Assert.assertEquals("000", post.getObjectId());

        NCMBUser author = post.getIncludeObject("author");
        Assert.assertEquals("testUser", author.getUserName());
    }

    @Test
    public void find_user_class() throws Exception {
        NCMBQuery<NCMBUser> query = NCMBUser.getQuery();
        query.whereEqualTo("userName", "Nifty Tarou");
        List<NCMBUser> result = query.find();

        Assert.assertEquals("Nifty Tarou", result.get(0).getUserName());
    }

    @Test
    public void find_push_class() throws Exception {
        NCMBQuery<NCMBPush> query = NCMBPush.getQuery();
        query.whereEqualTo("target", Arrays.asList("android"));
        List<NCMBPush> result = query.find();

        Assert.assertEquals("message1", result.get(0).getMessage());
    }

    @Test
    public void find_installation_class() throws Exception {
        NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();
        query.whereEqualTo("deviceType", "android");
        List<NCMBInstallation> result = query.find();

        Assert.assertEquals("dummyDeviceToken01", result.get(0).getDeviceToken());
    }

    @Test
    public void find_role_class() throws Exception {
        NCMBQuery<NCMBRole> query = NCMBRole.getQuery();
        query.whereEqualTo("roleName", "testRole");
        List<NCMBRole> result = query.find();

        Assert.assertEquals("testRole", result.get(0).getRoleName());
    }

    @Test
    public void find_file_class() throws Exception {
        NCMBQuery<NCMBFile> query = NCMBFile.getQuery();
        query.whereEqualTo("fileName", "testFile");
        List<NCMBFile> result = query.find();

        Assert.assertEquals("testFile", result.get(0).getFileName());
    }

    @Test
    public void find_in_background() {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("8FgKqFlH8dZRDrBJ", results.get(0).getObjectId());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void find_in_background_users_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBUser> query = NCMBUser.getQuery();
        query.findInBackground(new FindCallback<NCMBUser>() {
            @Override
            public void done(List<NCMBUser> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("Nifty Tarou", results.get(0).getUserName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);

    }

    @Test
    public void find_in_background_push_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBPush> query = NCMBPush.getQuery();
        query.findInBackground(new FindCallback<NCMBPush>() {
            @Override
            public void done(List<NCMBPush> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("message1", results.get(0).getMessage());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void find_in_background_installation_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();
        query.findInBackground(new FindCallback<NCMBInstallation>() {
            @Override
            public void done(List<NCMBInstallation> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("dummyDeviceToken01", results.get(0).getDeviceToken());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void find_in_background_role_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBRole> query = NCMBRole.getQuery();
        query.findInBackground(new FindCallback<NCMBRole>() {
            @Override
            public void done(List<NCMBRole> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("testRole", results.get(0).getRoleName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }

    @Test
    public void find_in_background_file_class() throws Exception {
        Assert.assertFalse(callbackFlag);
        NCMBQuery<NCMBFile> query = NCMBFile.getQuery();
        query.findInBackground(new FindCallback<NCMBFile>() {
            @Override
            public void done(List<NCMBFile> results, NCMBException e) {
                if (e != null) {
                    Assert.fail("this callback should not raise exception");
                } else {

                    Assert.assertEquals("testFile", results.get(0).getFileName());
                }
                callbackFlag = true;
            }
        });

        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();

        Assert.assertTrue(callbackFlag);
    }
}
