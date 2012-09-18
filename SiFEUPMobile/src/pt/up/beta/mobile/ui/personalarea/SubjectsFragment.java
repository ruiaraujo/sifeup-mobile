package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.loaders.SubjectsLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.utils.DateUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectsFragment extends BaseFragment implements
		OnItemClickListener, LoaderCallbacks<List<Subject>> {

	/** Contains all subscribed subjects */
	private List<Subject> subjects;
	private ListView list;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	
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

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {		
			setRefreshActionItemState(true);
			SyncAdapterUtils.syncSubjects(AccountUtils.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
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
						.isEmpty(subjects.get(position).getNameEn()))
			title = subjects.get(position).getNameEn();
		i.putExtra(Intent.EXTRA_TITLE, title);

		startActivity(i);

	}

	@Override
	public Loader<List<Subject>> onCreateLoader(int loaderId, Bundle args) {
		return new SubjectsLoader(getActivity(),
				SigarraContract.Subjects.CONTENT_URI, new String[] {
						SigarraContract.SubjectsColumns.CODE,
						SigarraContract.SubjectsColumns.YEAR,
						SigarraContract.SubjectsColumns.PERIOD,
						SigarraContract.SubjectsColumns.NAME_PT,
						SigarraContract.SubjectsColumns.NAME_EN },
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects
						.getUserSubjectsSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<List<Subject>> loader,
			List<Subject> cursor) {
		if (getActivity() == null)
			return;
		if (cursor == null) {
			// waiting
			return;
		}
		subjects = cursor;
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
						TextUtils.isEmpty(s.getNamePt()) ? s.getNameEn(): s
								.getNamePt());
			else
				map.put(from[0],
						TextUtils.isEmpty(s.getNameEn()) ? s.getNamePt() : s
								.getNameEn());
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
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<List<Subject>> loader) {
	}
}
