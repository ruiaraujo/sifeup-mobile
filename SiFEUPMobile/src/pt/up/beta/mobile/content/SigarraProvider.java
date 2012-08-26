package pt.up.beta.mobile.content;

import pt.up.beta.mobile.content.tables.DatabaseHelper;
import pt.up.beta.mobile.content.tables.SubjectsTable;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SigarraProvider extends ContentProvider {
	public final static String AUTHORITY = "pt.up.fe.mobile.content.SigarraProvider";
	private final static String BASE_URI = "content://" + AUTHORITY;
	private static final String BASE_SUBJECTS_PATH = "subjects";

	public static final Uri CONTENT_SUBJECTS_URI = Uri.parse(BASE_URI + "/"
			+ BASE_SUBJECTS_PATH);

	public static final String CONTENT_SUBJECTS_TYPE = "vnd.feup.cursor.dir/subject";


	// Used for the UriMacher
	private static final int SUBJECTS = 10;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_SUBJECTS_PATH, SUBJECTS);
	}

	private DatabaseHelper db;

	@Override
	public boolean onCreate() {
		db = new DatabaseHelper(getContext().getApplicationContext());
		return db != null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final int count;
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			count = db.getWritableDatabase().delete(
					SubjectsTable.TABLE_SUBJECTS, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		db.close();
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			return CONTENT_SUBJECTS_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:  {
			long rowID = db.getWritableDatabase().replace(
					SubjectsTable.TABLE_SUBJECTS, null, values);
			if (rowID > 0) {
				final Uri url = ContentUris.withAppendedId(
						CONTENT_SUBJECTS_URI, rowID);
				getContext().getContentResolver().notifyChange(uri, null);
				db.close();
				return url;
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			qb.setTables(SubjectsTable.TABLE_SUBJECTS);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		final Cursor c = qb.query(db.getReadableDatabase(), projection,
				selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final int count;
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			count = db.getWritableDatabase().update(
					SubjectsTable.TABLE_SUBJECTS, values, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		db.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
