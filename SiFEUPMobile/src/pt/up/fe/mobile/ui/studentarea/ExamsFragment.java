package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import external.com.google.android.apps.iosched.util.UIUtils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Exam;
import pt.up.fe.mobile.sifeup.ExamsUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.utils.calendar.CalendarHelper;
import pt.up.fe.mobile.utils.calendar.Event;

public class ExamsFragment extends BaseFragment implements ResponseCommand {

	private final static String EXAM_KEY = "pt.up.fe.mobile.ui.studentarea.EXAMS";
	
	/** Stores all exams from Student */
	private ArrayList<Exam> exams;
	final public static String PROFILE_CODE = "pt.up.fe.mobile.ui.studentarea.PROFILE";
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
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}
	

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        String personCode = (String) getArguments().get(PROFILE_CODE);
        if (personCode == null)
            personCode = SessionManager.getInstance(getActivity()).getLoginCode();
        if ( savedInstanceState != null )
        {
            exams = savedInstanceState.getParcelableArrayList(EXAM_KEY);
            if ( exams == null )
            {   
                ExamsUtils.getExamsReply(personCode, this);
            }
            else
            {
                populateList();
                showFastMainScreen();
            }
        }
        else
        {
            ExamsUtils.getExamsReply(personCode, this);
        }
    }
    

 	@Override
 	public void onSaveInstanceState (Bundle outState){
 		if ( exams != null )
 			outState.putParcelableArrayList(EXAM_KEY,exams);
 	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.exams_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_export_calendar) {
			if (exams == null || exams.isEmpty())
				return true;
			// export to Calendar (create event)
			calendarExport();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Exports the schedule to Google Calendar TODO: Produce an ICAL file which
	 * can be imported by most calendars.
	 * 
	 * @return true if correct export
	 */
	public boolean calendarExport() {

		final ContentResolver cr = getActivity().getContentResolver();
		final CalendarHelper calendarHelper = CalendarHelper.getInstance(cr);
		final Cursor cursor = calendarHelper.getCalendars();
		// Creating Queries

		if (cursor == null) {
			if (getActivity() != null)
				Toast
						.makeText(getActivity(),
								R.string.toast_export_calendar_error,
								Toast.LENGTH_LONG).show();
			return false;
		}
		// Iterate over calendars to store names and ids
		if (cursor.moveToFirst()) {
			final String[] calNames = new String[cursor.getCount()];
			final int[] calIds = new int[cursor.getCount()];
			for (int i = 0; i < calNames.length; i++) {
				calIds[i] = cursor.getInt(0);
				calNames[i] = cursor.getString(1);
				cursor.moveToNext();
			}

			// Throw a Calendar chooser menu
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setSingleChoiceItems(calNames, -1,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							// iterate over schedule and add them to schedule
							for (Exam b : exams) {
								// new event
								long time = getDate(b.getDate(),
										b.getStartTime()).toMillis(false);
								Event event = new Event(b.getCourseName(), b
										.getRooms(), b.getType(), time, time
										+ timeDifference(b.getStartTime(), b
												.getEndTime()) * 60000);
								final Uri newEvent = calendarHelper
										.insertEvent(calIds[which], event);
								// check event error
								if (newEvent == null)
									Log.e("ScheduleExport", "error on event");

							}
							dialog.dismiss();
							if (getActivity() != null)
								Toast
										.makeText(
												getActivity(),
												R.string.toast_export_calendar_finished,
												Toast.LENGTH_LONG).show();

						}

					});

			builder.create().show();
		}
		cursor.close();

		return true;
	}

	private int timeDifference(String begin, String end) {
		StringTokenizer s = new StringTokenizer(begin, ":");
		String hour = s.nextToken();
		String minute = s.nextToken();
		int beginInt = Integer.valueOf(hour) * 60 + Integer.valueOf(minute);
		s = new StringTokenizer(end, ":");
		hour = s.nextToken();
		minute = s.nextToken();
		int endInt = Integer.valueOf(hour) * 60 + Integer.valueOf(minute);
		return endInt - beginInt;
	}

	private Time getDate(String dateStr, String time) {
		Time date = new Time(UIUtils.TIME_REFERENCE);
		StringTokenizer s = new StringTokenizer(dateStr, "-");
		String year = s.nextToken();
		String month = s.nextToken();
		String day = s.nextToken();
		s = new StringTokenizer(time, ":");
		String hour = s.nextToken();
		String minute = s.nextToken();
		date.set(0, Integer.valueOf(minute), Integer.valueOf(hour), Integer
				.valueOf(day), Integer.valueOf(month) - 1, Integer
				.valueOf(year));
		return date;
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			((BaseActivity) getActivity()).goLogin();
			getActivity().finish();
			return;
		case NETWORK:
			Toast.makeText(getActivity(),
					getString(R.string.toast_server_error), Toast.LENGTH_LONG)
					.show();
			break;
		default:
			break;
		}
		getActivity().finish();
	}

	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (results[0] instanceof ArrayList<?>)
			exams = (ArrayList<Exam>) results[0];
		else
			throw new RuntimeException("Error receiving the result");
		populateList();
		showMainScreen();
	}
	
	private void populateList(){
		if (exams.isEmpty()) {
			showEmptyScreen(getString(R.string.label_no_exams));
			return;
		}

		String[] from = new String[] { "chair", "time", "room" };
		int[] to = new int[] { R.id.exam_chair, R.id.exam_time, R.id.exam_room };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Exam e : exams) {
			HashMap<String, String> map = new HashMap<String, String>();
			String tipo = "( "
					+ (e.getType().contains("Mini teste") ? "M" : "E") + " ) ";
			map.put("chair", tipo + e.getCourseName());
			map.put("time", e.getWeekDay() + ", " + e.getDate() + ": "
					+ e.getStartTime() + "-" + e.getEndTime());
			map.put("room", e.getRooms());
			fillMaps.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setClickable(false);
	}

}
