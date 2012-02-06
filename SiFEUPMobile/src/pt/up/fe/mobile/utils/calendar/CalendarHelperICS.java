package pt.up.fe.mobile.utils.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class CalendarHelperICS extends CalendarHelper {

    protected CalendarHelperICS(ContentResolver cr) {
        super(cr);
    }

    public Cursor getCalendars() {
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final String[] projection = new String[] { CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };

        return cr.query(uri, projection, null, null, null);
    }
    
    public Uri insertEvent(int calendarId, Event ev){
        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        event.put(CalendarContract.Events.TITLE, ev.getTitle());
        event.put(CalendarContract.Events.EVENT_LOCATION, ev.getEventLocation());
        event.put(CalendarContract.Events.DESCRIPTION, ev.getDescription());
        event.put(CalendarContract.Events.DTSTART,ev.getTimeStart() );
        event.put(CalendarContract.Events.DTEND, ev.getTimeEnd() );
        return cr.insert(CalendarContract.Events.CONTENT_URI, event);
    }
}
