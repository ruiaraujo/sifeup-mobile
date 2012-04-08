package pt.up.beta.mobile.utils.calendar;

import pt.up.beta.mobile.utils.DateUtils;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class CalendarHelperFroyo extends CalendarHelper {

    protected CalendarHelperFroyo(ContentResolver cr) {
        super(cr);
    }

    @Override
    public Cursor getCalendars() {
        return cr.query(Uri.parse("content://com.android.calendar/calendars"),
                new String[] { "_id", "displayName" }, null, null, null);
    }

    @Override
    public Uri insertEvent(long calendarId, Event ev){
        final ContentValues event = new ContentValues();
        event.put("calendar_id", calendarId);
        event.put("title", ev.getTitle());
        event.put("eventLocation", ev.getEventLocation());
        event.put("description", ev.getDescription());
        event.put("dtstart",ev.getTimeStart() );
        event.put("dtend", ev.getTimeEnd() );
        event.put("eventTimezone", DateUtils.TIME_REFERENCE );
        return cr.insert(Uri.parse("content://com.android.calendar/events"), event);
    }
}
