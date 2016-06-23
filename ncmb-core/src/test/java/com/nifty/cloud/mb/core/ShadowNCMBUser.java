package com.nifty.cloud.mb.core;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

// ShadowNCMBUserでImplementationされたメソッドをNCMBUserが呼び出した場合はShadowNCMBUser側のメソッドがコールされる
@Implements(NCMBUser.class)
public class ShadowNCMBUser {

    @Implementation
    public static String createUUID() {
        return "anonymousDummyId";
    }
}
