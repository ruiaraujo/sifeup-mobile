package pt.up.beta.mobile.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SigarraProvider extends ContentProvider {

	// Used for the UriMacher
	private static final int SUBJECTS = 10;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SUBJECTS, SUBJECTS);
	}

	private DatabaseHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext().getApplicationContext());
		return dbHelper != null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final int count;
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			count = dbHelper.getWritableDatabase().delete(
					SubjectsTable.TABLE_SUBJECTS, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		dbHelper.close();
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			return SigarraContract.Subjects.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS: {
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			try {
				db.beginTransaction();
				for (ContentValues v : values) {
					if (db.replace(SubjectsTable.TABLE_SUBJECTS, null, v) == -1)
						throw new SQLException("Failed to insert row into "
								+ uri);
				}
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return values.length;
		}
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS: {
			long rowID = dbHelper.getWritableDatabase().replace(
					SubjectsTable.TABLE_SUBJECTS, null, values);
			if (rowID > 0) {
				final Uri url = ContentUris.withAppendedId(
						SigarraContract.Subjects.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(url, null);
				getContext().getContentResolver().notifyChange(uri, null);
				dbHelper.close();
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
		final String orderBy;
		final int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS: 
			qb.setTables(SubjectsTable.TABLE_SUBJECTS);
			if ( TextUtils.isEmpty(sortOrder) )
				orderBy = SigarraContract.Subjects.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		final Cursor c = qb.query(dbHelper.getReadableDatabase(), projection,
				selection, selectionArgs, null, null, orderBy);
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
			count = dbHelper.getWritableDatabase().update(
					SubjectsTable.TABLE_SUBJECTS, values, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		dbHelper.close();
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
