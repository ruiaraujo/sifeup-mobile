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
		displayName = in.readString();
		user = in.readString();
		password = in.readString();
		type = in.readString();
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(displayName);
		dest.writeString(user);
		dest.writeString(password);
		dest.writeString(type);
	}
	
}
