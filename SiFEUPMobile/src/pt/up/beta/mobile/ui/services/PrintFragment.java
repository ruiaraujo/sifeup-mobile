

package pt.up.beta.mobile.ui.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.up.beta.mobile.sifeup.PrinterUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.R;

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
public class PrintFragment extends BaseFragment implements ResponseCommand{

	private final static String PRINTERS_KEY = "pt.up.fe.mobile.ui.studentservices.PRINTING_QUOTA";

	
    private String saldo;
    private TextView display;
    private TextView desc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState != null )
        {
            saldo = savedInstanceState.getString(PRINTERS_KEY);
            if ( saldo == null )
                task = PrinterUtils.getPrintReply(AccountUtils.getActiveUserCode(getActivity()), this);
            else
            {
                displayData();
                showMainScreen();
            }
        }
        else
        {
            task = PrinterUtils.getPrintReply(AccountUtils.getActiveUserCode(getActivity()), this);
        }
    }

 	@Override
 	public void onSaveInstanceState (Bundle outState){
 		if ( saldo != null )
 			outState.putString(PRINTERS_KEY,saldo);
 	}
    
	public void onError(ERROR_TYPE error) {
		if ( getActivity() == null )
	 		return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
			goLogin();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if ( getActivity() == null )
			return;
		saldo = results[0].toString();
		displayData();
		showMainScreen();
	}
	
	private void displayData(){
		display.setText(getString(R.string.print_balance, saldo));
		PrintFragment.this.saldo = saldo;
		long pagesA4Black =  Math.round(Double.parseDouble(saldo) / 0.03f);
		if ( pagesA4Black > 0 )
			desc.setText(getString(R.string.print_can_print_a4_black, Long.toString(pagesA4Black)));
	}

	protected void onRepeat() {
		showLoadingScreen();
        task = PrinterUtils.getPrintReply(AccountUtils.getActiveUserCode(getActivity()), this);
	}

}