package pt.up.beta.mobile.sendtosamba;

import com.google.analytics.tracking.android.EasyTracker;

import pt.up.beta.mobile.Constants;
import pt.up.mobile.R;
import pt.up.beta.mobile.authenticator.AuthenticatorActivity;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.ui.LauncherActivity;
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

public class SendToSambaActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EasyTracker.getInstance().setContext(getApplicationContext());
		final String user = AccountUtils.getActiveUserName(this);
		final String pass = AccountUtils.getActiveUserPassword(this);
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
			Intent i = new Intent(this, UploaderService.class);
			i.replaceExtras(getIntent());
			i.putExtra(UploaderService.USERNAME_KEY, user);
			i.putExtra(UploaderService.PASSWORD_KEY, pass);
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
			final AccountManager accountManager = AccountManager.get(getApplicationContext());
			Account[] accounts = accountManager
					.getAccountsByType(Constants.ACCOUNT_TYPE);
			if ( accounts.length == 0 ){
				finish();
				return;
			}
			SharedPreferences loginSettings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			final SharedPreferences.Editor editor = loginSettings.edit();
			
			editor.putString(LauncherActivity.PREF_ACTIVE_USER, accounts[0].name);
			new Thread(new Runnable() {
				public void run() {
					editor.commit();
				}
			}).start();
			Intent i = new Intent(this, UploaderService.class);
			i.replaceExtras(getIntent());
			i.putExtra(UploaderService.USERNAME_KEY,  accounts[0].name);
			i.putExtra(UploaderService.PASSWORD_KEY, accountManager.getPassword( accounts[0]));
			startService(i);
			finish();
		}

		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(this,
					R.string.error_credential,
					Toast.LENGTH_SHORT).show();

		}
	}

}
