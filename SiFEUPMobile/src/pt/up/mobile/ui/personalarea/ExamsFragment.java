package pt.up.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import pt.up.mobile.R;
import pt.up.mobile.content.SigarraContract;
import pt.up.mobile.datatypes.Exam;
import pt.up.mobile.loaders.ExamsLoader;
import pt.up.mobile.sifeup.AccountUtils;
import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.mobile.ui.BaseLoaderFragment;
import pt.up.mobile.utils.DateUtils;
import pt.up.mobile.utils.calendar.CalendarHelper;
import pt.up.mobile.utils.calendar.Event;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ExamsFragment extends BaseLoaderFragment implements
		LoaderCallbacks<Exam[]>, OnItemClickListener {

	private final static String EXAM_KEY = "pt.up.fe.mobile.ui.studentarea.EXAMS";

	/** Stores all exams from Student */
	private Exam[] exams;
	final public static String PROFILE_CODE = "pt.up.fe.mobile.ui.studentarea.PROFILE";
	private ListView list;
	private String personCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.generic_list);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		personCode = getArguments().getString(PROFILE_CODE);
		if (personCode == null)
			personCode = AccountUtils.getActiveUserCode(getActivity());
		if (savedInstanceState != null) {
			final Parcelable[] storedExams = savedInstanceState
					.getParcelableArray(EXAM_KEY);
			if (storedExams == null) {
				getActivity().getSupportLoaderManager().initLoader(0, null,
						this);
			} else {
				exams = new Exam[storedExams.length];
				for (int i = 0; i < storedExams.length; ++i)
					exams[i] = (Exam) storedExams[i];
				if (populateList())
					showMainScreen();
			}
		} else
			getActivity().getSupportLoaderManager().initLoader(0, null, this);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (exams != null)
			outState.putParcelableArray(EXAM_KEY, exams);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.exams_menu_items, menu);
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_export_calendar) {
			if (exams == null)
				return true;

			if (exams.length == 0) {
				Toast.makeText(getActivity(), R.string.label_no_exams,
						Toast.LENGTH_SHORT).show();
				return true;
			}
			// export to Calendar (create event)
			calendarExport();
			return true;
		}
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncExams(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncExams(AccountUtils
				.getActiveUserName(getActivity()));

	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			finish();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	/**
	 * Exports the schedule to Google Calendar
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
				Toast.makeText(getActivity(),
						R.string.toast_export_calendar_error, Toast.LENGTH_LONG)
						.show();
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
								Event event = new Event(b.getOcorrName(), b
										.getRoomsString(), b.getType(), time,
										time
												+ timeDifference(
														b.getStartTime(),
														b.getEndTime()) * 60000);
								final Uri newEvent = calendarHelper
										.insertEvent(calIds[which], event);
								// check event error
								if (newEvent == null)
									Log.d("ScheduleExport", "error on event");

							}
							dialog.dismiss();
							if (getActivity() != null)
								Toast.makeText(
										getActivity(),
										R.string.toast_export_calendar_finished,
										Toast.LENGTH_SHORT).show();

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
		Time date = new Time(DateUtils.TIME_REFERENCE);
		StringTokenizer s = new StringTokenizer(dateStr, "-");
		String year = s.nextToken();
		String month = s.nextToken();
		String day = s.nextToken();
		s = new StringTokenizer(time, ":");
		String hour = s.nextToken();
		String minute = s.nextToken();
		date.set(0, Integer.valueOf(minute), Integer.valueOf(hour),
				Integer.valueOf(day), Integer.valueOf(month) - 1,
				Integer.valueOf(year));
		return date;
	}

	private boolean populateList() {
		if (getActivity() == null)
			return false;
		if (exams.length == 0) {
			showEmptyScreen(getString(R.string.label_no_exams));
			return false;
		}

		String[] from = new String[] { "chair", "time", "room" };
		int[] to = new int[] { R.id.exam_chair, R.id.exam_time, R.id.exam_room };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Exam e : exams) {
			HashMap<String, String> map = new HashMap<String, String>();
			String tipo = "( "
					+ (e.getTypeDesc().contains("Mini-testes") ? "M" : "E")
					+ " ) ";
			map.put(from[0], tipo + e.getOcorrName());
			map.put(from[1],
					e.getDate() + ": " + e.getStartTime() + "-"
							+ e.getEndTime());
			if (e.getRooms().length == 0)
				map.put(from[2], getString(R.string.label_no_exam_room));
			else
				map.put(from[2], e.getRoomsString());
			fillMaps.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return true;
	}

	@Override
	public Loader<Exam[]> onCreateLoader(int loaderId, Bundle options) {
		return new ExamsLoader(getActivity(),
				SigarraContract.Exams.CONTENT_URI,
				SigarraContract.Exams.COLUMNS, SigarraContract.Exams.PROFILE,
				SigarraContract.Exams.getExamsSelectionArgs(AccountUtils
						.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<Exam[]> laoder, Exam[] exams) {
		if (getActivity() == null)
			return;
		if (exams == null)
			return;
		this.exams = exams;
		setRefreshActionItemState(false);
		if (populateList()) {
			showMainScreen();
		}
	}

	@Override
	public void onLoaderReset(Loader<Exam[]> loader) {
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		final Exam e = exams[position];
		final String tipo = "( "
				+ (e.getTypeDesc().contains("Mini-testes") ? "M" : "E") + " ) ";
		startActivity(new Intent(getActivity(), ExamDescriptionActivity.class)
				.putExtra(ExamDescriptionFragment.EXAM, e).putExtra(
						Intent.EXTRA_TITLE, tipo + e.getOcorrName()));
	}

}
