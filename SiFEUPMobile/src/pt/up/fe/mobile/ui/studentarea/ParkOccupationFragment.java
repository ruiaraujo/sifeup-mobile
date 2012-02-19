
package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;

import java.util.List;

import org.json.JSONException;
import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Park;
import pt.up.fe.mobile.sifeup.ParkUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;
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
public class ParkOccupationFragment extends BaseFragment implements ResponseCommand
{    
    private ListView list;
    
    /** List of Parks 1, 3, 4 */
    private List<Park> parks;
    
    private LayoutInflater mInflater;

	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		parks = new ArrayList<Park>();		
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
		ParkUtils.getParkReply("P1", this);
	
		return getParentContainer();//this is mandatory
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


	public void onError(ERROR_TYPE error) {
		if ( getActivity() == null )
	 		return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
			((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
			break;
		case NETWORK:
			Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
		default:
			//TODO: general error
			break;
		}

	}

	public void onResultReceived(Object... results) {
		if ( getActivity() == null )
			return;
		parks.add((Park) results[0]);
		switch (parks.size()) {
		case 1:
			parks.get(0).setName("P1");
			ParkUtils.getParkReply("P3", this);
			break;
		case 2:
	  		parks.get(1).setName("P3");
			ParkUtils.getParkReply("P4", this);
			break;
		case 3:
	  		parks.get(2).setName("P4");
	  		ParkAdapter adapter = new ParkAdapter(getActivity(), R.layout.list_item_park);
	         list.setAdapter(adapter);
	         list.setClickable(false);
	         showMainScreen();
			break;
		}
	}
    
}

