package pt.up.fe.mobile.sifeup;

import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.ui.LoginActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

public class SifeupUtils {
	
	private SifeupUtils(){}

	public static boolean loadSession( final Context context){
        SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        final String user = loginSettings.getString(LoginActivity.PREF_USERNAME, "");
        final String pass = loginSettings.getString(LoginActivity.PREF_PASSWORD, "") ;
        final String type = loginSettings.getString(LoginActivity.PREF_USER_TYPE, "") ;
        if ( !user.equals("") && !pass.equals("") && !type.equals("") )
        {
        	SessionManager.getInstance().setUser(new User(user, pass, type));
        	return true;
        }
        return false;
	}
	
	public static boolean isConnected( final Context context ){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectManager.getActiveNetworkInfo().isConnected();

	}
}
