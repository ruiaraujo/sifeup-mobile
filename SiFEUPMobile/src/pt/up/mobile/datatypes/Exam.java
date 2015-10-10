package pt.up.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/** Stores info about a exam */
public class Exam implements Parcelable {
	@SerializedName("ocorr_id")
	private final String ocorrId;
	@SerializedName("ocorr_nome")
	private final String ocorrName;
	@SerializedName("data")
	private final String date;
	@SerializedName("hora_inicio")
	private final String startTime;
	@SerializedName("hora_fim")
	private final String endTime;
	@SerializedName("duracao")
	private final String duration;
	@SerializedName("id")
	private final String examId;
	@SerializedName("pfunc_id")
	private final String funcId;
	@SerializedName("tipo")
	private final String type;
	@SerializedName("tipo_descricao")
	private final String typeDesc;
	@SerializedName("epoca")
	private final String season;
	@SerializedName("salas")
	private final Room[] rooms;

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
		for (Room r : rooms) {
			if (st.length() > 0)
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
		if (in == null) {
			rooms = null;
		} else {
			rooms = new Room[in.readInt()];
			in.readTypedArray(rooms, Room.CREATOR);
		}
	}

	public static class Room implements Parcelable {
		@SerializedName("espaco_id")
		private final String id;

		@SerializedName("espaco_sigla")
		private final String name;

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		private Room(Parcel in) {
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

		@Override
		public String toString() {
			return name;
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