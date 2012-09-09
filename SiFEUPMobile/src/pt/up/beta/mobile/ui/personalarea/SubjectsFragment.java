package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.sifeup.AccountUtils;
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
import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectsFragment extends BaseFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {

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
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
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

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(getActivity(),
				SigarraContract.Subjects.CONTENT_URI, new String[] {
						SigarraContract.SubjectsColumns.CODE,
						SigarraContract.SubjectsColumns.YEAR,
						SigarraContract.SubjectsColumns.PERIOD,
						SigarraContract.SubjectsColumns.NAME_PT,
						SigarraContract.SubjectsColumns.NAME_EN },
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects
						.getUserSubjectsSelectionArgs(AccountUtils
								.getActiveUserCode(getActivity())), null);
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
