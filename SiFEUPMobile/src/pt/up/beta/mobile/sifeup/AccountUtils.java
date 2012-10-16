package pt.up.beta.mobile.sifeup;

import java.io.IOException;

import pt.up.beta.mobile.Constants;
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

	public static boolean init(final Context context) {
		mAccountManager = AccountManager.get(context);
		SharedPreferences loginSettings = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		final String activeUser = loginSettings.getString(
				LauncherActivity.PREF_ACTIVE_USER, "");
		if (!TextUtils.isEmpty(activeUser)) {
			mAccount = new Account(activeUser, Constants.ACCOUNT_TYPE);
			return true;
		}
		else
			return false;
	}

	private static boolean needsInit() {
		return mAccount == null || mAccountManager == null;
	}

	public static String renewAuthToken(final Context context, String authToken)
			throws OperationCanceledException, AuthenticatorException,
			IOException {
		if (needsInit()) {
			if ( !init(context) ) 
				return null;
		}
		mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
		return getAuthToken(context);
	}

	public static String getAuthToken(final Context context)
			throws OperationCanceledException, AuthenticatorException,
			IOException {
		if (needsInit()) {
			if ( !init(context) ) 
				return null;
		}
		final String authToken = mAccountManager.peekAuthToken(mAccount,
				Constants.ACCOUNT_TYPE);
		if (authToken == null)
			return mAccountManager.blockingGetAuthToken(mAccount,
					Constants.AUTHTOKEN_TYPE, false);
		return authToken;
	}
	
	public static Account getActiveAccount(final Context context) {
		if (needsInit()) {
			if ( !init(context) ) 
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
			if ( !init(context) ) 
				return null;
		}
		return mAccountManager.getUserData(mAccount, Constants.USER_CODE);
	}

	/**
	 * Get Login Code
	 * 
	 * @return the login code
	 */
	public static String getActiveUserType(final Context context) {
		if (needsInit()) {
			if ( !init(context) ) 
				return null;
		}
		return mAccountManager.getUserData(mAccount, Constants.USER_TYPE);
	}

	/**
	 * Get Login Name
	 * 
	 * @return the login name
	 */
	public static String getActiveUserName(final Context context) {
		if (needsInit()) {
			if ( !init(context) ) 
				return null;
		}
		return mAccount.name;
	}

	/**
	 * Get Login Password
	 * 
	 * @return the login passowrd
	 */
	public static String getActiveUserPassword(final Context context) {
		if (needsInit()) {
			if ( !init(context) ) 
				return null;
		}
		return mAccountManager.getPassword(mAccount);
	}

	/*
	 * GetLoginPassword return null if the account doesn't exist
	 */
	public static boolean isAccountValid(final Context context) {
		return getActiveUserPassword(context) != null;
	}

}
