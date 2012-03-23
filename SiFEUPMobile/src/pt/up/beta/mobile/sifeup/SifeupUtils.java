package pt.up.beta.mobile.sifeup;

import java.util.Iterator;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;

public class SifeupUtils {
	
	private SifeupUtils(){}

	public static void removeEmptyKeys(final JSONObject jObject){
		// remove stupid mappings
		@SuppressWarnings("unchecked")
        Iterator<String> it = jObject.keys();
		while ( it.hasNext() )
		{
			String key = it.next();
			if ( jObject.optString(key).equals("") )
				it.remove();
		}
	}

	
	public static boolean isConnected( final Context context ){
		final ConnectivityManager connectManager = 
			   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectManager.getActiveNetworkInfo().isConnected();

	}
}
