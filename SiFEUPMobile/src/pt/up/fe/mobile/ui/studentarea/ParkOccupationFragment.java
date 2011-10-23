
package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;

import java.util.List;

import org.json.JSONException;
import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Park;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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
		list.setClickable(false);
		list.setFocusable(false);
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
		    				return "Error";// When not authenticated, it returns a null page.
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
        	View root = mInflater.inflate(R.layout.list_item_park, list, false);
            TextView tt = (TextView) root.findViewById(R.id.park_name);
            ImageView light = (ImageView) root.findViewById(R.id.park_light);
            TextView places = (TextView) root.findViewById(R.id.park_occupation);

            Park park = parks.get(position);
           

            tt.setText(park.getName());
            places.setText(Integer.toString(park.getPlacesNumber()));
                    
            int placesNumber = park.getPlacesNumber();
            
            if( placesNumber == 0)
            	light.setImageResource(R.drawable.red_light);
            else if (placesNumber < 10)
        	  	light.setImageResource(R.drawable.yellow_light);
        	 else
        		light.setImageResource(R.drawable.green_light);
                 
            return root;
        }
        
        public int getCount(){
        	return parks.size();
        }
    }
    
}

