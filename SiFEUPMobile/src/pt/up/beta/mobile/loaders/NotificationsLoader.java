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

package pt.up.beta.mobile.loaders;

import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.utils.GsonUtils;
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
public class NotificationsLoader extends AsyncTaskLoader<Notification[]> {
	final ForceLoadContentObserver mObserver;

	Uri mUri;
	String[] mProjection;
	String mSelection;
	String[] mSelectionArgs;
	String mSortOrder;

	Notification[] notifications;
	Cursor mCursor;

	/* Runs on a worker thread */
	@Override
	public Notification[] loadInBackground() {
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
					Notification[] notifications = new Notification[cursor.getCount()];
					final Gson gson = GsonUtils.getGson();
					int i = 0;
					do {
						notifications[i] = gson.fromJson(cursor.getString(cursor
												.getColumnIndex(SigarraContract.Notifcations.CONTENT)), Notification.class);
						notifications[i].setRead(SigarraContract.Notifcations.SEEN.equals(cursor.getString(cursor
								.getColumnIndex(SigarraContract.Notifcations.STATE))));
						++i;
					} while (cursor.moveToNext());
					return notifications;
			} else {
				final Cursor syncState = getContext().getContentResolver().query(
						SigarraContract.LastSync.CONTENT_URI,
						SigarraContract.LastSync.COLUMNS,
						SigarraContract.LastSync.PROFILE,
						SigarraContract.LastSync
								.getLastSyncSelectionArgs(AccountUtils
										.getActiveUserName(getContext())),
						null);
				if ( syncState.moveToFirst() ){
					if ( syncState.getLong(syncState.getColumnIndex(SigarraContract.LastSync.NOTIFICATIONS)) != 0 ){
						syncState.close();
						return new Notification[0];
					}
				}
				else
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
	public void deliverResult(Notification[] notifications) {
		if (isReset()) {
			// An async query came in while the loader is stopped
			if (notifications != null) {
				for ( int i = 0; i < notifications.length;++i )
					notifications[i] = null;
				notifications = null;
			}
			return;
		}
		final Notification[] oldNotifications = this.notifications;
		this.notifications = notifications;
		if (isStarted()) {
			super.deliverResult(notifications);
		}

		if (oldNotifications != null && oldNotifications != notifications
				&& oldNotifications.length != 0) {
			for ( int i = 0; i < oldNotifications.length;++i )
				oldNotifications[i] = null;
		}
	}

	/**
	 * Creates an empty unspecified CursorLoader. You must follow this with
	 * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc to
	 * specify the query to perform.
	 */
	public NotificationsLoader(Context context) {
		super(context);
		mObserver = new ForceLoadContentObserver();
	}

	/**
	 * Creates a fully-specified CursorLoader. See
	 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
	 * ContentResolver.query()} for documentation on the meaning of the
	 * parameters. These will be passed as-is to that call.
	 */
	public NotificationsLoader(Context context, Uri uri, String[] projection,
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
		if (notifications != null) {
			deliverResult(notifications);
		}
		if (takeContentChanged() || notifications == null) {
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
	public void onCanceled(Notification[] notifications) {
		if (notifications != null) {
			for ( int i = 0; i < notifications.length;++i )
				notifications[i] = null;
			notifications = null;
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
		if (notifications != null){
			for ( int i = 0; i < notifications.length;++i )
				notifications[i] = null;
		}
		notifications = null;
	}
}