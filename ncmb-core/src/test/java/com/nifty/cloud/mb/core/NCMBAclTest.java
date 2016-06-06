package com.nifty.cloud.mb.core;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.skyscreamer.jsonassert.JSONAssert;
//import junit.framework.Assert;

/**
 * Test for NCMBAcl
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class NCMBAclTest {
    @Before
    public void setup() {
    }

    @After
    public void teardown() {
    }

    /**
     * - 内容：Permission を生成
     * - 結果：正しく初期化される
     */
    @Test
    public void Permission_生成() throws Exception {
        NCMBAcl.Permission p1 = new NCMBAcl.Permission(true, false);
        JSONAssert.assertEquals(new JSONObject("{'read':true}"), p1.toJson(), false);

        NCMBAcl.Permission p2 = new NCMBAcl.Permission(false, true);
        JSONAssert.assertEquals(new JSONObject("{'write':true}"), p2.toJson(), false);

        NCMBAcl.Permission p3 = new NCMBAcl.Permission(true, true);
        JSONAssert.assertEquals(new JSONObject("{'read':true, 'write':true}"), p3.toJson(), false);
    }

    /**
     * - 内容：Permission を JSONOBject から生成
     * - 結果：正しく初期化される
     */
    @Test
    public void Permission_JSONから生成() throws Exception {
        JSONObject jsonRead = new JSONObject();
        jsonRead.put("read", true);
        NCMBAcl.Permission p1 = new NCMBAcl.Permission(jsonRead);
        Assert.assertTrue(p1.readable);
        Assert.assertFalse(p1.writable);
        JSONAssert.assertEquals(new JSONObject("{'read':true}"), p1.toJson(), false);

        JSONObject jsonWrite = new JSONObject();
        jsonWrite.put("write", true);
        NCMBAcl.Permission p2 = new NCMBAcl.Permission(jsonWrite);
        Assert.assertFalse(p2.readable);
        Assert.assertTrue(p2.writable);
        JSONAssert.assertEquals(new JSONObject("{'write':true}"), p2.toJson(), false);

        JSONObject jsonReadWrite = new JSONObject();
        jsonReadWrite.put("read", true);
        jsonReadWrite.put("write", true);
        NCMBAcl.Permission p3 = new NCMBAcl.Permission(jsonReadWrite);
        Assert.assertTrue(p3.readable);
        Assert.assertTrue(p3.writable);
        JSONAssert.assertEquals(new JSONObject("{'read':true, 'write':true}"), p3.toJson(), false);
    }

    /**
     * - 内容：NCMBAclを用いて単一ユーザACLを設定する
     * - 結果：JSON出力が正しい
     */
    @Test
    public void ACL_単一ユーザACLを設定() throws Exception {
        String userId = "acluser";

        NCMBAcl acl = new NCMBAcl();
        acl.setReadAccess(userId, true);
        Assert.assertTrue(acl.getReadAccess(userId));
        Assert.assertFalse(acl.getWriteAccess(userId));
        JSONAssert.assertEquals(new JSONObject("{'acluser':{'read':true}}"), acl.toJson(), false);

        NCMBAcl acl2 = new NCMBAcl();
        acl2.setWriteAccess(userId, true);
        Assert.assertFalse(acl2.getReadAccess(userId));
        Assert.assertTrue(acl2.getWriteAccess(userId));
        JSONAssert.assertEquals(new JSONObject("{'acluser':{'write':true}}"), acl2.toJson(), false);

        NCMBAcl acl3 = new NCMBAcl();
        acl3.setReadAccess(userId, true);
        acl3.setWriteAccess(userId, true);
        Assert.assertTrue(acl3.getReadAccess(userId));
        Assert.assertTrue(acl3.getWriteAccess(userId));
        JSONAssert.assertEquals(new JSONObject("{'acluser':{'read':true, 'write':true}}"), acl3.toJson(), false);
    }

    /**
     * - 内容：NCMBAclを用いて単一ロールACLを設定する
     * - 結果：JSON出力が正しい
     */
    @Test
    public void ACL_単一ロールACLを設定() throws Exception {
        String roleName = "aclrole";

        NCMBAcl acl = new NCMBAcl();
        acl.setRoleReadAccess(roleName, true);
        Assert.assertTrue(acl.getRoleReadAccess(roleName));
        Assert.assertFalse(acl.getRoleWriteAccess(roleName));
        JSONAssert.assertEquals(new JSONObject("{'role:aclrole':{'read':true}}"), acl.toJson(), false);

        NCMBAcl acl2 = new NCMBAcl();
        acl2.setRoleWriteAccess(roleName, true);
        Assert.assertFalse(acl2.getRoleReadAccess(roleName));
        Assert.assertTrue(acl2.getRoleWriteAccess(roleName));
        JSONAssert.assertEquals(new JSONObject("{'role:aclrole':{'write':true}}"), acl2.toJson(), false);

        NCMBAcl acl3 = new NCMBAcl();
        acl3.setRoleReadAccess(roleName, true);
        acl3.setRoleWriteAccess(roleName, true);
        Assert.assertTrue(acl3.getRoleReadAccess(roleName));
        Assert.assertTrue(acl3.getRoleWriteAccess(roleName));
        JSONAssert.assertEquals(new JSONObject("{'role:aclrole':{'read':true, 'write':true}}"), acl3.toJson(), false);
    }

    /**
     * - 内容：NCMBAclを用いてpublic ACLを設定する
     * - 結果：JSON出力が正しい
     */
    @Test
    public void ACL_publicACLを設定() throws Exception {
        NCMBAcl acl = new NCMBAcl();
        acl.setPublicReadAccess(true);
        Assert.assertTrue(acl.getPublicReadAccess());
        Assert.assertFalse(acl.getPublicWriteAccess());
        JSONAssert.assertEquals(new JSONObject("{'*':{'read':true}}"), acl.toJson(), false);

        acl.setPublicReadAccess(false);
        acl.setPublicWriteAccess(true);
        Assert.assertFalse(acl.getPublicReadAccess());
        Assert.assertTrue(acl.getPublicWriteAccess());
        JSONAssert.assertEquals(new JSONObject("{'*':{'write':true}}"), acl.toJson(), false);

        acl.setPublicReadAccess(true);
        Assert.assertTrue(acl.getPublicReadAccess());
        Assert.assertTrue(acl.getPublicWriteAccess());
        JSONAssert.assertEquals(new JSONObject("{'*':{'read':true, 'write':true}}"), acl.toJson(), false);
    }

    /**
     * - 内容：ユーザ、ロール、public を組みあせてACLを設定する
     * - 結果：JSON が実質同等
     */
    @Test
    public void ACL_複合ACLを設定() throws Exception {
        String userId = "acluser";
        String roleName = "aclrole";

        NCMBAcl acl = new NCMBAcl();
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        acl.setRoleReadAccess(roleName, true);
        acl.setPublicReadAccess(true);

        JSONObject json = acl.toJson();
        JSONObject acluser = json.getJSONObject("acluser");
        JSONObject aclrole = json.getJSONObject("role:aclrole");
        JSONObject aclpublic = json.getJSONObject("*");

        Assert.assertEquals(acluser.getBoolean("read"), true);
        Assert.assertEquals(acluser.getBoolean("write"), true);
        Assert.assertEquals(aclrole.getBoolean("read"), true);
        Assert.assertEquals(aclpublic.getBoolean("read"), true);

        NCMBAcl acl2 = new NCMBAcl(json);
        Assert.assertTrue(acl2.getReadAccess(userId));
        Assert.assertTrue(acl2.getWriteAccess(userId));
        Assert.assertTrue(acl2.getRoleReadAccess(roleName));
        Assert.assertFalse(acl2.getRoleWriteAccess(roleName));
        Assert.assertTrue(acl2.getPublicReadAccess());
        Assert.assertFalse(acl2.getPublicWriteAccess());
    }

    /**
     * - 内容：ACLの初期値をチェックする
     * - 結果：空状態を判別できる
     */
    @Test
    public void ACL_ACLの初期値() throws Exception {
        NCMBAcl acl = new NCMBAcl();

        Assert.assertTrue(acl.isEmpty());
    }

    /**
     * - 内容：未設定のACLにアクセスする
     * - 結果：正しく値を返す
     */
    @Test
    public void ACL_未設定のACLのアクセス() throws Exception {
        String userId = "acluser";
        String roleName = "aclrole";

        NCMBAcl acl = new NCMBAcl();
        Assert.assertFalse(acl.getReadAccess(userId));
        Assert.assertFalse(acl.getWriteAccess(userId));
        Assert.assertFalse(acl.getRoleReadAccess(roleName));
        Assert.assertFalse(acl.getRoleWriteAccess(roleName));
        Assert.assertFalse(acl.getPublicReadAccess());
        Assert.assertFalse(acl.getPublicWriteAccess());
    }

    /**
     * - 内容：Permission の削除
     * - 結果：削除されていること
     */
    @Test
    public void ACL_Permissionの削除() throws Exception {
        NCMBAcl acl = new NCMBAcl();
        String userId = "acluser";

        // set Permission
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        JSONAssert.assertEquals(new JSONObject("{'acluser':{'read':true, 'write':true}}"), acl.toJson(), false);
        // remove permission
        acl.removePermission(userId);
        Assert.assertTrue(acl.isEmpty());
    }
}
