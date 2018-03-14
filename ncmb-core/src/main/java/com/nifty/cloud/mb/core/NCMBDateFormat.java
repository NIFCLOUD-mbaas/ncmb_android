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

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * Utils for SimpleDateFormat
 */
public class NCMBDateFormat {
    /**
     * Get SimpleDataFomat object ready for ISO8601
     * @return SimpleDateFormat object ready for ISO8601
     */
    public static SimpleDateFormat getIso8601() {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        Locale locale = Locale.JAPAN;
        SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        format.setTimeZone(new SimpleTimeZone(0, "UTC"));

        return format;
    }
}
