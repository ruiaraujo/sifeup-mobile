package pt.up.beta.mobile.utils;

import java.util.Date;
import java.util.TimeZone;

import android.text.format.Time;
import external.com.google.android.apps.iosched.util.UIUtils;

public class DateUtils {
    private DateUtils(){} //private constructor

    private final static TimeZone zone = TimeZone
            .getTimeZone(UIUtils.TIME_REFERENCE);
    public static long firstDayofWeek() {
        Time yourDate = new Time(UIUtils.TIME_REFERENCE);
        yourDate.setToNow();
        yourDate.minute = 0;
        yourDate.hour = 0;
        yourDate.second = 0;
        yourDate.normalize(false);
        if (!zone.inDaylightTime(new Date(yourDate.toMillis(false)))) {
            yourDate.hour = 1;
            yourDate.normalize(false);
        }
        int weekDay = yourDate.weekDay - 1;
        // Our week starts at Monday
        if (weekDay < 0)
            weekDay = 6;
        long mondayMillis = yourDate.toMillis(false);
        mondayMillis -= (weekDay * 24 * 60 * 60 * 1000);
        if (moveDayofWeek(mondayMillis, 5) < UIUtils.getCurrentTime(false))
            mondayMillis = moveDayofWeek(mondayMillis, 7);
        return mondayMillis;
    }

    public static long moveDayofWeek(long millis, int dayOffset) {
        Time yourDate = new Time(UIUtils.TIME_REFERENCE);
        yourDate.set(millis);
        boolean usingDst = zone.inDaylightTime(new Date(yourDate
                .toMillis(false)));
        yourDate.monthDay += dayOffset;
        yourDate.normalize(false);
        if (zone.inDaylightTime(new Date(yourDate.toMillis(false)))) {
            if (!usingDst) {
                yourDate.hour -= 1;
                yourDate.normalize(false);
            }
        } else {
            if (usingDst) {
                yourDate.hour += 1;
                yourDate.normalize(false);
            }
        }
        return yourDate.toMillis(false);
    }
}
