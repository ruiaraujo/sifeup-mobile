

package pt.up.fe.mobile.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

public class ExamsFragment extends ListFragment {

    /** Stores all exams from Student */
	private ArrayList<Exam> exams = new ArrayList<Exam>();
    final public static String PROFILE_CODE  = "profile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        String personCode = (String) getArguments().get(PROFILE_CODE);
		if ( personCode == null )
			personCode = SessionManager.getInstance().getLoginCode();
        new ExamsTask().execute(personCode);

    }
    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_export_calendar) {
        	// export to Calendar (create event)
    		//calendarExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Classe privada para a busca de dados ao servidor */
    private class ExamsTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( result.equals("Success") )
        	{
				Log.e("Login","success");
				
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

    		}
			else if ( result.equals("Error") ){	
				Log.e("Login","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")  )
			{
				if ( getActivity() != null ) 	
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(String ... code) {
			String page = "";
		  	try {
		  			if ( code.length < 1)
		  				return "";
	    			page = SifeupAPI.getExamsReply(code[0]);
	    			int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    	    		JSONExams(page);
		    				return "Sucess";
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "";
		    		}
				
	    		
				return "";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
	
	/** Stores info about a exam */
	private class Exam{
		String type; // tipo de exame
		String courseAcronym; // codigo da cadeira
		String courseName; // nome da cadeira
		String weekDay; // [1 ... 6]
		String date; // data do exame
		String startTime; // hora de in�cio
		String endTime; // hora de fim
		String rooms; // salas
	}
	
	/**
	 * Parses a JSON String containing Exams info,
	 * Stores that info at Collection exams.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	private boolean JSONExams(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("exames")){
			Log.e("JSON", "exams found");
			
			// iterate over exams
			JSONArray jArray = jObject.getJSONArray("exames");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jExam = jArray.getJSONObject(i);
				// new Exam
				Exam exam = new Exam();
				
				if(jExam.has("tipo")) exam.type = jExam.getString("tipo");
				if(jExam.has("uc")) exam.courseAcronym = jExam.getString("uc");
				if(jExam.has("uc_nome")) exam.courseName = jExam.getString("uc_nome");
				if(jExam.has("dia")) exam.weekDay = jExam.getString("dia");
				if(jExam.has("data")) exam.date = jExam.getString("data");
				if(jExam.has("hora_inicio")) exam.startTime = jExam.getString("hora_inicio");
				if(jExam.has("hora_fim")) exam.endTime = jExam.getString("hora_fim");
				if(jExam.has("salas")) exam.rooms = jExam.getString("salas");
				
				// add exam
				exams.add(exam);
			}
			Log.e("JSON", "exams loaded");
			return true;
		}
		Log.e("JSON", "exams not found");
		return false;
		
	}
	
	
}
