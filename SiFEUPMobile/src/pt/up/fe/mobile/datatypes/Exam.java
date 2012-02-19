package pt.up.fe.mobile.datatypes;

/** Stores info about a exam */
public class Exam {
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
	
	
}