package pt.up.beta.mobile.friends;

import pt.up.beta.mobile.utils.StringUtils;

public class Friend implements Comparable<Friend> {

	final String code;
	final String name;
	final String course;

	public Friend(String code, String name, String course) {
		this.code = code;
		this.name = name;
		this.course = course;
	}


	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCourse() {
		return course;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Friend)
			return code.equals(((Friend) o).code);
		return false;
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public int compareTo(Friend another) {
		return StringUtils.toUpperCaseSansAccent(name).compareTo(
				StringUtils.toUpperCaseSansAccent(another.name));
	}

}
