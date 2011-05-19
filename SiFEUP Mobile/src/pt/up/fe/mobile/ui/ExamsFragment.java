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


import com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

public class ExamsFragment extends Fragment {

	//ExamsTask examsTask;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Printing");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	
		String page = "";
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.print_balance, null);
    	try {
    		page = SifeupAPI.getExamsReply(
							SessionManager.getInstance().getLoginCode());
    		if(JSONError(page))
    			((TextView)root.findViewById(R.id.printing_balance)).setText("F***");
			else
				((TextView)root.findViewById(R.id.printing_balance)).setText("Sucess");
			
		} catch (JSONException e) {
			Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
        return root;
    }

    
    //Depois usa-se isto.
    /** Classe privada para a busca de dados ao servidor */
	private class ExamsTask extends AsyncTask<Void, Void, Boolean> {
		
    	/*protected void onPreExecute (){
    	 	// initiate dialog
    		showDialog(DIALOG_CONNECTING);  
    	}*/

        protected void onPostExecute(Boolean result) {
        	if ( result )
        	{
				Log.e("Exames","success");
				//Toast.makeText(ExamsFragment.this, "Yeah!", Toast.LENGTH_LONG).show();
				
			}
			else{	
				Log.e("Exames","error");
				//Toast.makeText(ExamsActivity.this, "F***", Toast.LENGTH_LONG).show();
			}
        	// remove dialog
        	//removeDialog(DIALOG_CONNECTING);
        }
        
		@Override
		protected Boolean doInBackground(Void ... theVoid ) {
			String page = null;
			
			//after a click, fetches info from server
			try {
				page = SifeupAPI.getExamsReply(
						SessionManager.getInstance().getLoginCode());
				
				Log.e("APPPPPPPP", page);
				
				
				// check error
				if(JSONError(page))
					return false;
				else
					return true; 
			}
			catch (JSONException e) {e.printStackTrace();}
			
			return false;
		}
	}
	
	/** 
	 * Prints error message on Log.e()
	 * Returns true in case of a existing error.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public static boolean JSONError(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		String erro = null;
		String erro_msg = null;
		
		if(jObject.has("erro")){
			erro = (String) jObject.get("erro");
			Log.e("APPPPPPPPerro", erro);
			if(erro.substring(0, 8).equals("Autoriza")){
				erro_msg = (String) jObject.get("erro_msg");
				Log.e("APPPPPPPPerro_msg", erro_msg);
			}
			return true;
		}
		
		return false;
	}
}
