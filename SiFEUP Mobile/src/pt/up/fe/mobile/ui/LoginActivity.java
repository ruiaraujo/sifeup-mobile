package pt.up.fe.mobile.ui;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
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
	public static final String PREF_REMEMBER = "Remember Credentials";
	public static  final String PREF_USERNAME = "Username";
	public static  final String PREF_PASSWORD = "Password";
	public static  final String PREF_COOKIE = "Cookie";
	public static  final String PREF_COOKIE_TIME = "Cookie Time";
	public static  final String PREF_USERNAME_SAVED = "Cookie User";
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
        long now = System.currentTimeMillis();
        long before = loginSettings.getLong( PREF_COOKIE_TIME, 0);
        String oldCookie = loginSettings.getString( PREF_COOKIE, "");
        if ( ( ( now - before )/3600000 < 24 ) &&  !oldCookie.equals("") )
        {
        	SessionManager.getInstance().setCookie(oldCookie);
        	SessionManager.getInstance().setLoginCode(loginSettings.getString( PREF_USERNAME_SAVED, ""));
        	startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        	finish();
        	return;
        }
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
        		logintask.execute();
        	}
        }
        findViewById(R.id.login_confirm).setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
	        	user = username.getText().toString().trim();
	        	if ( user.equals("") )
	        	{
	        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_username), Toast.LENGTH_SHORT);
	        		return;
	        	}
	        	pass = password.getText().toString().trim();
	        	if ( pass.equals("") )
	        	{
	        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_password), Toast.LENGTH_SHORT);
	        		return;
	        	}
	        	logintask = new LoginTask();
	        	logintask.execute();
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
	
    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

    	protected void onPreExecute (){
    		showDialog(DIALOG_CONNECTING);  
    	}

        protected void onPostExecute(Boolean result) {
        	if ( result )
        	{
				SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
		        SharedPreferences.Editor prefEditor = loginSettings.edit();
		        prefEditor.putString(PREF_COOKIE, SessionManager.getInstance().getCookie());
				prefEditor.putLong(PREF_COOKIE_TIME, System.currentTimeMillis());
				prefEditor.putString(PREF_USERNAME_SAVED, SessionManager.getInstance().getLoginCode());
				Log.e("Login","success");
				if ( rememberUser )
				{

					prefEditor.putString(PREF_USERNAME, user);
					prefEditor.putString(PREF_PASSWORD, pass);
				}
				prefEditor.commit();
				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				finish();
			}
			else{	
				Log.e("Login","error");
				Toast.makeText(LoginActivity.this, "F***", Toast.LENGTH_LONG).show();
			}
        	removeDialog(DIALOG_CONNECTING);
        }

		@Override
		protected Boolean doInBackground(Void ... theVoid) {
				String page = "";
				try {					
					SessionManager.getInstance().setLoginCode(user);
					page = SifeupAPI.getAuthenticationReply(user, pass);
					JSONObject jObject = new JSONObject(page);
					return jObject.optBoolean("authenticated");					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
		}
    }
    

    
   
    
 
}
