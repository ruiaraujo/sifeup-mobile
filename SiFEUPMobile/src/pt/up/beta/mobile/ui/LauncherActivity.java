package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.authenticator.AuthenticatorActivity;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.SessionManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {
	private AccountManager mAccountManager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = AccountManager.get(getApplicationContext());
		Account[] accounts = mAccountManager
				.getAccountsByType(Constants.ACCOUNT_TYPE);
		if (accounts == null || accounts.length == 0) {
			startActivityForResult(
					new Intent(this, AuthenticatorActivity.class), 0);
		} else {
			final Account account = accounts[0];
			final SessionManager session = SessionManager.getInstance(this);
			session.setUser(new User(account.name, mAccountManager.getUserData(
					account, Constants.USER_NAME), mAccountManager
					.getPassword(account), mAccountManager.getUserData(account,
					Constants.USER_TYPE)));
			SessionManager.tuitionHistory.setLoaded(false);
			session.cleanFriends();
			startActivity(new Intent(this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				final User user = data
						.getParcelableExtra(AuthenticatorActivity.RESULT_USER);
				final SessionManager session = SessionManager.getInstance(this);
				session.setUser(user);
				SessionManager.tuitionHistory.setLoaded(false);
				session.cleanFriends();
				startActivity(new Intent(this, HomeActivity.class));
				finish();
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
			}

		}
		if (resultCode == RESULT_CANCELED) {
			finish();
		}
	}

}
