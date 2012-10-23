package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Represents a lecture. Holds all data about it. (time, place, teacher)
 * 
 */
public class ScheduleBlock implements Parcelable {
	@SerializedName("dia")
	private final int weekDay; // [0 ... 6]
	@SerializedName("hora_inicio")
	private final int startTime; // seconds from midnight
	@SerializedName("ocorrencia_id")
	private final String lectureCode; // EIC0036
	@SerializedName("ucurr_sigla")
	private final String lectureAcronym; // ex: SDIS
	@SerializedName("tipo")
	private final String lectureType; // T|TP|P
	@SerializedName("aula_id")
	private final String blockId; // T|TP|P
	@SerializedName("aula_duracao")
	private final double lectureDuration; // 2; 1,5 (in hours)
	@SerializedName("turma_sigla")
	private final String classAcronym; // 3MIEIC1
	@SerializedName("sala_sigla")
	private final String roomCod; // in case of mutiple rooms this holds a
									// string nice to show.
	@SerializedName("doc_sigla")
	private final String docAcronym; // in case of mutiple rooms this holds a
										// string nice to show.

	@SerializedName("docentes")
	private final ScheduleTeacher[] teachers;

	@SerializedName("salas")
	private final ScheduleRoom[] rooms;

	@SerializedName("turmas")
	private final ScheduleClass[] classes;

	@SerializedName("periodo")
	private int semester; // 2S

	private ScheduleBlock(Parcel in) {
		weekDay = in.readInt();
		startTime = in.readInt();
		semester = in.readInt();
		lectureDuration = in.readDouble();
		lectureCode = ParcelUtils.readString(in);
		lectureAcronym = ParcelUtils.readString(in);
		lectureType = ParcelUtils.readString(in);
		blockId = ParcelUtils.readString(in);
		classAcronym = ParcelUtils.readString(in);
		roomCod = ParcelUtils.readString(in);
		docAcronym = ParcelUtils.readString(in);
		teachers = new ScheduleTeacher[in.readInt()];
		in.readTypedArray(teachers, ScheduleTeacher.CREATOR);
		rooms = new ScheduleRoom[in.readInt()];
		in.readTypedArray(rooms, ScheduleRoom.CREATOR);
		classes = new ScheduleClass[in.readInt()];
		in.readTypedArray(classes, ScheduleClass.CREATOR);
	}

	public int getWeekDay() {
		return weekDay;
	}

	public int getStartTime() {
		return startTime;
	}

	public String getLectureCode() {
		return lectureCode;
	}

	public String getLectureAcronym() {
		return lectureAcronym;
	}

	public String getLectureType() {
		return lectureType;
	}

	public double getLectureDuration() {
		return lectureDuration;
	}

	public String getClassAcronym() {
		return classAcronym;
	}

	public int getSemester() {
		return semester;
	}

	public String getBlockId() {
		return blockId;
	}

	public String getDocAcronym() {
		return docAcronym;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public String getRoomCod() {
		return roomCod;
	}

	public ScheduleTeacher[] getTeachers() {
		return teachers;
	}

	public ScheduleRoom[] getRooms() {
		return rooms;
	}

	public ScheduleClass[] getClasses() {
		return classes;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(weekDay);
		dest.writeInt(startTime);
		dest.writeInt(semester);
		dest.writeDouble(lectureDuration);
		ParcelUtils.writeString(dest, lectureCode);
		ParcelUtils.writeString(dest, lectureAcronym);
		ParcelUtils.writeString(dest, lectureType);
		ParcelUtils.writeString(dest, blockId);
		ParcelUtils.writeString(dest, classAcronym);
		ParcelUtils.writeString(dest, roomCod);
		ParcelUtils.writeString(dest, docAcronym);
		dest.writeInt(teachers.length);
		dest.writeTypedArray(teachers,flags);
		dest.writeInt(rooms.length);
		dest.writeTypedArray(rooms,flags);
		dest.writeInt(classes.length);
		dest.writeTypedArray(classes,flags);
	}

	public static final Parcelable.Creator<ScheduleBlock> CREATOR = new Parcelable.Creator<ScheduleBlock>() {
		public ScheduleBlock createFromParcel(Parcel in) {
			return new ScheduleBlock(in);
		}

		public ScheduleBlock[] newArray(int size) {
			return new ScheduleBlock[size];
		}
	};

}