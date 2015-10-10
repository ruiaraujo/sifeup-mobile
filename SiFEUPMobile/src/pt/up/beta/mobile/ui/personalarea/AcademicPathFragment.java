package pt.up.beta.mobile.ui.personalarea;

import pt.up.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.AcademicPath;
import pt.up.beta.mobile.datatypes.AcademicYear;
import pt.up.beta.mobile.datatypes.SubjectEntry;
import pt.up.beta.mobile.loaders.AcademicPathLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionActivity;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import external.com.google.android.apps.iosched.util.UIUtils;

/**
 * Academic Path Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class AcademicPathFragment extends BaseLoaderFragment implements
		OnChildClickListener, LoaderCallbacks<AcademicPath[]> {

	private final static String ACADEMIC_KEY = "pt.up.fe.mobile.ui.studentarea.ACADEMIC_PATH";

	/** All info about the student Academic Path */
	private AcademicPath[] academicPaths;

	private ViewPager viewPager;
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
		View root = inflateMainScreen(R.layout.fragment_view_pager);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);
		viewPager.setAdapter(new PagerCourseAdapter());
		return getParentContainer();// mandatory
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (academicPaths != null)
			outState.putParcelableArray(ACADEMIC_KEY, academicPaths);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			final Parcelable[] storedAcademicPaths = savedInstanceState
					.getParcelableArray(ACADEMIC_KEY);
			if (storedAcademicPaths == null)
				getActivity().getSupportLoaderManager().initLoader(0, null,
						this);
			else {
				academicPaths = new AcademicPath[storedAcademicPaths.length];
				for (int i = 0; i < storedAcademicPaths.length; ++i)
					academicPaths[i] = (AcademicPath) storedAcademicPaths[i];
				viewPager.setAdapter(new PagerCourseAdapter());
				setRefreshActionItemState(false);
				showMainScreen();
			}
		} else {
			getActivity().getSupportLoaderManager().initLoader(0, null, this);

		}
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
			SigarraSyncAdapterUtils.syncAcademicPath(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncAcademicPath(AccountUtils
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

	private class AcademicPathAdapter extends BaseExpandableListAdapter {

		public Object getChild(int groupPosition, int childPosition) {
			AcademicYear year = academicPaths[viewPager.getCurrentItem()]
					.getUcs().get(groupPosition);
			if (childPosition == 0) {
				// first marker
				return null;
			} else if (year.getFirstSemester().size() + 1 == childPosition) {
				// second marker
				return null;
			}
			SubjectEntry uc = null;
			if (childPosition <= year.getFirstSemester().size() + 1)
				uc = year.getFirstSemester().get(childPosition - 1);
			else
				uc = year.getSecondSemester().get(
						childPosition - 2 - year.getFirstSemester().size());
			return uc;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			SubjectEntry uc = (SubjectEntry) getChild(groupPosition,
					childPosition);
			if (uc == null) {
				TextView marker = (TextView) mInflater.inflate(
						R.layout.list_item_grade_marker, null);
				if (childPosition == 0) {
					// first marker
					marker.setText(getString(R.string.path_semestre, 1));
				} else {
					// second marker
					marker.setText(getString(R.string.path_semestre, 2));
				}
				marker.setPadding(marker.getPaddingLeft() + 16,
						marker.getPaddingTop(), marker.getPaddingRight(),
						marker.getPaddingBottom());

				marker.setBackgroundColor(getResources().getColor(R.color.feup));
				marker.setTextColor(getResources().getColor(
						R.color.body_text_white));
				return marker;
			}
			View root = mInflater.inflate(R.layout.list_item_grade, null);
			TextView gradeName = (TextView) root
					.findViewById(R.id.grade_subject_name);
			TextView gradeNumber = (TextView) root
					.findViewById(R.id.grade_number);
			if (UIUtils.isLocalePortuguese()
					|| TextUtils.isEmpty(uc.getUcurrnome()))
				gradeName.setText(uc.getUcurrnome());
			else {
				gradeName.setText(uc.getUcurrname());
			}
			if (uc.getResultadomelhor() == null)
				gradeNumber.setText(null);
			else
				gradeNumber.setText(getString(R.string.path_grade,
						uc.getResultadomelhor()));
			return root;
		}

		public int getChildrenCount(int groupPosition) {

			AcademicYear year = academicPaths[viewPager.getCurrentItem()]
					.getUcs().get(groupPosition);
			return year.getFirstSemester().size()
					+ year.getSecondSemester().size() + 2;
		}

		public Object getGroup(int groupPosition) {
			return getString(
					R.string.path_year,
					academicPaths[viewPager.getCurrentItem()].getUcs()
							.get(groupPosition).getYear());
		}

		public int getGroupCount() {
			return academicPaths[viewPager.getCurrentItem()].getUcs().size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mInflater.inflate(
						R.layout.list_item_grade_marker, null);
			((TextView) convertView).setText(Html.fromHtml(getGroup(
					groupPosition).toString()));
			return convertView;
		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			if (getChild(groupPosition, childPosition) == null)
				return false;
			return true;
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		SubjectEntry uc = (SubjectEntry) parent.getExpandableListAdapter()
				.getChild(groupPosition, childPosition);
		if (uc == null)
			return true;
		if (getActivity() == null)
			return true;
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);

		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE, uc.getOcorrid());
		String title = uc.getUcurrnome();
		if (!UIUtils.isLocalePortuguese()
				&& !TextUtils.isEmpty(uc.getUcurrname()))
			title = uc.getUcurrname();
		i.putExtra(Intent.EXTRA_TITLE, title);
		startActivity(i);
		return true;
	}

	@Override
	public Loader<AcademicPath[]> onCreateLoader(int loaderId, Bundle options) {
		return new AcademicPathLoader(getActivity(),
				SigarraContract.AcademicPath.CONTENT_URI,
				SigarraContract.AcademicPath.COLUMNS,
				SigarraContract.AcademicPath.PROFILE,
				SigarraContract.AcademicPath
						.getAcademicPathSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<AcademicPath[]> loader,
			AcademicPath[] result) {
		if (result == null)
			return;
		academicPaths = result;
		viewPager.setAdapter(new PagerCourseAdapter());
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<AcademicPath[]> loader) {
	}

	class PagerCourseAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return academicPaths[position].getCourseAcronym();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			if (academicPaths == null)
				return 0;
			return academicPaths.length;
		}

		public Object instantiateItem(View collection, int position) {
			ViewGroup root = (ViewGroup) mInflater.inflate(
					R.layout.academic_path, getParentContainer(), false);
			final ExpandableListView grades = (ExpandableListView) root
					.findViewById(R.id.path_ucs_grade);
			final TextView year = (TextView) root.findViewById(R.id.path_year);
			final TextView average = (TextView) root
					.findViewById(R.id.path_average);

			final String averageStr;
			if (academicPaths[position].getAverage() == null)
				averageStr = getString(R.string.no_data);
			else
				averageStr = academicPaths[position].getAverage();
			average.setText(Html.fromHtml(getString(R.string.path_average,
					averageStr)));
			year.setText(Html.fromHtml(getString(R.string.path_year,
					academicPaths[position].getCourseYears())));
			grades.setAdapter(new AcademicPathAdapter());
			grades.setOnChildClickListener(AcademicPathFragment.this);
			((ViewPager) collection).addView(root, 0);
			return root;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
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
