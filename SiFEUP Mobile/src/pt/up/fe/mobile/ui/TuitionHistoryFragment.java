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

package pt.up.fe.mobile.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.*;

public class TuitionHistoryFragment extends ListFragment {

   TuitionHistory history=new TuitionHistory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TuitionHistory");
        new TuitionTask().execute();

    }

    /** Classe privada para a busca de dados ao servidor */
    private class TuitionTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null && !history.isLoaded()) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.i("Propinas","Loaded successfully");
				
				String[] from = new String[] {"year", "paid", "to_pay"};
		        int[] to = new int[] { R.id.tuition_history_year, R.id.tuition_history_paid, R.id.tuition_history_to_pay};
			    //prepare the list of all records
		        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         
		        for(YearsTuition y: history.getHistory()){
		            HashMap<String, String> map = new HashMap<String, String>();
		            //String tipo = "( " +  (e.type.contains("Mini teste")?"M":"E") + " ) ";
		            map.put("year",y.getYear());
		            map.put("paid", y.getTotal_paid()+"€");
		            if(y.getTotal_in_debt()>0.0)
		            	map.put("to_pay", "-"+y.getTotal_in_debt()+"€");
		            fillMaps.add(map);
		        }
				 
		        // fill in the grid_item layout
		        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_tuition_history, from, to);
		        setListAdapter(adapter);
		        Log.i("Propinas", "List view loaded successfully");
    		}
			else
			{	
				Log.e("Propinas","Data not loaded or parsed correctly");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        	if ( getActivity() != null) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			try {
			  		if(!history.isLoaded())
			  		{
			  			String page = "";
		    			page = SifeupAPI.getTuitionReply(
									SessionManager.getInstance().getLoginCode());
			    		if(SifeupAPI.JSONError(page))
			    		{
				    		 return "";
			    		}
						
			    		JSONObject jHistory=new JSONObject(page);
			    		
			    		if(history.load(jHistory))
			    			return "Sucess";
			    		else
			    			return "";			    		
			  		}
			  		return "";
				
				} catch (JSONException e) {
					if ( getActivity() != null ) 
						Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
					e.printStackTrace();
					return "";
				}
		}
    }	
}
