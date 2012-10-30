package pt.up.beta.mobile.utils;

import android.os.Parcel;

public class ParcelUtils {
	private ParcelUtils() {
	}

	public static void writeString(Parcel dest, String value) {
		dest.writeByte((byte) (value != null ? 1 : 0));
		if (value != null)
			dest.writeString(value);
	}

	public static String readString(Parcel in) {
		if (in.readByte() == 1)
			return in.readString();
		return null;
	}
}
