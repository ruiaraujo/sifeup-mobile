package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.BuildConfig;
import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.authenticator.AuthenticatorActivity;
import pt.up.beta.mobile.contacts.ContactManager;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.ui.services.DynamicMailFilesActivity;
import pt.up.beta.mobile.ui.webclient.WebviewActivity;
import pt.up.beta.mobile.ui.webclient.WebviewFragment;
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
import android.provider.ContactsContract;
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
import com.google.analytics.tracking.android.EasyTracker;

@SuppressLint("CommitPrefEdits")
public class LauncherActivity extends SherlockFragmentActivity implements
		OnItemClickListener {
	private static final int FIRST_ACCOUNT = 0;
	private static final int ADDING_OTHER_ACCOUNT = 1;

	private AccountManager mAccountManager;
	public static final String LOGOUT_FLAG = "pt.up.fe.mobile.ui.logout";
	public static final String PREF_ACTIVE_USER = "pt.up.fe.mobile.ui.USERNAME";

	private boolean logOut = false;

	/** Called when the activity is first created. */
	@TargetApi(11)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_account);
		EasyTracker.getInstance().setContext(getApplicationContext());
		final Button addAccount = (Button) findViewById(R.id.add_account);
		addAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getBaseContext(),
						AuthenticatorActivity.class), ADDING_OTHER_ACCOUNT);
			}
		});
		mAccountManager = AccountManager.get(getApplicationContext());
		if (BuildConfig.DEBUG) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
				StrictMode
						.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
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
					AccountUtils.init(getApplicationContext());
					launchNextActivity();
					return;
				}
			}
			if (!TextUtils.isEmpty(activeUser) && !logOut) {
				Log.d("FEUPMobile", "account " + activeUser + " was deleted.");
				SigarraProvider.deleteUserData(getApplicationContext(), activeUser);
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
			final String accountName = data
					.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			editor.putString(PREF_ACTIVE_USER, accountName);
			new Thread(new Runnable() {
				public void run() {
					editor.commit();
					AccountUtils.init(getApplicationContext());
				}
			}).start();
			launchNextActivity();
		}

		if (resultCode == RESULT_CANCELED) {
			if (requestCode != ADDING_OTHER_ACCOUNT)
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
				AccountUtils.init(getApplicationContext());
			}
		}).start();
		launchNextActivity();
	}

	private void launchNextActivity() {
		final Intent intent = getIntent();
		if (intent.getAction() != null
				&& intent.getAction().equals(Intent.ACTION_VIEW)) {
			final Uri uri = intent.getData();
			if (uri.getAuthority().matches(ContactsContract.AUTHORITY)) {
				new AsyncTask<Void, Void, String[]>() {
					@Override
					protected void onPostExecute(String[] profileDetails) {
						final Intent intent;
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
													: ProfileActivity.PROFILE_EMPLOYEE)
									.putExtra(Intent.EXTRA_TITLE,
											profileDetails[3]);
						else
							intent = new Intent(getApplicationContext(),
									ScheduleActivity.class)
									.putExtra(ScheduleFragment.SCHEDULE_CODE,
											profileDetails[1])
									.putExtra(
											ScheduleFragment.SCHEDULE_TYPE,
											SifeupAPI.STUDENT_TYPE
													.equals(profileDetails[2]) ? ScheduleFragment.SCHEDULE_STUDENT
													: ScheduleFragment.SCHEDULE_EMPLOYEE)
									.putExtra(
											Intent.EXTRA_TITLE,
											getString(
													R.string.title_schedule_arg,
													profileDetails[3]));
						;
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.fade_in,
								R.anim.fade_out);
					}

					@Override
					protected void onPreExecute() {
						setContentView(R.layout.loading_view);
					}

					@Override
					protected String[] doInBackground(Void... params) {
						return ContactManager.getProfileDataContact(
								getApplicationContext(), uri);
					}
				}.execute();
				return;
			}
			final String lastSegment = uri.getLastPathSegment();
			if (lastSegment.startsWith("mail_dinamico.ficheiros")) {
				startActivity(new Intent(this, DynamicMailFilesActivity.class));
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				return;
			}

			if (lastSegment.startsWith("fest_geral.cursos_list")
					&& !TextUtils
							.isEmpty(uri.getQueryParameter("pv_num_unico"))) {
				startActivity(new Intent(this, ProfileActivity.class).putExtra(
						ProfileActivity.PROFILE_TYPE,
						ProfileActivity.PROFILE_STUDENT).putExtra(
						ProfileActivity.PROFILE_CODE,
						uri.getQueryParameter("pv_num_unico")));
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				return;
			}

			if (lastSegment.startsWith("func_geral.formview")
					&& !TextUtils.isEmpty(uri.getQueryParameter("p_codigo"))) {
				startActivity(new Intent(this, ProfileActivity.class).putExtra(
						ProfileActivity.PROFILE_TYPE,
						ProfileActivity.PROFILE_EMPLOYEE).putExtra(
						ProfileActivity.PROFILE_CODE,
						uri.getQueryParameter("p_codigo")));
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				return;
			}

			if (lastSegment.startsWith("instal_geral.espaco_view")
					&& !TextUtils.isEmpty(uri.getQueryParameter("pv_id"))) {
				startActivity(new Intent(this, ProfileActivity.class).putExtra(
						ProfileActivity.PROFILE_TYPE,
						ProfileActivity.PROFILE_ROOM).putExtra(
						ProfileActivity.PROFILE_CODE,
						uri.getQueryParameter("pv_id")));
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				return;
			}
			startActivity(new Intent(this, WebviewActivity.class).putExtra(
					WebviewFragment.URL_INTENT, uri.toString()));
			finish();
			overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
		}
		startActivity(new Intent(this, PersonalAreaActivity.class));
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

	}
}
