

package pt.up.fe.mobile.ui.studentservices;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

/**
 * Esta interface está responsável por ir buscar a informação
   do saldo de impressão ao servidor e mostra-la. Existe um
   campo para inserção de um valor e um botão que inicia a
     actividade PrintRefActivity.
     
 * This interface is responsible for fetching the information
 *   the balance to the print server and shows it. There is a
 *   field to insert a value and a button that starts
 *    PrintRefActivity activity.
 *    
 * @author Ângela Igreja
 *
 */
public class PrintFragment extends BaseFragment {

    private String saldo;
    private TextView display;
    private TextView desc;
    public String getSaldo() {
		return saldo;
	}

	public  void setSaldo(String saldo) {
		this.saldo = saldo;
	}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Printing");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	new PrintTask().execute();
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.print_balance, getParentContainer(), true);
    	display = ((TextView)root.findViewById(R.id.print_balance));
    	desc = ((TextView)root.findViewById(R.id.print_desc));
    	final EditText value = (EditText)root.findViewById(R.id.print_value);
    	root.findViewById(R.id.print_generate_reference).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newValue = value.getText().toString().trim();
				try{
					Double.valueOf(newValue);
				}
				catch (NumberFormatException e) {
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					value.requestFocus();
					return;
				}
				newValue = newValue.replace(".",",");
				Intent i = new Intent(getActivity(), PrintRefActivity.class);
				i.putExtra("value", newValue);
				startActivity(i);
			}
		});
    	return getParentContainer(); //mandatory

    }
    private class PrintTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String saldo) {
        	if ( getActivity() == null )
        		return;
        	if ( saldo.equals("") )
        	{
        		if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
			else if ( saldo.equals("Error") ){	
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
					return;
				}
			}
			else{
				Log.e("Login","success");
				display.setText(getString(R.string.print_balance, saldo));
				PrintFragment.this.saldo = saldo;
				long pagesA4Black =  Math.round(Double.parseDouble(saldo) / 0.03f);
				if ( pagesA4Black > 0 )
					desc.setText(getString(R.string.print_can_print_a4_black, Long.toString(pagesA4Black)));
				showMainScreen();
			}
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
			try {
	    			page = SifeupAPI.getPrintingReply(
								SessionManager.getInstance().getLoginCode());
	    		
	    			int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				return new JSONObject(page).optString("saldo");
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