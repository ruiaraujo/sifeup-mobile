package pt.up.fe.mobile.service;

public class Friend 
{
	String code;
	String name;
	String course;
	
	public Friend(String code, String name, String course)
	{
		this.code=code;
		this.name=name;
		this.course=course;
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
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	
	
}
