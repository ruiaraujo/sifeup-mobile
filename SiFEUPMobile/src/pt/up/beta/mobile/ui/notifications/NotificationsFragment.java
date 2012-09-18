package pt.up.beta.mobile.ui.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.loaders.NotificationsLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Notifications Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class NotificationsFragment extends BaseFragment implements
		OnItemClickListener,
		LoaderCallbacks<List<Notification>> {

	private ListView list;

	private List<Notification> notifications;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			SyncAdapterUtils.syncNotifications(AccountUtils.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		Intent i = new Intent(getActivity(), NotificationsDescActivity.class);
		i.putExtra(NotificationsDescActivity.NOTIFICATION,
				notifications.get(position));
		startActivity(i);

	}

	@Override
	public Loader<List<Notification>> onCreateLoader(int arg0, Bundle arg1) {
		return new NotificationsLoader(getActivity(),
				SigarraContract.Notifcations.CONTENT_URI,
				SigarraContract.Notifcations.COLUMNS,
				SigarraContract.Notifcations.PROFILE,
				SigarraContract.Notifcations
						.getNotificationsSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())),
				SigarraContract.Notifcations.DEFAULT_SORT);
	}

	@Override
	public void onLoadFinished(Loader<List<Notification>> loader,
			List<Notification> notifications) {
		if (getActivity() == null || notifications == null)
			return;

		this.notifications = notifications;
		if (notifications.isEmpty()) {
			showEmptyScreen(getString(R.string.lb_no_notification));
			setRefreshActionItemState(false);
			return;
		}
		Log.d("JSON", "Notifications visual list loaded");

		String[] from = new String[] { "subject", "date", "designation",
				"priority" };
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

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_notification, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(NotificationsFragment.this);
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<List<Notification>> arg0) {

	}
}
