package pt.up.fe.mobile.ui.studentservices;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    	Button cancel = (Button) root.findViewById(R.id.set_password_cancel);
    	cancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				if ( getActivity() == null )
					return;
				getActivity().finish();
			}
		});
    	
    	//TODO: Apenas quando confirm ver a qualidade da password????
    	Button setPassword = (Button) root.findViewById(R.id.set_password_confirm);
    	setPassword.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				new PasswordTask().execute();
			}
		});
    	showMainScreen();
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
				 return;
        	if ( result.equals("Success") )
        	{

    		}
			else if ( result.equals("Error") ){	
			
			}
			else if ( result.equals("") )
			{

			}
        }

		@Override
		//TODO: passar argumentos
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
	    		/*page = SifeupAPI.getSetPasswordReply(login, actualPassword, newPassword, confirmNewPassword, system)Reply(
								SessionManager.getInstance().getLoginCode(),
								"2010");*/
	    			int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:

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

