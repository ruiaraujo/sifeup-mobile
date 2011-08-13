package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobile.R;

import android.content.res.Resources;

/**
 * Class that represents the Object Student
 * 
 * @author Ângela Igreja
 * 
 */
public class Student implements Serializable
{
	private static final long serialVersionUID = 1727093503991901167L;
	private String code;
	private String name;
	private String programmeAcronym;
	private String programmeName;
	private String programmeNameEn;
	private String programmeCode;
	private String registrationYear;
	private String state;
	private String academicYear;
	private String email;
	private String emailAlt;
	private String mobile;
	private String telephone;
	private String branch;


	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile"; 
	}
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

	public String getProgrammeAcronym() {
		return programmeAcronym;
	}

	public void setProgrammeAcronym(String programmeAcronym) {
		this.programmeAcronym = programmeAcronym;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
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

	public void setProgrammeCode(String programmeCode) {
		this.programmeCode = programmeCode;
	}

	public String getProgrammeCode() {
		return programmeCode;
	}

	public void setProgrammeNameEn(String programmeNameEn) {
		this.programmeNameEn = programmeNameEn;
	}

	public String getProgrammeNameEn() {
		return programmeNameEn;
	}
	
	public void setEmailAlt(String emailAlt) {
		this.emailAlt = emailAlt;
	}

	public String getEmailAlt() {
		return emailAlt;
	}
	
	/**
	 * 
	 * @author Rui Araújo
	 *
	 */

	public class StudentDetail{
		public String title;
		public String content;
		public String type;
		public StudentDetail(String title, String content, String type) {
			this.title = title;
			this.content = content;
			this.type = type;
		}
		
	}
	
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public List<StudentDetail> getStudentContents( Resources res ){
		List<StudentDetail> result = new ArrayList<StudentDetail>();
		if ( email != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_email), email, Type.EMAIL));
		}
		if ( emailAlt != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_email_alt), emailAlt, Type.EMAIL));
		}
		if ( mobile != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_mobile), mobile, Type.MOBILE));
		}
		if ( telephone != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_telephone), telephone, Type.MOBILE));
		}
		if ( programmeName != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_programme), programmeName, null));
		}
		if ( branch != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_branch), branch, null));
		}
		if ( state != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_status), state, null));
		}
		if ( academicYear != null )
		{
			result.add(new StudentDetail(res.getString(R.string.profile_title_year), academicYear, null));
		}
		return result;
		
	}

}