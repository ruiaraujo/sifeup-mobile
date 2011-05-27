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

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.YearsTuition;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class TuitionMenuFragment extends Fragment {
	
	SimpleAdapter adapter;

    public void fireTrackerEvent(String label) {
        AnalyticsUtils.getInstance(getActivity()).trackEvent(
                "Tuition Menu", "Click", label, 0);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TuitionMenu");
        new TuitionTask().execute();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tuition_menu, null);

        // Attach event handlers
        root.findViewById(R.id.tuition_menu_btn_history).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("TuitionHistory");
                startActivity(new Intent(getActivity(), TuitionHistoryActivity.class));
                    
            }
            
        });
        
     
        root.findViewById(R.id.tuition_menu_btn_refs).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("MB Refs");
                startActivity(new Intent(getActivity(), TuitionRefListActivity.class));
                    
            }
            
        });

        return root;
    }
	
	 /** Classe privada para a busca de dados ao servidor */
    private class TuitionTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null && !SessionManager.tuitionHistory.isLoaded()) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.i("Propinas","Propinas Loaded successfully");
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
			  		if(!SessionManager.tuitionHistory.isLoaded())
			  		{
			  			String page = "";
		    			page = SifeupAPI.getTuitionReply(
									SessionManager.getInstance().getLoginCode());
			    		if(SifeupAPI.JSONError(page))
			    		{
				    		 return "";
			    		}
						
			    		JSONObject jHistory=new JSONObject(page);
			    		
			    		if(SessionManager.tuitionHistory.load(jHistory))
			    			return "Sucess";
			    		else
			    			return "";			    		
			  		}
			  		else
			  			return "Sucess";
				
				} catch (JSONException e) {
					if ( getActivity() != null ) 
						Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
					e.printStackTrace();
					return "";
				}
		}
    }	
}
