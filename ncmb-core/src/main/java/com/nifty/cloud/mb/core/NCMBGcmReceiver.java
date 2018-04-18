/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifty.cloud.mb.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.gcm.GcmReceiver;

/**
 * Custom GcmReceiver for Google Cloud Messaging
 */
public class NCMBGcmReceiver extends GcmReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences recentPushIdPref = context.getSharedPreferences("ncmbPushId", Context.MODE_PRIVATE);
        String recentPushId = recentPushIdPref.getString("recentPushId", "");
        String currentPushId = intent.getStringExtra("com.nifty.PushId");
        if (!recentPushId.equals(currentPushId)) {
            SharedPreferences.Editor editor = recentPushIdPref.edit();
            editor.putString("recentPushId", currentPushId);
            editor.commit();

            super.onReceive(context, intent);
        }

    }
}
