package pt.up.mobile.authenticator;

import pt.up.mobile.Constants;
import pt.up.mobile.content.SigarraContract;
import pt.up.mobile.content.SigarraProvider;
import pt.up.mobile.sifeup.AccountUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class AccountWatcher extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(
				AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final Account[] accounts = AccountManager.get(context)
							.getAccountsByType(Constants.ACCOUNT_TYPE);

					final Cursor cursor = context.getContentResolver().query(
							SigarraContract.Users.CONTENT_URI,
							SigarraContract.Users.COLUMNS, null, null, null);
					try {
						if (cursor.moveToFirst()) {
							do {
								final String accountName = cursor
										.getString(SigarraContract.Users.ID_COLUMN);
								boolean foundAccount = false;
								for (Account a : accounts) {
									if (a.name.equals(accountName)) {
										foundAccount = true;
										break;
									}
								}
								if (!foundAccount) {
									if (accountName.equals(AccountUtils
											.getActiveUserName(context))) {
										AccountUtils.invalidate();
									}
									SigarraProvider.deleteUserData(context,
											accountName);
								}

							} while (cursor.moveToNext());
						}
					} finally {
						cursor.close();
					}

				}
			}).start();
		}
	}

}
