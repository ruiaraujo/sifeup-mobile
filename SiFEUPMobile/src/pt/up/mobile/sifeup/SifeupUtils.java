package pt.up.mobile.sifeup;

import android.content.Context;
import android.net.ConnectivityManager;

public class SifeupUtils {
	
	private SifeupUtils(){}

	
	public static boolean isConnected( final Context context ){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectManager.getActiveNetworkInfo().isConnected();

	}
}
