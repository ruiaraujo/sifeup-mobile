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
public class ScheduleClass implements Parcelable {

	@SerializedName("turma_id")
	private final String code;
	@SerializedName("turma_sigla")
	private final String name;

	private ScheduleClass(Parcel in) {
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


	public static final Parcelable.Creator<ScheduleClass> CREATOR = new Parcelable.Creator<ScheduleClass>() {
		public ScheduleClass createFromParcel(Parcel in) {
			return new ScheduleClass(in);
		}

		public ScheduleClass[] newArray(int size) {
			return new ScheduleClass[size];
		}
	};

	@Override
	public String toString() {
		return name;
	}
}