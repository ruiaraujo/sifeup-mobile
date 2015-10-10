package pt.up.beta.mobile.ui.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.loaders.NotificationsLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Notifications Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class NotificationsFragment extends BaseLoaderFragment implements
		OnItemClickListener, LoaderCallbacks<Notification[]> {

	private ListView list;

	private Notification[] notifications;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.generic_list);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NotificationsFragment.class.getName()
				.hashCode());
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncNotifications(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncNotifications(AccountUtils
				.getActiveUserName(getActivity()));

	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			finish();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		Intent i = new Intent(getActivity(), NotificationsDescActivity.class);
		i.putExtra(NotificationsDescFragment.NOTIFICATION,
				notifications[position]);
		startActivity(i);

	}

	@Override
	public Loader<Notification[]> onCreateLoader(int arg0, Bundle arg1) {
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
	public void onLoadFinished(Loader<Notification[]> loader,
			Notification[] notifications) {
		if (getActivity() == null || notifications == null)
			return;

		this.notifications = notifications;
		if (notifications.length == 0) {
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
			map.put("priority", " " + n.getPriority());
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
	public void onLoaderReset(Loader<Notification[]> arg0) {

	}
}
