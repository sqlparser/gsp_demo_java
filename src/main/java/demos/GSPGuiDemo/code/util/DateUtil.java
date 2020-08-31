package com.gudusoft.format.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public DateUtil() {
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        } else {
            return null;
        }
    }
}
