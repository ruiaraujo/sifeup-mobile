package pt.up.fe.mobile.ui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.commonsware.cwac.endless.EndlessAdapter;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Profile;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Student;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This interface is responsible for fetching the results of 
 * research to the server and shows them a list. 
 * When loading a list item launches the activity ProfileActivity.
 * 
 * @author Ângela Igreja
 *
 */
public class StudentsSearchFragment extends BaseFragment implements OnItemClickListener {
	
	// query is in SearchActivity, sent to here in the arguments
	private ArrayList<ResultsPage> results = new ArrayList<ResultsPage>();
	private List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    private ListAdapter adapter;
    private String query;
    private ListView list;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        query = getArguments().get(SearchManager.QUERY).toString();

    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { 
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list, getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
        new StudentsSearchTask().execute();
    	return getParentContainer();
    	
    } 
	
	private boolean hasMoreResults(){
		if ( results.isEmpty() )
			return true;
		if ( totalItemLoaded() >= results.get(0).searchSize )
			return false;
		return true;
	}
	
	/** Classe privada para a busca de dados ao servidor */
    public class StudentsSearchTask extends AsyncTask<Integer, Void, String> {

    	protected void onPreExecute (){
			showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( result.equals("Success") )
        	{
        		Log.e("Search","success");
        		
	        /*
		        // assumed only one page of results
		        for(Student s : results.get(results.size()-1).students ){
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("name", s.getName());
		            map.put("course", s.getProgrammeName());
		            fillMaps.add(map);
		        }
		*/		
				// fill in the grid_item layout
		        if ( hasMoreResults() )
		        {
			      //  adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_friend, 
			        //		new String[] {"name", "course"}, new int[] { R.id.friend_name,  R.id.friend_course});
		        	adapter = new EndlessSearchAdapter(getActivity(), 
		        			 new SearchCustomAdapter(getActivity(), R.layout.list_item_friend, new Student[0]),
		        			 R.layout.list_item_loading);
		        }
		        else
		        {
			        adapter = new SearchCustomAdapter(getActivity(), R.layout.list_item_friend, new Student[0]);

		        }
		        list.setAdapter(adapter);
		        list.setOnItemClickListener(StudentsSearchFragment.this);
		        list.setSelection(0);
		        showMainScreen();
		        
    		}
			else if ( result.equals("Error") ){	
				Log.e("Search","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
			else if ( result.equals("") )
			{
				Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
				getActivity().finish();
				return;
			}
			else if ( result.equals("Empty") )
			{      
				Toast.makeText(getActivity(), getString(R.string.toast_search_error), Toast.LENGTH_LONG).show();
				return;
    		}
        }

		@Override
		protected String doInBackground(Integer ... pages) {
			String page = "";
		  	try {
		  		if ( query == null )
		  			return "";
		  		boolean isNumber = query.matches(".*[0-9].*");
		  		if ( isNumber )
		  			page = SifeupAPI.getStudentReply(query);
		  		else
		  			page = SifeupAPI.getStudentsSearchReply(query , 1);
	    		int error =	SifeupAPI.JSONError(page);
	    		switch (error)
	    		{
	    			case SifeupAPI.Errors.NO_AUTH:
	    				return "Error";
	    			case SifeupAPI.Errors.NO_ERROR:
	    	    		JSONStudentsSearch(page);
	    	    		if ( results.isEmpty())
	    	    			return "Empty";
	    	    		else
	    	    			return "Success";
	    			case SifeupAPI.Errors.NULL_PAGE:
	    				return "";
	    		}
	    		
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
		
    	if (  query.matches(".*[0-9].*") )
    	{
    		ResultsPage resultsPage = new ResultsPage();
    		resultsPage.searchSize = resultsPage.page = resultsPage.pageResults = 1;
    		Student student = new Student();
    		if(jObject.has("codigo"))
    			student.setCode(jObject.getString("codigo"));
			if(jObject.has("nome"))
				student.setName(jObject.getString("nome"));
			if(jObject.has("curso_sigla"))
				student.setProgrammeAcronym(jObject.getString("curso_sigla"));
			if(jObject.has("curso_nome"))
				student.setProgrammeName(jObject.getString("curso_nome"));

			// add student to the page results
			resultsPage.students.add(student);
			// add page to global results
    		results.add(resultsPage);
    		Log.e("JSON", "loaded search");
    		return true;
    	}
    	
    	if(jObject.has("alunos")){
    		Log.e("JSON", "founded search");
    		
    		// new results page
    		ResultsPage resultsPage = new ResultsPage();
    		if(jObject.has("total")) resultsPage.searchSize = jObject.getInt("total");
    		if(jObject.has("primeiro")) resultsPage.page = jObject.getInt("primeiro");
    		if(jObject.has("tam_pagina")) resultsPage.pageResults = jObject.getInt("tam_pagina");
    		if ( resultsPage.searchSize - resultsPage.page < 15 )
    			resultsPage.pageResults = resultsPage.searchSize - resultsPage.page;
    		JSONArray jArray = jObject.getJSONArray("alunos");
    		
    		// iterate over jArray	
    		for(int i = 0; i < jArray.length(); i++){
    			// new JSONObject
    			JSONObject jStudent = jArray.getJSONObject(i);
    			// new Block
    			Student student = new Student();
    			
    			if(jStudent.has("codigo")) student.setCode(""+jStudent.getString("codigo"));
    			if(jStudent.has("nome")) student.setName(jStudent.getString("nome"));
    			if(jStudent.has("cur_sigla")) student.setProgrammeCode(jStudent.getString("cur_sigla"));
    			if(jStudent.has("cur_nome")) student.setProgrammeName(jStudent.getString("cur_nome"));
    			if(jStudent.has("cur_name")) student.setProgrammeNameEn(jStudent.getString("nome"));
    			
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
    
    
	@Override
	public void onItemClick(AdapterView<?> adapter, View list, int position, long id ) {
		if ( getActivity() == null )
			return;
		Intent i = new Intent(getActivity() , ProfileActivity.class);
		// assumed only one page of results
		Profile profile = results.get(position/15).students.get(position%15);
		i.putExtra(Intent.EXTRA_TITLE,profile.getName() );
		i.putExtra(ProfileActivity.PROFILE_TYPE,ProfileActivity.PROFILE_STUDENT);
		i.putExtra(ProfileActivity.PROFILE_CODE, 
				profile.getCode());
		startActivity(i);
	}

	
	public class EndlessSearchAdapter extends EndlessAdapter{

		public EndlessSearchAdapter(Context context, ListAdapter wrapped,
				int pendingResource) {
			super(context, wrapped, pendingResource);
		}
		@Override
		protected boolean cacheInBackground() throws Exception {
			String page = SifeupAPI.getStudentsSearchReply(query ,results.size() * 15 + 1 );
    		int error =	SifeupAPI.JSONError(page);
    		switch (error)
    		{
    			case SifeupAPI.Errors.NO_AUTH:
    				return false;
    			case SifeupAPI.Errors.NO_ERROR:
    	    		JSONStudentsSearch(page);
    	    		if ( results.isEmpty() || !hasMoreResults() )
    	    			return false;
    	    		else
    	    			return true;
    			case SifeupAPI.Errors.NULL_PAGE:
    				return false;
    		}
			return true;
		}
		
		@Override
		protected void appendCachedData() {
			SearchCustomAdapter adapter = (SearchCustomAdapter)getWrappedAdapter();
			adapter.notifyDataSetChanged();
		}
	}

	public class SearchCustomAdapter extends ArrayAdapter<Student> {

		public SearchCustomAdapter(Context context, int textViewResourceId,
		  Student[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
		
			if(row==null){
				LayoutInflater inflater=getActivity().getLayoutInflater();
				row=inflater.inflate(R.layout.list_item_friend, parent, false);
			} 
			TextView name = (TextView) row.findViewById(R.id.friend_name);
			name.setText(results.get(position/15).students.get(position%15).getName());
			TextView course =  (TextView) row.findViewById(R.id.friend_course);
			course.setText(results.get(position/15).students.get(position%15).getProgrammeName());
			return row;
		}
		
		public int getCount(){
			return totalItemLoaded();
		}
	}
	
	private int totalItemLoaded(){
		int total =0;
		for ( ResultsPage result : results )
			total += result.students.size();
		return total;
	}

}
