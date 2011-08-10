package pt.up.fe.mobile.ui.profile;


import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;

import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Teacher;
import pt.up.fe.mobile.ui.BaseActivity;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Teacher Profile Fragment
 * @author Ângela Igreja
 */
public class TeacherProfileFragment extends Fragment implements OnItemClickListener
{
	private TextView name;
	private ListView details;
	private TextView code;

	/** Teacher Info */
    private Teacher teacher = new Teacher();

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/TeacherProfile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) 
    {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile, null);
		
		name = ((TextView)root.findViewById(R.id.profile_name));
		
		code = ((TextView)root.findViewById(R.id.profile_code));
		
		details = ((ListView)root.findViewById(R.id.profile_details));
				
        return root;
    }
    


    /** Classe privada para a busca de dados ao servidor */
    private class TeacherProfileTask extends AsyncTask<String, Void, String> 
    {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null ) 
        		return;
        	
        	if ( result.equals("Success") )
        	{
				Log.e("TeacherProfile","success");
				name.setText(teacher.getName());
				code.setText(teacher.getCode());
			}
			else if ( result.equals("Error")) 
			{	
				Log.e("TeacherProfile","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")) 
			{	
				Log.e("TeacherProfile","error");
				if ( getActivity() != null ) 	
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(String ... code) 
		{
			String page = "";
		  	try {
		  		if ( code.length < 1 )
		  			return "";
	    			page = SifeupAPI.getStudentReply(code[0]);
	    		int error =	SifeupAPI.JSONError(page);
	    		
	    		switch (error)
	    		{
	    			case SifeupAPI.Errors.NO_AUTH:
	    				return "Error";
	    			case SifeupAPI.Errors.NO_ERROR:
	    				JSONStudent(page);
	    				return "Success";
	    			case SifeupAPI.Errors.NULL_PAGE:
	    				return "";	
	    		}

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
			
			if(jObject.has("codigo"))
				teacher.setCode(jObject.getString("codigo"));
			if(jObject.has("nome"))
				teacher.setName(jObject.getString("nome"));
			if(jObject.has("curso_sigla"))
				teacher.setProgrammeAcronym(jObject.getString("curso_sigla"));
			if(jObject.has("curso_nome"))
				teacher.setProgrammeName(jObject.getString("curso_nome"));
			if(jObject.has("ano_lect_matricula"))
				teacher.setRegistrationYear(jObject.getString("ano_lect_matricula"));
			if(jObject.has("estado"))
				teacher.setState(jObject.getString("estado"));
			if(jObject.has("ano_curricular"))
				teacher.setAcademicYear(jObject.getString("ano_curricular"));
			if(jObject.has("email"))
				teacher.setEmail(jObject.getString("email"));
			if(jObject.has("email_alternativo"))
				teacher.setEmailAlt(jObject.getString("email_alternativo"));
			if(jObject.has("telemovel"))
				teacher.setMobile(jObject.getString("telemovel"));
			if(jObject.has("telefone"))
				teacher.setTelephone(jObject.getString("telefone"));
			if(jObject.has("ramo"))
				teacher.setBranch(jObject.getString("ramo"));
			Log.e("JSON", "loaded student");
			return true;
		}
		Log.e("JSON", "student not found");
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		
	}
}
