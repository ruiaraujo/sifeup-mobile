package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	private final String displayName;
	private final String user;
	private final String password;
	private final String type;
	public User(String displayName, String user, String password, String type) {
		this.displayName = displayName;
		this.user = user;
		this.password = password;
		this.type = type;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	public String getType() {
		return type;
	}
	
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
	
	public int describeContents() {
		return 0;
	}
	

	private User(Parcel in) {
		if ( in.readInt() == 1 ) displayName = in.readString();
		else displayName = null;
		if ( in.readInt() == 1 ) user = in.readString();
		else user = null;
		if ( in.readInt() == 1 ) password = in.readString();
		else password = null;
		if ( in.readInt() == 1 ) type = in.readString();
		else type = null;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(displayName!=null?1:0);
		if ( displayName != null ) dest.writeString(displayName);
		dest.writeInt(user!=null?1:0);
		if ( user != null ) dest.writeString(user);
		dest.writeInt(password!=null?1:0);
		if ( password != null ) dest.writeString(password);
		dest.writeInt(type!=null?1:0);
		if ( type != null ) dest.writeString(type);
	}
	
}
