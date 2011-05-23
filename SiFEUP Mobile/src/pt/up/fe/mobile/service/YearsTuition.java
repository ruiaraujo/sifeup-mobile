package pt.up.fe.mobile.service;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class YearsTuition 
{
	String year;
	String courseCode;
	String courseName;
	String state;
	String type;
	Vector<Payment> payments;
	
	public YearsTuition(JSONObject yearInfo) 
	{
		//TODO: meter isto na classe que constroi este objecto
		/*JSONArray jPayments=yearInfo.optJSONArray("pagamentos");
		for(int i=0; i<jPayments.length(); i++)
		{
			
		}*/
		try 
		{
			this.year=yearInfo.getString("ano_lectivo");
			this.courseCode=yearInfo.getString("curso_sigla");
			this.courseName=yearInfo.getString("curso_nome");
			this.state=yearInfo.getString("estado");
			this.type=yearInfo.getString("tipo");
			
			JSONArray jChild=yearInfo.getJSONArray("planos_pag");
			JSONArray jPayments = (JSONArray) jChild.get(0);
		
		
			payments=new Vector<Payment>();
			for(int i=0; i<jPayments.length(); i++)
			{
				payments.add(new Payment((JSONObject) jPayments.get(i)));
			}
			
			//TODO referencias MB
			//JSONArray jRefs=yearInfo.getJSONArray("referencias");
		} 
		catch (JSONException e) 
		{
			Log.e("Propinas","erro do JSON");
			e.printStackTrace();
		}
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Vector<Payment> getPayments() {
		return payments;
	}

	public void setPayments(Vector<Payment> payments) {
		this.payments = payments;
	}
	
}
