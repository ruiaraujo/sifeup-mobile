package pt.up.beta.mobile.datatypes;

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
		dest.writeInt(type!=null?1:0);
		if ( type != null )
			dest.writeString(type);
		dest.writeInt(courseAcronym!=null?1:0);
		if ( courseAcronym != null )
			dest.writeString(courseAcronym);
		dest.writeInt(courseName!=null?1:0);
		if ( courseName != null )
			dest.writeString(courseName);
		dest.writeInt(weekDay!=null?1:0);
		if ( weekDay != null )
			dest.writeString(weekDay);
		dest.writeInt(date!=null?1:0);
		if ( date != null )
			dest.writeString(date);
		dest.writeInt(startTime!=null?1:0);
		if ( startTime != null )
			dest.writeString(startTime);
		dest.writeInt(endTime!=null?1:0);
		if ( endTime != null )
			dest.writeString(endTime);
		dest.writeInt(rooms!=null?1:0);
		if ( rooms != null )
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
    	if ( in.readInt() == 1 )
    		this.type = in.readString();
    	else
    		this.type = null;
    	if ( in.readInt() == 1 )
    		this.courseAcronym = in.readString();
    	else
    		this.courseAcronym = null;
    	if ( in.readInt() == 1 )
    		this.courseName = in.readString();
    	else
    		this.courseName = null;
    	if  ( in.readInt() == 1 )
    		this.weekDay = in.readString();
    	else
    		this.weekDay = null;
    	if ( in.readInt() == 1 )
    		this.date = in.readString();
    	else
    		this.date = null;
    	if ( in.readInt() == 1 )
    		this.startTime = in.readString();
    	else
    		this.startTime = null;
    	if ( in.readInt() == 1 )
    		this.endTime = in.readString();
    	else
    		this.endTime = null;
    	if ( in.readInt() == 1 )
    		this.rooms = in.readString();
    	else
    		this.rooms = null;
		
	}
	
}