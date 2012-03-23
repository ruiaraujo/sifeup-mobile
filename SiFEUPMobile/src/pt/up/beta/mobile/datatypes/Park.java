package pt.up.beta.mobile.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
       
/**
 * Class Park Occupation
 * 
 * @author Ângela Igreja
 *
 */
public class Park implements Parcelable 
{
	/** Free places in the park */
	private int places;
	private String name;
	
	public Park(){
		places = 0;
		name = "";
	}

	public String getPlaces() {
		return Integer.toString(places);
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public int getPlacesNumber() {
		return places;
	}

	public void setPlaces(int places) {
		this.places = places;
	}

	
	/**
	 *  Park Occupation Parser.
	 *  Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return itself
	 * @throws JSONException 
	 */
    public Park JSONParkOccupation(String page) throws JSONException
    {
    	JSONObject jObject = new JSONObject(page);
			
		if(jObject.has("lugares"))
			this.places = jObject.getInt("lugares");
		return this;

    }

	public int describeContents() {
		return 0;
	}
	
    public static final Parcelable.Creator<Park> CREATOR = new Parcelable.Creator<Park>() {
        public Park createFromParcel(Parcel in) {
            return new Park(in);
        }

        public Park[] newArray(int size) {
            return new Park[size];
        }
    };
	
	private Park(Parcel in){
		places = in.readInt();
		if ( in.readInt() == 1 )
			name = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(places);
		dest.writeInt(name!=null?1:0);
		if ( name != null )
			dest.writeString(name);
	}
    
}
