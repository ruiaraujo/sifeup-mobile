package pt.up.fe.mobile.ui;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Student;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
	
	private TextView name;
	private TextView code;
	private TextView email;
	
	/** User Info */
    private Student me = new Student();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile, null);
		name = ((TextView)root.findViewById(R.id.profile_name));
		code = ((TextView)root.findViewById(R.id.profile_code));
		email = ((TextView)root.findViewById(R.id.profile_email));
		me = (Student) getArguments().get("profile");
		if ( me != null )
		{
			name.setText(me.getName());
			code.setText(me.getCode());
			email.setText(me.getEmail());
		}
		else
		{
			new ProfileTask().execute();
		}
        return root;
    }
    


    /** Classe privada para a busca de dados ao servidor */
    private class ProfileTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.e("Profile","success");
				name.setText(me.getName());
				code.setText(me.getCode());
				email.setText(me.getEmail());
			}
			else{	
				Log.e("Profile","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
	    			page = SifeupAPI.getStudentReply(
								SessionManager.getInstance().getLoginCode());
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
				
	    		JSONStudent(page);
	    		
				return page;
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    

	/**
	 * Parses a JSON String containing Student info,
	 * Stores that info at Collection me.
	 * @param String page
	 * @return boolean
	 * @throws JSONException
	 */
	public boolean JSONStudent(String page) throws JSONException{
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("codigo")){
			Log.e("JSON", "founded student");
			
			// clear old fields
			//me.clearAll();
			
			if(jObject.has("codigo")) me.setCode(jObject.getString("codigo"));
			if(jObject.has("nome")) me.setName(jObject.getString("nome"));
			if(jObject.has("curso_sigla")) me.setCourseAcronym(jObject.getString("curso_sigla"));
			if(jObject.has("curso_nome")) me.setCourseName(jObject.getString("curso_nome"));
			if(jObject.has("ano_lect_matricula")) me.setRegistrationYear(jObject.getString("ano_lect_matricula"));
			if(jObject.has("estado")) me.setState(jObject.getString("estado"));
			if(jObject.has("ano_curricular")) me.setAcademicYear(jObject.getString("ano_curricular"));
			if(jObject.has("email")) me.setEmail(jObject.getString("email"));
			
			Log.e("JSON", "loaded student");
			return true;
		}
		Log.e("JSON", "student not found");
		return false;
	}
}
