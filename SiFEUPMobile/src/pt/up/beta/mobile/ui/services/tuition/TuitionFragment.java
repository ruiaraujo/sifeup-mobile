package pt.up.beta.mobile.ui.services.tuition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.Payment;
import pt.up.beta.mobile.datatypes.YearsTuition;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;

/**
 * A {@link ListFragment} showing a list of sessions.
 */
public class TuitionFragment extends ListFragment {

	public static final String CURRENT_YEAR = "current_year";
	private YearsTuition currentYear;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentYear = getArguments().getParcelable(CURRENT_YEAR);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        String[] from = new String[] {"name", "date", "amount", "debt"};
        int[] to = new int[] { R.id.tuition_year_payment_name, R.id.tuition_year_payment_date, R.id.tuition_year_payment_amount, R.id.tuition_year_payment_to_pay};
	    //prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();         
        for(Payment p: currentYear.getPayments()){
            HashMap<String, String> map = new HashMap<String, String>();
        	map.put("name", p.getName());
        	if(p.getDueDate()!=null)
        		map.put("date", p.getDueDate().format3339(true));
        	map.put("amount", Double.toString(p.getAmount())+"€");
        	if(p.getAmountDebt()>0)
        		map.put("debt", getString(R.string.lbl_still_to_pay)+": "+Double.toString(p.getAmount())+"€");
            fillMaps.add(map);
        }
		 
        // fill in the grid_item layout
        setListAdapter( new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_tuition_year, from, to));
		getListView().setClickable(false);
		getListView().setFocusable(false);
    }
  
}
