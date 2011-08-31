package pt.up.fe.mobile.service;

import java.io.Serializable;
/**
 * 
 * Represents a lecture.
 * Holds all data about it.
 * (time, place, teacher)
 *
 */
@SuppressWarnings("serial")
public class Block implements Serializable{
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
}