package pt.up.beta.mobile.sifeup;

import java.io.IOException;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.ui.LauncherActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Ângela Igreja
 * 
 */
public class AccountUtils {

	private static Account mAccount;
	private static AccountManager mAccountManager;
	private static User user;
	private static String cookie;

	public synchronized static boolean init(final Context context) {
		mAccountManager = AccountManager.get(context);
		SharedPreferences loginSettings = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		final String activeUser = loginSettings.getString(
				LauncherActivity.PREF_ACTIVE_USER, "");
		if (!TextUtils.isEmpty(activeUser)) {
			mAccount = new Account(activeUser, Constants.ACCOUNT_TYPE);
			user = new User(mAccount.name, mAccountManager.getUserData(
					mAccount, Constants.USER_CODE),
					mAccountManager.getPassword(mAccount),
					mAccountManager.getUserData(mAccount, Constants.USER_TYPE));
			return true;
		} else
			return false;
	}

	private static boolean needsInit() {
		return mAccount == null || mAccountManager == null;
	}

	public static String renewAuthToken(final Context context,
			final Account account, final String authToken)
			throws OperationCanceledException, AuthenticatorException,
			IOException {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
		cookie = null;
		return getAuthToken(context, account);
	}

	private static final Object LOCK = new Object();

	public synchronized static String getAuthToken(final Context context,
			final Account account) throws OperationCanceledException,
			AuthenticatorException, IOException {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		// I think that two things calling this function leads to problems.
		synchronized (LOCK) {
			if (cookie == null)
				cookie = mAccountManager.blockingGetAuthToken(account,
						Constants.AUTHTOKEN_TYPE, true);
			return cookie;
		}
	}

	public static Account getActiveAccount(final Context context) {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		return mAccount;
	}

	/**
	 * Get Login Code
	 * 
	 * @return the login code
	 */
	public static String getActiveUserCode(final Context context) {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		return user.getUserCode();
	}

	/**
	 * Get Login Code
	 * 
	 * @return the login code
	 */
	public static String getActiveUserType(final Context context) {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		return user.getType();
	}

	/**
	 * Get Login Name
	 * 
	 * @return the login name
	 */
	public static String getActiveUserName(final Context context) {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		return user.getDisplayName();
	}

	/**
	 * Get Login Password
	 * 
	 * @return the login passowrd
	 */
	public static String getActiveUserPassword(final Context context) {
		if (needsInit()) {
			if (!init(context))
				return null;
		}
		return user.getPassword();
	}

	/*
	 * GetLoginPassword return null if the account doesn't exist
	 */
	public static boolean isAccountValid(final Context context) {
		return mAccountManager.getPassword(mAccount) != null;
	}

}
