
package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.fe.mobile.R;

import pt.up.fe.mobile.service.Park;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


/**
 * Park Occupation Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ParkOccupationFragment extends BaseFragment 
{    
    private ListView list;
    
    private Park park1;
    private Park park3;
    private Park park4;
    

	public void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			park1 = new Park();
			park3 = new Park();
			park4 = new Park();
		    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Park Occupation");
		}
	
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		            Bundle savedInstanceState) 
		{
			super.onCreateView(inflater, container, savedInstanceState);
			
			View root = inflater.inflate(R.layout.generic_list, getParentContainer(), true);
			list = (ListView) root.findViewById(R.id.generic_list);
			
			new ParkOccupationTask().execute();
		
			return getParentContainer();//this is mandatory
		}
	

    private class ParkOccupationTask extends AsyncTask<String, Void, String> 
    {

    	protected void onPreExecute (){ 
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) 
        {
        	if ( getActivity() == null )
				 return;
        	
        	if ( result.equals("Success") )
        	{
				Log.e("Park Occupation","success");
				
				 String[] from = new String[] {"parkName", "parkOccupation"};
		         int[] to = new int[] { R.id.park_name, R.id.park_occupation};
			         
		         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         
	
		             HashMap<String, String> map1 = new HashMap<String, String>();
		             map1.put("parkName", "P1");
		             map1.put("parkOccupation", park1.getPlaces());
		             fillMaps.add(map1);
		            
		            HashMap<String, String> map3 = new HashMap<String, String>();
		             map3.put("parkName", "P3");
		             map3.put("parkOccupation", park3.getPlaces());
		             fillMaps.add(map3);
		             
		             HashMap<String, String> map4 = new HashMap<String, String>();
		             map4.put("parkName", "P4");
		             map4.put("parkOccupation", park4.getPlaces());
		             fillMaps.add(map4);
		            
		         // fill in the grid_item layout
		         if ( getActivity() == null ) 
		        	 return;
		         
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_park, from, to);
		         list.setAdapter(adapter);
		         list.setClickable(false);
		         showMainScreen();
		         Log.e("JSON", "Parks occupation visual list loaded");
		         return;

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
		protected String doInBackground(String ... code) 
		{
			String page = "";
			String pages [] = new String[3];
		  	try {
	  			for ( int i = 0 ; i < 4 ; ++i )
	  			{
	  				if ( i+1 == 2 )
	  					continue;
	  				page = SifeupAPI.getParkOccupationReply("P" + (i+1));
	  				int error = SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				pages[i==0?i:i-1] = page;
		    				break;
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "Error";// When not autenticathed, it returns a null page.
		    		}
	  			}
		  		park1.JSONParkOccupation(pages[0]);
		  		park3.JSONParkOccupation(pages[1]);
		  		park4.JSONParkOccupation(pages[2]);

		    	return "Success";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
}
