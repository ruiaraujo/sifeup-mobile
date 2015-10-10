/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.mobile.loaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import pt.up.mobile.content.SigarraContract;
import pt.up.mobile.datatypes.StudentCourse;
import pt.up.mobile.datatypes.SubjectEntry;
import pt.up.mobile.sifeup.AccountUtils;
import pt.up.mobile.utils.GsonUtils;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;

/**
 * Static library support version of the framework's
 * {@link android.content.CursorLoader}. Used to write apps that run on
 * platforms prior to Android 3.0. When running on Android 3.0 or above, this
 * implementation is still used; it does not try to switch to the framework's
 * implementation. See the framework SDK documentation for a class overview.
 */
public class SubjectsLoader extends AsyncTaskLoader<StudentCourse[]> {
	final ForceLoadContentObserver mObserver;

	Uri mUri;
	String[] mProjection;
	String mSelection;
	String[] mSelectionArgs;
	String mSortOrder;

	StudentCourse[] subjects;
	Cursor mCursor;

	/* Runs on a worker thread */
	@Override
	public StudentCourse[] loadInBackground() {
		Cursor cursor = getContext().getContentResolver().query(mUri,
				mProjection, mSelection, mSelectionArgs, mSortOrder);
		if (cursor != null) {
			// Ensure the cursor window is filled
			cursor.getCount();
			registerContentObserver(cursor, mObserver);

			if (mCursor != null && !mCursor.isClosed()) {
				mCursor.close();
			}
			mCursor = cursor;
			if (cursor.moveToFirst()) {
				// This is not one of my finest moments
				final Gson gson = GsonUtils.getGson();
				final HashMap<String, List<SubjectEntry>> entries = new HashMap<String, List<SubjectEntry>>();
				final HashMap<String, String> courses = new HashMap<String, String>();
				do {
					final String courseId = cursor
							.getString(cursor
									.getColumnIndex(SigarraContract.SubjectsColumns.COURSE_ID));
					if (entries.get(courseId) == null) {
						entries.put(courseId, new ArrayList<SubjectEntry>());
						courses.put(
								courseId,
								cursor.getString(cursor
										.getColumnIndex(SigarraContract.SubjectsColumns.COURSE_ACRONYM)));
					}
					final List<SubjectEntry> courseEntries = entries
							.get(courseId);
					courseEntries
							.add(gson.fromJson(
									cursor.getString(cursor
											.getColumnIndex(SigarraContract.SubjectsColumns.COURSE_ENTRY)),
									SubjectEntry.class));
				} while (cursor.moveToNext());
				cursor.close();
				final List<StudentCourse> coursesList = new ArrayList<StudentCourse>();
				Iterator<Entry<String, List<SubjectEntry>>> iterator = entries
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, List<SubjectEntry>> entry = iterator.next();
					coursesList.add(new StudentCourse(entry.getKey(), courses
							.get(entry.getKey()), entry.getValue().toArray(
							new SubjectEntry[0])));
				}
				return coursesList.toArray(new StudentCourse[0]);
			} else {
				final Cursor syncState = getContext()
						.getContentResolver()
						.query(SigarraContract.LastSync.CONTENT_URI,
								SigarraContract.LastSync.COLUMNS,
								SigarraContract.LastSync.PROFILE,
								SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())), null);
				if (syncState.moveToFirst()) {
					if (syncState.getLong(syncState
							.getColumnIndex(SigarraContract.LastSync.SUBJECTS)) != 0) {
						syncState.close();
						return new StudentCourse[0];
					}
				} else
					throw new RuntimeException("It should always have a result");
				syncState.close();
			}
		}
		return null;
	}

	/**
	 * Registers an observer to get notifications from the content provider when
	 * the cursor needs to be refreshed.
	 */
	void registerContentObserver(Cursor cursor, ContentObserver observer) {
		cursor.registerContentObserver(mObserver);
	}

	/* Runs on the UI thread */
	@Override
	public void deliverResult(StudentCourse[] subjects) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (subjects != null) {
				subjects = null;
			}
			return;
		}
		this.subjects = subjects;
		if (isStarted()) {
			super.deliverResult(subjects);
		}

	}

	/**
	 * Creates an empty unspecified CursorLoader. You must follow this with
	 * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc to
	 * specify the query to perform.
	 */
	public SubjectsLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}

	/**
	 * Creates a fully-specified CursorLoader. See
	 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
	 * ContentResolver.query()} for documentation on the meaning of the
	 * parameters. These will be passed as-is to that call.
	 */
	public SubjectsLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context);
		mObserver = new ForceLoadContentObserver();
		mUri = uri;
		mProjection = projection;
		mSelection = selection;
		mSelectionArgs = selectionArgs;
		mSortOrder = sortOrder;
	}

	/**
	 * Starts an asynchronous load of the contacts list data. When the result is
	 * ready the callbacks will be called on the UI thread. If a previous load
	 * has been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStartLoading() {
		if (subjects != null) {
			deliverResult(subjects);
		}
		if (takeContentChanged() || subjects == null) {
			forceLoad();
		}
	}

	/**
	 * Must be called from the UI thread
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	@Override
	public void onCanceled(StudentCourse[] subjects) {
		if (subjects != null) {
			subjects = null;
		}
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = null;
		subjects = null;
	}
}