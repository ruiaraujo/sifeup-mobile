package pt.up.fe.mobile.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;

public class RefMB implements Parcelable
{	
	String name;
	long entity;
	long ref;
	double amount;
	Time startDate;
	Time endDate;
	
	public RefMB(){
		
	}
	
	public boolean load(JSONObject jRef)
	{
		try 
		{
			this.name=jRef.getString("designacao");
			this.entity=jRef.getLong("entidade");
			this.ref=jRef.getLong("referencia");
			this.amount=jRef.getDouble("valor");
			
			String[] start=jRef.getString("pagamento_ini").split("-");
			if(start.length==3)
			{
				this.startDate=new Time(Time.TIMEZONE_UTC);
				this.startDate.set(Integer.parseInt(start[2]), Integer.parseInt(start[1])-1, Integer.parseInt(start[0]));
			}
			String[] end=jRef.getString("pagamento_fim").split("-");
			if(end.length==3)
			{
				this.endDate=new Time(Time.TIMEZONE_UTC);
				this.endDate.set(Integer.parseInt(end[2]), Integer.parseInt(end[1])-1, Integer.parseInt(end[0]));
			}
			return true;
		} 
		catch (JSONException e) 
		{
			Log.e("Propinas", "JSON error in RefMB");
			e.printStackTrace();
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getEntity() {
		return entity;
	}

	public void setEntity(long entity) {
		this.entity = entity;
	}

	public long getRef() {
		return ref;
	}

	public void setRef(long ref) {
		this.ref = ref;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Time getStartDate() {
		return startDate;
	}

	public void setStartDate(Time startDate) {
		this.startDate = startDate;
	}

	public Time getEndDate() {
		return endDate;
	}

	public void setEndDate(Time endDate) {
		this.endDate = endDate;
	}

	public int describeContents() {
		return 0;
	}
	

    public static final Parcelable.Creator<RefMB> CREATOR = new Parcelable.Creator<RefMB>() {
        public RefMB createFromParcel(Parcel in) {
            return new RefMB(in);
        }

        public RefMB[] newArray(int size) {
            return new RefMB[size];
        }
    };

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeLong(entity);
		dest.writeLong(ref);
		dest.writeDouble(amount);
		dest.writeString(startDate.format3339(true));
		dest.writeString(endDate.format3339(true));
	}
	
	private RefMB( Parcel in){
		name = in.readString();
		entity = in.readLong();
		ref = in.readLong();
		amount = in.readDouble();
		String[] start=in.readString().split("-");
		this.startDate=new Time(Time.TIMEZONE_UTC);
		this.startDate.set(Integer.parseInt(start[2]), Integer.parseInt(start[1])-1, Integer.parseInt(start[0]));
		String[] end=in.readString().split("-");
		this.endDate=new Time(Time.TIMEZONE_UTC);
		this.endDate.set(Integer.parseInt(end[2]), Integer.parseInt(end[1])-1, Integer.parseInt(end[0]));
		
	}
		
}
