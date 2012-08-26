package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.content.tables.SubjectsTable;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SubjectUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.utils.DateUtils;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectsFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand, LoaderCallbacks<Cursor> {

	/** Contains all subscribed subjects */
	private List<Subject> subjects;
	private ListView list;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*
		 * task =
		 * SubjectUtils.getSubjectsReply(SessionManager.getInstance(getActivity
		 * ()) .getLoginCode(), Integer.toString(DateUtils
		 * .secondYearOfSchoolYear() - 1), this);
		 */
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			return;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		subjects = (ArrayList<Subject>) results[0];
		if (subjects.isEmpty()) {
			showEmptyScreen(getString(R.string.lb_no_subjects));
			return;
		}
		final String[] from = new String[] { "name", "code", "time" };
		final int[] to = new int[] { R.id.exam_chair, R.id.exam_time,
				R.id.exam_room };
		// prepare the list of all records
		final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Subject s : subjects) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (UIUtils.isLocalePortuguese())
				map.put(from[0],
						(s.getNamePt().trim().length() != 0) ? s.getNamePt()
								: s.getNameEn());
			else
				map.put(from[0],
						(s.getNameEn().trim().length() != 0) ? s.getNameEn()
								: s.getNamePt());
			map.put(from[1], s.getAcronym());
			map.put(from[2],
					getString(R.string.subjects_year, s.getYear(),
							s.getSemestre()));
			fillMaps.add(map);
		}
		// fill in the grid_item layout
		final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				fillMaps, R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(SubjectsFragment.this);
		showMainScreen();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		if (getActivity() == null)
			return;
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);

		int secondYear = DateUtils.secondYearOfSchoolYear();
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
				subjects.get(position).getCode());
		i.putExtra(
				SubjectDescriptionFragment.SUBJECT_YEAR,
				Integer.toString(secondYear - 1) + "/"
						+ Integer.toString(secondYear));
		i.putExtra(SubjectDescriptionFragment.SUBJECT_PERIOD,
				subjects.get(position).getSemestre());
		String title = subjects.get(position).getNamePt();
		if (!UIUtils.isLocalePortuguese()
				&& !TextUtils
						.isEmpty(subjects.get(position).getNameEn().trim()))
			title = subjects.get(position).getNameEn();
		i.putExtra(Intent.EXTRA_TITLE, title);

		startActivity(i);

	}

	protected void onRepeat() {
		showLoadingScreen();
		task = SubjectUtils.getSubjectsReply(
				SessionManager.getInstance(getActivity()).getLoginCode(),
				Integer.toString(DateUtils.secondYearOfSchoolYear() - 1), this);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(getActivity(),
				SigarraProvider.CONTENT_SUBJECTS_URI, new String[] {
						SubjectsTable.COLUMN_CODE, SubjectsTable.COLUMN_YEAR,
						SubjectsTable.COLUMN_PERIOD,
						SubjectsTable.COLUMN_NAME_PT,
						SubjectsTable.COLUMN_NAME_EN },
				SubjectsTable.COLUMN_USER_CODE + "=?",
				new String[] { SessionManager.getInstance(getActivity())
						.getLoginCode() }, SubjectsTable.COLUMN_PERIOD);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null)
			return;
		subjects = Subject.parseCursor(cursor);
		if (subjects.isEmpty()) {
			showEmptyScreen(getString(R.string.lb_no_subjects));
			return;
		}
		final String[] from = new String[] { "name", "code", "time" };
		final int[] to = new int[] { R.id.exam_chair, R.id.exam_time,
				R.id.exam_room };
		// prepare the list of all records
		final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Subject s : subjects) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (UIUtils.isLocalePortuguese())
				map.put(from[0],
						(s.getNamePt().trim().length() != 0) ? s.getNamePt()
								: s.getNameEn());
			else
				map.put(from[0],
						(s.getNameEn().trim().length() != 0) ? s.getNameEn()
								: s.getNamePt());
			map.put(from[1], s.getAcronym());
			map.put(from[2],
					getString(R.string.subjects_year, s.getYear(),
							s.getSemestre()));
			fillMaps.add(map);
		}
		// fill in the grid_item layout
		final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				fillMaps, R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(SubjectsFragment.this);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
