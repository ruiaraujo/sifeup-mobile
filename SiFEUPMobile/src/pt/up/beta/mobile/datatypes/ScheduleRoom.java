package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

public class ScheduleRoom implements Parcelable {
	
	private final String roomCode; // 002
	private final String buildingCode; // B
	private final String blockCode; // ii
	
	public ScheduleRoom(String buildingCode, String blockCode, String roomCode) {
		this.roomCode = roomCode;
		this.buildingCode = buildingCode;
		this.blockCode = blockCode;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public String getBuildingCode() {
		return buildingCode;
	}

	public String getBlockCode() {
		return blockCode;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(roomCode!=null?1:0);
		if ( roomCode != null ) dest.writeString(roomCode);
		dest.writeInt(buildingCode!=null?1:0);
		if ( buildingCode != null ) dest.writeString(buildingCode);
		dest.writeInt(blockCode!=null?1:0);
		if ( blockCode != null ) dest.writeString(blockCode);
	}

	private ScheduleRoom(Parcel in){
		if ( in.readInt() == 1 ) roomCode = in.readString();
		else roomCode = "";
		if ( in.readInt() == 1 ) buildingCode = in.readString();
		else buildingCode = "";
		if ( in.readInt() == 1 ) blockCode = in.readString();
		else blockCode = "";
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
		return buildingCode+roomCode;
	}
}
