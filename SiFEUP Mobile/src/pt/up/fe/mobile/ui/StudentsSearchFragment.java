package pt.up.fe.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class StudentsSearchFragment extends ListFragment {
	
	Student me = new Student();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        new StudentsSearchTask().execute();

    }
	
	/** Classe privada para a busca de dados ao servidor */
    private class StudentsSearchTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.e("Search","success");
				String[] from = new String[] {"chair", "time", "room"};
		        int[] to = new int[] { R.id.exam_chair, R.id.exam_time,R.id.exam_room };
				
				List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				//for(Student s : students){
		             HashMap<String, String> map = new HashMap<String, String>();
		             // name
		             map.put("chair", me.name);
		             // academic year
		             map.put("time", me.academicYear);
		             // course acronym
		             map.put("room", me.courseAcronym);
		             fillMaps.add(map);
		         //}
				
				// fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_exam, from, to);
		         setListAdapter(adapter);
				/*
				 String[] from = new String[] {"chair", "time", "room"};
		         int[] to = new int[] { R.id.exam_chair, R.id.exam_time,R.id.exam_room };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for(Exam e : exams){
		             HashMap<String, String> map = new HashMap<String, String>();
		             String tipo = "( " +  (e.type.contains("Mini teste")?"M":"E") + " ) ";
		             map.put("chair", tipo + e.courseName);
		             map.put("time", e.weekDay +", " + e.date + ": " + e.startTime +"-" + e.endTime);
		             map.put("room", e.rooms );
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_exam, from, to);
		         setListAdapter(adapter);
		         Log.e("JSON", "exams visual list loaded");
				*/
    		}
			else{	
				Log.e("Search","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
	    			page = SifeupAPI.getStudentReply(
								SessionManager.getInstance().getLoginCode());
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
				
	    		//JSONExams(page);
	    		JSONStudent(page);
	    		
				return page;
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
    
    /** Stores all User Info */
	private class Student{
		private String code; // 090501049
		private String name; // "Nome do aluno"
		private String courseAcronym; // "Sigla do Curso"
		private String courseName; // "Nome do curso"
		private String registrationYear; // ano lectivo da matr�cula
		private String state; // "Estado (Frequentar, Interrompido,...)"
		private String academicYear; // (1|2|3|4|5)
		private String email; // "xxx@fe.up.pt"
		
		private void clearAll() {
			code = name = courseAcronym = 
				courseName = registrationYear = 
					state = academicYear = email = "";
		}
	}
	
	/**
	 * Parses a JSON String containing Student info,
	 * Stores that info at Collection me.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	public boolean JSONStudent(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("codigo")){
			Log.e("JSON", "founded student");
			
			// clear old fields
			//me.clearAll();
			
			if(jObject.has("codigo")) me.code = jObject.getString("codigo");
			if(jObject.has("nome")) me.name = jObject.getString("nome");
			if(jObject.has("curso_sigla")) me.courseAcronym = jObject.getString("curso_sigla");
			if(jObject.has("curso_nome")) me.courseName = jObject.getString("curso_nome");
			if(jObject.has("ano_lect_matricula")) me.registrationYear = jObject.getString("ano_lect_matricula");
			if(jObject.has("estado")) me.state = jObject.getString("estado");
			if(jObject.has("ano_curricular")) me.academicYear = jObject.getString("ano_curricular");
			if(jObject.has("email")) me.email = jObject.getString("email");
			
			Log.e("JSON", "loaded student");
			return true;
		}
		Log.e("JSON", "student not found");
		return false;
	}
}
