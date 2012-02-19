package pt.up.fe.mobile.ui.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Notification;
import pt.up.fe.mobile.sifeup.NotificationUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

/**
 * Notifications Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class NotificationsFragment extends BaseFragment implements
        OnItemClickListener, ResponseCommand {

    private ListView list;

    private ArrayList<Notification> notifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView(
                "/Notifications");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.generic_list,
                getParentContainer(), true);
        list = (ListView) root.findViewById(R.id.generic_list);
        notifications = new ArrayList<Notification>();
        NotificationUtils.getNotificationsReply(this);
        return getParentContainer(); // this is mandatory.
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long id) {
    	Intent i = new Intent(getActivity(), NotificationsDescActivity.class);
        i.putExtra(NotificationsDescActivity.NOTIFICATION,
                notifications.get(position));
        startActivity(i);

    }

	public void onError(ERROR_TYPE error) {
		if ( getActivity() == null )
	 		return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
			((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
			break;
		case NETWORK:
			Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
		default:
			//TODO: general error
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
        if (getActivity() == null)
            return;
        
        notifications = (ArrayList<Notification>) results[0];
        if (notifications.isEmpty()) {
            showEmptyScreen(getString(R.string.lb_no_notification));
            return;
        }
        Log.e("JSON", "Notifications visual list loaded");

        String[] from = new String[] { "subject", "date",
                "designation", "priority" };
        int[] to = new int[] { R.id.notification_subject,
                R.id.notification_date, R.id.notification_designation,
                R.id.notification_priority };

        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

        for (Notification n : notifications) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("subject", " " + n.getSubject());
            map.put("date", " " + n.getDate());
            map.put("designation", " " + n.getDesignation());
            map.put("priority", " " + n.getPriorityString());
            fillMaps.add(map);
        }


        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                fillMaps, R.layout.list_item_notification, from, to);
        list.setAdapter(adapter);
        list.setOnItemClickListener(NotificationsFragment.this);
        showMainScreen();
	}
}
