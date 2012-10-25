package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/** Stores info about a exam */
public class Exam implements Parcelable {
	private final String ocorrId;
	private final String ocorrName;
	private final String date;
	private final String startTime;
	private final String endTime;
	private final String duration;
	private final String examId;
	private final String funcId;
	private final String type;
	private final String typeDesc;
	private final String season;
	private final Room [] rooms;
	
	public String getOcorrId() {
		return ocorrId;
	}

	public String getOcorrName() {
		return ocorrName;
	}

	public String getDate() {
		return date;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getDuration() {
		return duration;
	}

	public String getExamId() {
		return examId;
	}

	public String getFuncId() {
		return funcId;
	}

	public String getType() {
		return type;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public String getSeason() {
		return season;
	}

	public Room[] getRooms() {
		return rooms;
	}
	
	public String getRoomsString() {
		StringBuilder st = new StringBuilder();
		for ( Room r : rooms ){
			if ( st.length() > 0 )
				st.append(", ");
			st.append(r.name);
		}
		return st.toString();
	}


	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, ocorrId);
		ParcelUtils.writeString(dest, ocorrName);
		ParcelUtils.writeString(dest, date);
		ParcelUtils.writeString(dest, startTime);
		ParcelUtils.writeString(dest, endTime);
		ParcelUtils.writeString(dest, duration);
		ParcelUtils.writeString(dest, examId);
		ParcelUtils.writeString(dest, funcId);
		ParcelUtils.writeString(dest, type);
		ParcelUtils.writeString(dest, typeDesc);
		ParcelUtils.writeString(dest, season);
		dest.writeInt(rooms.length);
		dest.writeTypedArray(rooms, flags);
	}

	public static final Parcelable.Creator<Exam> CREATOR = new Parcelable.Creator<Exam>() {
		public Exam createFromParcel(Parcel in) {
			return new Exam(in);
		}

		public Exam[] newArray(int size) {
			return new Exam[size];
		}
	};

	private Exam(Parcel in) {
		ocorrId = ParcelUtils.readString(in);
		ocorrName = ParcelUtils.readString(in);
		date = ParcelUtils.readString(in);
		startTime = ParcelUtils.readString(in);
		endTime = ParcelUtils.readString(in);
		duration = ParcelUtils.readString(in);
		examId = ParcelUtils.readString(in);
		funcId = ParcelUtils.readString(in);
		type = ParcelUtils.readString(in);
		typeDesc = ParcelUtils.readString(in);
		season = ParcelUtils.readString(in);
		rooms = new Room[in.readInt()];
		in.readTypedArray(rooms, Room.CREATOR);
	}

	
	public static class Room implements Parcelable{
		private final String id;

		private final String name;
		

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		private Room(Parcel in){
			id = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
			
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, id);
			ParcelUtils.writeString(dest, name);
			
		}

		public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
			public Room createFromParcel(Parcel in) {
				return new Room(in);
			}

			public Room[] newArray(int size) {
				return new Room[size];
			}
		};
		
	}

}