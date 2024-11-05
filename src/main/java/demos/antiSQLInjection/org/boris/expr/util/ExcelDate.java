/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ExcelDate
{
    public static final double MS_IN_DAY = 86400000; // 24*60*60*1000

    public static long toJavaDate(double value) {
        Calendar c = new GregorianCalendar();
        long days = Math.round(Math.floor(value));
        long millis = Math.round(MS_IN_DAY * (value - days));
        c.setLenient(true);
        c.set(1900, 0, 0, 0, 0, 0);
        c.set(Calendar.DAY_OF_YEAR, (int) days - 1);
        c.set(Calendar.MILLISECOND, (int) millis);
        return c.getTimeInMillis();
    }

    public static double toExcelDate(long millis) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(millis);
        int year = c.get(Calendar.YEAR);
        int days = (year - 1601) * 365 - 109207;
        year -= 1601;
        days += year / 4; // leap year
        days -= year / 100; // centuries aren't leap years
        days += year / 400; // unless they are divisible by 400
        days += c.get(Calendar.DAY_OF_YEAR) + 1;
        double m = c.get(Calendar.HOUR_OF_DAY) * 60;
        m += c.get(Calendar.MINUTE);
        m *= 60;
        m += c.get(Calendar.SECOND);
        m *= 1000;
        m += c.get(Calendar.MILLISECOND);
        m /= MS_IN_DAY;
        return days + m;
    }

    private static int get(double value, int field) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(toJavaDate(value));
        return c.get(field);
    }

    public static int getDayOfMonth(double value) {
        return get(value, Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(double value) {
        return get(value, Calendar.MONTH) + 1;
    }

    public static int getYear(double value) {
        return get(value, Calendar.YEAR);
    }

    public static int getWeekday(double value) {
        return get(value, Calendar.DAY_OF_WEEK);
    }

    public static int getHour(double value) {
        double h = value - Math.floor(value);
        h *= 24;
        h = Math.floor(h);
        return (int) h;
    }

    public static int getMinute(double value) {
        double m = value - Math.floor(value);
        m *= 24;
        m -= Math.floor(m);
        m = m * 60;
        m = Math.floor(m);
        return (int) m;
    }

    public static int getSecond(double value) {
        double d = (value - Math.floor(value)) * 1440;
        d -= Math.floor(d);
        int s = (int) Math.round(d * 60);
        return s;
    }

    public static double date(double y, double m, double d) {
        y %= 24;
        Calendar c = new GregorianCalendar();
        c.set((int) y, (int) m, (int) d, 0, 0, 0);
        double t = ExcelDate.toExcelDate(c.getTimeInMillis());
        if (t > 10000)
            return -1;
        return t;
    }

    public static double time(double h, double m, double s) {
        h %= 24;

        double t = (h + m + s) / ExcelDate.MS_IN_DAY;
        if (t < 0 || t >= 1)
            return -1;
        return t;
    }
}
