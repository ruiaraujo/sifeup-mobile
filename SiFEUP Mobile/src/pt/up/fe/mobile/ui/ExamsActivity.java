package pt.up.fe.mobile.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.ByteArrayBuffer;

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
        
		//description_refresh
        // definir accao para o botao de recarregarExames
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
    	 	// iniciar dialogo de busca de dados
    		showDialog(DIALOG_CONNECTING);  
    	}*/

        protected void onPostExecute(Boolean result) {
        	if ( result )
        	{
				Log.e("Exames","success");
				//startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				Toast.makeText(ExamsActivity.this, "Yeah!", Toast.LENGTH_LONG).show();
				
			}
			else{	
				Log.e("Exames","error");
				Toast.makeText(ExamsActivity.this, "Fuck", Toast.LENGTH_LONG).show();
			}
        	// retirar dialogo de busca de dados
        	//removeDialog(DIALOG_CONNECTING);
        }
        
		@Override
		protected Boolean doInBackground(String ... params) {
			InputStream in = null;
			String page = null;
			
			//apos o click, pede a informacao ao servidor
			try {
				// iniciar coneccao
				HttpsURLConnection connection = LoginActivity.getUncheckedConnection(SifeupAPI.getExamsUrl("070509170"));
				connection.setRequestProperty("Cookie", SessionCookie.getInstance().getCookie());
				connection.connect();
				
				// buscar conteudo da coneccao
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
				//String conteudoConeccao = SifeupAPI.getContents(coneccao);
				//if(conteudoConeccao==null)
				//	return false;
				Log.e("APPPPPPPP", page);
				
				// terminar coneccao
				connection.disconnect();
				
				
				// tratar dados
				//JSONObject jObject = new JSONObject(conteudoConeccao);
				//return jObject.optBoolean("authenticated");
				
				return true;
			}
			catch (MalformedURLException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();}
			
			return false;
		}
	}

}

