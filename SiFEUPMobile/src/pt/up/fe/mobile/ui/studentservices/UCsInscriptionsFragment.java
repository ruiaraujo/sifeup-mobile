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
import android.widget.SimpleAdapter;
import android.widget.Toast;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;


public class UCsInscriptionsFragment extends Fragment
{
	 @Override
    public void onCreate(Bundle savedInstanceState) 
 	{
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/UCsInscriptions");
    }
	 
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	 {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.list_ucs_inscriptions, null);
		
		return  root;
	
	  } 
	    
	      
}
