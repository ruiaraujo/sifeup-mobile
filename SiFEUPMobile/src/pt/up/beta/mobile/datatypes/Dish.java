package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Dish. Save the information of dish.
 * 
 * @author Ângela Igreja
 * 
 */
public class Dish implements Parcelable {
    private String state;
    private String description;
    private int type;
    private String descriptionType;

    public Dish() {
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    public String getDescriptionType() {
        return descriptionType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(state!=null?1:0);
    	if ( state != null )
    		dest.writeString(state);
    	dest.writeInt(description!=null?1:0);
    	if ( description != null )
    		dest.writeString(description);
        dest.writeInt(type);
    	dest.writeInt(descriptionType!=null?1:0);
    	if ( descriptionType != null )
    		dest.writeString(descriptionType);
    }

    public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
        public Dish createFromParcel(Parcel in) {
            return new Dish(in);
        }

        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

    private Dish(Parcel in) {
    	if ( in.readInt() == 1 )
    		state = in.readString();
        if ( in.readInt() == 1 )
        	description = in.readString();
        type = in.readInt();
        if ( in.readInt() == 1 )
        	descriptionType = in.readString();
    }


}