

package pt.up.beta.mobile.ui.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.sifeup.PrinterUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseLoadingFragment;
import pt.up.beta.mobile.R;

/**
 * This interface is responsible for fetching information from
 * ATM reference generated to charge the account
 * printing and shows it.
 *    
 * @author Ângela Igreja
 *
 */
public class PrintRefFragment extends BaseLoadingFragment implements ResponseCommand<RefMB>{
	
	private final static String PRINT_REF_KEY = "pt.up.fe.mobile.ui.studentservices.PRINTING_REF";

	private RefMB ref;
	private TextView nome;
	private TextView entidade;
	private TextView referencia;
	private TextView valor;
	private TextView dataFim;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ref_mb, getParentContainer() , true);
    	nome=(TextView)root.findViewById(R.id.tuition_ref_detail_name);
    	entidade = ((TextView)root.findViewById(R.id.tuition_ref_detail_entity));
    	referencia=(TextView)root.findViewById(R.id.tuition_ref_detail_reference);
    	valor=(TextView)root.findViewById(R.id.tuition_ref_detail_amount);
    	dataFim=(TextView)root.findViewById(R.id.tuition_ref_detail_date_end);
    	root.findViewById(R.id.tableRow4).setVisibility(View.GONE);


    	return getParentContainer(); //mandatory

    } 

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);        
        if ( savedInstanceState != null )
        {
            ref = savedInstanceState.getParcelable(PRINT_REF_KEY);
            if ( ref == null )
                task = PrinterUtils.getPrintRefReply(getArguments().getString("value"), this, getActivity());
            else
            {
                displayData();
                showMainScreen();
            }
        }
        else
        {
            task = PrinterUtils.getPrintRefReply(getArguments().getString("value"), this, getActivity());
        }
    }
    
    @Override   
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
 	@Override
 	public void onSaveInstanceState (Bundle outState){
 		if ( ref != null )
 			outState.putParcelable(PRINT_REF_KEY, ref);
 	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
        	try
			{
        		if ( ref == null || ref.getStartDate() == null )
        			return true;
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_SUBJECT ,ref.getName() );
				StringBuilder message = new StringBuilder(getString(R.string.lbl_tuition_ref_detail_entity) + ": " + 
												ref.getEntity() + "\n");
			
				String refStr = Long.toString(ref.getRef());
                while ( refStr.length() < 9 )
                    refStr = "0" + refStr; 
				message.append(getString(R.string.lbl_tuition_ref_detail_reference) + ": " + 
        				        refStr.substring(0,3) + " " + refStr.substring(3,6) + 
                                " " + refStr.substring(6,9) + "\n");
				message.append(getString(R.string.lbl_tuition_ref_detail_amount) + ": " + 
								ref.getAmount() + "\n");
				message.append(getString(R.string.lbl_tuition_ref_detail_date_end) + ": " + 
								ref.getEndDate().format3339(true) + "\n");
				i.putExtra(Intent.EXTRA_TEXT, message.toString());
				startActivity(Intent.createChooser(i, getString(R.string.print_ref_share_title)));
			}
			catch(Exception e)
			{
				Toast.makeText( getActivity() , getString(R.string.print_ref_share_err) , 
						Toast.LENGTH_SHORT).show();
			}
            return true;
        }
        return super.onOptionsItemSelected(item);
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

	public void onResultReceived(RefMB result) {
		ref = result;
		displayData();
		showMainScreen();
	}
	
	private void displayData(){
    	ref.setName(getString(R.string.lb_print_ref_title));
    	nome.setText(ref.getName());
		entidade.setText(Long.toString(ref.getEntity()));
        String refStr = Long.toString(ref.getRef());
        while ( refStr.length() < 9 )
            refStr = "0" + refStr;  
		referencia.setText(refStr.substring(0,3) + " " + refStr.substring(3,6) + 
		                    " " + refStr.substring(6,9));
		valor.setText(ref.getAmount()+"€");
		dataFim.setText(ref.getEndDate().format3339(true));
	}

	protected void onRepeat() {
		showLoadingScreen();
        task = PrinterUtils.getPrintRefReply(getArguments().getString("value"), this, getActivity());
	}

}