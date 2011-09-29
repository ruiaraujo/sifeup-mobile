package pt.up.fe.mobile.ui.studentservices;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.PasswordCheck;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;


/**
 * Change Password Fragment
 * 
 * @author Ângela Igreja
 *
 */
public class ChangePasswordFragment extends BaseFragment
{
	private EditText actualPasswordText;
	private EditText usernameText;
	private EditText newPasswordText;
	private EditText confirmNewPasswordText;
	private TextView newPasswordSecurity;
	private PasswordCheck checker;
	private int currentQuality = -1;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       AnalyticsUtils.getInstance(getActivity()).trackPageView("/Change Password");
    }
	 
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
    	View root = inflater.inflate(R.layout.change_password, getParentContainer(), true);
    	
    	/** Cancel */
    	Button cancel = (Button) root.findViewById(R.id.set_password_cancel);
    	cancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				if ( getActivity() == null )
					return;
				getActivity().finish();
			}
		});
    	
    	/** Confirm */
    	Button setPassword = (Button) root.findViewById(R.id.set_password_confirm);
    	setPassword.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				String username = usernameText.getText().toString();
		    	if(username.equals(""))
		    	{
		    		Toast.makeText(getActivity(), "Username can not be empty.",Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	String actualPassword = actualPasswordText.getText().toString();
		    	
		    	if(actualPassword.equals(""))
		    	{
		    		Toast.makeText(getActivity(), "Current password can not be empty.",Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	if ( currentQuality <= 1  )
		    	{
		    		;    	//TODO: error message pass
		    		return;
		    	}
				new PasswordTask().execute();
			}
		});
    	
    	/** Username */
    	newPasswordSecurity = (TextView) root.findViewById(R.id.new_password_security);
    	
    	/** Username */
    	usernameText = (EditText) root.findViewById(R.id.username);
 
    	/** Current Password */
    	actualPasswordText = (EditText) root.findViewById(R.id.current_password);    	
    	
    	/** New Password */
    	newPasswordText = (EditText) root.findViewById(R.id.new_password);
    	
    	newPasswordText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
	
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String password = s.toString();
    	        currentQuality = checker.validatePassword(password);
    	        //TODO: switch case para colocar texto
    	        newPasswordSecurity.setText(" Pass Security = " + currentQuality);
    	    }
    	});
    	
    	
    	/** Confirm New Password */
    	confirmNewPasswordText = (EditText) root.findViewById(R.id.confirm_new_password);
    	
    	
    	
    	new AsyncTask<Void , Void, Void>() {
            protected void onPostExecute(Void result) {
            	showMainScreen();
            }
            
			protected Void doInBackground(Void... params) {
			    checker = new PasswordCheck();
				return null;
			}
		}.execute();
        SharedPreferences loginSettings = getActivity().getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        boolean rememberUser = loginSettings.getBoolean(LoginActivity.PREF_REMEMBER, false);
        if ( rememberUser )
        {
        	String user = loginSettings.getString(LoginActivity.PREF_USERNAME, "");
        	String pass = loginSettings.getString(LoginActivity.PREF_PASSWORD, "") ;
        	if ( !user.equals("") && !pass.equals("") )
        	{
        		usernameText.setText(user);
        		actualPasswordText.setText(pass);
        		
        	}
        }
		return getParentContainer();
	} 
    private String errorTitle;
    private String errorContent;
    private void getError(String page) throws JSONException{
    	JSONObject jObject = new JSONObject(page);
		if(jObject.has("erro")) errorTitle = (String) jObject.get("erro");
		if(jObject.has("erro_msg")) errorContent = (String) jObject.get("erro_msg");
    }
    
    /** Classe privada para a busca de dados ao servidor */
    private class PasswordTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null )
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
    	}

        protected void onPostExecute(String result) {
			if ( getActivity() == null )
				 return;//TODO: move this string to xml
			getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        	if ( result.equals("Success") )
        	{
        		
        		Toast.makeText(getActivity(), "Password successfully changed.",Toast.LENGTH_SHORT).show();
    		}
			else if ( result.equals("Error") ){	
				//TODO: present dialog with error
				DialogFragment df = new DialogFragment(){//TODO: check how this works
        		    @Override
        		    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        		            Bundle savedInstanceState) {
        		    	TextView tv = new TextView(getActivity());
        		        tv.setText("This is an instance of MyDialogFragment");
        		        return tv;
        		    }
        		};
        		df.show(getFragmentManager(), "MyDF");
				Toast.makeText(getActivity(), "Error.",Toast.LENGTH_SHORT).show();
			}
			else if ( result.equals("") )
			{//TODO: problem with the webservice or no internet
				Toast.makeText(getActivity(), "Error.Empty!",Toast.LENGTH_SHORT).show();
			}
        }

		@Override
		//TODO: passar argumentos
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
	    		   page = SifeupAPI.getSetPasswordReply(usernameText.getText().toString(),
	    				   								actualPasswordText.getText().toString(), 
	    				   								newPasswordText.getText().toString(),
	    				   								confirmNewPasswordText.getText().toString(), "S");
	    			
	    		   int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				getError(page);
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				//TODO:parse the returned object  to check for erros
		    				return "Success";
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "";	
		    		}

				return "";
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
	  
	      
}

