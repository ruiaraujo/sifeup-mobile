package pt.up.fe.mobile.ui.studentservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.studentarea.AcademicPathActivity;
import pt.up.fe.mobile.ui.studentarea.ExamsActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.SubjectsActivity;
import pt.up.fe.mobile.ui.tuition.TuitionActivity;
import pt.up.fe.mobile.ui.tuition.TuitionMenuActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class StudentServicesFragment extends ListFragment
{
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] from = new String[] {"title"};
        int[] to = new int[] { R.id.list_menu_title};
	         
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> printing = new HashMap<String, String>();
        printing.put(from[0],getString(R.string.btn_printing));
        fillMaps.add(printing);
        
        HashMap<String, String> tuition = new HashMap<String, String>();
        tuition.put(from[0],getString(R.string.btn_tuition));
        fillMaps.add(tuition);
        
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
    public void onListItemClick(ListView l, View v, int position, long id) {
    	//TODO
    	switch( position ){
    	case 0:
    		 startActivity(new Intent(getActivity(),PrintActivity.class));
    		 return;
    	case 1:
	   		 startActivity(new Intent(getActivity(),TuitionMenuActivity.class));
			 return;
   
    	}
    }
    

}
