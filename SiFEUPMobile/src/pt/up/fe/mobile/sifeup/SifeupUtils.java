package pt.up.fe.mobile.sifeup;

import pt.up.fe.mobile.ui.LoginActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

public class SifeupUtils {
	
	private SifeupUtils(){}
	private final static String PREF_CONNECTION_TYPE =
		"pt.up.fe.mobile.service.CONNECTION_TYPE";
	

	
	private static boolean checkConnectionType( final Context context ){
		final SharedPreferences settings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
		if ( getConnectionType(context).equals(settings.getString( PREF_CONNECTION_TYPE, "")) ) 
				return true;
		return false;
	}
	
	private static String getConnectionType(final Context context){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if ( connectManager.getActiveNetworkInfo() == null )
			return "DISCONNECTED";
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
