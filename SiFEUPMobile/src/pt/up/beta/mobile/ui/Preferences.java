package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.authenticator.PeriodicSyncReceiver;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class Preferences extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private String[] syncDisplayOptions;
	private String[] syncDisplayValues;
	private Preference syncInterval;
	private Preference notificationsSyncInterval;

	private SharedPreferences sharedPreferences;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle o) {
		super.onCreate(o);
		addPreferencesFromResource(R.layout.preferences);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		syncInterval = findPreference(getString(R.string.key_sync_interval));
		notificationsSyncInterval = findPreference(getString(R.string.key_notifications_sync_interval));
		syncDisplayOptions = getResources().getStringArray(
				R.array.sync_intervals);
		syncDisplayValues = getResources().getStringArray(
				R.array.sync_intervals_values);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE
						| ActionBar.DISPLAY_SHOW_HOME);

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// Setup the initial values
		String syncIntervalValue = sharedPreferences.getString(
				getString(R.string.key_sync_interval),
				Integer.toString(getResources().getInteger(
						R.integer.default_sync_interval)));

		String syncNotIntervalValue = sharedPreferences.getString(
				getString(R.string.key_notifications_sync_interval),
				Integer.toString(getResources().getInteger(
						R.integer.default_sync_interval)));
		updateListSummary(syncIntervalValue, syncNotIntervalValue);

		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (android.os.Build.VERSION.SDK_INT < 5
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goUp();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Handle the HOME / UP affordance. Since the app is only two levels
			// deep
			// hierarchically, UP always just goes home.
			goUp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Invoke "home" action, returning to {@link HomeActivity}.
	 */
	protected void goUp() {

		final Intent upIntent = new Intent(this, PersonalAreaActivity.class);

		if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// This activity is not part of the application's task, so create a
			// new task
			// with a synthesized back stack.
			TaskStackBuilder.create(this).addNextIntent(upIntent)
					.startActivities();
			finish();
		} else {
			// This activity is part of the application's task, so simply
			// navigate up to the hierarchical parent activity.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(upIntent);
				finish();
			} else
				NavUtils.navigateUpTo(this, upIntent);
		}
		overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
	}

	private void updateListSummary(String syncIntervalValue,
			String syncNotIntervalValue) {

		for (int i = 0; i < syncDisplayValues.length; ++i) {
			if (syncDisplayValues[i].equals(syncIntervalValue)) {
				syncInterval.setSummary(syncDisplayOptions[i]);
			}
			if (syncDisplayValues[i].equals(syncNotIntervalValue)) {
				notificationsSyncInterval.setSummary(syncDisplayOptions[i]);
			}
		}
	}

	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
	}

	@TargetApi(8)
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(getString(R.string.key_sync_interval))
				|| key.equals(getString(R.string.key_notifications_sync_interval))) {

			String syncIntervalValue = sharedPreferences.getString(
					getString(R.string.key_sync_interval),
					Integer.toString(getResources().getInteger(
							R.integer.default_sync_interval)));

			String syncNotIntervalValue = sharedPreferences.getString(
					getString(R.string.key_notifications_sync_interval),
					Integer.toString(getResources().getInteger(
							R.integer.default_sync_interval)));
			updateListSummary(syncIntervalValue, syncNotIntervalValue);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				ContentResolver.addPeriodicSync(
						AccountUtils.getActiveAccount(getApplicationContext()),
						SigarraContract.CONTENT_AUTHORITY, new Bundle(),
						Integer.parseInt(syncIntervalValue) * 3600);
				ContentResolver.addPeriodicSync(
						AccountUtils.getActiveAccount(getApplicationContext()),
						SigarraContract.CONTENT_AUTHORITY,
						SigarraSyncAdapterUtils.getNotificationsBundle(),
						Integer.parseInt(syncNotIntervalValue) * 3600);
			} else {
				PeriodicSyncReceiver.cancelPreviousAlarms(this,
						AccountUtils.getActiveAccount(getApplicationContext()),
						SigarraContract.CONTENT_AUTHORITY, new Bundle());
				PeriodicSyncReceiver.addPeriodicSync(this,
						AccountUtils.getActiveAccount(getApplicationContext()),
						SigarraContract.CONTENT_AUTHORITY, new Bundle(),
						Integer.parseInt(syncIntervalValue) * 3600);
				PeriodicSyncReceiver.addPeriodicSync(this,
						AccountUtils.getActiveAccount(getApplicationContext()),
						SigarraContract.CONTENT_AUTHORITY,
						SigarraSyncAdapterUtils.getNotificationsBundle(),
						Integer.parseInt(syncNotIntervalValue) * 3600);

			}
			return;
		}
	}
}
