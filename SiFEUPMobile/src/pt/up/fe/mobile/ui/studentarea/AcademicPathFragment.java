package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AcademicPathFragment extends Fragment {
	
	/** All info about the student Academic Path */
	AcademicPath academicPath = new AcademicPath();
	TextView average;
	TextView year;
	TextView entries;
	ListView grades;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Academic Path");

    }
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.academic_path, null);
			grades = (ListView) root.findViewById(R.id.path_ucs_grade);
			year = (TextView) root.findViewById(R.id.path_year);
			average = (TextView) root.findViewById(R.id.path_average);
			entries = (TextView) root.findViewById(R.id.path_entries);
			((TextView) root.findViewById(R.id.path_link_sifeup)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url = "https://www.fe.up.pt/si/ALUNOS_FICHA.FICHA?p_cod=" +
								SessionManager.getInstance().getLoginCode();
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
				}
			});
	        new AcademicPathTask().execute();
			return root;
	    }
	 

	private class AcademicPathTask extends AsyncTask<Void, Void, String> {
		
		protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( result.equals("Success") )
        	{
				Log.e("AcademicPath","success");
				average.setText(getString(R.string.path_average, academicPath.average));
				entries.setText(getString(R.string.path_entries, academicPath.numberEntries));
				year.setText(getString(R.string.path_year, academicPath.courseYears));
				String[] from = new String[] {"name", "number"};
		         int[] to = new int[] { R.id.grade_subject_name, R.id.grade_number };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for(UC e : academicPath.ucs){
		             HashMap<String, String> map = new HashMap<String, String>();
		             map.put(from[0], e.name);
		             map.put(from[1], getString(R.string.path_grade , e.grade));
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_grade, from, to);
		         grades.setAdapter(adapter);
		         
    		}
			else if ( result.equals("Error")){	
				Log.e("AcademicPath","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
			else if ( result.equals("")){
				
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
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
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return "";
		}
		 
	}
	
	
	
	/**
	 * 
	 * Holds All Info about 
	 * Academic Path
	 *
	 */
	private class AcademicPath{
		private String code; // "numero"
		private String state; // "estado"
		private String courseAcronym; // "cur_codigo"
		private String courseName; // "cur_nome"
		private String courseNameEn; // "cur_name"
		private String average; // "media"
		private int courseYears; // "anos_curso"
		private int numberEntries; // "inscricoes_ucs"
		ArrayList<UC> ucs = new ArrayList<UC>();
	}
	
	/**
	 * 
	 * Represents a Course, and
	 * holds all data about the course
	 * like, grade, name, semester, etc
	 *
	 */
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
				
				// add uc to academic path
				academicPath.ucs.add(uc);
			}
			Log.e("JSON", "academic path loaded");
			return true;
		}
		Log.e("JSON", "academic path not found");
		return false;
	}
	
}
