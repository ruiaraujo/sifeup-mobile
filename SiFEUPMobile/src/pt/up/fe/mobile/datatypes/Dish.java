package pt.up.fe.mobile.datatypes;

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
        dest.writeString(getState());
        dest.writeString(description);
        dest.writeInt(getType());
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
        setState(in.readString());
        description = in.readString();
        setType(in.readInt());
        descriptionType = in.readString();
    }


}