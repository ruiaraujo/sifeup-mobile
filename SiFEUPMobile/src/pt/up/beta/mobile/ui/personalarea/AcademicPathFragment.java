package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.AcademicPath;
import pt.up.beta.mobile.datatypes.AcademicUC;
import pt.up.beta.mobile.datatypes.AcademicYear;
import pt.up.beta.mobile.loaders.AcademicPathLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.webclient.WebviewActivity;
import pt.up.beta.mobile.ui.webclient.WebviewFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

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
public class AcademicPathFragment extends BaseFragment implements
		 OnChildClickListener, LoaderCallbacks<AcademicPath> {

	private final static String ACADEMIC_KEY = "pt.up.fe.mobile.ui.studentarea.ACADEMIC_PATH";

	/** All info about the student Academic Path */
	private AcademicPath academicPath;

	private TextView average;
	private TextView year;

	private ExpandableListView grades;
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
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.academic_path,
				getParentContainer(), true);
		grades = (ExpandableListView) root.findViewById(R.id.path_ucs_grade);
		year = (TextView) root.findViewById(R.id.path_year);
		average = (TextView) root.findViewById(R.id.path_average);
		return getParentContainer(); // mandatory
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (academicPath != null)
			outState.putParcelable(ACADEMIC_KEY, academicPath);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			academicPath = savedInstanceState.getParcelable(ACADEMIC_KEY);
			if (academicPath == null)
				getActivity().getSupportLoaderManager().initLoader(0, null,
						this);
			else {
				displayData();
				showMainScreen();
			}
		} else {
			getActivity().getSupportLoaderManager().initLoader(0, null, this);

		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.webclient_menu_items, menu);
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_browser) {
			Intent i = new Intent(getActivity(), WebviewActivity.class);
			i.putExtra(WebviewFragment.URL_INTENT, SifeupAPI
					.getAcademicPathSigarraUrl(AccountUtils
							.getActiveUserCode(getActivity())));
			startActivity(i);
			return true;
		}
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncAcademicPath(AccountUtils.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayData() {
		average.setText(getString(R.string.path_average,
				academicPath.getAverage()));
		year.setText(getString(R.string.path_year,
				academicPath.getCourseYears()));
		grades.setAdapter(new AcademicPathAdapter());
		grades.setOnChildClickListener(this);
	}

	private class AcademicPathAdapter extends BaseExpandableListAdapter {

		public Object getChild(int groupPosition, int childPosition) {
			AcademicYear year = academicPath.getUcs().get(groupPosition);
			if (childPosition == 0) {
				// first marker
				return null;
			} else if (year.getFirstSemester().size() + 1 == childPosition) {
				// second marker
				return null;
			}
			AcademicUC uc = null;
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
			AcademicUC uc = (AcademicUC) getChild(groupPosition, childPosition);
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
			gradeName.setText(uc.getNamePt());
			gradeNumber.setText(getString(R.string.path_grade, uc.getGrade()));
			return root;
		}

		public int getChildrenCount(int groupPosition) {

			AcademicYear year = academicPath.getUcs().get(groupPosition);
			return year.getFirstSemester().size()
					+ year.getSecondSemester().size() + 2;
		}

		public Object getGroup(int groupPosition) {
			return getString(R.string.path_year,
					academicPath.getUcs().get(groupPosition).getYear()
							- academicPath.getBaseYear() + 1);
		}

		public int getGroupCount() {
			return academicPath.getUcs().size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mInflater.inflate(
						R.layout.list_item_grade_marker, null);
			((TextView) convertView)
					.setText((CharSequence) getGroup(groupPosition));
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
		AcademicUC uc = (AcademicUC) parent.getExpandableListAdapter()
				.getChild(groupPosition, childPosition);
		if (uc == null)
			return true;
		if (getActivity() == null)
			return true;
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);

		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
				uc.getCourseAcronym());
		i.putExtra(
				SubjectDescriptionFragment.SUBJECT_YEAR,
				Integer.toString(uc.getYear()) + "/"
						+ Integer.toString(uc.getYear() + 1));
		final String semester = uc.getSemester().equals("A") ? uc.getSemester()
				: uc.getSemester() + "S";
		i.putExtra(SubjectDescriptionFragment.SUBJECT_PERIOD, semester);
		String title = uc.getNamePt();
		if (!UIUtils.isLocalePortuguese() && uc.getNameEn().trim().length() > 0)
			title = uc.getNameEn();
		i.putExtra(Intent.EXTRA_TITLE, title);
		startActivity(i);
		return true;
	}

	@Override
	public Loader<AcademicPath> onCreateLoader(int loaderId, Bundle options) {
		return new AcademicPathLoader(getActivity(),
				SigarraContract.AcademicPath.CONTENT_URI,
				SigarraContract.AcademicPath.COLUMNS,
				SigarraContract.AcademicPath.PROFILE,
				SigarraContract.AcademicPath
						.getAcademicPathSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<AcademicPath> loader, AcademicPath result) {
		if (result == null)
			return;
		academicPath = result;
		displayData();
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<AcademicPath> loader) {
	}

}
