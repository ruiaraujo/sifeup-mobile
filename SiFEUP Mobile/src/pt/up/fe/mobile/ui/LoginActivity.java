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

import pt.fe.up.mobile.service.SessionCookie;
import pt.fe.up.mobile.service.SifeupAPI;
import pt.up.fe.mobile.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	final String PREF_REMEMBER = "Remember Credentials";
	final String PREF_USERNAME = "Username";
	final String PREF_PASSWORD = "Password";

	private LoginTask logintask;
	private boolean rememberUser;
	private String user;
	private String pass;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        logintask = null;

        setContentView(R.layout.login);
        SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
        final SharedPreferences.Editor prefEditor = loginSettings.edit();
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.pass);
        final CheckBox check = (CheckBox) findViewById(R.id.login_remember);
        rememberUser = loginSettings.getBoolean(PREF_REMEMBER, false);
    	check.setChecked(rememberUser);
        if ( rememberUser )
        {
        	user = loginSettings.getString(PREF_USERNAME, "");
        	pass = loginSettings.getString(PREF_PASSWORD, "") ;
        	if ( !user.equals("") && !pass.equals("") )
        	{
        		username.setText(user);
        		password.setText(pass);
        		logintask = new LoginTask();
        		logintask.execute(SifeupAPI.getAuthenticationUrl( user , pass ));
        	}
        }
        findViewById(R.id.login_confirm).setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
	        	logintask = new LoginTask();
	        	user = username.getText().toString().trim();
	        	pass = password.getText().toString().trim();
				logintask.execute(SifeupAPI.getAuthenticationUrl( user, pass));
			}
				
		});
        findViewById(R.id.login_reset).setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				username.setText("");
				password.setText("");
			}
				
		});
        findViewById(R.id.login_cancel).setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
        check.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				if ( check.isChecked() )
					rememberUser = true;
				else
					rememberUser = false;
				prefEditor.putBoolean(PREF_REMEMBER, rememberUser);
				prefEditor.commit(); 
			}
		});
    }

    private static final int DIALOG_CONNECTING = 3000;
	protected Dialog onCreateDialog(int id ) {
		switch (id) {
			case DIALOG_CONNECTING: {
				ProgressDialog progressDialog =new ProgressDialog(LoginActivity.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(true);
				progressDialog.setMessage(getString(R.string.lb_login_cancel));
				progressDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
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
				if ( rememberUser )
				{
					SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
			        SharedPreferences.Editor prefEditor = loginSettings.edit();
					prefEditor.putString(PREF_USERNAME, user);
					prefEditor.putString(PREF_PASSWORD, pass);
					prefEditor.commit();
				}
				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				
			}
			else{	
				Log.e("Login","error");
				Toast.makeText(LoginActivity.this, "Fuck", Toast.LENGTH_LONG).show();
			}
        	removeDialog(DIALOG_CONNECTING);

        }

        
		@Override
		protected Boolean doInBackground(String ... url) {
				InputStream in = null;
				String page = "";
				try {
					Log.e("Login",url[0] );
					HttpsURLConnection conn = getUncheckedConnection(url[0]);
					conn.connect();
					in = conn.getInputStream();
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
					
					//Saving cookie for later using throughout the program
					String cookie = "";
     				String headerName=null;
					for (int i=1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
					    if (headerName.equalsIgnoreCase("Set-Cookie")) {
					    	cookie +=conn.getHeaderField(i)+";";
					    }
					}
					SessionCookie.getInstance().setCookie(cookie);
					Log.e("Login cookie" ,  cookie);

					bis.close();
					in.close();
					conn.disconnect();	
					JSONObject jObject = new JSONObject(page);
					return jObject.optBoolean("authenticated");					
				} catch (MalformedURLException e) {
				 // DEBUG
				 Log.e("DEBUG url exceptop: ", e.toString());
				} catch (IOException e) {
				 // DEBUG
				 Log.e("DEBUG: ioexcep ", e.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
		}
    }
    

    
    public static HttpsURLConnection getUncheckedConnection(String url){ 
		try {
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
    }
    
 
}