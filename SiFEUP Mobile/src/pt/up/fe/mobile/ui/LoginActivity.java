package pt.up.fe.mobile.ui;

import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	
	LoginTask logintask;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        logintask = new LoginTask();
        setContentView(R.layout.login);
        
        final EditText login = (EditText) findViewById(R.id.login);
        final EditText pass = (EditText) findViewById(R.id.pass);

        Button confirm = (Button) findViewById(R.id.confirm);
        
        confirm.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				String urlS = "https://www.fe.up.pt/si/MOBC_GERAL.autentica?pv_login="+
				login.getText().toString().trim() + "&pv_password=" + pass.getText().toString().trim();
				logintask.execute(urlS);
			}
				
		});
    }

    
    private HttpsURLConnection getDangerousCon(String url) throws 
    				NoSuchAlgorithmException, KeyManagementException, MalformedURLException, IOException{
    	X509TrustManager tm = new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
			}
		};
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[] { tm }, null);					
		HttpsURLConnection httpConn = (HttpsURLConnection) new URL(url).openConnection();
		httpConn.setSSLSocketFactory(ctx.getSocketFactory());
		httpConn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String paramString, SSLSession paramSSLSession) {
				return true;
			}
		});
		return httpConn;
    }
    
    private static final int DIALOG_CONNECTING = 3000;
	protected Dialog onCreateDialog(int id ) {
		switch (id) {
			case DIALOG_CONNECTING: {
				ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(false);
				progressDialog.setTitle(getString(R.string.lb_login_cancel));
				progressDialog.setButton(getString(R.string.bt_cancel),
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						if ( LoginActivity.this.logintask != null )
							LoginActivity.this.logintask.cancel(true);
						removeDialog(DIALOG_CONNECTING);
					}
				});
				progressDialog.setIndeterminate(false);
				return progressDialog;
			}
		}
		return null;
	}
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

    	protected void onPreExecute (){
    		showDialog(DIALOG_CONNECTING);
    	}

        protected void onPostExecute(Boolean result) {
        	if ( result )
        	{
				Log.e("Login","success");
				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				
			}
			else{	
				Log.e("Login","error");
				Toast.makeText(LoginActivity.this, "Fuck", Toast.LENGTH_LONG).show();
			}
        	removeDialog(DIALOG_CONNECTING);
        	logintask = new LoginTask();
        }

		@Override
		protected Boolean doInBackground(String ... params) {
				InputStream in = null;
				String page = "";
				try {
					
					Log.e("Login",params[0] );
					HttpsURLConnection httpConn = getDangerousCon(params[0]);
					httpConn.connect();
					in = httpConn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(in);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
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
					httpConn.disconnect();
				} catch (MalformedURLException e) {
				 // DEBUG
				 Log.e("DEBUG url exceptop: ", e.toString());
				} catch (IOException e) {
				 // DEBUG
				 Log.e("DEBUG: ioexcep ", e.toString());
				}  catch (KeyManagementException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				JSONObject jObject;
				try {
					jObject = new JSONObject(page);
					return jObject.optBoolean("authenticated");					
				} catch (JSONException e) {
				 // DEBUG
				 Log.e("DEBUG: ", e.toString());
				}			
				return false;
		}
    }
    
 
}