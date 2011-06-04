package pt.up.fe.mobile.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Student;
import pt.up.fe.mobile.service.Student.StudentDetail;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileFragment extends Fragment  implements OnItemClickListener{
	
	private TextView name;
	private ListView details;
	
	/** User Info */
    private Student me = new Student();
    private List<StudentDetail> contents;
    
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
		details = ((ListView)root.findViewById(R.id.profile_details));
		String code = getArguments().get("profile").toString();
		if ( code != null )
		{
			new ProfileTask().execute(code);
		}
		else
			new ProfileTask().execute(SessionManager.getInstance().getLoginCode());
        return root;
    }
    


    /** Classe privada para a busca de dados ao servidor */
    private class ProfileTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null ) 
        		return;
        	if ( !result.equals("") )
        	{
				Log.e("Profile","success");
				contents = me.getStudentContents(getResources());
				name.setText(me.getName());
				String[] from = new String[] { "title", "content" };
		         int[] to = new int[] { R.id.profile_item_title, R.id.profile_item_content };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for ( StudentDetail s : contents )   
		         {
		        	 HashMap<String, String> map = new HashMap<String, String>();
		             map.put("title", s.title);
		             map.put("content",s.content);
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
		        		 							R.layout.list_item_profile, from, to);
		         details.setAdapter(adapter);
		         details.setOnItemClickListener(ProfileFragment.this);
		         details.setSelection(0);
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
		protected String doInBackground(String ... code) {
			String page = "";
		  	try {
		  		if ( code.length < 1 )
		  			return "";
	    			page = SifeupAPI.getStudentReply(code[0]);
	    		int error =	SifeupAPI.JSONError(page);
	    		switch (error)
	    		{
	    		case SifeupAPI.Errors.NO_AUTH: return "";
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
			
			if(jObject.has("codigo"))
				me.setCode(jObject.getString("codigo"));
			if(jObject.has("nome"))
				me.setName(jObject.getString("nome"));
			if(jObject.has("curso_sigla"))
				me.setProgrammeAcronym(jObject.getString("curso_sigla"));
			if(jObject.has("curso_nome"))
				me.setProgrammeName(jObject.getString("curso_nome"));
			if(jObject.has("ano_lect_matricula"))
				me.setRegistrationYear(jObject.getString("ano_lect_matricula"));
			if(jObject.has("estado"))
				me.setState(jObject.getString("estado"));
			if(jObject.has("ano_curricular"))
				me.setAcademicYear(jObject.getString("ano_curricular"));
			if(jObject.has("email"))
				me.setEmail(jObject.getString("email"));
			if(jObject.has("email_alternativo"))
				me.setEmailAlt(jObject.getString("email_alternativo"));
			if(jObject.has("telemovel"))
				me.setMobile(jObject.getString("telemovel"));
			if(jObject.has("telefone"))
				me.setTelephone(jObject.getString("telefone"));
			if(jObject.has("ramo"))
				me.setBranch(jObject.getString("ramo"));
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
