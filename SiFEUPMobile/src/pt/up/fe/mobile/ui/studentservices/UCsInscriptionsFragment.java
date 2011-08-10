package pt.up.fe.mobile.ui.studentservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;


public class UCsInscriptionsFragment extends Fragment
{
	ListView ucList;
	 @Override
    public void onCreate(Bundle savedInstanceState) 
 	{
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/UCsInscriptions");
    }
	 
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.uc_inscription, null);
		ucList = (ListView) root.findViewById(R.id.uc_listview);
		
		//TODO: proof of concept
		 String[] from = new String[] {"uc"};
         int[] to = new int[] { R.id.uc_check };
	         // prepare the list of all records
         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
         
         HashMap<String, String> map = new HashMap<String, String>();
         map.put("uc", "COMP" );
         fillMaps.add(map);
         map = new HashMap<String, String>();
         map.put("uc", "IELE" );
         fillMaps.add(map);
         map = new HashMap<String, String>();
         map.put("uc", "EIND" );
         fillMaps.add(map);
         // fill in the grid_item layout
         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_ucs_inscriptions, from, to);
         ucList.setAdapter(adapter);
		return  root;
	
	 } 
	    
	      
}
