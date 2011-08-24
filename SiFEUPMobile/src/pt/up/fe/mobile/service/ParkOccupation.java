package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
       
import android.util.Log;

/**
 * Class Park Occupation
 * 
 * @author Ângela Igreja
 *
 */
@SuppressWarnings("serial")
public class ParkOccupation  implements Serializable 
{
	/** Free places in the park */
	private int places;
	
	public ParkOccupation(){
	}
	
	/**
	 *  Park Occupation Parser.
	 *  Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return
	 */
    public boolean JSONParkOccupation(String page)
    {
    	JSONObject jObject;
		try 
		{
			jObject = new JSONObject(page);
			
			if(jObject.has("lugares")) this.places = jObject.getInt("lugares");

			return true;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    	Log.e("JSON", "ParK Occupation not found");
    	return false;
    }
    
}
