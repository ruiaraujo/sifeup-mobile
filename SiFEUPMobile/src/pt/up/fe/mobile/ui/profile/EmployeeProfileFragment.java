package pt.up.fe.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Employee;
import pt.up.fe.mobile.service.Friend;
import pt.up.fe.mobile.service.Profile;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Profile.ProfileDetail;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleFragment;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Employee Profile Fragment
 * This interface is responsible for fetching the employee's profile information 
 * to the server and shows it. Have one argument that is the number of employee. 
 * 
 * @author Ângela Igreja
 */
public class EmployeeProfileFragment extends BaseFragment implements OnItemClickListener
{
	private TextView name;
	private ListView details;
	private CheckBox friend;
	private TextView code;

	/** User Info */
    private Employee me = new Employee();
    private List<ProfileDetail> contents;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Employee Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile, getParentContainer(), true);
		name = ((TextView)root.findViewById(R.id.profile_name));
		code = ((TextView)root.findViewById(R.id.profile_code));
		details = ((ListView)root.findViewById(R.id.profile_details));
		friend = ((CheckBox)root.findViewById(R.id.profile_star_friend));
		String code = getArguments().get(ProfileActivity.PROFILE_CODE).toString();
		
		friend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Friend fr = new Friend(me.getCode(),me.getName(), me.getName());
				if ( friend.isChecked())
					SessionManager.friends.addFriend(fr);
				else
					SessionManager.friends.removeFriend(fr);
				SessionManager.friends.saveToFile(getActivity());
			}
		});
		((Button)root.findViewById(R.id.profile_link_schedule)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ScheduleActivity.class);
				i.putExtra(ScheduleFragment.SCHEDULE_TYPE,ScheduleFragment.SCHEDULE_EMPLOYEE);
	    		i.putExtra(ScheduleFragment.SCHEDULE_CODE, me.getCode());
	    		i.putExtra(Intent.EXTRA_TITLE , getString(R.string.title_schedule_arg,me.getName()));
	    		startActivity(i);
			}
		});
		
		if ( code != null )
		{
			new ProfileTask().execute(code);
		}
		else
			new ProfileTask().execute(SessionManager.getInstance().getLoginCode());
        return getParentContainer();
    }
    


    /** Classe privada para a busca de dados ao servidor */
    private class ProfileTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null ) 
        		return;
        	if ( result.equals("Success") )
        	{
				Log.e("Profile","success");
				contents = me.getProfileContents(getResources());
				name.setText(me.getName());
				code.setText(me.getCode());
				if ( SessionManager.friends.isFriend(me.getCode()) )
					friend.setChecked(true);
				else
					friend.setChecked(false);
				String[] from = new String[] { "title", "content" };
		        int[] to = new int[] { R.id.profile_item_title, R.id.profile_item_content };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for ( ProfileDetail s : contents )   
		         { 
		        	 HashMap<String, String> map = new HashMap<String, String>();
		             map.put(from[0], s.title);
		             map.put(from[1],s.content);
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
		        		 							R.layout.list_item_profile, from, to);
		         details.setAdapter(adapter);
		         details.setOnItemClickListener(EmployeeProfileFragment.this);
		         details.setSelection(0);
		         showMainScreen();
			}
			else if ( result.equals("Error")) {	
				Log.e("Profile","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")) {	
				Log.e("Profile","error");
				if ( getActivity() != null ) 	
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        }

		@Override
		protected String doInBackground(String ... code) {
			String page = "";
		  	try {
		  		if ( code.length < 1 )
		  			return "";
	    			page = SifeupAPI.getEmployeeReply(code[0]);
	    		int error =	SifeupAPI.JSONError(page);
	    		switch (error)
	    		{
	    			case SifeupAPI.Errors.NO_AUTH:
	    				return "Error";
	    			case SifeupAPI.Errors.NO_ERROR:
	    				//JSONEmployee(page);
	    				if ( me.JSONSubject(page) )
	    					return "Success";
	    				else
	    					return "";
	    			case SifeupAPI.Errors.NULL_PAGE:
	    				return "";	
	    		}

				return page;
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		
	}
}
