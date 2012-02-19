package pt.up.fe.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Friend;
import pt.up.fe.mobile.datatypes.Profile;
import pt.up.fe.mobile.datatypes.Student;
import pt.up.fe.mobile.datatypes.Profile.ProfileDetail;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleFragment;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Student Profile Fragment
 * This interface is responsible for fetching the student's profile information 
 * to the server and shows it. Have one argument that is the number of student. 
 * 
 * @author Ângela Igreja
 */
public class StudentProfileFragment extends BaseFragment  implements OnItemClickListener
{
	private TextView name;
	private ListView details;
	private CheckBox friend;
	private TextView code;

	/** User Info */
    private Student me = new Student();
    private List<ProfileDetail> contents;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/StudentProfile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile, getParentContainer(), true);
		name = ((TextView)root.findViewById(R.id.profile_name));
		code = ((TextView)root.findViewById(R.id.profile_code));
		details = ((ListView)root.findViewById(R.id.profile_details));
		friend = ((CheckBox)root.findViewById(R.id.profile_star_friend));
		String code = getArguments().get(ProfileActivity.PROFILE_CODE).toString();
		
		//You can't friend yourself
		if ( code.equals(SessionManager.getInstance().getLoginCode()) )
			friend.setVisibility(View.INVISIBLE);
		friend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Friend fr = new Friend(me.getCode(),me.getName(), me.getProgrammeAcronym());
				if ( friend.isChecked())
					SessionManager.friends.addFriend(fr);
				else
					SessionManager.friends.removeFriend(fr);
				SessionManager.friends.saveToFile(getActivity());
			}
		});
		((Button)root.findViewById(R.id.profile_link_schedule)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), ScheduleActivity.class);
	    		i.putExtra(ScheduleFragment.SCHEDULE_CODE, me.getCode());
	    		i.putExtra(ScheduleFragment.SCHEDULE_TYPE, ScheduleFragment.SCHEDULE_STUDENT);
	    		startActivity(i);
			}
		});
		
		new ProfileTask().execute(code);
        return getParentContainer();
    }
    


    /** Classe privada para a busca de dados ao servidor */
    private class ProfileTask extends AsyncTask<String, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null ) 
        		return;
        	if ( result.equals("Success") )
        	{
				Log.e("Profile","success");
				contents = me.getProfileContents(getResources());
				name.setText(me.getName());
				code.setText(me.getCode());
				if ( SessionManager.friends.isFriend(me.getCode()) )
					friend.setChecked(true);
				else
					friend.setChecked(false);
				String[] from = new String[] { "title", "content" };
		        int[] to = new int[] { R.id.profile_item_title, R.id.profile_item_content };
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for ( ProfileDetail s : contents )   
		         { 
		        	 HashMap<String, String> map = new HashMap<String, String>();
		             map.put(from[0], s.title);
		             map.put(from[1],s.content);
		             fillMaps.add(map);
		         }
				 
		         // fill in the grid_item layout
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
		        		 							R.layout.list_item_profile, from, to);
		         details.setAdapter(adapter);
		         details.setOnItemClickListener(StudentProfileFragment.this);
		         details.setSelection(0);
		         showMainScreen();
			}
			else if ( result.equals("Error")) {	
				Log.e("Profile","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
					getActivity().finish();
					return;
				}
			}
			else if ( result.equals("")) {	
				Log.e("Profile","error");
				if ( getActivity() != null ) 	
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
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
	 * @param page
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
		if ( contents.get(position).type == Profile.Type.WEBPAGE )
		{
			final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contents.get(position).content));
			startActivity(browserIntent);
		}
		else if ( contents.get(position).type == Profile.Type.ROOM )
		{
			final Intent i = new Intent(getActivity() , ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,ScheduleFragment.SCHEDULE_ROOM) ;
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, contents.get(position).content  );
    		i.putExtra(Intent.EXTRA_TITLE , getString(R.string.title_schedule_arg,
    											contents.get(position).content));
    		startActivity(i);
		}
		else if ( contents.get(position).type == Profile.Type.EMAIL )
		{
			final Intent i = new Intent(Intent.ACTION_SEND);  
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{contents.get(position).content});
			startActivity(Intent.createChooser(i, getString(R.string.profile_choose_email_app))); 
		}
		else if ( contents.get(position).type == Profile.Type.MOBILE )
		{
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"+contents.get(position).content));
			startActivity(callIntent);
		}
	}
}
