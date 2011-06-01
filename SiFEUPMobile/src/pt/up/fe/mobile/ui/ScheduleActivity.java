package pt.up.fe.mobile.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ScheduleActivity extends BaseSinglePaneActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
	}

	@Override
	protected Fragment onCreatePane() {
		return new ScheduleFragment();
	}
	
	
	 /** Exports the schedule to Google Calendar */
    private boolean calendarExport(){
    	
    	Context ctx = this.getApplicationContext();
    	final ContentResolver cr = ctx.getContentResolver();
        Cursor cursor;
        
        // Creating Queries
        if (Integer.parseInt(Build.VERSION.SDK) == 8)
            cursor = cr.query(
            		Uri.parse("content://com.android.calendar/calendars"), 
            		new String[]{ "_id", "displayname" }, 
            		null, null, null);
        else
            cursor = cr.query(
            		Uri.parse("content://calendar/calendars"), 
            		new String[]{ "_id", "displayname" }, 
            		null, null, null);
        
        // Iterate over calendars to store names and ids
        if ( cursor.moveToFirst() ) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                Log.e("CALENDAR id", ""+calIds[i]);
                Log.e("CALENDAR name", calNames[i]);
                cursor.moveToNext();
            }
            
            // Throw a Calendar chooser menu
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {
     
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	
                	// Set event details
                    ContentValues cv = new ContentValues();
                    cv.put("calendar_id", calIds[which]);
                    cv.put("title", "event android test");
                    cv.put("description", "teste descricao");
                    cv.put("eventLocation", "teste lugar");
                    //cv.put("allDay", 1);
                    cv.put("dtstart", 20110602 );
                    //cv.put("hasAlarm", 1);
                    cv.put("dtend", 20110603 );
                    
                    // Insert Event
                    Uri newEvent;
                    if (Integer.parseInt(Build.VERSION.SDK) == 8 )
                        newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
                    else
                        newEvent = cr.insert(Uri.parse("content://calendar/events"), cv);
                    
                    /* Alert
                    if (newEvent != null) {
                        long id = Long.parseLong( newEvent.getLastPathSegment() );
                        ContentValues values = new ContentValues();
                        values.put( "event_id", id );
                        values.put( "method", 1 );
                        values.put( "minutes", 15 ); // 15 minutes
                        if (Integer.parseInt(Build.VERSION.SDK) == 8 )
                            cr.insert( Uri.parse( "content://com.android.calendar/reminders" ), values );
                        else
                            cr.insert( Uri.parse( "content://calendar/reminders" ), values );
     
                    }*/
                    dialog.cancel();
                }
     
            });
     
            builder.create().show();
        }
        cursor.close();
    	
    	return true;
    }

}
