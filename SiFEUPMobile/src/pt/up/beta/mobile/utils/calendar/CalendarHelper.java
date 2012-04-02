package pt.up.beta.mobile.utils.calendar;

import pt.up.beta.mobile.utils.DateUtils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

public class CalendarHelper {

    protected ContentResolver cr;

    protected CalendarHelper(ContentResolver cr) {
        this.cr = cr;
    }

    public static CalendarHelper getInstance(ContentResolver cr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            return new CalendarHelperICS(cr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            return new CalendarHelperFroyo(cr);
        return new CalendarHelper(cr);
    }

    public Cursor getCalendars() {

        return cr.query(Uri.parse("content://calendar/calendars"),
                new String[] { "_id", "displayName" }, null, null, null);
    }
    
    public Uri insertEvent(long calendarId, Event ev){
        final ContentValues event = new ContentValues();
        event.put("calendar_id", calendarId);
        event.put("title", ev.getTitle());
        event.put("eventLocation", ev.getEventLocation());
        event.put("description", ev.getDescription());
        event.put("dtstart",ev.getTimeStart() );
        event.put("dtend", ev.getTimeEnd() );
        event.put("eventTimezone", DateUtils.TIME_REFERENCE );
        return cr.insert(Uri.parse("content://calendar/events"), event);
    }
}
