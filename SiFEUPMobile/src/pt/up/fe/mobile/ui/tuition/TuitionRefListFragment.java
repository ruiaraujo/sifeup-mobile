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

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.RefMB;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.YearsTuition;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

public class TuitionRefListFragment extends ListFragment {

	SimpleAdapter adapter;
	YearsTuition currentYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TuitionRefsList");
        loadList();
    }
    
    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	currentYear.setSelectedReference(position);
    	startActivity(new Intent(getActivity(), TuitionRefDetailActivity.class));
    }
    
    private void loadList() 
    {		
    	String[] from = new String[] {"name", "amount", "date"};
        int[] to = new int[] { R.id.tuition_ref_name, R.id.tuition_ref_amount, R.id.tuition_ref_date};
	    //prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        ArrayList<YearsTuition> history=SessionManager.tuitionHistory.getHistory();
        currentYear=history.get(SessionManager.tuitionHistory.currentYear);
         
        for(RefMB r: currentYear.getReferences()){
            HashMap<String, String> map = new HashMap<String, String>();
        	map.put("name", r.getName());
        	map.put("amount", Double.toString(r.getAmount())+"€");
        	map.put("date", r.getStartDate().format3339(true)+" "+getString(R.string.interval_separator)+" "+r.getEndDate().format3339(true));
            fillMaps.add(map);
        }
		 
        // fill in the grid_item layout
        adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_tuition_ref, from, to);
        setListAdapter(adapter);
	}

    
}
