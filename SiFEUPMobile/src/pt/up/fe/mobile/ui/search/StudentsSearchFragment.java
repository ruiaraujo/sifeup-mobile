package pt.up.fe.mobile.ui.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Student;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class StudentsSearchFragment extends ListFragment implements OnItemClickListener {
	
	// query is in SearchActivity, sent to here in the arguments
	ArrayList<ResultsPage> results = new ArrayList<ResultsPage>();
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
	String query;
	View loading;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        query = getArguments().get(SearchManager.QUERY).toString();
        new StudentsSearchTask().execute();

    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.list_item_loading, null);
		loading = root.findViewById(R.id.search_itemLoading);
			
    	return  super.onCreateView(inflater, container, savedInstanceState);

    } 
	
	
	/** Classe privada para a busca de dados ao servidor */
    public class StudentsSearchTask extends AsyncTask<Integer, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null  )
    		{
    			if (results.isEmpty())
        			getActivity().showDialog(BaseActivity.DIALOG_FETCHING); 
    			else
    			{
    				getListView().addFooterView(loading);
    			}
    		}
    			 
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( result.equals("Success") )
        	{
        		Log.e("Search","success");
        		
	        
		        // assumed only one page of results
		        for(Student s : results.get(results.size()-1).students ){
		            HashMap<String, String> map = new HashMap<String, String>();
		            map.put("name", s.getName());
		            map.put("course", s.getProgrammeName());
		            fillMaps.add(map);
		        }
				
				// fill in the grid_item layout
		        if ( results.size() == 1 )
		        {
			        adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_friend, 
			        		new String[] {"name", "course"}, new int[] { R.id.friend_name,  R.id.friend_course});
			        setListAdapter(adapter);
			        getListView().setOnItemClickListener(StudentsSearchFragment.this);
			        getListView().setOnScrollListener(new EndlessScrollListener(14));
			        setSelection(0);
		        }
		        else
		        	adapter.notifyDataSetChanged();
		        
    		}
			else if ( result.equals("Error") ){	
				Log.e("Search","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
			else if ( result.equals("") )
			{
				getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
				Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
				getActivity().finish();
				return;
			}
			else if ( result.equals("Empty") )
			{      
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
				Toast.makeText(getActivity(), getString(R.string.toast_search_error), Toast.LENGTH_LONG).show();
				return;
    		}
        	if ( getActivity() != null )
        	{
        		if ( !results.isEmpty() )
        			getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        		else
        			getListView().removeFooterView(loading);
        	}
        }

		@Override
		protected String doInBackground(Integer ... pages) {
			String page = "";
		  	try {
		  		if ( pages.length < 1 )
		  		{
		  			pages = new Integer[1];
		  			pages[0] = 1;
		  		}
		  		if ( query == null )
		  			return "";
		  		boolean isNumber = query.matches(".*[0-9].*");
		  		if ( isNumber )
		  			page = SifeupAPI.getStudentReply(query);
		  		else
		  			page = SifeupAPI.getStudentsSearchReply(query , pages[0]);
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
    			if(jStudent.has("cur_codigo")) student.setProgrammeCode(jStudent.getString("cur_codigo"));
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
		i.putExtra("profile", results.get(position/15).students.get(position%15).getCode());
		startActivity(i);
	}
	
	public class EndlessScrollListener implements OnScrollListener {
		 
	    private int visibleThreshold = 12;
	    private int currentPage = 1;
	    private int previousTotal = 0;
	    private boolean loading = true;
	    public EndlessScrollListener() {
	    }
	    public EndlessScrollListener(int visibleThreshold) {
	        this.visibleThreshold = visibleThreshold;
	    }
	 
	    public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        if (loading) {
	            if (totalItemCount > previousTotal) {
	                loading = false;
	                previousTotal = totalItemCount;
	                currentPage++;
	            }
	        }
	        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
	        	if ( results.size() >= 1 )
	        	{ //Stop trying to load after having loaded them all.
	        		if ( results.get(0).pageResults >= totalItemCount )
	        			return;
	        	}
	            new StudentsSearchTask().execute(results.size() * 15 + 1);
	            loading = true;
	        }
	    }
	 
	    @Override
	    public void onScrollStateChanged(AbsListView view, int scrollState) {
	    }
	}

}
