package pt.up.beta.mobile.contacts;

import pt.up.beta.mobile.Constants;
import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.ContactsContract;

public class ContactsSyncAdapterUtils {

	public static void syncContacts(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), ContactsContract.AUTHORITY,
				extras);
	}

	private ContactsSyncAdapterUtils() {
	}

}
