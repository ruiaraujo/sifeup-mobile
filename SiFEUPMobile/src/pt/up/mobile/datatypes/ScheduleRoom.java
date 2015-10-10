package pt.up.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleRoom implements Parcelable {
	@SerializedName("espaco_id")
	private final String roomCode; // 70252
	@SerializedName("espaco_nome")
	private final String roomName; // B002
	
	private ScheduleRoom(Parcel in){
		roomCode = ParcelUtils.readString(in);
		roomName = ParcelUtils.readString(in);
	}
	
	
	public String getRoomCode() {
		return roomCode;
	}

	public String getRoomName() {
		return roomName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, roomCode);
		ParcelUtils.writeString(dest, roomName);
	}

    public static final Parcelable.Creator<ScheduleRoom> CREATOR = new Parcelable.Creator<ScheduleRoom>() {
        public ScheduleRoom createFromParcel(Parcel in) {
            return new ScheduleRoom(in);
        }

        public ScheduleRoom[] newArray(int size) {
            return new ScheduleRoom[size];
        }
    };
    
	@Override
	public String toString(){
		return roomName;
	}
}
