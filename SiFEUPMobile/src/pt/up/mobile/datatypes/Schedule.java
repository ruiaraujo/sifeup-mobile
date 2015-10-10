package pt.up.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Schedule implements Parcelable {
	@SerializedName("horario")
	private final ScheduleBlock[] blocks;

	private Schedule(Parcel in) {
		if (in == null) {
			blocks = null;
		} else {
			blocks = new ScheduleBlock[in.readInt()];
			in.readTypedArray(blocks, ScheduleBlock.CREATOR);
		}
	}

	public ScheduleBlock[] getBlocks() {
		return blocks;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(blocks.length);
		dest.writeTypedArray(blocks, flags);
	}

	public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
		public Schedule createFromParcel(Parcel in) {
			return new Schedule(in);
		}

		public Schedule[] newArray(int size) {
			return new Schedule[size];
		}
	};
}
