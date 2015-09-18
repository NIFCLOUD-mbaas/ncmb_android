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
