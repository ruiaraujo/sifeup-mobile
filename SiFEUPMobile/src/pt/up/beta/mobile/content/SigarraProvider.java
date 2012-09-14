package pt.up.beta.mobile.content;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SyncAdapter;
import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class SigarraProvider extends ContentProvider {

	// Used for the UriMacher
	private static final int SUBJECTS = 10;
	private static final int FRIENDS = 20;
	private static final int PROFILES = 30;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_SUBJECTS, SUBJECTS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_FRIENDS, FRIENDS);
		sURIMatcher.addURI(SigarraContract.CONTENT_AUTHORITY,
				SigarraContract.PATH_PROFILES, PROFILES);
	}

	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	private synchronized SQLiteDatabase getWritableDatabase(){
		if ( database == null )
			database = dbHelper.getWritableDatabase();
		return database;
	}
	
	
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
			count = getWritableDatabase().delete(
					SubjectsTable.TABLE_SUBJECTS, selection, selectionArgs);
			break;
		case FRIENDS:
			count = getWritableDatabase().delete(
					FriendsTable.TABLE_FRIENDS, selection, selectionArgs);
			break;
		case PROFILES:
			count = getWritableDatabase().delete(
					ProfilesTable.TABLE_PROFILES, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS:
			return SigarraContract.Subjects.CONTENT_TYPE;
		case FRIENDS:
			return SigarraContract.Friends.CONTENT_TYPE;
		case PROFILES:
			return SigarraContract.Profiles.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int uriType = sURIMatcher.match(uri);
		final String table;
		switch (uriType) {
		case SUBJECTS:
			table = SubjectsTable.TABLE_SUBJECTS;
			break;
		case PROFILES:
			table = ProfilesTable.TABLE_PROFILES;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		final SQLiteDatabase db = getWritableDatabase();
		try {
			db.beginTransaction();
			for (ContentValues v : values) {
				if (db.replace(table, null, v) == -1)
					throw new SQLException("Failed to insert row into " + uri);
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

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBJECTS: {
			long rowID = getWritableDatabase().replace(
					SubjectsTable.TABLE_SUBJECTS, null, values);
			if (rowID > 0) {
				final Uri url = ContentUris.withAppendedId(
						SigarraContract.Subjects.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(url, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return url;
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
		case FRIENDS: {
			long rowID = getWritableDatabase().replace(
					FriendsTable.TABLE_FRIENDS, FriendsTable.KEY_COURSE_FRIEND,
					values);
			if (rowID > 0) {
				final Uri url = ContentUris.withAppendedId(
						SigarraContract.Friends.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(url, null);
				getContext().getContentResolver().notifyChange(uri, null);
				return url;
			}
			throw new SQLException("Failed to insert row into " + uri);
		}
		case PROFILES: {
			long rowID = getWritableDatabase().replace(
					ProfilesTable.TABLE_PROFILES, null, values);
			if (rowID > 0) {
				final Uri url = ContentUris.withAppendedId(
						SigarraContract.Profiles.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(url, null);
				getContext().getContentResolver().notifyChange(uri, null);
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
		final Cursor c;
		switch (uriType) {
		case SUBJECTS:
			qb.setTables(SubjectsTable.TABLE_SUBJECTS);
			if (TextUtils.isEmpty(sortOrder))
				orderBy = SigarraContract.Subjects.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			c = qb.query(dbHelper.getReadableDatabase(), projection, selection,
					selectionArgs, null, null, orderBy);
			if (c.getCount() == 0) {
				final Bundle extras = new Bundle();
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				if (selectionArgs.length == 3) {
					/*
					 * This means we re getting single subject
					 */
					extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
					extras.putString(SyncAdapter.REQUEST_TYPE,
							SyncAdapter.SUBJECT);
					extras.putString(SyncAdapter.SUBJECT_CODE, selectionArgs[0]);
					extras.putString(SyncAdapter.SUBJECT_PERIOD,
							selectionArgs[1]);
					extras.putString(SyncAdapter.SUBJECT_YEAR, selectionArgs[2]);

				}
				ContentResolver.requestSync(
						new Account(AccountUtils
								.getActiveUserName(getContext()),
								Constants.ACCOUNT_TYPE),
						SigarraContract.CONTENT_AUTHORITY, extras);
			}
			break;
		case FRIENDS:
			qb.setTables(FriendsTable.TABLE_FRIENDS);
			if (TextUtils.isEmpty(sortOrder))
				orderBy = SigarraContract.Friends.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			c = qb.query(dbHelper.getReadableDatabase(), projection, selection,
					selectionArgs, null, null, orderBy);
			break;
		case PROFILES:
			qb.setTables(ProfilesTable.TABLE_PROFILES);
			if (TextUtils.isEmpty(sortOrder))
				orderBy = SigarraContract.Profiles.DEFAULT_SORT;
			else
				orderBy = sortOrder;
			c = qb.query(dbHelper.getReadableDatabase(), projection, selection,
					new String[]{ selectionArgs[0]}, null, null, orderBy);
			if (c.getCount() == 0) {
				final Bundle extras = new Bundle();
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
				extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
				extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.PROFILE);
				extras.putString(SyncAdapter.PROFILE_CODE, selectionArgs[0]);
				extras.putString(SyncAdapter.PROFILE_TYPE, selectionArgs[1]);
				ContentResolver.requestSync(
						new Account(AccountUtils
								.getActiveUserName(getContext()),
								Constants.ACCOUNT_TYPE),
						SigarraContract.CONTENT_AUTHORITY, extras);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final int count;
		int uriType = sURIMatcher.match(uri);
		final String table;
		switch (uriType) {
		case SUBJECTS:
			table = SubjectsTable.TABLE_SUBJECTS;
			break;
		case FRIENDS:
			table = FriendsTable.TABLE_FRIENDS;
			break;
		case PROFILES:
			table = ProfilesTable.TABLE_PROFILES;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		count = getWritableDatabase().update(
				table, values, selection,
				selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
