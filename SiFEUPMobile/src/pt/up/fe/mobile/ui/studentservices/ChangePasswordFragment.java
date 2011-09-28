package pt.up.fe.mobile.ui.studentservices;

import org.json.JSONException;

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
    	//TODO: Apenas quando confirm ver a qualidade da password????
    	Button setPassword = (Button) root.findViewById(R.id.set_password_confirm);
    	setPassword.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				String username = usernameText.getText().toString();
		    	
		    	if(username.equals(""))
		    	{
		    		Toast.makeText(getActivity(), "Username can not be empty.",Toast.LENGTH_SHORT).show();
		    	}
		    	String actualPassword = actualPasswordText.getText().toString();
		    	
		    	if(actualPassword.equals(""))
		    	{
		    		Toast.makeText(getActivity(), "Current password can not be empty.",Toast.LENGTH_SHORT).show();
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
    	        int result = checker.validatePassword(password);
    	        //TODO: switch case para colocar texto
    	        newPasswordSecurity.setText(" Pass Security = " + result);
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
				Toast.makeText(getActivity(), "Error.",Toast.LENGTH_SHORT).show();
			}
			else if ( result.equals("") )
			{
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

