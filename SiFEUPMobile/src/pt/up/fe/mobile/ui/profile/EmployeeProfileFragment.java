package pt.up.fe.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Employee;
import pt.up.fe.mobile.datatypes.Friend;
import pt.up.fe.mobile.datatypes.Profile;
import pt.up.fe.mobile.datatypes.Profile.ProfileDetail;
import pt.up.fe.mobile.sifeup.ProfileUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleFragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
public class EmployeeProfileFragment extends BaseFragment implements OnItemClickListener, ResponseCommand
{
	private TextView name;
	private ListView details;
	private CheckBox friend;
	private TextView code;

	/** User Info */
    private Employee me;
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
		if ( code == null )
			code = SessionManager.getInstance().getLoginCode();
		friend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Friend fr = new Friend(me.getCode(),me.getName(),null);
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
		
		ProfileUtils.getEmployeeReply(code, this);
        return getParentContainer();
    }
    

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			((BaseActivity) getActivity()).goLogin();
			break;
		case NETWORK:
			Toast.makeText(getActivity(),
					getString(R.string.toast_server_error), Toast.LENGTH_LONG)
					.show();
		default:
			// TODO: general error
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		me = (Employee) results[0];
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
    
	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
		if ( contents.get(position).type == Profile.Type.WEBPAGE )
		{
			final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contents.get(position).content));
			startActivity(browserIntent);
		}
		else if ( contents.get(position).type == Profile.Type.ROOM )
		{
			final Intent i = new Intent(getActivity() , ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,ScheduleFragment.SCHEDULE_ROOM) ;
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, contents.get(position).content  );
    		i.putExtra(Intent.EXTRA_TITLE , getString(R.string.title_schedule_arg,
    											contents.get(position).content));
    		startActivity(i);
		}
		else if ( contents.get(position).type == Profile.Type.EMAIL )
		{
			final Intent i = new Intent(Intent.ACTION_SEND);  
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{contents.get(position).content});
			startActivity(Intent.createChooser(i, getString(R.string.profile_choose_email_app))); 
		}
		else if ( contents.get(position).type == Profile.Type.MOBILE )
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+contents.get(position).content));
			startActivity(callIntent);
		}
	}
}
