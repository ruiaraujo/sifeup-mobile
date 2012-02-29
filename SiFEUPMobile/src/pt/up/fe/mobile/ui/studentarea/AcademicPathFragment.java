package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.AcademicPath;
import pt.up.fe.mobile.datatypes.AcademicUC;
import pt.up.fe.mobile.datatypes.AcademicYear;
import pt.up.fe.mobile.sifeup.AcademicPathUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.webclient.WebviewActivity;
import pt.up.fe.mobile.ui.webclient.WebviewFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Academic Path Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class AcademicPathFragment extends BaseFragment implements
        ResponseCommand {

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
        AnalyticsUtils.getInstance(getActivity()).trackPageView(
                "/Academic Path");

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

        // TODO: colocar talvez este link no menu de cima
        ((TextView) root.findViewById(R.id.path_link_sifeup))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final String url = "https://www.fe.up.pt/si/ALUNOS_FICHA.FICHA?p_cod="
                                + SessionManager.getInstance(getActivity()).getLoginCode();
                        Intent i = new Intent(getActivity(),
                                WebviewActivity.class);
                        i.putExtra(WebviewFragment.URL_INTENT, url);
                        startActivity(i);
                    }
                });
        return getParentContainer(); // mandatory
    }

	

 	@Override
 	public void onSaveInstanceState (Bundle outState){
 	    if ( academicPath != null )
 	        outState.putParcelable(ACADEMIC_KEY,academicPath);
 	}

    
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState != null )
        {
            academicPath = savedInstanceState.getParcelable(ACADEMIC_KEY);
            if ( academicPath == null )
            	task = AcademicPathUtils.getAcademicPathReply(SessionManager.getInstance(getActivity())
                        .getLoginCode(), this);
            else
            {
                displayData();
                showFastMainScreen();
            }
        }
        else
        {
        	task = AcademicPathUtils.getAcademicPathReply(SessionManager.getInstance(getActivity())
                    .getLoginCode(), this);
        }
    }

    public void onError(ERROR_TYPE error) {
        if (getActivity() == null)
            return;
        switch (error) {
        case AUTHENTICATION:
            Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
                    Toast.LENGTH_LONG).show();
            ((BaseActivity) getActivity()).goLogin();
            break;
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

    public void onResultReceived(Object... results) {
        if (getActivity() == null)
            return;
        academicPath = (AcademicPath) results[0];
        displayData();
        showMainScreen();
    }
    
    private void displayData(){
        average.setText(getString(R.string.path_average,
                academicPath.getAverage()));
        year.setText(getString(R.string.path_year,
                academicPath.getCourseYears()));
        grades.setAdapter(new AcademicPathAdapter());
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

                marker.setBackgroundColor(R.color.accent_1);
                return marker;
            }
            View root = mInflater.inflate(R.layout.list_item_grade, null);
            TextView gradeName = (TextView) root
                    .findViewById(R.id.grade_subject_name);
            TextView gradeNumber = (TextView) root
                    .findViewById(R.id.grade_number);
            gradeName.setText(uc.getName());
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
            return false;
        }

    }

}
