
package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicListHeaderIterator;
import org.json.JSONException;

import pt.up.fe.mobile.R;

import pt.up.fe.mobile.service.Park;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    
    /** List of Parks 1, 3, 4 */
    private List<Park> parks;
    
    int NUMBER_PARKS = 3;
    
    private LayoutInflater mInflater;

	public void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			parks = new ArrayList<Park>();
			
			for (int i=0; i< NUMBER_PARKS;i++)
				parks.add(new Park());
			
		    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Park Occupation");
		}
	
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		            Bundle savedInstanceState) 
		{
			super.onCreateView(inflater, container, savedInstanceState);
			mInflater = inflater;
			
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
				
				/* String[] from = new String[] {"parkName", "parkOccupation"};
		         int[] to = new int[] { R.id.park_name, R.id.park_occupation};
			         
		         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         
		             HashMap<String, String> map1 = new HashMap<String, String>();
		             map1.put("parkName", "P1");
		             map1.put("parkOccupation", parks.get(0).getPlaces());
		             fillMaps.add(map1);
		            
		             HashMap<String, String> map3 = new HashMap<String, String>();
		             map3.put("parkName", "P3");
		             map3.put("parkOccupation", parks.get(1).getPlaces());
		             fillMaps.add(map3);
		             
		             HashMap<String, String> map4 = new HashMap<String, String>();
		             map4.put("parkName", "P4");
		             map4.put("parkOccupation", parks.get(2).getPlaces());
		             fillMaps.add(map4);*/
		            
		         // fill in the grid_item layout
		         if ( getActivity() == null ) 
		        	 return;
		               
		         ParkAdapter adapter = new ParkAdapter(getActivity(), R.layout.list_item_park);
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
		  		parks.get(0).JSONParkOccupation(pages[0]);
		  		parks.get(0).setName("P1");
		  		parks.get(1).JSONParkOccupation(pages[1]);
		  		parks.get(1).setName("P3");
		  		parks.get(2).JSONParkOccupation(pages[2]);
		  		parks.get(2).setName("P4");

		    	return "Success";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
    
	public class ParkAdapter extends ArrayAdapter<Park>
	{

	    public ParkAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }    
		
        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	View root = mInflater.inflate(R.layout.list_item_park, null);

            Park park = parks.get(position);
           
            if (park != null) 
            {
                TextView tt = (TextView) root.findViewById(R.id.park_name);
                if (tt != null) {
                    tt.setText(park.getName());
                }
                
                TextView places = (TextView) root.findViewById(R.id.park_occupation);
                if (places != null) {
                    places.setText(park.getPlacesNumber());
                    
                    ImageView light = (ImageView) root.findViewById(R.id.park_light);
                    int placesNumber = park.getPlacesNumber();
                    
                    if( placesNumber == 0)
                    	light.setImageResource(R.drawable.btn_focused);
                    else if (placesNumber < 10)
                    	  	light.setImageResource(R.drawable.btn_bg_pressed);
                    	 else
                    		  light.setImageResource(R.drawable.btn_bg_selected);
                    	
                }
                
            }
            return root;
        }
    }
    
}
