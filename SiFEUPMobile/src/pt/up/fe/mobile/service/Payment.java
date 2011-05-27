package pt.up.fe.mobile.service;

import java.io.IOException;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

public class Payment {

	static enum State {PAID, LATE_ON_PAYMENT, TO_BE_PAID}
	State state;
	String name;
	Time dueDate;
	double amount;
	double amountPaid;
	double amountDebt;
	
	public boolean load(JSONObject payment)
	{
		String sit;
		try 
		{
			sit = payment.getString("situacao");
		
			if(sit.equals("Valor pago na totalidade"))
				this.state=State.PAID;
			else if(sit.equals("Valor não pago tendo sido já excedido o prazo"))
				this.state=State.LATE_ON_PAYMENT;
			else if(sit.equals("Valor não pago mas o prazo ainda não foi excedido"))
				this.state=State.TO_BE_PAID;
			else
				Log.e("Propinas", "estado nao contemplado");
			
			this.name=payment.getString("nome_prestacao");
			String[] dueDateString=payment.getString("data_limite_pag").split("-");
			//TODO: GregorianCalendar is very very very slow. Check if Time can't do the same thing
			if(dueDateString.length==3)
			{
				this.dueDate=new Time(Time.TIMEZONE_UTC);
				this.dueDate.set(Integer.parseInt(dueDateString[2]), Integer.parseInt(dueDateString[1])-1, Integer.parseInt(dueDateString[0]));
			}
			this.amount=payment.getDouble("valor");
			this.amountPaid=payment.getDouble("valor_pago");
			this.amountDebt=payment.getDouble("valor_divida");
			return true;
		} 
		catch (JSONException e) 
		{
			Log.e("Propinas", "JSON error in payment");
			//e.printStackTrace();
			return false;
		}
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Time getDueDate() {
		return dueDate;
	}

	public void setDueDate(Time dueDate) {
		this.dueDate = dueDate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public double getAmountDebt() {
		return amountDebt;
	}

	public void setAmountDebt(double amountDebt) {
		this.amountDebt = amountDebt;
	}
}
