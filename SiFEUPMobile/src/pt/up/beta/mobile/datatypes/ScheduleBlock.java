package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
/**
 * 
 * Represents a lecture.
 * Holds all data about it.
 * (time, place, teacher)
 *
 */
public class ScheduleBlock implements Parcelable{
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
	@SerializedName("aula_duracao")
	private final double lectureDuration; // 2; 1,5 (in hours)
	@SerializedName("turma_sigla")
	private final String classAcronym; // 3MIEIC1
	@SerializedName("sala_sigla")
    private final String roomCod; // in case of mutiple rooms this holds a string nice to show.
	@SerializedName("doc_sigla")
    private final String docAcronym; // in case of mutiple rooms this holds a string nice to show.

	@SerializedName("docentes")
	private final List<ScheduleTeacher> teachers;
	
	@SerializedName("salas")
	private final List<ScheduleRoom> rooms;
	
	@SerializedName("turmas")
	private final List<ScheduleClass> classes;
	
	@SerializedName("periodo")
	private int semester; // 2S

	
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
	
	public String getRoomCod() {
        return roomCod;
    }

    public void addTeacher(ScheduleTeacher teacher){
		teachers.add(teacher);
	}

	public List<ScheduleTeacher> getTeachers(){
		return teachers;
	}
	
	public void addRoom(ScheduleRoom room){
		rooms.add(room);
	}

	public List<ScheduleRoom> getRooms(){
		return rooms;
	}

	public List<ScheduleClass> getClasses(){
		return classes;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(weekDay);
		dest.writeInt(startTime);
		dest.writeInt(semester);
		ParcelUtils.writeString(dest, lectureCode);
		ParcelUtils.writeString(dest, lectureAcronym);
		ParcelUtils.writeString(dest, lectureType);
		ParcelUtils.writeString(dest, classAcronym);
		ParcelUtils.writeString(dest, roomCod);
		ParcelUtils.writeString(dest, docAcronym);
		dest.writeTypedList(teachers);
		dest.writeTypedList(rooms);
		dest.writeTypedList(classes);
	}
	
	private ScheduleBlock(Parcel in){
		weekDay = in.readInt();
		startTime = in.readInt();
		semester = in.readInt();
		lectureCode = ParcelUtils.readString(in);
		lectureAcronym = ParcelUtils.readString(in);
		lectureType = ParcelUtils.readString(in);
		lectureDuration = in.readDouble();
		classAcronym =ParcelUtils.readString(in);
        roomCod =ParcelUtils.readString(in);
        docAcronym = ParcelUtils.readString(in);
		teachers = new ArrayList<ScheduleTeacher>();
		in.readTypedList(teachers, ScheduleTeacher.CREATOR);
		rooms = new ArrayList<ScheduleRoom>();
		in.readTypedList(rooms, ScheduleRoom.CREATOR);
		classes = new ArrayList<ScheduleClass>();
		in.readTypedList(classes, ScheduleClass.CREATOR);
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