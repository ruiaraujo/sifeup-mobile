package pt.up.beta.mobile.content;

import android.net.Uri;

public final class SigarraContract {

	//TODO
	public interface SubjectsColumns {
		String USER_CODE = SubjectsTable.COLUMN_USER_CODE;
		String CODE = SubjectsTable.COLUMN_CODE;
		String NAME_PT = SubjectsTable.COLUMN_NAME_PT;
		String NAME_EN = SubjectsTable.COLUMN_NAME_EN;
		String YEAR = SubjectsTable.COLUMN_YEAR;
		String PERIOD = SubjectsTable.COLUMN_PERIOD;
		String CONTENT = SubjectsTable.COLUMN_CONTENT;
		String FILES = SubjectsTable.COLUMN_FILES;
	}

	public interface FriendsColumns {
		String ID = FriendsTable.KEY_ID_FRIEND;
		String USER_CODE = FriendsTable.KEY_USER_CODE;
		String CODE_FRIEND = FriendsTable.KEY_CODE_FRIEND;
		String NAME_FRIEND = FriendsTable.KEY_NAME_FRIEND;
		String COURSE_FRIEND = FriendsTable.KEY_COURSE_FRIEND;
	}

	public static final String CONTENT_AUTHORITY = "pt.up.fe.mobile.content.SigarraProvider";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	static final String PATH_SUBJECTS = "subjects";
	static final String PATH_FRIENDS = "friends";

	/**
	 * The public contract for the subjects.
	 */
	public static class Subjects implements SubjectsColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SUBJECTS).build();

		public static final String CONTENT_TYPE = "vnd.feup.cursor.dir/vnd.feup.subject";
		public static final String CONTENT_ITEM_TYPE = "vnd.feup.cursor.item/vnd.feup.subject";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = PERIOD
				+ " ASC, " + NAME_PT + " ASC, "
				+ NAME_EN + " ASC ";

		public static final String USER_SUBJECTS = USER_CODE
				+ "=?";
		public static final String [] getUserSubjectsSelectionArgs(String code ){
			return new String[]{code};
		}
		public static final String SUBJECT_SELECTION = USER_CODE
				+ "=? AND "
				+ CODE
				+ "=? AND "
				+ PERIOD
				+ "=? AND "
				+ YEAR
				+ "=?";

		public static final String [] getSubjectsSelectionArgs(String userCode, String code, String year, String period ){
			return new String[] {userCode, code, period, year };
		}
	}
	
	public static class Friends implements FriendsColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_FRIENDS).build();
		

		public static final String CONTENT_TYPE = "vnd.feup.cursor.dir/vnd.feup.subject";
		public static final String CONTENT_ITEM_TYPE = "vnd.feup.cursor.item/vnd.feup.subject";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = NAME_FRIEND
				+ " ASC ";

		public static final String USER_FRIENDS = USER_CODE
				+ "=?";
		public static final String [] getUserFriendsSelectionArgs(String code ){
			return new String[]{code};
		}
		
		public static final String [] FRIENDS_COLUMNS = {
			CODE_FRIEND, NAME_FRIEND , COURSE_FRIEND
		};
		
		public static final String FRIEND_SELECTION = USER_CODE
				+ "=? AND "
				+ CODE_FRIEND
				+ "=?";

		public static final String [] getFriendSelectionArgs(String userCode, String code ){
			return new String[] {userCode, code };
		}
	}

	private SigarraContract() {
	}
}
