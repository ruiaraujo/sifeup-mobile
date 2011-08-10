package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import pt.up.fe.mobile.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class StudentAreaFragment extends ListFragment{
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Student Area");
        String[] from = new String[] {"title"};
        int[] to = new int[] { R.id.list_menu_title};
	         
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> schedule = new HashMap<String, String>();
        schedule.put(from[0],getString(R.string.btn_schedule));
        fillMaps.add(schedule);
        
        HashMap<String, String> exams = new HashMap<String, String>();
        exams.put(from[0],getString(R.string.btn_exams));
        fillMaps.add(exams);
        
        HashMap<String, String> subjects = new HashMap<String, String>();
        subjects.put(from[0],getString(R.string.btn_subjects));
        fillMaps.add(subjects);
        
        HashMap<String, String> food = new HashMap<String, String>();
        food.put(from[0],getString(R.string.btn_lunch_menu));
        fillMaps.add(food);
        
        HashMap<String, String> academic = new HashMap<String, String>();
        academic.put(from[0],getString(R.string.btn_academicpath));
        fillMaps.add(academic);
        
        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
       		 							R.layout.list_item_menu, from, to);
        setListAdapter(adapter);
        

    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	registerForContextMenu(getActivity().findViewById(android.R.id.list));
    }
    

    /** {@inheritDoc} */
    public void onListItemClick(ListView l, View v, int position, long id) 
    {
    	
    	switch( position )
    	{
	    	case 0:
	    		 startActivity(new Intent(getActivity(),ScheduleActivity.class));
	    		 return;
	    	case 1:
		   		 startActivity(new Intent(getActivity(),ExamsActivity.class));
				 return;
	   		case 2:
		   		 startActivity(new Intent(getActivity(),SubjectsActivity.class));
				 return;
	   		case 3:
		   		 startActivity(new Intent(getActivity(),LunchMenuActivity.class));
				 return;
	   		case 4:
		   		 startActivity(new Intent(getActivity(), AcademicPathActivity.class));
				 return;
    	}
    }
    
}
