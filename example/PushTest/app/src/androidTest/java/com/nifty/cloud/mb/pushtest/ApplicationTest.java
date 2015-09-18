package com.nifty.cloud.mb.pushtest;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.nifty.cloud.mb.core.NCMBConnection;
import com.nifty.cloud.mb.core.NCMBRequest;
import com.nifty.cloud.mb.core.NCMBResponse;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test() throws Exception{
        Log.d("tag", "POST実行");
        NCMBRequest request = new NCMBRequest(
                "https://mb.api.cloud.nifty.com/2013-09-01/classes/SaveTest",
                "POST",
                "{\"score\":100}",
                null,
                null,
                "d22614e80e85c3452a07c467e19e8ad8e7716727225c72bda929f0203d844f77",
                "2988b9a74adbdbfb78620b0b3321a6b3e5064cd6e9502e036cbbb7b5a8865f33");

        NCMBConnection connection = new NCMBConnection(request);
        NCMBResponse response = connection.sendRequest();
        String objectId = response.responseData.getString("objectId");

        Log.d("tag", "DELETE実行:"+objectId);
        request = new NCMBRequest(
                "https://mb.api.cloud.nifty.com/2013-09-01/classes/SaveTest/"+objectId,
                "DELETE",
                null,
                null,
                null,
                "d22614e80e85c3452a07c467e19e8ad8e7716727225c72bda929f0203d844f77",
                "2988b9a74adbdbfb78620b0b3321a6b3e5064cd6e9502e036cbbb7b5a8865f33");

        connection = new NCMBConnection(request);
        response = connection.sendRequest();

    }

}