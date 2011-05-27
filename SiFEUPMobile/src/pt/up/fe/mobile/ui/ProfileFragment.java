package pt.up.fe.mobile.ui;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
		new ProfileTask().execute();
        return root;
    }
    

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    



    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_friends) {
                Toast.makeText(getActivity(), "Exemplo de Acção",
                        Toast.LENGTH_SHORT).show();
            
            return true;
        }
        return super.onOptionsItemSelected(item);
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
				name.setText(me.name);
				code.setText(me.code);
				email.setText(me.email);
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
    
    /** Stores all User Info */
	private class Student{
		private String code; // 090501049
		private String name; // "Nome do aluno"
		private String courseAcronym; // "Sigla do Curso"
		private String courseName; // "Nome do curso"
		private String registrationYear; // ano lectivo da matr�cula
		private String state; // "Estado (Frequentar, Interrompido,...)"
		private String academicYear; // (1|2|3|4|5)
		private String email; // "xxx@fe.up.pt"
		
		private void clearAll() {
			code = name = courseAcronym = 
				courseName = registrationYear = 
					state = academicYear = email = "";
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
			
			if(jObject.has("codigo")) me.code = jObject.getString("codigo");
			if(jObject.has("nome")) me.name = jObject.getString("nome");
			if(jObject.has("curso_sigla")) me.courseAcronym = jObject.getString("curso_sigla");
			if(jObject.has("curso_nome")) me.courseName = jObject.getString("curso_nome");
			if(jObject.has("ano_lect_matricula")) me.registrationYear = jObject.getString("ano_lect_matricula");
			if(jObject.has("estado")) me.state = jObject.getString("estado");
			if(jObject.has("ano_curricular")) me.academicYear = jObject.getString("ano_curricular");
			if(jObject.has("email")) me.email = jObject.getString("email");
			
			Log.e("JSON", "loaded student");
			return true;
		}
		Log.e("JSON", "student not found");
		return false;
	}
}
