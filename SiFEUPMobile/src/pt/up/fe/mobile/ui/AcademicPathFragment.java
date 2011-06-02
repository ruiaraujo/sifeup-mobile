package pt.up.fe.mobile.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class AcademicPathFragment extends ListFragment {

	AcademicPath academicPath = new AcademicPath();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AnalyticsUtils.getInstance(getActivity()).trackPageView("/Exams");
        new AcademicPathTask().execute();

    }
	
	private class AcademicPathTask extends AsyncTask<Void, Void, String> {
		
		protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.e("AcademicPath","success");
    		}
			else{	
				Log.e("AcademicPath","error");
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
		protected String doInBackground(Void... theVoid) {
			/*
			String page = "";
		  	try {
	    			page = SifeupAPI.getExamsReply(
								SessionManager.getInstance().getLoginCode());
	    		if(	SifeupAPI.JSONError(page))
	    		{
		    		 return "";
	    		}
				
	    		JSONAcademicPath(page);
	    		
				return "Sucess";
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			*/
			return "";
		}
		 
	}
	
	private class AcademicPath{
		private String code; // "numero"
		private String state; // "estado"
		private String courseAcronym; // "cur_codigo"
		private String courseName; // "cur_nome"
		private String courseNameEn; // "cur_name"
		private String average; // "media"
		private int courseYears; // "anos_curso"
		private int numberEntries; // "inscricoes_ucs"
		ArrayList<UC> ucs = new ArrayList<UC>();
	}
	
	private class UC{
		private int semester; // "reg_d_codigo"
		private int year; // "a_lectivo"
		private int grade; // "resultado"
		private String courseAcronym; // "dis_codigo"
		private int equivalencesNumber; // "n_equiv"
		private int academicYear; // "ano_curricular"
		private String state; // "estado"
		private String type; // "tipo"
		private String name; // "nome"
		private String nameEn; // "name"
	}
	
	private boolean JSONAcademicPath(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		
		if(jObject.has("ucs")){
			Log.e("JSON", "academic path found");
			
			if(jObject.has("numero")) academicPath.code = jObject.getString("numero");
			if(jObject.has("estado")) academicPath.state = jObject.getString("estado");
			if(jObject.has("cur_codigo")) academicPath.courseAcronym = jObject.getString("cur_codigo");
			if(jObject.has("cur_nome")) academicPath.courseName = jObject.getString("cur_nome");
			if(jObject.has("cur_name")) academicPath.courseNameEn = jObject.getString("cur_name");
			if(jObject.has("media")) academicPath.average = jObject.getString("media");
			if(jObject.has("anos_curso")) academicPath.courseYears = jObject.getInt("anos_curso");
			if(jObject.has("inscricoes_ucs")) academicPath.courseYears = jObject.getInt("inscricoes_ucs");
			
			// iterate over ucs
			JSONArray jArray = jObject.getJSONArray("ucs");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jUc = jArray.getJSONObject(i);
				// new UC
				UC uc = new UC();
				
				if(jUc.has("reg_d_codigo")) uc.semester = jUc.getInt("reg_d_codigo");
				if(jUc.has("a_lectivo")) uc.year = jUc.getInt("a_lectivo");
				if(jUc.has("resultado")) uc.grade = jUc.getInt("resultado");
				if(jUc.has("dis_codigo")) uc.courseAcronym = jUc.getString("dis_codigo");
				if(jUc.has("n_equiv")) uc.equivalencesNumber = jUc.getInt("n_equiv");
				if(jUc.has("ano_curricular")) uc.academicYear = jUc.getInt("ano_curricular");
				if(jUc.has("estado")) uc.state = jUc.getString("estado");
				if(jUc.has("tipo")) uc.type = jUc.getString("tipo");
				if(jUc.has("nome")) uc.name = jUc.getString("nome");
				if(jUc.has("name")) uc.nameEn = jUc.getString("name");
				
				// add uc to academic path
				academicPath.ucs.add(uc);
			}
			Log.e("JSON", "academic path loaded");
			return true;
		}
		Log.e("JSON", "academic path not found");
		return false;
	}
	
}
