package pt.up.beta.mobile.ui.subjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.StudentCourse;
import pt.up.beta.mobile.datatypes.SubjectEntry;
import pt.up.beta.mobile.loaders.SubjectsLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import pt.up.beta.mobile.utils.StringUtils;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectsFragment extends BaseLoaderFragment implements
		OnItemClickListener, LoaderCallbacks<StudentCourse[]> {

	/** Contains all subscribed subjects */
	private StudentCourse[] studentCourses = new StudentCourse[0];

	private ViewPager viewPager;
	private TitlePageIndicator indicator;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		View root = inflater.inflate(R.layout.fragment_view_pager,
				getParentContainer(), true);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);
		viewPager.setAdapter(new PagerCourseAdapter());
		// Find the indicator from the layout
		indicator = (TitlePageIndicator) root.findViewById(R.id.indicator_menu);
		return getParentContainer();// mandatory
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncSubjects(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncSubjects(AccountUtils
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

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
		final SubjectEntry subject = studentCourses[viewPager.getCurrentItem()]
				.getSubjectEntries()[position];
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
				subject.getOcorrid());
		String title = subject.getUcurrnome();
		if (!UIUtils.isLocalePortuguese()
				&& !TextUtils.isEmpty(subject.getUcurrname()))
			title = subject.getUcurrname();
		i.putExtra(Intent.EXTRA_TITLE, title);
		startActivity(i);

	}

	@Override
	public Loader<StudentCourse[]> onCreateLoader(int loaderId, Bundle args) {
		return new SubjectsLoader(getActivity(),
				SigarraContract.Subjects.CONTENT_URI,
				SigarraContract.Subjects.SUBJECTS_COLUMNS,
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects
						.getUserSubjectsSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())),
				SigarraContract.Subjects.SUBJECTS_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<StudentCourse[]> loader,
			StudentCourse[] cursor) {
		if (getActivity() == null)
			return;
		if (cursor == null) {
			// waiting
			return;
		}
		studentCourses = cursor;
		if (studentCourses.length == 0) {
			showEmptyScreen(getString(R.string.lb_no_courses));
			return;
		}
		viewPager.setAdapter(new PagerCourseAdapter());
		indicator.setViewPager(viewPager);

		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<StudentCourse[]> loader) {
	}

	class PagerCourseAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			if (TextUtils.isEmpty(studentCourses[position].getCourseName()))
				return studentCourses[position].getCourseTypeDesc();
			return studentCourses[position].getCourseAcronym() == null ? StringUtils
					.getAcronym(studentCourses[position].getCourseName())
					: studentCourses[position].getCourseAcronym();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			return studentCourses.length;
		}

		public Object instantiateItem(View collection, int position) {
			final SubjectEntry[] subjects = studentCourses[position]
					.getSubjectEntries();
			if (subjects.length == 0) {
				View emptyScreen = mInflater.inflate(
						R.layout.fragment_no_results, viewPager, false);
				TextView text = (TextView) emptyScreen
						.findViewById(R.id.message);
				emptyScreen.findViewById(R.id.action).setVisibility(View.GONE);
				text.setText(R.string.lb_no_subjects);
				return emptyScreen;
			}
			ListView list = (ListView) mInflater.inflate(R.layout.generic_list,
					viewPager, false);
			final String[] from = new String[] { "name", "code", "time" };
			final int[] to = new int[] { R.id.exam_chair, R.id.exam_time,
					R.id.exam_room };
			// prepare the list of all records
			final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
			for (SubjectEntry s : studentCourses[position].getSubjectEntries()) {
				HashMap<String, String> map = new HashMap<String, String>();
				if (UIUtils.isLocalePortuguese())
					map.put(from[0],
							TextUtils.isEmpty(s.getUcurrnome()) ? s
									.getUcurrname() : s.getUcurrnome());
				else
					map.put(from[0],
							TextUtils.isEmpty(s.getUcurrname()) ? s
									.getUcurrnome() : s.getUcurrname());
				map.put(from[1], s.getUcurrsigla());
				map.put(from[2],
						getString(R.string.subjects_year, s.getAno(),
								s.getPernome()));
				fillMaps.add(map);
			}
			// fill in the grid_item layout
			final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
					fillMaps, R.layout.list_item_exam, from, to);
			list.setAdapter(adapter);
			list.setOnItemClickListener(SubjectsFragment.this);
			((ViewPager) collection).addView(list, 0);
			return list;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			indicator.setViewPager(viewPager);
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {
		}

		public void finishUpdate(View arg0) {
		}

	}

}
