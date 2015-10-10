package pt.up.mobile.utils;

import java.util.Date;
import java.util.TimeZone;

import android.text.format.Time;

public class DateUtils {
    private DateUtils(){} //private constructor
    public static String TIME_REFERENCE = "Europe/Lisbon";

    private final static TimeZone zone = TimeZone
            .getTimeZone(TIME_REFERENCE);
    public static long firstDayofWeek() {
        Time yourDate = new Time(TIME_REFERENCE);
        yourDate.setToNow();
        yourDate.minute = 0;
        yourDate.hour = 0;
        yourDate.second = 0;
        yourDate.normalize(false);
       // if (!zone.inDaylightTime(new Date(yourDate.toMillis(false)))) {
         //   yourDate.hour = 1;
        //    yourDate.normalize(false);
        //}
        int weekDay = yourDate.weekDay - 1;
        // Our week starts at Monday
        if (weekDay < 0)
            weekDay = 6;
        long mondayMillis = yourDate.toMillis(false);
        mondayMillis -= (weekDay * 24 * 60 * 60 * 1000);
        if (moveDayofWeek(mondayMillis, 5) < getCurrentTime(false))
            mondayMillis = moveDayofWeek(mondayMillis, 7);
        return mondayMillis;
    }

    public static long moveDayofWeek(long millis, int dayOffset) {
        Time yourDate = new Time(TIME_REFERENCE);
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
    

    public static int secondYearOfSchoolYear(){
    	Time nowT = new Time(TIME_REFERENCE);
    	nowT.setToNow();
    	nowT.normalize(false);
    	if ( nowT.month >= 8 )
    		return nowT.year+1;
    	return nowT.year;
    }
    
    public static int secondYearOfSchoolYear(long millis){
    	Time nowT = new Time(TIME_REFERENCE);
    	nowT.set(millis);
    	nowT.normalize(false);
    	if ( nowT.month >= 8 )
    		return nowT.year+1;
    	return nowT.year;
    }
    

    public static long getCurrentTime(boolean utc) {
        Time yourDate = new Time(TIME_REFERENCE);
        yourDate.setToNow();
        return yourDate.toMillis(false);
    }
    
    public static long convertToUtc(long now) {
    	TimeZone local_tz = TimeZone.getTimeZone(TIME_REFERENCE); //Gets current local TZ of phone
        long tz_offset_gmt = local_tz.getOffset(System.currentTimeMillis ()); // Get Offset in ms, divide by 3600000
        now -= tz_offset_gmt;
        return now;
    }
}
