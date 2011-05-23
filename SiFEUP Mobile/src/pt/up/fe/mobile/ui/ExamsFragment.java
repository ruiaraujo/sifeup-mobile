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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

public class ExamsFragment extends Fragment {

    private TextView display;
    /** Stores all exams from Student */
	private ArrayList<Exam> exams = new ArrayList<Exam>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.exams, null);
		display = ((TextView)root.findViewById(R.id.exams_test));
		new ExamsTask().execute();
        return root;
    }

    /** Classe privada para a busca de dados ao servidor */
    private class ExamsTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.e("Login","success");
				display.setText(result);
			}
			else{	
				Log.e("Login","error");
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
	    			page = SifeupAPI.getExamsReply(
								SessionManager.getInstance().getLoginCode());
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
				
	    		JSONExams(page);
	    		
				return "Sucess";
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
	
	/** Stores info about a exam */
	private class Exam{
		String type; // tipo de exame
		String courseAcronym; // codigo da cadeira
		String courseName; // nome da cadeira
		String weekDay; // [1 ... 6]
		String date; // data do exame
		String startTime; // hora de início
		String endTime; // hora de fim
		String rooms; // salas
	}
	
	/**
	 * Parses a JSON String containing Exams info,
	 * Stores that info at Collection exams.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	private boolean JSONExams(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("exames")){
			Log.e("JSON", "exams found");
			
			// iterate over exams
			JSONArray jArray = jObject.getJSONArray("exames");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jExam = jArray.getJSONObject(i);
				// new Exam
				Exam exam = new Exam();
				
				if(jExam.has("tipo")) exam.type = jExam.getString("tipo");
				if(jExam.has("uc")) exam.courseAcronym = jExam.getString("uc");
				if(jExam.has("uc_nome")) exam.courseName = jExam.getString("uc_nome");
				if(jExam.has("dia")) exam.weekDay = jExam.getString("dia");
				if(jExam.has("Data")) exam.date = jExam.getString("Data");
				if(jExam.has("hora_inicio")) exam.startTime = jExam.getString("hora_inicio");
				if(jExam.has("hora_fim")) exam.endTime = jExam.getString("hora_fim");
				if(jExam.has("salas")) exam.rooms = jExam.getString("salas");
				
				// add exam
				exams.add(exam);
			}
			Log.e("JSON", "exams loaded");
			return true;
		}
		Log.e("JSON", "exams not found");
		return false;
		
	}
	
	
}
