package pt.up.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import pt.up.mobile.content.SigarraContract;
import pt.up.mobile.utils.StringUtils;

public class Friend implements Comparable<Friend> {

	private final String code;
	private final String name;
	private final String type;
	private final String course;

	public Friend(String code, String name, String type, String course) {
		this.code = code;
		this.name = name;
		this.type = type;
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

	public String getType() {
		return type;
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

	public static List<Friend> parseCursor(Cursor mCursor) {
		final List<Friend> friends = new ArrayList<Friend>();
		if (mCursor != null) {
			if (mCursor.moveToFirst()) {

				do {
					friends.add(new Friend(
							mCursor.getString(mCursor
									.getColumnIndex(SigarraContract.FriendsColumns.CODE_FRIEND)),
							mCursor.getString(mCursor
									.getColumnIndex(SigarraContract.FriendsColumns.NAME_FRIEND)),
							mCursor.getString(mCursor
									.getColumnIndex(SigarraContract.FriendsColumns.TYPE_FRIEND)),
							mCursor.getString(mCursor
									.getColumnIndex(SigarraContract.FriendsColumns.COURSE_FRIEND))));
				} while (mCursor.moveToNext());
				mCursor.close();
			}
		}
		return friends;
	}

}
