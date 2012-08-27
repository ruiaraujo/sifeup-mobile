package pt.up.beta.mobile.content;

import android.net.Uri;

public final class SigarraContract {

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

	public static final String CONTENT_AUTHORITY = "pt.up.fe.mobile.content.SigarraProvider";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	static final String PATH_SUBJECTS = "subjects";

	/**
	 * The public contract for the subjects.
	 */
	public static class Subjects implements SubjectsColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SUBJECTS).build();

		public static final String CONTENT_TYPE = "vnd.feup.cursor.dir/vnd.feup.subject";
		public static final String CONTENT_ITEM_TYPE = "vnd.feup.cursor.item/vnd.feup.subject";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = SubjectsColumns.PERIOD
				+ " ASC, " + SubjectsColumns.NAME_PT + " ASC, "
				+ SubjectsColumns.NAME_EN + " ASC ";

		public static final String USER_SUBJECTS = SubjectsColumns.USER_CODE
				+ "=?";
		public static final String [] getUserSubjectsSelectionArgs(String code ){
			return new String[]{code};
		}
		public static final String SUBJECT_SELECTION = SubjectsColumns.USER_CODE
				+ "=? AND "
				+ SubjectsColumns.CODE
				+ "=? AND "
				+ SubjectsColumns.PERIOD
				+ "=? AND "
				+ SubjectsColumns.YEAR
				+ "=?";

		public static final String [] getSubjectsSelectionArgs(String userCode, String code, String year, String period ){
			return new String[] {userCode, code, period, year };
		}
	}

	private SigarraContract() {
	}
}
