package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;

public class Student implements Serializable{


	private static final long serialVersionUID = 1727093503991901167L;
	private String code;
	private String name;
	private String courseAcronym;
	private String courseName;
	private String courseNameEn;
	private String courseCode;
	private String registrationYear;
	private String state;
	private String academicYear;
	private String email;
	private String emailAlt;

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile"; }
	public Student() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCourseAcronym() {
		return courseAcronym;
	}

	public void setCourseAcronym(String courseAcronym) {
		this.courseAcronym = courseAcronym;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getRegistrationYear() {
		return registrationYear;
	}

	public void setRegistrationYear(String registrationYear) {
		this.registrationYear = registrationYear;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	private void clearAll() {
		name = courseAcronym = 
			courseName = registrationYear = 
				state = academicYear = email = "";
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseNameEn(String courseNameEn) {
		this.courseNameEn = courseNameEn;
	}

	public String getCourseNameEn() {
		return courseNameEn;
	}
	
	public void setEmailAlt(String emailAlt) {
		this.emailAlt = emailAlt;
	}

	public String getEmailAlt() {
		return emailAlt;
	}

	public class StudentDetail{
		public String title;
		public String content;
		public Type type;
	}
	
	//TODO : finishing adding details to these class and then finishing this function
	public List<StudentDetail> getStudentContents( Resources res ){
		return new ArrayList<StudentDetail>();
		
	}
}