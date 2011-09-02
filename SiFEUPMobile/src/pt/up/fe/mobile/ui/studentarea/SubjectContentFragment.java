package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.SubjectContent;
import pt.up.fe.mobile.service.Subject.Book;
import pt.up.fe.mobile.service.SubjectContent.Folder;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SubjectContentFragment extends BaseFragment {
	
	private String code;
	private String year;
	private String period;
	
	private ListView list;
	
    SubjectContent subjectContent = new SubjectContent();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        code = args.get(SubjectDescriptionActivity.SUBJECT_CODE).toString();
		year = args.get(SubjectDescriptionActivity.SUBJECT_YEAR).toString();
		period = args.get(SubjectDescriptionActivity.SUBJECT_PERIOD).toString();
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Subject Content");
        setHasOptionsMenu(true);
    }
	
	 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View root = inflater.inflate(R.layout.generic_list, getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		
        new SubjectContentTask().execute();
		
        return getParentContainer();
	}


    /** 
     * Private class to fetch data to server
     * 
     * @author Ângela Igreja
     * 
     */
    private class SubjectContentTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

		protected void onPostExecute(String result) 
		{
         	if ( result.equals("Success") )
         	{
 				Log.e("Login","success");
 				
				 String[] from = new String[] {"name"};
				 int[] to = new int[] {R.id.folder_name};
				 
				 // prepare the list of all records
				 List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				 
				 for(Folder f : subjectContent.getFolders())
				 {
				     HashMap<String, String> map = new HashMap<String, String>();
				     map.put("name", f.getName());
				     fillMaps.add(map);
				 }
				 
				 // fill in the grid_item layout
				 if ( getActivity() == null ) 
					 return;
				 SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_folder, from, to);
				 list.setAdapter(adapter);
				 list.setClickable(true);
			
			        list.setOnItemClickListener(new OnItemClickListener() 
			        {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
										
						}
					});
				
				 showMainScreen();
				 
				 Log.e("JSON", "subject conten visual list loaded");

     		}
 			else if ( result.equals("Error") ){	
 				Log.e("Login","error");
 				if ( getActivity() != null ) 
 				{
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
 					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
 					getActivity().finish();
 					return;
 				}
 			}
         }

		@Override
		protected String doInBackground(Void ... theVoid) 
		{
			String page = "";
		  	try {
	    			page = SifeupAPI.getSubjectContentReply(code,year,period);
	    			int error =	SifeupAPI.JSONError(page);
		    		
	    			switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				if (subjectContent.JSONSubjectContent(page) )
		    				{
		    					return "Success";
		    				}
		    				else
		    					return "";
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
    
}
