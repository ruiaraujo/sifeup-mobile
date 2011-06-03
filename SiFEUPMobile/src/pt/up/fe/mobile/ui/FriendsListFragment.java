package pt.up.fe.mobile.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Friend;
import pt.up.fe.mobile.service.SessionManager;
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
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

public class FriendsListFragment extends ListFragment {
	
	final String TAG="FriendsListFragment";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Friends");
        new FriendsTask().execute();
        registerForContextMenu(getActivity().findViewById(android.R.id.list));
    }
    
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add("Teste");
    	MenuInflater inflater = getActivity().getMenuInflater();
    	inflater.inflate(R.menu.friends_menu_context, menu);
    }
    
    @Override
    public boolean onContextItemSelected (MenuItem item)
    {
		return false;    	
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    /** {@inheritDoc} */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	//TODO
    	//startActivity(new Intent(getActivity(), TuitionRefDetailActivity.class));
    }
    
    
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_friends) 
        {
        	SessionManager.friends.addFriend(new Friend("0558520555","Angela","MIEIC"));         
        	SessionManager.friends.addFriend(new Friend("0558520555","Gaspar","MIEIC"));
        	SessionManager.friends.addFriend(new Friend("0558520555","Andre","MIEIC"));
        	SessionManager.friends.saveToFile(getActivity().getApplicationContext());
        	SessionManager.friends.setList(new ArrayList<Friend>());
            return true;
        }
        return super.onOptionsItemSelected(item);
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
		             map.put("name", f.getName());
		             map.put("course",f.getCourse());
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
	    			if(SessionManager.friends.loadFromFile(getActivity().getApplicationContext()))
	    				return "Sucess";
	    		}
	    					
				
			/*} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}*/

			return "";
		}
    }
	

	

	
	
}
