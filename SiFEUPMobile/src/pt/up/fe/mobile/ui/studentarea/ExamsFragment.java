

package pt.up.fe.mobile.ui.studentarea;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.google.android.apps.iosched.util.UIUtils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;

public class ExamsFragment extends BaseFragment {

    /** Stores all exams from Student */
	private ArrayList<Exam> exams = new ArrayList<Exam>();
    final public static String PROFILE_CODE  = "profile";
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
    }
    
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
		list = new ListView(getActivity());
		switcher.addView(list);
        String personCode = (String) getArguments().get(PROFILE_CODE);
		if ( personCode == null )
			personCode = SessionManager.getInstance().getLoginCode();
        new ExamsTask().execute(personCode);
		return switcher; //this is mandatory.
	 }
	 
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exams_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_export_calendar) {
        	// export to Calendar (create event)
    		calendarExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Classe privada para a busca de dados ao servidor */
    private class ExamsTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){ 
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( result.equals("Success") )
        	{
				Log.e("Login","success");
				
				 String[] from = new String[] {"chair", "time", "room"};
		         int[] to = new int[] { R.id.exam_chair, R.id.exam_time,R.id.exam_room };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for(Exam e : exams){
		             HashMap<String, String> map = new HashMap<String, String>();
		             String tipo = "( " +  (e.type.contains("Mini teste")?"M":"E") + " ) ";
		             map.put("chair", tipo + e.courseName);
		             map.put("time", e.weekDay +", " + e.date + ": " + e.startTime +"-" + e.endTime);
		             map.put("room", e.rooms );
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         if ( getActivity() == null ) 
		        	 return;
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_exam, from, to);
		         list.setAdapter(adapter);
		         list.setClickable(false);
		         showMainScreen();
		         Log.e("JSON", "exams visual list loaded");

    		}
			else if ( result.equals("Error") ){	
				Log.e("Login","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")  )
			{
				if ( getActivity() != null ) 	
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        }

		@Override
		protected String doInBackground(String ... code) {
			String page = "";
		  	try {
		  			if ( code.length < 1)
		  				return "";
	    			page = SifeupAPI.getExamsReply(code[0]);
	    			int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    	    		JSONExams(page);
		    				return "Success";
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "";
		    		}
				
	    		
				return "";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
	
	/** Stores info about a exam */
	private class Exam{
		String type; // tipo de exame
		String courseAcronym; // codigo da cadeira
		String courseName; // nome da cadeira
		String weekDay; // [1 ... 6]
		String date; // data do exame
		String startTime; // hora de in�cio
		String endTime; // hora de fim
		String rooms; // salas
	}
	
	/**
	 * Parses a JSON String containing Exams info,
	 * Stores that info at Collection exams.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	private boolean JSONExams(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("exames")){
			Log.e("JSON", "exams found");
			
			// iterate over exams
			JSONArray jArray = jObject.getJSONArray("exames");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jExam = jArray.getJSONObject(i);
				// new Exam
				Exam exam = new Exam();
				
				if(jExam.has("tipo")) exam.type = jExam.getString("tipo");
				if(jExam.has("uc")) exam.courseAcronym = jExam.getString("uc");
				if(jExam.has("uc_nome")) exam.courseName = jExam.getString("uc_nome");
				if(jExam.has("dia")) exam.weekDay = jExam.getString("dia");
				if(jExam.has("data")) exam.date = jExam.getString("data");
				if(jExam.has("hora_inicio")) exam.startTime = jExam.getString("hora_inicio");
				if(jExam.has("hora_fim")) exam.endTime = jExam.getString("hora_fim");
				if(jExam.has("salas")) exam.rooms = jExam.getString("salas");
				
				// add exam
				exams.add(exam);
			}
			Log.e("JSON", "exams loaded");
			return true;
		}
		Log.e("JSON", "exams not found");
		return false;
		
	}
	
	  /** Exports the schedule to Google Calendar
     * WARNING: This is done against Google recomendations.
     * TODO: Change to access the GData API directly.
     * 		 Produce an ICAL file which can be imported by most calendars.*/
    public boolean calendarExport(){
    	
    	Context ctx = this.getActivity();
    	final ContentResolver cr = ctx.getContentResolver();
    	Cursor cursor = null;
        //Creating Queries
        if ( Build.VERSION.SDK_INT >= 8)
            cursor = cr.query(
            		Uri.parse("content://com.android.calendar/calendars"), 
            		new String[]{ "_id", "displayName", "selected"  }, 
            		null, null, null);
        else
            cursor = cr.query(
            		Uri.parse("content://calendar/calendars"), 
            		new String[]{ "_id","displayName", "selected"  }, 
            		null, null, null);
        if ( cursor == null )
        {
        	if ( getActivity() != null ) 
        		Toast.makeText(getActivity(), R.string.toast_export_calendar_error, Toast.LENGTH_LONG).show();
        	return false;
        }
        // Iterate over calendars to store names and ids
        if ( cursor.moveToFirst() ) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            
            // Throw a Calendar chooser menu
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {
     
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	
                	// iterate over schedule and add them to schedule
                	for(Exam b : exams){
                		// new event
                		ContentValues event = new ContentValues();
                		event.put("calendar_id", calIds[which]);
                		event.put("title", b.courseName);
                		event.put("eventLocation", b.rooms);
                		event.put("description", b.type);
                		long time = UIUtils.convertToUtc(getDate(b.date , b.startTime).toMillis(false));
                		event.put("dtstart",time );
                		event.put("dtend", time + timeDifference(b.startTime, b.endTime)*60000 );
                		
                		Uri newEvent = null;
                		// insert event
                		if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                			newEvent =cr.insert(Uri.parse("content://com.android.calendar/events"), event);
                		else
                			newEvent = cr.insert(Uri.parse("content://calendar/events"), event);
                		// check event error
                		if(newEvent == null) Log.e("ScheduleExport", "error on event");
                		
                	}
                	
                	
                    dialog.cancel();
                    if ( getActivity() != null ) 
                		Toast.makeText(getActivity(), R.string.toast_export_calendar_finished, Toast.LENGTH_LONG).show();

                }
     
            });
     
            builder.create().show();
        }
        cursor.close();
    	
    	return true;
    }
    
    private int timeDifference( String begin , String end ){
    	StringTokenizer s = new StringTokenizer(begin, ":");
    	String hour = s.nextToken();
    	String minute  = s.nextToken();
    	int beginInt = Integer.valueOf(hour) * 60 + Integer.valueOf(minute);
    	s = new StringTokenizer(end, ":");
    	hour = s.nextToken();
    	minute  = s.nextToken();
    	int endInt = Integer.valueOf(hour) * 60 + Integer.valueOf(minute);
    	return endInt - beginInt;
    }
    
    private Time getDate(String dateStr , String time){
    	Time date = new Time(UIUtils.TIME_REFERENCE);
    	StringTokenizer s = new StringTokenizer(dateStr, "-");
    	String year = s.nextToken();
    	String month = s.nextToken();
    	String day = s.nextToken();
    	s = new StringTokenizer(time, ":");
    	String hour = s.nextToken();
    	String minute  = s.nextToken();
    	date.set(0,Integer.valueOf(minute), Integer.valueOf(hour), 
    			Integer.valueOf(day), Integer.valueOf(month)-1, Integer.valueOf(year));
    	return date;
    }
	
}
