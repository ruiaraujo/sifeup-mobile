package pt.up.fe.mobile.service;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

public class YearsTuition 
{
	String year;
	String courseCode;
	String courseName;
	String state;
	String type;
	Vector<Payment> payments;
	double total_paid=0;
	double total_in_debt=0;
	
	public YearsTuition() 
	{
		payments=new Vector<Payment>();
	}
	
	public boolean load(JSONObject yearInfo)
	{
		try 
		{
			this.year=yearInfo.getString("ano_lectivo");
			this.courseCode=yearInfo.getString("curso_sigla");
			this.courseName=yearInfo.getString("curso_nome");
			this.state=yearInfo.getString("estado");
			this.type=yearInfo.getString("tipo");
			
			JSONArray jChild=yearInfo.getJSONArray("planos_pag");
			
			JSONObject jPayment = jChild.getJSONObject(0);	
			JSONArray jPayments=jPayment.getJSONArray("prestacoes");
		
			payments=new Vector<Payment>();
			for(int i=0; i<jPayments.length(); i++)
			{
				payments.add(new Payment(jPayments.getJSONObject(i)));
			}
			calculateAmounts();
			return true;
			//TODO referencias MB
			//JSONArray jRefs=yearInfo.getJSONArray("referencias");
		} 
		catch (JSONException e) 
		{
			Log.e("Propinas","JSON error in year");
			return false;
			//e.printStackTrace();
		}
	}
	
	public void calculateAmounts()
	{
		for(Payment p: this.payments)
		{
			this.total_paid+=p.getAmountPaid();
			this.total_in_debt+=p.getAmountDebt();
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

	public double getTotal_paid() {
		return total_paid;
	}

	public void setTotal_paid(double totalPaid) {
		total_paid = totalPaid;
	}

	public double getTotal_in_debt() {
		return total_in_debt;
	}

	public void setTotal_in_debt(double totalInDebt) {
		total_in_debt = totalInDebt;
	}
	
}
