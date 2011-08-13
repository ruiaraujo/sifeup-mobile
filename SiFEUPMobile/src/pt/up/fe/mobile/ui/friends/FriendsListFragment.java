
package pt.up.fe.mobile.ui.friends;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Friend;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

/**
 * Fragment of the Friends Activity, communication with 
 * graphical interface. Loading a list item, is initiated 
 * {@link ProfileActivity} activity. 
 * 
 * @author Ângela Igreja
 *
 */
public class FriendsListFragment extends ListFragment {
	
	final String TAG="FriendsListFragment";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    	new FriendsTask().execute();
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Friends");
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	registerForContextMenu(getActivity().findViewById(android.R.id.list));
    }
    
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getActivity().getMenuInflater();
    	inflater.inflate(R.menu.friends_menu_context, menu);
    }
    public void onResume(){
        super.onResume();
    	new FriendsTask().execute();
    }
    
    @Override
    public boolean onContextItemSelected (MenuItem item)
    {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch(item.getItemId())
    	{
    	case R.id.menu_friends_delete:
    		SessionManager.friends.removeFriend((int)info.id);
    		SessionManager.friends.saveToFile(getActivity().getApplicationContext());
    		new FriendsTask().execute();  
    		break;
    	case R.id.menu_friends_timetable:
    		String loginCode=SessionManager.friends.getList().get((int)info.id).getCode();
    		if ( getActivity() == null )
    			return true;
    		Intent i = new Intent(getActivity(), ScheduleActivity.class);
    		i.putExtra(ScheduleFragment.PROFILE_CODE, loginCode);
    		startActivity(i);
    		break;
    	}
		return false;    	
    }
    
    public void onStop(){
    	super.onStop();
    }


    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	//TODO
    	SessionManager.friends.setSelectedFriend(position);
    	Intent i = new Intent(getActivity(), ProfileActivity.class);
    	i.putExtra("profile", SessionManager.friends.getFriend(position).getCode());
    	startActivity(i);
    }
    

    /** Classe privada para a busca de dados ao servidor */
    private class FriendsTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.i(TAG,"loading list...");
				
				 String[] from = new String[] {"name", "course"};
		         int[] to = new int[] { R.id.friend_name, R.id.friend_course };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for(Friend f : SessionManager.friends.getList()){

		             HashMap<String, String> map = new HashMap<String, String>();
		             map.put(from[0], f.getName());
		             map.put(from[1],f.getCourse());
		             fillMaps.add(map);
		         }

				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
		        		 							R.layout.list_item_friend, from, to);
		         setListAdapter(adapter);
		         Log.i(TAG, "list loaded successfully");

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
			//String page = "";
		  	/*try 
		  	{
    			page = SifeupAPI.getExamsReply(SessionManager.getInstance().getLoginCode());
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}*/
	    		if(!SessionManager.friends.isLoaded())
	    		{
	    			if(SessionManager.friends.loadFromFile(getActivity()))
	    				return "Sucess";
	    		}
	    		else
	    			return "Sucess";		
				
			/*} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}*/

			return "";
		}
    }
	

	

	
	
}