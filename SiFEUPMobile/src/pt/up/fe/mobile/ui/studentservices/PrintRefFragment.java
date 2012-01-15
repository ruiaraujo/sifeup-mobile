

package pt.up.fe.mobile.ui.studentservices;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.RefMB;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

/**
 * This interface is responsible for fetching information from
 * ATM reference generated to charge the account
 * printing and shows it.
 *    
 * @author Ângela Igreja
 *
 */
public class PrintRefFragment extends BaseFragment {
	
	RefMB ref = new RefMB();
	private TextView nome;
	private TextView entidade;
	private TextView referencia;
	private TextView valor;
	private TextView dataFim;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Printing Ref");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	new PrintRefTask().execute(getArguments().get("value").toString());
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.ref_mb, getParentContainer() , true);
    	ref.setName(getString(R.string.lb_print_ref_title));
    	nome=(TextView)root.findViewById(R.id.tuition_ref_detail_name);
    	entidade = ((TextView)root.findViewById(R.id.tuition_ref_detail_entity));
    	referencia=(TextView)root.findViewById(R.id.tuition_ref_detail_reference);
    	valor=(TextView)root.findViewById(R.id.tuition_ref_detail_amount);
    	dataFim=(TextView)root.findViewById(R.id.tuition_ref_detail_date_end);
    	root.findViewById(R.id.tableRow4).setVisibility(View.GONE);

    	return getParentContainer(); //mandatory

    } 
    
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    



    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
        	try
			{
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
    private class PrintRefTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String saldo) {
        	if ( saldo.equals("Success") )
        	{
        		nome.setText(ref.getName());
        		entidade.setText(Long.toString(ref.getEntity()));
                String refStr = Long.toString(ref.getRef());
                while ( refStr.length() < 9 )
                    refStr = "0" + refStr;  
        		referencia.setText(refStr.substring(0,3) + " " + refStr.substring(3,6) + 
        		                    " " + refStr.substring(6,9));
        		valor.setText(ref.getAmount()+"€");
        		dataFim.setText(ref.getEndDate().format3339(true));
        		showMainScreen();
			}
			else if ( saldo.equals("Error") )  {	
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
					getActivity().finish();
					return;
				}
			}
			else if ( saldo.equals("") ){
				if ( getActivity() != null ) 	
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        }

		@Override
		protected String doInBackground(String ... value) {
			String page = "";
			try {
				if ( value.length < 1 )
					return "";
	    			page = SifeupAPI.getPrintingRefReply(value[0]);
	    		
	    			int error =	SifeupAPI.JSONError(page);
		    		switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    	    		JSONMBRef(page);
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
    /** 
	 * Schedule Parser
	 * Stores Blocks in ScheduleFragment.schedule
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
    public void JSONMBRef(String page) throws JSONException{
    	JSONObject jObject = new JSONObject(page);
    	
    	ref.setEntity(jObject.getLong("Entidade"));
    	ref.setRef(jObject.getLong("Referencia"));
    	ref.setAmount(jObject.getDouble("Valor"));
    	String[] end=jObject.getString("Data Limite").split("-");
		if(end.length==3)
		{
			Time endDate=new Time(Time.TIMEZONE_UTC);
			endDate.set(Integer.parseInt(end[2]), Integer.parseInt(end[1])-1, Integer.parseInt(end[0]));
			ref.setEndDate(endDate);
		}
    	
    		
    	Log.e("JSON", "loaded print ref");

    }
}