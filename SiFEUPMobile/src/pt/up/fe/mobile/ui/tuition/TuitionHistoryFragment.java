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

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.*;

public class TuitionHistoryFragment extends ListFragment {

	SimpleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TuitionHistory");
        loadList();
    }
    
    public void loadList()
    {
    	String[] from = new String[] {"year", "paid", "to_pay"};
        int[] to = new int[] { R.id.tuition_history_year, R.id.tuition_history_paid, R.id.tuition_history_to_pay};
	    //prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
         
        for(YearsTuition y: SessionManager.tuitionHistory.getHistory()){
            HashMap<String, String> map = new HashMap<String, String>();
            
            map.put("year", getString(R.string.lbl_year)+" "+y.getYear());
            map.put("paid", getString(R.string.lbl_paid)+": "+y.getTotal_paid()+"€");
            if(y.getTotal_in_debt()>0.0)
            	map.put("to_pay", getString(R.string.lbl_still_to_pay)+": "+y.getTotal_in_debt()+"€");
            fillMaps.add(map);
        }
		 
        // fill in the grid_item layout
        adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_tuition_history, from, to);
        setListAdapter(adapter);
        Log.i("Propinas", "List view loaded successfully");
    }
    
    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	SessionManager.tuitionHistory.setSelected_year(position);
    	startActivity(new Intent(getActivity(), TuitionActivity.class));
    }

   
}
