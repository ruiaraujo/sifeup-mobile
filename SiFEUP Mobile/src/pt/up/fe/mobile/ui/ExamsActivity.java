package pt.up.fe.mobile.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionCookie;
import pt.up.fe.mobile.service.SifeupAPI;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ExamsActivity extends Activity {
	
	ExamsTask examsTask;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.exams);
        
        // define action for examsRefresh button
        findViewById(R.id.examsRefresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				examsTask = new ExamsTask();
				examsTask.execute("exec");
			}
			
		});
	}
	
 /////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Classe privada para a busca de dados ao servidor */
	private class ExamsTask extends AsyncTask<String, Void, Boolean> {
		
    	/*protected void onPreExecute (){
    	 	// initiate dialog
    		showDialog(DIALOG_CONNECTING);  
    	}*/

        protected void onPostExecute(Boolean result) {
        	if ( result )
        	{
				Log.e("Exames","success");
				Toast.makeText(ExamsActivity.this, "Yeah!", Toast.LENGTH_LONG).show();
				
			}
			else{	
				Log.e("Exames","error");
				Toast.makeText(ExamsActivity.this, "Fuck", Toast.LENGTH_LONG).show();
			}
        	// remove dialog
        	//removeDialog(DIALOG_CONNECTING);
        }
        
		@Override
		protected Boolean doInBackground(String ... params) {
			InputStream in = null;
			String page = null;
			
			//after a click, fetches info from server
			try {
				// initiate connection
				HttpsURLConnection connection = LoginActivity.getUncheckedConnection(SifeupAPI.getExamsUrl("070509170"));
				connection.setRequestProperty("Cookie", SessionCookie.getInstance().getCookie());
				connection.connect();
				
				// fetch connection content
				in = connection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(in);
				ByteArrayBuffer baf = new ByteArrayBuffer(100);
				int read = 0;
				int bufSize = 512;
				byte[] buffer = new byte[bufSize];
				while ( true ) {
					read = bis.read( buffer );
					if( read == -1 ){
						break;
					}
					baf.append(buffer, 0, read);
				}
				page = new String(baf.toByteArray());
				bis.close();
				in.close();
				
				Log.e("APPPPPPPP", page);
				
				// close connection
				connection.disconnect();
				
				// check error
				if(JSONError(page))
					return false;
				else
					return true; 
			}
			catch (MalformedURLException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();} 
			catch (JSONException e) {e.printStackTrace();}
			
			return false;
		}
	}
	
	/** 
	 * Prints error message on Log.e()
	 * Returns true in case of a existing error.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public static boolean JSONError(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		String erro = null;
		String erro_msg = null;
		
		if(jObject.has("erro")){
			erro = (String) jObject.get("erro");
			Log.e("APPPPPPPPerro", erro);
			if(erro.substring(0, 8).equals("Autoriza")){
				erro_msg = (String) jObject.get("erro_msg");
				Log.e("APPPPPPPPerro_msg", erro_msg);
			}
			return true;
		}
		
		return false;
	}

}

