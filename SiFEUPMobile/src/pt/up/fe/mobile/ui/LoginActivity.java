package pt.up.fe.mobile.ui;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.sifeup.SifeupUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity 
{
	public static final String PREF_REMEMBER = "Remember Credentials";
	public static final String PREF_USERNAME = "Username";
	public static final String PREF_PASSWORD = "Password";
	public static final String PREF_COOKIE = "Cookie";
	public static final String PREF_COOKIE_TIME = "Cookie Time";
	public static final String PREF_USERNAME_SAVED = "Cookie User";
	
	
	public static final String EXTRA_DIFFERENT_LOGIN =
        					"pt.up.fe.mobile.extra.DIFFERENT_LOGIN";
	public static final int EXTRA_DIFFERENT_LOGIN_LOGOUT = 1;
	public static final int EXTRA_DIFFERENT_LOGIN_REVALIDATE = 2;

	private LoginTask logintask;
	private boolean rememberUser;
	private String user;
	private String pass;
	
	private EditText passwordEditText;
    private EditText username;
    private CheckBox rememberPassCheckbox;
    
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        logintask = null;
       
        setContentView(R.layout.login);
        SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
        final SharedPreferences.Editor prefEditor = loginSettings.edit();
        //como os objectos sao declarados em xml para termos a referencia deles temos de fazer isto
        username = (EditText) findViewById(R.id.login_username);
        passwordEditText = (EditText) findViewById(R.id.login_pass);
        rememberPassCheckbox = (CheckBox) findViewById(R.id.login_remember);
        final Button about = (Button) findViewById(R.id.login_about);
        //verficar se o utilizador carregou no remember me e se sim
        //preencher os campos com as informações gravadas
        rememberUser = loginSettings.getBoolean(PREF_REMEMBER, false);
        if ( rememberUser )
        {
        	user = loginSettings.getString(PREF_USERNAME, "");
        	pass = loginSettings.getString(PREF_PASSWORD, "") ;
        	if ( !user.equals("") && !pass.equals("") )
        	{
        		username.setText(user);
        		passwordEditText.setText(pass);
        	}
        }
    	rememberPassCheckbox.setChecked(rememberUser);
    	rememberPassCheckbox.setText("   " + rememberPassCheckbox.getText());
    	
    	
        findViewById(R.id.login_confirm).setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
	        	user = username.getText().toString().trim();
	        	if ( user.equals("") )
	        	{
	        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_username), Toast.LENGTH_SHORT).show();
	        		return;
	        	}
	        	pass = passwordEditText.getText().toString().trim();
	        	if ( pass.equals("") )
	        	{
	        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_password), Toast.LENGTH_SHORT).show();
	        		return;
	        	}

	        	logintask = new LoginTask();
	        	logintask.execute();

			}
				
		});

        rememberPassCheckbox.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				if ( rememberPassCheckbox.isChecked() )
				{
					rememberUser = true;
					int ul = username.getText().toString().trim().length();
					int pl = passwordEditText.getText().toString().trim().length();
		        	if ( ul == 0 )
		        	{
		        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_username), Toast.LENGTH_SHORT).show();
						rememberPassCheckbox.setChecked(false);
		        	}
		        	if ( pl == 0 )
		        	{
		        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_empty_password), Toast.LENGTH_SHORT).show();
						rememberPassCheckbox.setChecked(false);
		        	}
					else if(pl < 8)	// Password must be at least 8 chars long.
		        	{
		        		Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_password_too_short), Toast.LENGTH_SHORT).show();
						rememberPassCheckbox.setChecked(false);
		        	}
				}
				else
					rememberUser = false;
				prefEditor.putBoolean(PREF_REMEMBER, rememberUser); //sempre qe se carrega na checkbox, o programa guarda a preferencia.
				prefEditor.commit(); 
			}
		});
        
        about.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				showDialog(DIALOG_ABOUT);
			}
		});
        
        
        
        // A actividade de login pode ser chamada no launcher ou caso a pessoa faça logout
        // In case of a logout.
        Intent i = getIntent();
        final int action = i.getIntExtra(EXTRA_DIFFERENT_LOGIN, 0);
        switch( action ){
	        case 0:
	        {
		        //Take advantage of the 24h period while the session
		        // is still active on the main server.
	        	if ( SifeupUtils.checkCookie(this) ){
	    	        SessionManager.getInstance().setCookie(loginSettings.getString( PREF_COOKIE, ""));
		        	SessionManager.getInstance().setLoginCode(loginSettings.getString( PREF_USERNAME_SAVED, ""));
		        	startActivity(new Intent(LoginActivity.this, HomeActivity.class));
	        		finish();
	        		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	        		return;
	        	}

		        if ( rememberUser )
		        {
			        logintask = new LoginTask();
		    		logintask.execute();
		        }
		        break;
        	}
	        case EXTRA_DIFFERENT_LOGIN_LOGOUT:
	        {	// if logging out the cookie is removed
	        	prefEditor.putString(PREF_COOKIE, "");
	        	prefEditor.commit(); 
	        }
	        break;
	        case EXTRA_DIFFERENT_LOGIN_REVALIDATE:
	        {
	        	// if logging out the cookie is removed
	        	prefEditor.putString(PREF_COOKIE, "");
	        	prefEditor.commit(); 
		        if ( rememberUser )
		        {
			        logintask = new LoginTask();
		    		logintask.execute();
		        }
		        break;
	        }
        }
        
    }

    private static final int DIALOG_CONNECTING = 3000;
    private static final int DIALOG_ABOUT = 3001;

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
			case DIALOG_ABOUT:{
				AlertDialog.Builder aboutDialog =  new Builder(this);
				aboutDialog.setTitle(R.string.bt_about_title)
				.setNegativeButton(R.string.bt_cancel, new  DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						removeDialog(DIALOG_ABOUT);
					}
				})
				.setMessage(R.string.bt_about_message); 
				AlertDialog welcomeAlert = aboutDialog.create();
		        welcomeAlert.show();
		        // Make the textview clickable. Must be called after show()
		        ((TextView)welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
				return welcomeAlert;
			}	
		}
		return null;
	}
	
    /**
     * AsyncTask for login functionality.
     * Background class that checks the authentication on 
     * the server and saves the session cookie for later use.
     * 
     * @author angela
     */
    private class LoginTask extends AsyncTask<Void, Void, Boolean> {


		/**
    	 * This function is invoked on the UI thread immediately 
    	 * after the task is executed. 
    	 */
    	protected void onPreExecute (){
    		showDialog(DIALOG_CONNECTING);  
    	}

    	/**
    	 * This function is invoked on the UI thread after the 
    	 * background computation finishes.
    	 * @param result The result of the background computation
    	 */
        protected void onPostExecute(Boolean result) {
        	if ( result == null )
        	{
        		Log.e("Login", "error page is null");
				Toast.makeText(LoginActivity.this, getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
	        	removeDialog(DIALOG_CONNECTING);
				return;
        	}
        	
        	if ( result )
        	{
				SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
		        SharedPreferences.Editor prefEditor = loginSettings.edit();
		        prefEditor.putString(PREF_COOKIE, SessionManager.getInstance().getCookie());
				prefEditor.putLong(PREF_COOKIE_TIME, System.currentTimeMillis());
				prefEditor.putString(PREF_USERNAME_SAVED, SessionManager.getInstance().getLoginCode());
				Log.e("Login", "success");
				if ( rememberUser )
				{
					prefEditor.putString(PREF_USERNAME, user);
					prefEditor.putString(PREF_PASSWORD, pass);
				}
				prefEditor.commit();

		        final Intent i = getIntent();
		        final int action = i.getIntExtra(EXTRA_DIFFERENT_LOGIN, 0);
		        
		        //only start the new activity if not revalidating
		        if ( action != EXTRA_DIFFERENT_LOGIN_REVALIDATE )
		        	startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
			}
			else{	
				Log.e("Login", "error");
		        final Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
		        Toast.makeText(LoginActivity.this, getString(R.string.toast_login_error_wrong_password), Toast.LENGTH_LONG).show();
				// Remove stored password...
				passwordEditText.setText("");
				passwordEditText.startAnimation(shake);
				rememberPassCheckbox.setChecked(false);
			}
        	removeDialog(DIALOG_CONNECTING);
        }

        /**
         * This function is invoked on the background thread immediately after 
         * onPreExecute() finishes executing. 
         * @param theVoid The parameters of the asynchronous task
         * @return The result of the computation that will 
         * be passed to function onPostExecute.
         */
		@Override
		protected Boolean doInBackground(Void ... theVoid) {
				String page = "";
				try {					
					page = SifeupAPI.getAuthenticationReply(user, pass);
					if ( page == null )
						return null;
					JSONObject jObject = new JSONObject(page);
					if ( jObject.optBoolean("authenticated") )
					{
						SessionManager.getInstance().setLoginCode(jObject.getString("codigo"));
						SifeupUtils.storeConnectionType(LoginActivity.this);
						if ( jObject.getString("tipo").equals("A") )
							return Boolean.TRUE;
						else
							return null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return Boolean.FALSE;
		}
    }
    
}
