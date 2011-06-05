/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.fe.mobile.ui.tuition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.*;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;



/**
 * A {@link ListFragment} showing a list of sessions.
 */
public class TuitionFragment extends ListFragment {

	SimpleAdapter adapter;
	YearsTuition currentYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TuitionHistory");
        loadList();

    }
    
    private void loadList() 
    {		
    	String[] from = new String[] {"name", "date", "amount", "debt"};
        int[] to = new int[] { R.id.tuition_year_payment_name, R.id.tuition_year_payment_date, R.id.tuition_year_payment_amount, R.id.tuition_year_payment_to_pay};
	    //prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        ArrayList<YearsTuition> history=SessionManager.tuitionHistory.getHistory();
        currentYear=history.get(SessionManager.tuitionHistory.getSelected_year());
         
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
        adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_tuition_year, from, to);
        setListAdapter(adapter);
	}
}
