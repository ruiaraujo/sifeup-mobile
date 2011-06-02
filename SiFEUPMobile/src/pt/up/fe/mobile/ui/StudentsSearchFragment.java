package pt.up.fe.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Student;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StudentsSearchFragment extends ListFragment implements OnItemClickListener {
	
	// query is in SearchActivity, sent to here in the arguments
	ArrayList<ResultsPage> results = new ArrayList<ResultsPage>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        new StudentsSearchTask().execute(getArguments().get(SearchManager.QUERY).toString());

    }
	
	/** Classe privada para a busca de dados ao servidor */
    private class StudentsSearchTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( results.isEmpty() )
    		{      
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
				Toast.makeText(getActivity(), getString(R.string.toast_search_error), Toast.LENGTH_LONG).show();
				return;
    		}
        	if ( !result.equals("") )
        	{
        		Log.e("Search","success");
        		
				String[] from = new String[] {"name"};
		        int[] to = new int[] { R.id.friend_name};
			    // prepare the list of all records
		        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        
		        // assumed only one page of results
		        for(Student s : results.get(0).students ){
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("name", s.getName());
		            fillMaps.add(map);
		        }
				
				// fill in the grid_item layout
		        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_friend, from, to);
		        setListAdapter(adapter);
		        getListView().setOnItemClickListener(StudentsSearchFragment.this);
		         setSelection(0);
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
		protected String doInBackground(String ... code) {
			String page = "";
		  	try {
		  		if ( code.length < 1 )
		  			return "";
	    		page = SifeupAPI.getStudentsSearchReply(code[0]);
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
				
	    		JSONStudentsSearch(page);
	    		
				return page;
				
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
     * Holds a Search page
     * With pageResults number
     * of students
     *
     */
    private class ResultsPage{
    	private int searchSize; // "total" : 583
    	private int page; // "primeiro" : 1
    	private int pageResults; // "tam_pagina" : 15
    	private List<Student> students = new ArrayList<Student>();
    }
    
    /**
	 * Parses a JSON String containing Student Search Info,
	 * Stores that results page at Collection results.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
    private boolean JSONStudentsSearch(String page) throws JSONException {
    	JSONObject jObject = new JSONObject(page);
		
    	
    	if(jObject.has("alunos")){
    		Log.e("JSON", "founded search");
    		
    		// new results page
    		ResultsPage resultsPage = new ResultsPage();
    		if(jObject.has("total")) resultsPage.searchSize = jObject.getInt("total");
    		if(jObject.has("primeiro")) resultsPage.page = jObject.getInt("primeiro");
    		if(jObject.has("tam_pagina")) resultsPage.pageResults = jObject.getInt("tam_pagina");
    		
    		JSONArray jArray = jObject.getJSONArray("alunos");
    		
    		// iterate over jArray
    		for(int i = 0; i < jArray.length(); i++){
    			// new JSONObject
    			JSONObject jStudent = jArray.getJSONObject(i);
    			// new Block
    			Student student = new Student();
    			
    			if(jStudent.has("codigo")) student.setCode(""+jStudent.getInt("codigo"));
    			if(jStudent.has("nome")) student.setName(jStudent.getString("nome"));
    			if(jStudent.has("cur_codigo")) student.setCourseCode(jStudent.getString("cur_codigo"));
    			if(jStudent.has("cur_nome")) student.setCourseName(jStudent.getString("cur_nome"));
    			if(jStudent.has("cur_name")) student.setCourseNameEn(jStudent.getString("nome"));
    			
    			// add student to the page results
    			resultsPage.students.add(student);
    		}
    		
    		// add page to global results
    		results.add(resultsPage);
    		
    		Log.e("JSON", "loaded search");
    		return true;
    	}
    	Log.e("JSON", "search not found");
    	return false;
	}
    
	
	/**
	 * Parses a JSON String containing Student info,
	 * Stores that info at Collection me.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
    /*
	public boolean JSONStudent(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("codigo")){
			Log.e("JSON", "founded student");
			Student student = new Student();
			if(jObject.has("codigo")) student.setCode(jObject.getString("codigo"));
			if(jObject.has("nome")) student.setName(jObject.getString("nome"));
			if(jObject.has("curso_sigla")) student.setCourseAcronym(jObject.getString("curso_sigla"));
			if(jObject.has("curso_nome")) student.setCourseName(jObject.getString("curso_nome"));
			if(jObject.has("ano_lect_matricula")) student.setRegistrationYear(jObject.getString("ano_lect_matricula"));
			if(jObject.has("estado")) student.setState(jObject.getString("estado"));
			if(jObject.has("ano_curricular")) student.setAcademicYear(jObject.getString("ano_curricular"));
			if(jObject.has("email")) student.setEmail(jObject.getString("email"));
			
			Log.e("JSON", "loaded student");
			
			results.add(student);
			return true;
		}
		Log.e("JSON", "student not found");
		return false;
	}
	*/
    
    
	@Override
	public void onItemClick(AdapterView<?> adapter, View list, int position, long id ) {
		if ( getActivity() == null )
			return;
		Intent i = new Intent(getActivity() , ProfileActivity.class);
		
		// assumed only one page of results
		i.putExtra("profile", results.get(0).students.get(position));
		startActivity(i);
	}
}
