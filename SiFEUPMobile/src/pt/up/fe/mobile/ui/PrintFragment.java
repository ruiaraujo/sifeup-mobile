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


import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

public class PrintFragment extends Fragment {

    private String saldo;
    private TextView display;
    private TextView desc;
    public String getSaldo() {
		return saldo;
	}

	public  void setSaldo(String saldo) {
		this.saldo = saldo;
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Printing");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	new PrintTask().execute();
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.print_balance, null);
    	display = ((TextView)root.findViewById(R.id.print_balance));
    	desc = ((TextView)root.findViewById(R.id.print_desc));
    	root.findViewById(R.id.print_generate_reference).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "I do nothing yet", Toast.LENGTH_LONG).show();
			}
		});
    	return root;

    }
    private class PrintTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String saldo) {
        	if ( getActivity() == null )
        		return;
        	if ( !saldo.equals("") )
        	{
				Log.e("Login","success");
				display.setText(getString(R.string.print_lbl) + ": "+ saldo+" €");
				PrintFragment.this.saldo = saldo;
				long pagesA4Black =  Math.round(Double.parseDouble(saldo) / 0.03f);
				if ( pagesA4Black > 0 )
					desc.setText(getString(R.string.print_can_print_a4_black, Long.toString(pagesA4Black)));
								}
			else{	
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
			try {
	    			page = SifeupAPI.getPrintingReply(
								SessionManager.getInstance().getLoginCode());
	    		
    			if(	SifeupAPI.JSONError(page))
    				return "";

	    		JSONObject jObject = new JSONObject(page);			
				return jObject.optString("saldo");
				
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			return "";
		}
    }

}