package pt.up.fe.mobile.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

public class TuitionHistory {

	ArrayList<YearsTuition> history;
	boolean loaded;

	public TuitionHistory() 
	{
		history=new ArrayList<YearsTuition>();
		loaded=false;		
	}
	
	public boolean load(JSONObject historyInfo)
	{
		JSONArray jHistory;
		try {
			jHistory = historyInfo.getJSONArray("pagamentos");
		} catch (JSONException e1) {
			Log.e("Propinas", "error getting the array pagamentos");
			//e1.printStackTrace();
			return false;
		}
		for(int i=0; i<jHistory.length(); i++)
		{
			try 
			{
				YearsTuition yT=new YearsTuition();
				if(yT.load(jHistory.getJSONObject(i)))
					history.add(yT);
				
			} catch (JSONException e) {
				Log.e("Propinas", "Error getting the year from the JSON list in tuitions");
				//e.printStackTrace();
				return false;
			}
		}
		loaded=true;
		Log.i("Propinas", "Loaded history");
		return true;
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public ArrayList<YearsTuition> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<YearsTuition> history) {
		this.history = history;
	}
}
