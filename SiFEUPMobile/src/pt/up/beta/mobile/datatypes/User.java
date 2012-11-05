package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private final String displayName;
	private final String userCode;
	private final String password;
	private final String type;

	public User(String displayName, String user, String password, String type) {
		this.displayName = displayName;
		this.userCode = user;
		this.password = password;
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getUserCode() {
		return userCode;
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
		displayName = ParcelUtils.readString(in);
		userCode = ParcelUtils.readString(in);
		password = ParcelUtils.readString(in);
		type = ParcelUtils.readString(in);
	}

	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, displayName);
		ParcelUtils.writeString(dest, userCode);
		ParcelUtils.writeString(dest, password);
		ParcelUtils.writeString(dest, type);
	}

}
