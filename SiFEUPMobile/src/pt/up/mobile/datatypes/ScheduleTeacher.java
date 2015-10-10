package pt.up.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Teacher object for the Schedule Block
 * 
 * @author Rui Araújo
 * 
 */
public class ScheduleTeacher implements Parcelable {

	@SerializedName("doc_codigo")
	private final String code;
	@SerializedName("doc_nome")
	private final String name;
	
	private ScheduleTeacher(Parcel in) {
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
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
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
	}

	public static final Parcelable.Creator<ScheduleTeacher> CREATOR = new Parcelable.Creator<ScheduleTeacher>() {
		public ScheduleTeacher createFromParcel(Parcel in) {
			return new ScheduleTeacher(in);
		}

		public ScheduleTeacher[] newArray(int size) {
			return new ScheduleTeacher[size];
		}
	};

	@Override
	public String toString() {
		return name;
	}
}