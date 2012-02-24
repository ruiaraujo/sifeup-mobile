package pt.up.fe.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/** Stores info about a exam */
public class Exam implements Parcelable {
	private final String type; // tipo de exame
	private final String courseAcronym; // codigo da cadeira
	private final String courseName; // nome da cadeira
	private final String weekDay; // [1 ... 6]
	private final String date; // data do exame
	private final String startTime; // hora de in�cio
	private final String endTime; // hora de fim
	private final String rooms; // salas
	public Exam(String type, String courseAcronym, String courseName,
			String weekDay, String date, String startTime, String endTime,
			String rooms) {
		this.type = type;
		this.courseAcronym = courseAcronym;
		this.courseName = courseName;
		this.weekDay = weekDay;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.rooms = rooms;
	}
	public String getType() {
		return type;
	}
	public String getCourseAcronym() {
		return courseAcronym;
	}
	public String getCourseName() {
		return courseName;
	}
	public String getWeekDay() {
		return weekDay;
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
	public String getRooms() {
		return rooms;
	}
	public int describeContents() {
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeString(courseAcronym);
		dest.writeString(courseName);
		dest.writeString(weekDay);
		dest.writeString(date);
		dest.writeString(type);
		dest.writeString(startTime);
		dest.writeString(endTime);
		dest.writeString(rooms);
	}
	

    public static final Parcelable.Creator<Exam> CREATOR = new Parcelable.Creator<Exam>() {
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };
	
	private Exam(Parcel in){
		this.type = in.readString();
		this.courseAcronym = in.readString();
		this.courseName = in.readString();
		this.weekDay = in.readString();
		this.date = in.readString();
		this.startTime = in.readString();
		this.endTime = in.readString();
		this.rooms = in.readString();
		
	}
	
}