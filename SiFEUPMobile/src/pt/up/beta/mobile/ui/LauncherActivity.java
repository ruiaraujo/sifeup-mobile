package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.authenticator.AuthenticatorActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("CommitPrefEdits")
public class LauncherActivity extends SherlockFragmentActivity implements
		OnItemClickListener {
	private AccountManager mAccountManager;
	public static final String LOGOUT_FLAG = "pt.up.fe.mobile.ui.logout";
	public static final String PREF_ACTIVE_USER = "pt.up.fe.mobile.ui.USERNAME";

	private boolean logOut;

	/** Called when the activity is first created. */
	@TargetApi(16)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_account);
		mAccountManager = AccountManager.get(getApplicationContext());
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll() // or
																		// .detectAll()
																		// for
																		// all
																		// detectable
																		// problems
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

	}

	@TargetApi(14)
	@Override
	public void onStart() {
		super.onStart();
		logOut = getIntent().getBooleanExtra(LOGOUT_FLAG, false);
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);

		SharedPreferences loginSettings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (logOut) {
			final SharedPreferences.Editor editor = loginSettings.edit();
			editor.putString(PREF_ACTIVE_USER, "");
			new Thread(new Runnable() {
				public void run() {
					editor.commit();
				}
			}).start();
		}
		final String activeUser = loginSettings.getString(PREF_ACTIVE_USER, "");
		if (accounts.length == 0) {
			startActivityForResult(
					new Intent(this, AuthenticatorActivity.class), 0);
		} else {
			final String[] accountNames = new String[accounts.length];
			int i = 0;
			for (Account account : accounts) {
				accountNames[i++] = account.name;
				if (account.name.equals(activeUser) && !logOut) {
					startActivity(new Intent(this, HomeActivity.class));
					finish();
					overridePendingTransition(R.anim.slide_right_in,
							R.anim.slide_right_out);
					return;
				}
			}
			if (!TextUtils.isEmpty(activeUser) && !logOut) {
				Log.e("FEUPMobile", "account " + activeUser + " was deleted.");
				// TODO: remove all data from db from this user
			}

			ListView accountList = (ListView) findViewById(R.id.account_list);
			accountList.setAdapter(new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_list_item_1, accountNames));
			accountList.setOnItemClickListener(this);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Account[] accounts = mAccountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if ( accounts.length == 0 ){
				finish();
				return;
			}
			SharedPreferences loginSettings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			final SharedPreferences.Editor editor = loginSettings.edit();
			
			editor.putString(PREF_ACTIVE_USER, accounts[0].name);
			new Thread(new Runnable() {
				public void run() {
					editor.commit();
				}
			}).start();
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}

		if (resultCode == RESULT_CANCELED) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final String accountName = parent.getAdapter().getItem(position)
				.toString();
		SharedPreferences loginSettings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		final SharedPreferences.Editor editor = loginSettings.edit();
		editor.putString(PREF_ACTIVE_USER, accountName);
		new Thread(new Runnable() {
			public void run() {
				editor.commit();
			}
		}).start();
		startActivity(new Intent(this, HomeActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}
}
