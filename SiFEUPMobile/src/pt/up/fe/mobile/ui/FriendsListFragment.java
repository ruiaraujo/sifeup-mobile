package pt.up.fe.mobile.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

public class FriendsListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        new ExamsTask().execute();

    }
    
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    



    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_friends) {
                Toast.makeText(getActivity(), "Exemplo de Acção",
                        Toast.LENGTH_SHORT).show();
            
            return true;
        }
        return super.onOptionsItemSelected(item);
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
				
				 String[] from = new String[] {"name", "course"};
		         int[] to = new int[] { R.id.friend_name, R.id.friend_course };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		             HashMap<String, String> map = new HashMap<String, String>();
		             map.put("name", "Rui");
		             map.put("course","MIEEC");
		             fillMaps.add(map);
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
		        		 							R.layout.list_item_friend, from, to);
		         setListAdapter(adapter);
		         Log.e("JSON", "exams visual list loaded");

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
				
	    		
	    		
				return "Sucess";
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
	

	

	
	
}
