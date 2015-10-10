package pt.up.mobile.ui.services.print;

import com.google.analytics.tracking.android.EasyTracker;

import pt.up.mobile.Constants;
import pt.up.mobile.R;
import pt.up.mobile.authenticator.AuthenticatorActivity;
import pt.up.mobile.sifeup.AccountUtils;
import pt.up.mobile.ui.LauncherActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

public class MobilePrintActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(getApplicationContext());
		final String user = AccountUtils.getActiveUserName(this);
		final String pass = AccountUtils.getActiveUserPassword(this);
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
			Intent i = new Intent(this, MobilePrintService.class);
			i.replaceExtras(getIntent());
			final String sender;
			if (AccountUtils.getActiveUserName(this).endsWith("@fe.up.pt"))
				sender = AccountUtils.getActiveUserName(this);
			else
				sender = AccountUtils.getActiveUserName(this) + "@fe.up.pt";
			i.putExtra(MobilePrintService.USERNAME_KEY, sender);
			i.putExtra(MobilePrintService.PASSWORD_KEY, pass);
			startService(i);
			finish();
		} else {
			startActivityForResult(
					new Intent(this, AuthenticatorActivity.class), 0);
		}
	}

	@SuppressLint("CommitPrefEdits")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			final AccountManager accountManager = AccountManager
					.get(getApplicationContext());
			Account[] accounts = accountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if (accounts.length == 0) {
				finish();
				return;
			}
			SharedPreferences loginSettings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			final SharedPreferences.Editor editor = loginSettings.edit();

			editor.putString(LauncherActivity.PREF_ACTIVE_USER,
					accounts[0].name);
			new Thread(new Runnable() {
				public void run() {
					editor.commit();
				}
			}).start();
			Intent i = new Intent(this, MobilePrintService.class);
			i.replaceExtras(getIntent());
			final String sender;
			if (accounts[0].name.endsWith("@fe.up.pt"))
				sender = accounts[0].name;
			else
				sender = accounts[0].name + "@fe.up.pt";
			i.putExtra(MobilePrintService.USERNAME_KEY, sender);
			i.putExtra(MobilePrintService.PASSWORD_KEY,
					accountManager.getPassword(accounts[0]));
			startService(i);
			finish();
		}

		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this, R.string.error_credential, Toast.LENGTH_SHORT)
					.show();

		}
	}

}
