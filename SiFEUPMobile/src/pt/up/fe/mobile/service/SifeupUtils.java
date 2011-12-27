package pt.up.fe.mobile.service;

import pt.up.fe.mobile.ui.LoginActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

public class SifeupUtils {
	
	private SifeupUtils(){}
	private final static String PREF_CONNECTION_TYPE =
		"pt.up.fe.mobile.service.CONNECTION_TYPE";
	private final static int COOKIE_VALID_TIME_IN_HOURS = 24;
	
	/**
	 * Check if the cookie stored is still valid.
	 * @param context
	 * @return false if the login must be redone.
	 */
	public static boolean checkCookie(final Context context){
        final SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        final long now = System.currentTimeMillis();
        final long before = loginSettings.getLong( LoginActivity.PREF_COOKIE_TIME, 0);
        final String oldCookie = loginSettings.getString( LoginActivity.PREF_COOKIE, "");
        if ( oldCookie.trim().length() == 0 )
        	return false;
        if ( ( ( now - before )/3600000 < COOKIE_VALID_TIME_IN_HOURS ) )
        {
        	if ( !checkConnectionType(context) )
        		return false;
        	SessionManager.getInstance().setCookie(oldCookie);
        	SessionManager.getInstance().setLoginCode(loginSettings.getString(
        										LoginActivity.PREF_USERNAME_SAVED, ""));
        	
        	return true;
        }
        return false;
        
	}
	
	private static boolean checkConnectionType( final Context context ){
		final SharedPreferences settings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
		if ( getConnectionType(context).equals(settings.getString( PREF_CONNECTION_TYPE, "")) ) 
				return true;
		return false;
	}
	
	private static String getConnectionType(final Context context){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectManager.getActiveNetworkInfo().getTypeName();
	}
	
	/**
	 * Stores the current connection type
	 * @param context
	 */
	public static void storeConnectionType( final Context context ){
		final SharedPreferences settings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        final SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString(PREF_CONNECTION_TYPE, getConnectionType(context));
        prefEditor.commit();
	}
	
	public static boolean isConnected( final Context context ){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectManager.getActiveNetworkInfo().isConnected();

	}
}
