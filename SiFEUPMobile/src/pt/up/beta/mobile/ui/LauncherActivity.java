package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.authenticator.AuthenticatorActivity;
import pt.up.beta.mobile.contacts.ContactManager;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("CommitPrefEdits")
public class LauncherActivity extends SherlockFragmentActivity implements
		OnItemClickListener {
	private static final int FIRST_ACCOUNT = 0;
	private static final int ADDING_OTHER_ACCOUNT = 1;

	private AccountManager mAccountManager;
	public static final String LOGOUT_FLAG = "pt.up.fe.mobile.ui.logout";
	public static final String PREF_ACTIVE_USER = "pt.up.fe.mobile.ui.USERNAME";

	private boolean logOut;

	/** Called when the activity is first created. */
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_account);
		final Button addAccount = (Button) findViewById(R.id.add_account);
		addAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivityForResult(new Intent(getBaseContext(),
						AuthenticatorActivity.class), ADDING_OTHER_ACCOUNT);
			}
		});
		mAccountManager = AccountManager.get(getApplicationContext());
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			final StrictMode.VmPolicy.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				builder = new StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects()
						.detectLeakedClosableObjects().penaltyLog()
						.penaltyDeath();
			else
				builder = new StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects().penaltyLog()
						.penaltyDeath();
			StrictMode.setVmPolicy(builder.build());
		}

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
					new Intent(this, AuthenticatorActivity.class),
					FIRST_ACCOUNT);
		} else {
			final String[] accountNames = new String[accounts.length];
			int i = 0;
			for (Account account : accounts) {
				accountNames[i++] = account.name;
				if (account.name.equals(activeUser) && !logOut) {
					launchNextActivity();
					return;
				}
			}
			if (!TextUtils.isEmpty(activeUser) && !logOut) {
				Log.d("FEUPMobile", "account " + activeUser + " was deleted.");
				// TODO: remove all data from db from this user
			}

			ListView accountList = (ListView) findViewById(R.id.account_list);
			accountList.setAdapter(new ArrayAdapter<String>(
					getApplicationContext(), R.layout.list_item_simple,
					accountNames));
			accountList.setOnItemClickListener(this);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Account[] accounts = mAccountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if (accounts.length == 0) {
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
			launchNextActivity();
		}

		if (resultCode == RESULT_CANCELED) {
			if (requestCode == FIRST_ACCOUNT)
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
		launchNextActivity();
	}

	private void launchNextActivity() {
		final Intent intent = getIntent();
		if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			final Uri uri = intent.getData();
			new AsyncTask<Void, Void, String[]>() {
				@Override
				protected void onPostExecute(String[] profileDetails) {
					Intent intent = null;
					if (profileDetails[0]
							.equals(SigarraContract.Profiles.CONTENT_ITEM_TYPE))
						intent = new Intent(getApplicationContext(),
								ProfileActivity.class)
								.putExtra(ProfileActivity.PROFILE_CODE,
										profileDetails[1])
								.putExtra(
										ProfileActivity.PROFILE_TYPE,
										SifeupAPI.STUDENT_TYPE
												.equals(profileDetails[2]) ? ProfileActivity.PROFILE_STUDENT
												: ProfileActivity.PROFILE_EMPLOYEE).putExtra(
														Intent.EXTRA_TITLE,profileDetails[3]);
					//TODO:
					/*else
						intent = new Intent(getApplicationContext(),
								ScheduleActivity.class)
								.putExtra(ScheduleFragment.SCHEDULE_CODE,
										profileDetails[1])
								.putExtra(
										ScheduleFragment.SCHEDULE_TYPE,
										SifeupAPI.STUDENT_TYPE
												.equals(profileDetails[2]) ? ScheduleFragment.SCHEDULE_STUDENT
												: ScheduleFragment.SCHEDULE_EMPLOYEE).putExtra(
														Intent.EXTRA_TITLE,
														getString(R.string.title_schedule_arg,
																profileDetails[3]));;*/
					startActivity(intent);
					finish();
				}

				@Override
				protected void onPreExecute() {
					setContentView(R.layout.loading_view);
				}

				@Override
				protected String[] doInBackground(Void... params) {
					return ContactManager.getProfileDataContact(
							getContentResolver(), uri);
				}
			}.execute();
			return;

		}
		startActivity(new Intent(this, PersonalAreaActivity.class));
		finish();

	}
}
