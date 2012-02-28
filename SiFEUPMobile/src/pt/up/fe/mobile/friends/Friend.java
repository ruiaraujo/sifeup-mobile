package pt.up.fe.mobile.friends;

import java.util.StringTokenizer;

import pt.up.fe.mobile.sifeup.SifeupUtils;
import pt.up.fe.mobile.utils.StringUtils;

public class Friend implements Comparable<Friend> {

	final String code;
	final String name;
	final String course;
	final private static String SEPARATOR = "|";

	public Friend(String code, String name, String course) {
		this.code = code;
		this.name = name;
		this.course = course;
	}

	public Friend(String untokenizedString) {
		StringTokenizer reader = new StringTokenizer(untokenizedString,
				SEPARATOR);
		if (reader.countTokens() == 3) {
			this.code = reader.nextToken();
			this.name = reader.nextToken();
			this.course = reader.nextToken();
		} else if (reader.countTokens() == 2) {
			this.code = reader.nextToken();
			this.name = reader.nextToken();
			this.course = null;
		} else
			throw new IllegalArgumentException(
					"There must be three or two tokens");
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

	public String toString() {
		if (course == null)
			return code + SEPARATOR + name;
		return code + SEPARATOR + name + SEPARATOR + course;
	}

	@Override
	public int compareTo(Friend another) {
		return StringUtils.toUpperCaseSansAccent(name).compareTo(
				StringUtils.toUpperCaseSansAccent(another.name));
	}

}
