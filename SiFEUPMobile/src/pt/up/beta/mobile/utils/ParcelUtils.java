package pt.up.beta.mobile.utils;

import android.os.Parcel;

public class ParcelUtils {
	private ParcelUtils() {
	}

	private static int TRUE = 1;
	private static int FALSE = 0;

	public static void writeString(Parcel dest, String value) {
		dest.writeByte((byte) (value != null ? 1 : 0));
		if (value != null)
			dest.writeString(value);
	}

	public static String readString(Parcel in) {
		if (in == null)
			return null;
		if (in.readByte() == 1)
			return in.readString();
		return null;
	}

	public static void writeBoolean(Parcel dest, boolean value) {
		dest.writeInt(value ? TRUE : FALSE);
	}

	public static boolean readBoolean(Parcel in) {
		if (in == null)
			return false;
		return in.readInt() == TRUE;
	}
}
