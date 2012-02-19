
package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;
import pt.up.fe.mobile.ui.webclient.WebviewActivity;
import pt.up.fe.mobile.ui.webclient.WebviewFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
public class AcademicPathFragment extends BaseFragment {
	
	/** All info about the student Academic Path */
	private AcademicPath academicPath = new AcademicPath();
	
	private TextView average;
	private TextView year;
	//private TextView entries;
	//private TextView state;
	//private TextView course;
	
	private ExpandableListView grades;
	private LayoutInflater mInflater;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Academic Path");

    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.academic_path, getParentContainer(), true);
		
		grades = (ExpandableListView) root.findViewById(R.id.path_ucs_grade);
		
		year = (TextView) root.findViewById(R.id.path_year);
		
		average = (TextView) root.findViewById(R.id.path_average);
		
		//entries = (TextView) root.findViewById(R.id.path_entries);
		
		//state = (TextView) root.findViewById(R.id.path_state);
		
		//course = (TextView) root.findViewById(R.id.path_course);
		
		//TODO: colocar talvez este link no menu de cima
		((TextView) root.findViewById(R.id.path_link_sifeup)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String url = "https://www.fe.up.pt/si/ALUNOS_FICHA.FICHA?p_cod=" +
							SessionManager.getInstance().getLoginCode();
                Intent i = new Intent(getActivity(), WebviewActivity.class);
                i.putExtra(WebviewFragment.URL_INTENT, url);
                startActivity(i);
			}
		});
        new AcademicPathTask().execute();
		return getParentContainer(); //mandatory
    }
	 
	/**
	 * 
	 * Holds All Info about 
	 * Academic Path
	 *
	 */
	@SuppressWarnings("unused")
	private class AcademicPath{
        private String code; // "numero"
		private String state; // "estado"
		private String courseAcronym; // "cur_codigo"
		private String courseName; // "cur_nome"
		private String courseNameEn; // "cur_name"
		private String average; // "media"
		private int courseYears; // "anos_curso"
		private int numberEntries; // "inscricoes_ucs"
		private int baseYear;
		private List<Year> ucs = new ArrayList< Year>();
	}
	
	/**
	 * 
	 * Represents a Course, and
	 * holds all data about the course
	 * like, grade, name, semester, etc
	 *
	 */
    @SuppressWarnings("unused")
	private class UC{
		private int semester; // "reg_d_codigo"
		private int year; // "a_lectivo"
		private String grade; // "resultado" (int or string)
		private String courseAcronym; // "dis_codigo"
		private int equivalencesNumber; // "n_equiv"
		private int academicYear; // "ano_curricular"
		private String state; // "estado"
		private String type; // "tipo"
		private String name; // "nome"
		private String nameEn; // "name"
	}
	
	private class Year{
		private int year;
		private List<UC> firstSemester = new ArrayList<UC>();
		private List<UC> secondSemester = new ArrayList<UC>();
	}
	
	/**
	 * Parses a JSON String containing Academic Path info,
	 * Stores that info at Object academicPath.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	private boolean JSONAcademicPath(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("ucs")){
			Log.e("JSON", "academic path found");
			
			if(jObject.has("numero")) academicPath.code = jObject.getString("numero");
			if(jObject.has("estado")) academicPath.state = jObject.getString("estado");
			if(jObject.has("cur_codigo")) academicPath.courseAcronym = jObject.getString("cur_codigo");
			if(jObject.has("cur_nome")) academicPath.courseName = jObject.getString("cur_nome");
			if(jObject.has("cur_name")) academicPath.courseNameEn = jObject.getString("cur_name");
			if(jObject.has("media")) academicPath.average = jObject.getString("media");
			if(jObject.has("anos_curso")) academicPath.courseYears = jObject.getInt("anos_curso");
			if(jObject.has("inscricoes_ucs")) academicPath.numberEntries = jObject.getInt("inscricoes_ucs");
			academicPath.baseYear = Integer.MAX_VALUE;
			// iterate over ucs
			JSONArray jArray = jObject.getJSONArray("ucs");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jUc = jArray.getJSONObject(i);
				// new UC
				UC uc = new UC();
				
				if(jUc.has("reg_d_codigo")) uc.semester = jUc.getInt("reg_d_codigo");
				if(jUc.has("a_lectivo")) uc.year = jUc.getInt("a_lectivo");
				if(jUc.has("resultado")) uc.grade = jUc.getString("resultado");
				if(jUc.has("dis_codigo")) uc.courseAcronym = jUc.getString("dis_codigo");
				if(jUc.has("n_equiv")) uc.equivalencesNumber = jUc.getInt("n_equiv");
				if(jUc.has("ano_curricular")) uc.academicYear = jUc.getInt("ano_curricular");
				if(jUc.has("estado")) uc.state = jUc.getString("estado");
				if(jUc.has("tipo")) uc.type = jUc.getString("tipo");
				if(jUc.has("nome")) uc.name = jUc.getString("nome");
				if(jUc.has("name")) uc.nameEn = jUc.getString("name");
				academicPath.baseYear = Math.min(academicPath.baseYear, uc.year);
				Year year = null;
				for ( int j = 0 ; j < academicPath.ucs.size() ; ++j )
				    if ( academicPath.ucs.get(j).year == uc.year )
				        year = academicPath.ucs.get(j);
				// add uc to academic path
				if ( year ==  null ) 
				{
					year = new Year();
					year.year = uc.year;
					academicPath.ucs.add(year);
				}
				if ( uc.semester == 1 )
				{
					year.firstSemester.add(uc);
				}
				else if ( uc.semester == 2)
				{
					year.secondSemester.add(uc);
				}
			}
			Log.e("JSON", "academic path loaded");
			return true;
		}
		Log.e("JSON", "academic path not found");
		return false;
	}
	
	/**
	 * Academic Path Task
	 */
	private class AcademicPathTask extends AsyncTask<Void, Void, String> {
		
		protected void onPreExecute (){
			showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( result.equals("Success") )
        	{
				Log.e("AcademicPath","success");
				average.setText(getString(R.string.path_average, academicPath.average));
				//entries.setText(getString(R.string.path_entries, academicPath.numberEntries));
				year.setText(getString(R.string.path_year, academicPath.courseYears));
				//state.setText(getString(R.string.path_state, academicPath.state));
				//course.setText(getString(R.string.path_course, academicPath.courseAcronym));
		        grades.setAdapter(new AcademicPathAdapter()); 
				showMainScreen();
    		}
			else if ( result.equals("Error")){	
				Log.e("AcademicPath","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
					return;
				}
			}
			else if ( result.equals("")){
				
			}
        } 
		
		@Override
		protected String doInBackground(Void... theVoid) {
			String page = "";
		  	try {
	    			page = SifeupAPI.getAcademicPathReply(
								SessionManager.getInstance().getLoginCode());
    			int error =	SifeupAPI.JSONError(page);
	    		switch (error)
	    		{
	    			case SifeupAPI.Errors.NO_AUTH:
	    				return "Error";
	    			case SifeupAPI.Errors.NO_ERROR:
	    	    		JSONAcademicPath(page);
	    				return "Success";
	    			case SifeupAPI.Errors.NULL_PAGE:
	    				return "";	
	    		}
			} catch (JSONException e) {
				Looper.prepare();
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return "";
		}
		 
	}
	
	private class AcademicPathAdapter extends BaseExpandableListAdapter {

		public Object getChild(int groupPosition, int childPosition) {
			Year year = academicPath.ucs.get(groupPosition );
			if ( childPosition == 0 )
			{
				//first marker
				return null;
			}
			else if ( year.firstSemester.size() + 1 == childPosition )
			{
				// second marker
				return null;
			}
			UC uc = null;
			if ( childPosition <= year.firstSemester.size() + 1  )
				uc = year.firstSemester.get(childPosition-1);
			else
				uc = year.secondSemester.get(childPosition-2-year.firstSemester.size());
			return uc;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			UC uc = (UC) getChild(groupPosition, childPosition);
			if ( uc == null  )
			{
				TextView marker = (TextView) mInflater.inflate(R.layout.list_item_grade_marker, null);
				if ( childPosition == 0 )
				{
					//first marker
					marker.setText(getString(R.string.path_semestre, 1));
				}
				else
				{
					// second marker
					marker.setText(getString(R.string.path_semestre, 2));
				}
				marker.setPadding(marker.getPaddingLeft() + 16 , 
								marker.getPaddingTop(),
								marker.getPaddingRight(),
								marker.getPaddingBottom());
				
				marker.setBackgroundColor(R.color.accent_1);
				return marker;
			}
			View root = mInflater.inflate(R.layout.list_item_grade, null);
			TextView gradeName = (TextView) root.findViewById(R.id.grade_subject_name);
			TextView gradeNumber = (TextView) root.findViewById(R.id.grade_number);
			gradeName.setText(uc.name);
			gradeNumber.setText( getString(R.string.path_grade , uc.grade));
			return root;
		}

		public int getChildrenCount(int groupPosition) {
		    
			Year year = academicPath.ucs.get(groupPosition);
			return year.firstSemester.size() + year.secondSemester.size() + 2;
		}

		public Object getGroup(int groupPosition) {
			return getString(R.string.path_year, academicPath.ucs.get(groupPosition).year-academicPath.baseYear+1);
		}

		public int getGroupCount() {
			return academicPath.ucs.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if ( convertView == null )
				convertView = mInflater.inflate(R.layout.list_item_grade_marker, null);
			((TextView) convertView).setText((CharSequence) getGroup(groupPosition));
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
