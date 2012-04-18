package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Teacher object for the Schedule Block
 * 
 * @author Rui Araújo
 * 
 */
public class ScheduleTeacher implements Parcelable {

	private final String code;
	private final String name;

	public ScheduleTeacher(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(code != null ? 1 : 0);
		if (code != null)
			dest.writeString(code);
		dest.writeInt(name != null ? 1 : 0);
		if (name != null)
			dest.writeString(name);
	}

	private ScheduleTeacher(Parcel in) {
		if (in.readInt() == 1)
			code = in.readString();
		else
			code = null;
		if (in.readInt() == 1)
			name = in.readString();
		else
			name = null;
	}

	public static final Parcelable.Creator<ScheduleTeacher> CREATOR = new Parcelable.Creator<ScheduleTeacher>() {
		public ScheduleTeacher createFromParcel(Parcel in) {
			return new ScheduleTeacher(in);
		}

		public ScheduleTeacher[] newArray(int size) {
			return new ScheduleTeacher[size];
		}
	};

}