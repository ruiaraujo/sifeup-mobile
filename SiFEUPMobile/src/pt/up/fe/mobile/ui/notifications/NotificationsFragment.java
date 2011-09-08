package pt.up.fe.mobile.ui.notifications;

import java.util.ArrayList;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Notification;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;


/**
 * Notifications Fragment
 * @author Ângela Igreja
 *
 */
public class NotificationsFragment extends BaseFragment  {

	private ListView list;
	
	private ArrayList<Notification> notifications;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Notifications");

    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 super.onCreateView(inflater, container, savedInstanceState);
		 View root = inflater.inflate(R.layout.generic_list, getParentContainer(), true);
	     list = (ListView) root.findViewById(R.id.generic_list);
	     notifications = new ArrayList<Notification>();
	 	 new NotificationsTask().execute();
	 return getParentContainer(); //this is mandatory.
	}
	 
	/** Classe privada para a busca de dados ao servidor */
	private class NotificationsTask extends AsyncTask<String, Void, String> {
	
		protected void onPreExecute (){
			showLoadingScreen();
		} 
	
		protected void onPostExecute(String result) 
		{
			if ( getActivity() == null )
					 return;
				
			if ( result.equals("Success") )
			{
				Log.e("Notifications","success");
				 
			     // fill in the grid_item layout
			     if ( getActivity() == null ) 
			    	 return;
			     //TODO:SHOW data 
			     showMainScreen();
			     Log.e("JSON", "Notifications visual list loaded");
			     return;
			
			}
			else if ( result.equals("Error") ){	
				Log.e("Login","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")  )
			{
				if ( getActivity() != null ) 	
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
		}
		
		@Override
		protected String doInBackground(String ... code) 
		{
			String page = "";
		  	try {
					page = SifeupAPI.getNotificationsReply();
					int error = SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				JSONObject jObject = new JSONObject(page);
		    				
		    				if(jObject.has("notificacoes"))
		    				{
		    					Log.e("JSON", "founded notifications");
		    					
		    					JSONArray jArray = jObject.getJSONArray("notificacoes");
		    					
		    					if(jArray.length() == 0)
		    						return "";
		    					
		    					for(int i = 0; i < jArray.length(); i++){
		    		    			JSONObject jNotification = jArray.getJSONObject(i);
		    		    			
			    		    		Notification noti = new Notification();
			    		    		
			    		    		noti.JSONNotification(jNotification);
			    		    		notifications.add(noti);
		    					}
		    				}	
		    				else
		    					return "";
		    				
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "Error";// When not authenticated, it returns a null page.
		    		}		
		    	return "Success";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		
			return "";
		}
	}
}
