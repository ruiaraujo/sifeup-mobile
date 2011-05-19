<<<<<<< .mine
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class PrintFragment extends Fragment {

    private String saldo;
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

    	
		String page = "";
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.print_balance, null);
    	try {
    		page = SifeupAPI.getPrintingReply(
							SessionManager.getInstance().getLoginCode());
    		JSONObject jObject = new JSONObject(page);			
			saldo="Saldo "+jObject.optDouble("saldo")+" €";
			((TextView)root.findViewById(R.id.printing_balance)).setText(saldo);
			
		} catch (JSONException e) {
			Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
        return root;
    }

}