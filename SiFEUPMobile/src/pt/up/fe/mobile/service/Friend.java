package pt.up.fe.mobile.service;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Friend implements Serializable , Comparable<Friend>
{

	private static final long serialVersionUID = 8493032318077234968L;
	String code;
	String name;
	String course;
	final private static String SEPARATOR = "|";
	public Friend(String code, String name, String course)
	{
		this.code=code;
		this.name=name;
		this.course=course;
	}
	
	public Friend(String untokenizedString)
	{
		StringTokenizer reader = new StringTokenizer(untokenizedString , SEPARATOR);
		if ( reader.countTokens() == 3 )
		{
			this.code=reader.nextToken();
			this.name=reader.nextToken();
			this.course=reader.nextToken();
		} 
		else if (reader.countTokens() == 2 )
		{
			this.code=reader.nextToken();
			this.name=reader.nextToken();
			this.course=null;
		}
		else
			throw new IllegalArgumentException("There must be three or two tokens");
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

	@Override
	public boolean equals(Object o) {
		if ( o instanceof Friend )
			return code.equals(((Friend)o).code);
		return false;
	}
	
	@Override
	public int hashCode() {
		return code.hashCode();
	}

	public String toString(){
		if ( course == null )
			return code + SEPARATOR + name;
		return code + SEPARATOR + name + SEPARATOR + course;
	}

	@Override
	public int compareTo(Friend another) {
		return code.compareTo(another.code);
	}
	
	
}
