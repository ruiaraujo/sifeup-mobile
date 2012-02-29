package pt.up.fe.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 
 * Represents a lecture.
 * Holds all data about it.
 * (time, place, teacher)
 *
 */
public class Block implements Parcelable{
	private int weekDay; // [0 ... 6]
	private int startTime; // seconds from midnight
	
	private String lectureCode; // EIC0036
	private String lectureAcronym; // ex: SDIS
	private String lectureType; // T|TP|P
	private double lectureDuration; // 2; 1,5 (in hours)
	private String classAcronym; // 3MIEIC1
	
	private String teacherAcronym; // RMA
	private String teacherCode; // 466651
	
	private String roomCode; // 002
	private String buildingCode; // B
	private String semester; // 2S
	
	private String year; // 2010/2011

	public Block(){}
	
	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getLectureCode() {
		return lectureCode;
	}

	public void setLectureCode(String lectureCode) {
		this.lectureCode = lectureCode;
	}

	public String getLectureAcronym() {
		return lectureAcronym;
	}

	public void setLectureAcronym(String lectureAcronym) {
		this.lectureAcronym = lectureAcronym;
	}

	public String getLectureType() {
		return lectureType;
	}

	public void setLectureType(String lectureType) {
		this.lectureType = lectureType;
	}

	public double getLectureDuration() {
		return lectureDuration;
	}

	public void setLectureDuration(double lectureDuration) {
		this.lectureDuration = lectureDuration;
	}

	public String getClassAcronym() {
		return classAcronym;
	}

	public void setClassAcronym(String classAcronym) {
		this.classAcronym = classAcronym;
	}

	public String getTeacherAcronym() {
		return teacherAcronym;
	}

	public void setTeacherAcronym(String teacherAcronym) {
		this.teacherAcronym = teacherAcronym;
	}

	public String getTeacherCode() {
		return teacherCode;
	}

	public void setTeacherCode(String teacherCode) {
		this.teacherCode = teacherCode;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getBuildingCode() {
		return buildingCode;
	}

	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return year;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(weekDay);
		dest.writeInt(startTime);
		dest.writeInt(lectureCode!=null?1:0);
		if ( lectureCode != null ) dest.writeString(lectureCode);
		dest.writeInt(lectureAcronym!=null?1:0);
		if ( lectureAcronym != null ) dest.writeString(lectureAcronym);
		dest.writeInt(lectureType!=null?1:0);
		if ( lectureType != null ) dest.writeString(lectureType);
		dest.writeDouble(lectureDuration);
		dest.writeInt(classAcronym!=null?1:0);
		if (  classAcronym != null ) dest.writeString(classAcronym);
		dest.writeInt(teacherAcronym!=null?1:0);
		if ( teacherAcronym != null ) dest.writeString(teacherAcronym);
		dest.writeInt(teacherCode!=null?1:0);
		if ( teacherCode != null ) dest.writeString(teacherCode);
		dest.writeInt(roomCode!=null?1:0);
		if ( roomCode != null ) dest.writeString(roomCode);
		dest.writeInt(buildingCode!=null?1:0);
		if ( buildingCode != null ) dest.writeString(buildingCode);
		dest.writeInt(semester!=null?1:0);
		if ( semester != null ) dest.writeString(semester);
		dest.writeInt(year!=null?1:0);
		if ( year != null ) dest.writeString(year);
	}
	
	private Block(Parcel in){
		weekDay = in.readInt();
		startTime = in.readInt();
		if ( in.readInt() == 1 ) lectureCode = in.readString();
		if ( in.readInt() == 1 ) lectureAcronym = in.readString();
		if ( in.readInt() == 1 ) lectureType = in.readString();
		lectureDuration = in.readDouble();
		if ( in.readInt() == 1 ) classAcronym = in.readString();
		if ( in.readInt() == 1 ) teacherAcronym = in.readString();
		if ( in.readInt() == 1 ) teacherCode = in.readString();
		if ( in.readInt() == 1 ) roomCode = in.readString();
		if ( in.readInt() == 1 ) buildingCode = in.readString();
		if ( in.readInt() == 1 ) semester = in.readString();
		if ( in.readInt() == 1 ) year = in.readString();
	}
	

    public static final Parcelable.Creator<Block> CREATOR = new Parcelable.Creator<Block>() {
        public Block createFromParcel(Parcel in) {
            return new Block(in);
        }

        public Block[] newArray(int size) {
            return new Block[size];
        }
    };

}