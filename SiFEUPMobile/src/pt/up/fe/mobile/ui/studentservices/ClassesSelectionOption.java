package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Classes Selection Option
 * @author Ângela Igreja
 *
 */
public class ClassesSelectionOption extends Fragment {

	// These can be static as they are common between different options
	private static String [] subjects;
	private static String [] classes;
	
    private int [] selectedClasses;
    
    private LayoutInflater mInflater;
    
	final static private String STATE = "pt.up.fe.mobile.ui.studentservices.STATE";

	public static ClassesSelectionOption getInstance( String [] subjects , String [] classes ){
		ClassesSelectionOption f = new ClassesSelectionOption();
		// we have to init this fragment in case the user 
		// wants to submit its choice without visiting every option.
		f.init(subjects, classes);
		return f;
	}
	
	/**
	 * Init parameters subjects and classes
	 * @param s
	 * @param c
	 */
	public void init( String [] s , String [] c ) {
		subjects= s;
		classes = c;
		selectedClasses = new int[subjects.length];
	}
	
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	{
	    mInflater = inflater;
	 	View root = inflater.inflate(R.layout.classes_selection_option ,null);
	    ListView list = (ListView) root.findViewById(R.id.classes_selection_list);
	    list.setAdapter(new ClassesOptionAdapter());
	   // 
	    return root;
	}

	/**
	 * Get Status of Classes Selection	
	 * @return
	 */
	public String getChoiceStatus(){
		StringBuilder st = new StringBuilder();
		for ( int i = 0 ; i < subjects.length ; ++i )
		{
			st.append(subjects[i]);
			st.append(": ");
			st.append(classes[selectedClasses[i]]);
			st.append(" , ");
		}
		return st.toString();
	}
	
	//We save here the current selection state
	@Override
	public void onSaveInstanceState (Bundle outState){
		if ( outState == null )
			return;
		super.onSaveInstanceState(outState);
		outState.putIntArray(STATE,selectedClasses);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    if ( savedInstanceState == null )
	    	return;
	    int [] tmp = savedInstanceState.getIntArray(STATE);
	    if ( tmp != null )
	    	selectedClasses = tmp;
	}
        
    /**
     * This class create a Classes Option Adapter.
     * This adapter creates the views for the main list.
     *
     */
    private class ClassesOptionAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return subjects.length;
		}

		@Override
		public Object getItem(int position) {
			return subjects[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int positionList, View convertView, ViewGroup parent) {
			LinearLayout root = (LinearLayout) mInflater.inflate(R.layout.list_item_classes_option, null);
			((TextView)root.findViewById(R.id.classes_option_text)).setText(subjects[positionList]);
			Spinner spinner = (Spinner) root.findViewById(R.id.classes_option_spinner);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_selectable_list_item, classes);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				//the id is the 
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					selectedClasses[positionList] = pos;	
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
			spinner.setAdapter(adapter);
			//restoring previous position
			spinner.setSelection(selectedClasses[positionList]);
			return root;
		}

    }

}
