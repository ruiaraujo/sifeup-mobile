/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pt.up.beta.mobile.syncadapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import pt.up.beta.mobile.Constants;
import pt.up.mobile.R;
import pt.up.beta.mobile.content.BaseColumns;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.content.SubjectsTable;
import pt.up.beta.mobile.content.SyncStates;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.datatypes.StudentCourse;
import pt.up.beta.mobile.datatypes.SubjectEntry;
import pt.up.beta.mobile.datatypes.TeachingService;
import pt.up.beta.mobile.datatypes.TeachingService.Subject;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.notifications.NotificationsActivity;
import pt.up.beta.mobile.ui.notifications.NotificationsDescActivity;
import pt.up.beta.mobile.ui.notifications.NotificationsDescFragment;
import pt.up.beta.mobile.ui.notifications.NotificationsFragment;
import pt.up.beta.mobile.utils.DateUtils;
import pt.up.beta.mobile.utils.FileUtils;
import pt.up.beta.mobile.utils.GsonUtils;
import pt.up.beta.mobile.utils.LogUtils;
import pt.up.beta.mobile.utils.StringUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SigarraSyncAdapter extends AbstractThreadedSyncAdapter {

	final static String SINGLE_REQUEST = "single_request";
	final static String REQUEST_TYPE = "request_type";

	final static String PROFILE = "profile";
	final static String PROFILE_CODE = "profile_code";
	final static String PROFILE_TYPE = "profile_type";

	final static String SUBJECT = "subject";
	final static String EXAMS = "exams";
	final static String CANTEENS = "canteens";
	final static String TUITION = "tuition";
	final static String TEACHING_SERVICE = "teaching_service";
	final static String ACADEMIC_PATH = "academic_path";
	final static String PRINTING_QUOTA = "printing_quota";
	final static String NOTIFICATIONS = "notifications";
	final static String PROFILE_PIC = "pic";
	final static String CODE = "code";

	final static String SCHEDULE = "schedule";
	final static String SCHEDULE_CODE = "schedule_code";
	final static String SCHEDULE_TYPE = "schedule_type";
	final static String SCHEDULE_INITIAL = "initial";
	final static String SCHEDULE_FINAL = "final";

	public final static String NETWORK_ERROR = "pt.up.beta.mobile.syncadapter.NETWORK_ERROR";
	public final static String CANCELLED_ERROR = "pt.up.beta.mobile.syncadapter.CANCELLED_ERROR";
	public final static String GENERAL_ERROR = "pt.up.beta.mobile.syncadapter.GENERAL_ERROR";
	public final static String AUTHENTICATION_ERROR = "pt.up.beta.mobile.syncadapter.AUTHENTICATION_ERROR";
	public final static String SIGARRASYNCADAPTER_STATUS = "pt.up.beta.mobile.syncadapter.SIGARRASYNCADAPTER_STATUS";

	private final AccountManager mAccountManager;
	private LocalBroadcastManager broadcastManager;

	public SigarraSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context.getApplicationContext());
		broadcastManager = LocalBroadcastManager.getInstance(context
				.getApplicationContext());
		EasyTracker.getInstance().setContext(context);
	}

	@TargetApi(8)
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		boolean listeningToProblems = false;
		try {
			if (extras.getBoolean(SINGLE_REQUEST)) {
				listeningToProblems = true;
				Log.d(getClass().getSimpleName(), "Fetching Sigarra");
				if (SUBJECT.equals(extras.getString(REQUEST_TYPE))) {
					getSubject(account, extras.getString(CODE), syncResult);
					return;
				}
				if (PROFILE.equals(extras.getSerializable(REQUEST_TYPE))) {
					getProfile(account, extras.getString(PROFILE_CODE),
							extras.getString(PROFILE_TYPE), syncResult);
					return;
				}

				if (EXAMS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncExams(account, syncResult);
					return;
				}
				if (ACADEMIC_PATH.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncAcademicPath(account, syncResult);
					return;
				}
				if (PROFILE_PIC.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncProfilePic(extras.getString(CODE), account, syncResult);
					return;
				}
				if (TUITION.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncTuition(account, syncResult);
					return;
				}
				if (TEACHING_SERVICE.equals(extras
						.getSerializable(REQUEST_TYPE))) {
					syncTeachingService(account, syncResult);
					return;
				}
				if (PRINTING_QUOTA.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncPrintingQuota(account, syncResult);
					return;
				}
				if (NOTIFICATIONS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncNotifications(account, syncResult);
					return;
				}
				if (CANTEENS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncCanteens(account, syncResult);
					return;
				}
				if (SCHEDULE.equals(extras.getSerializable(REQUEST_TYPE))) {
					getSchedule(account, extras.getString(SCHEDULE_CODE),
							extras.getString(SCHEDULE_TYPE),
							extras.getString(SCHEDULE_INITIAL),
							extras.getString(SCHEDULE_FINAL), SyncStates.PRUNE,
							syncResult);
					return;
				}
			} else {
				Log.d(getClass().getSimpleName(), "Sync Sigarra");
				syncProfiles(account, syncResult);
				syncExams(account, syncResult);
				syncTuition(account, syncResult);
				syncPrintingQuota(account, syncResult);
				syncSchedule(account, syncResult);
				syncNotifications(account, syncResult);
				syncCanteens(account, syncResult);
				final User user = AccountUtils.getUser(getContext(),
						account.name);
				if (user.getType().equals(SifeupAPI.STUDENT_TYPE)) {
					syncAcademicPath(account, syncResult);
					syncSubjects(account, syncResult);
				} else {
					syncTeachingService(account, syncResult);
				}
			}
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				syncResult.delayUntil = 3600;
			if (listeningToProblems)
				broadcastManager.sendBroadcast(new Intent(
						SIGARRASYNCADAPTER_STATUS).putExtra(
						SIGARRASYNCADAPTER_STATUS, NETWORK_ERROR));
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, null);
			syncResult.stats.numAuthExceptions++;
			if (listeningToProblems)
				broadcastManager.sendBroadcast(new Intent(
						SIGARRASYNCADAPTER_STATUS).putExtra(
						SIGARRASYNCADAPTER_STATUS, AUTHENTICATION_ERROR));
		} catch (Exception e) {
			if (listeningToProblems)
				broadcastManager.sendBroadcast(new Intent(
						SIGARRASYNCADAPTER_STATUS).putExtra(
						SIGARRASYNCADAPTER_STATUS, GENERAL_ERROR));
			e.printStackTrace();
			LogUtils.trackException(getContext(), e, null, true);
		}
	}

	private void syncCanteens(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException {
		final String canteens = SifeupAPI.getReply(SifeupAPI.getCanteensUrl(),
				account, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.Canteens.ID,
				SigarraContract.Canteens.DEFAULT_ID);
		values.put(SigarraContract.Canteens.CONTENT, canteens);
		getContext().getContentResolver().insert(
				SigarraContract.Canteens.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncNotifications(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String notificationReply = SifeupAPI.getReply(
				SifeupAPI.getNotificationsUrl(user.getUserCode()), account,
				getContext());
		final Gson gson = GsonUtils.getGson();
		final Notification[] notifications = gson.fromJson(notificationReply,
				Notification[].class);
		if (notifications == null) {
			syncResult.stats.numParseExceptions++;
			LogUtils.trackException(getContext(), new RuntimeException(),
					notificationReply, true);
			return;
		}
		ArrayList<String> fetchedNotCodes = new ArrayList<String>();
		ArrayList<ContentValues> bulkValues = new ArrayList<ContentValues>();
		for (Notification not : notifications) {
			final ContentValues values = new ContentValues();
			values.put(SigarraContract.Notifcations.CONTENT, gson.toJson(not));
			fetchedNotCodes.add(not.getCode());
			if (getContext().getContentResolver().update(
					SigarraContract.Notifcations.CONTENT_URI,
					values,
					SigarraContract.Notifcations.UPDATE_NOTIFICATION,
					SigarraContract.Notifcations.getNotificationsSelectionArgs(
							account.name, not.getCode())) == 0) {
				values.put(SigarraContract.Notifcations.CODE, account.name);
				values.put(SigarraContract.Notifcations.ID_NOTIFICATION,
						not.getCode());
				values.put(SigarraContract.Notifcations.STATE,
						SigarraContract.Notifcations.NEW);
				values.put(SigarraContract.Notifcations.CODE, account.name);
				bulkValues.add(values);
			}

		}
		// inserting the values
		if (bulkValues.size() > 0) {
			getContext().getContentResolver().bulkInsert(
					SigarraContract.Notifcations.CONTENT_URI,
					bulkValues.toArray(new ContentValues[0]));
			// if the account being synced is the current active accout
			// display notification
			if (AccountUtils.getActiveUserName(getContext()).equals(
					account.name)) {
				final NotificationManager mNotificationManager = (NotificationManager) getContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
						getContext());
				notBuilder
						.setAutoCancel(true)
						.setOnlyAlertOnce(true)
						.setSound(
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
				if (bulkValues.size() == 1) {
					final Notification notification = gson.fromJson(
							bulkValues.get(0).getAsString(
									SigarraContract.Notifcations.CONTENT),
							Notification.class);
					Intent notifyIntent = new Intent(getContext(),
							NotificationsDescActivity.class).putExtra(
							NotificationsDescFragment.NOTIFICATION,
							notification);
					// Creates the PendingIntent
					PendingIntent notifyPendingIntent = PendingIntent
							.getActivity(getContext(), 0, notifyIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);

					// Sets the Activity to start in a new, empty task
					notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					notBuilder
							.setSmallIcon(R.drawable.icon)
							.setTicker(notification.getMessage())
							.setContentTitle(notification.getSubject())
							.setContentText(notification.getMessage())
							.setContentIntent(notifyPendingIntent)
							.setStyle(
									new NotificationCompat.BigTextStyle()
											.bigText(notification.getMessage())
											.setBigContentTitle(
													notification.getSubject())
											.setSummaryText(
													notification.getMessage()));
					mNotificationManager.notify(notification.getCode()
							.hashCode(), notBuilder.build());
				} else {
					final String notTitle = getContext().getString(
							R.string.new_notifications, bulkValues.size());

					Intent notifyIntent = new Intent(getContext(),
							NotificationsActivity.class);
					// Sets the Activity to start in a new, empty task
					notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					// Creates the PendingIntent
					PendingIntent notifyPendingIntent = PendingIntent
							.getActivity(getContext(), 0, notifyIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
					// Sets a title for the Inbox style big view
					inboxStyle.setBigContentTitle(notTitle);
					// Moves events into the big view
					for (ContentValues value : bulkValues) {
						final Notification notification = gson
								.fromJson(
										value.getAsString(SigarraContract.Notifcations.CONTENT),
										Notification.class);
						inboxStyle.addLine(notification.getSubject());
					}
					// Moves the big view style object into the notification
					// object.
					notBuilder.setStyle(inboxStyle);
					notBuilder.setSmallIcon(R.drawable.icon)
							.setTicker(notTitle).setContentTitle(notTitle)
							.setContentText("")
							.setContentIntent(notifyPendingIntent);
					mNotificationManager.notify(NotificationsFragment.class
							.getName().hashCode(), notBuilder.build());
				}

			}
		}
		final Cursor syncState = getContext().getContentResolver().query(
				SigarraContract.LastSync.CONTENT_URI,
				SigarraContract.LastSync.COLUMNS,
				SigarraContract.LastSync.PROFILE,
				SigarraContract.LastSync.getLastSyncSelectionArgs(AccountUtils
						.getActiveUserName(getContext())), null);
		try {
			if (syncState.moveToFirst()) {
				if (syncState
						.getLong(syncState
								.getColumnIndex(SigarraContract.LastSync.NOTIFICATIONS)) == 0) {
					// Report that we have checked the notifications
					final ContentValues values = new ContentValues();
					values.put(SigarraContract.LastSync.NOTIFICATIONS,
							System.currentTimeMillis());
					getContext().getContentResolver().update(
							SigarraContract.LastSync.CONTENT_URI,
							values,
							SigarraContract.LastSync.PROFILE,
							SigarraContract.LastSync
									.getLastSyncSelectionArgs(account.name));
				}
			}
		} finally {
			syncState.close();
		}
		ArrayList<String> notToDelete = new ArrayList<String>();
		final Cursor cursor = getContext().getContentResolver().query(
				SigarraContract.Notifcations.CONTENT_URI,
				new String[] { SigarraContract.Notifcations.ID_NOTIFICATION },
				SigarraContract.Notifcations.PROFILE,
				SigarraContract.Notifcations
						.getNotificationsSelectionArgs(account.name), null);
		try {
			if (cursor.moveToFirst()) {
				do {
					final String code = cursor.getString(0);
					if (!fetchedNotCodes.contains(code))
						notToDelete.add(code);
				} while (cursor.moveToNext());
			} else {
				// no notifications
				getContext().getContentResolver().notifyChange(
						SigarraContract.Notifcations.CONTENT_URI, null);
			}
		} finally {
			cursor.close();
		}
		if (notToDelete.size() > 0)
			getContext().getContentResolver().delete(
					SigarraContract.Notifcations.CONTENT_URI,
					SigarraContract.Notifcations
							.getNotificationsDelete(notToDelete
									.toArray(new String[0])),
					SigarraContract.Notifcations.getNotificationsSelectionArgs(
							account.name, notToDelete.toArray(new String[0])));
		syncResult.stats.numEntries += notifications.length;
	}

	private void syncSchedule(Account account, SyncResult syncResult)
			throws JSONException, AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String type;
		if (user.getType().equals(SifeupAPI.STUDENT_TYPE))
			type = SigarraContract.Schedule.STUDENT;
		else
			type = SigarraContract.Schedule.EMPLOYEE;
		final Long mondayMillis = DateUtils.firstDayofWeek();
		Time monday = new Time(DateUtils.TIME_REFERENCE);
		monday.set(mondayMillis);
		monday.normalize(false);
		String initialDay = monday.format("%Y%m%d");
		// Friday
		monday.set(DateUtils.moveDayofWeek(mondayMillis, 4));
		monday.normalize(false);
		String finalDay = monday.format("%Y%m%d");
		getSchedule(account,user.getUserCode(),
				type, initialDay, finalDay, SyncStates.KEEP, syncResult);
	}

	private void getSchedule(Account account, String code, String type,
			String initialDay, String finalDay, String state,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		final String page;
		if (SigarraContract.Schedule.STUDENT.equals(type))
			page = SifeupAPI
					.getReply(SifeupAPI.getStudentScheduleUrl(code, initialDay,
							finalDay), account, getContext());
		else if (SigarraContract.Schedule.EMPLOYEE.equals(type)) {
			page = SifeupAPI
					.getReply(SifeupAPI.getTeacherScheduleUrl(code, initialDay,
							finalDay), account, getContext());
		} else if (SigarraContract.Schedule.ROOM.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getRoomScheduleUrl(code, initialDay, finalDay),
					account, getContext());
		} else if (SigarraContract.Schedule.UC.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getUcScheduleUrl(code, initialDay, finalDay),
					account, getContext());
		} else if (SigarraContract.Schedule.CLASS.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getClassScheduleUrl(code, initialDay, finalDay),
					account, getContext());
		} else
			throw new RuntimeException("Unknown schedule type " + type);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ScheduleColumns.CODE, code);
		values.put(SigarraContract.ScheduleColumns.TYPE, type);
		values.put(SigarraContract.ScheduleColumns.CONTENT, page);
		values.put(SigarraContract.ScheduleColumns.INITIAL_DAY, initialDay);
		values.put(SigarraContract.ScheduleColumns.FINAL_DAY, finalDay);
		values.put(BaseColumns.COLUMN_STATE, state);
		getContext().getContentResolver().insert(
				SigarraContract.Schedule.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncPrintingQuota(Account account, SyncResult syncResult)
			throws JSONException, AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String printing = SifeupAPI.getReply(SifeupAPI
				.getPrintingUrl(user.getUserCode()), account, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.PrintingQuotaColumns.ID, account.name);
		values.put(SigarraContract.PrintingQuotaColumns.QUOTA, printing);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.PrintingQuota.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncTuition(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String tuition = SifeupAPI.getReply(SifeupAPI
				.getCurrentAccountUrl(user.getUserCode()), account, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.TuitionColumns.ID, account.name);
		values.put(SigarraContract.TuitionColumns.CONTENT, tuition);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Tuition.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncAcademicPath(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String academicPath = SifeupAPI.getReply(SifeupAPI
				.getStudentAcademicPathUrl(user.getUserCode()), account, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.AcademicPathColumns.ID, account.name);
		values.put(SigarraContract.AcademicPathColumns.CONTENT, academicPath);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.AcademicPath.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncExams(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String exams;
		if (user.getType().equals(SifeupAPI.STUDENT_TYPE))
			exams = SifeupAPI.getReply(
					SifeupAPI.getStudentExamsUrl(user.getUserCode()), account,
					getContext());
		else
			exams = SifeupAPI.getReply(
					SifeupAPI.getEmployeeExamsUrl(user.getUserCode()), account,
					getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ExamsColumns.ID, account.name);
		values.put(SigarraContract.ExamsColumns.CONTENT, exams);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Exams.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void getProfile(Account account, String userCode, String type,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String profile;
		if (type.equals(SifeupAPI.STUDENT_TYPE))
			profile = SifeupAPI.getReply(
					SifeupAPI.getStudenProfiletUrl(userCode), account,
					getContext());
		else
			profile = SifeupAPI.getReply(
					SifeupAPI.getEmployeeProfileUrl(userCode), account,
					getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, userCode);
		values.put(SigarraContract.ProfileColumns.CONTENT, profile);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.PRUNE);
		getContext().getContentResolver().insert(
				SigarraContract.Profiles.CONTENT_URI, values);

		// Getting pic
		final ContentValues pic = new ContentValues();
		final String picPath = getProfilePic(userCode, account, syncResult);
		if (picPath != null) {
			pic.put(SigarraContract.ProfileColumns.PIC, picPath);
			getContext().getContentResolver().update(
					SigarraContract.Profiles.PIC_CONTENT_URI,
					pic,
					SigarraContract.Profiles.PROFILE,
					SigarraContract.Profiles
							.getProfilePicSelectionArgs(userCode));
		}
		syncResult.stats.numEntries += 1;
	}

	private void syncProfiles(Account account, SyncResult syncResult)
			throws AuthenticationException, IOException, JSONException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String profile;
		if (user.getType().equals(SifeupAPI.STUDENT_TYPE)) {
			profile = SifeupAPI.getReply(
					SifeupAPI.getStudenProfiletUrl(user.getUserCode()),
					account, getContext());
		} else {
			profile = SifeupAPI.getReply(
					SifeupAPI.getEmployeeProfileUrl(user.getUserCode()),
					account, getContext());
		}
		final String picPath = getProfilePic(user.getUserCode(), account,
				syncResult);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, user.getUserCode());
		values.put(SigarraContract.ProfileColumns.CONTENT, profile);
		if (picPath != null)
			values.put(SigarraContract.ProfileColumns.PIC, picPath);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Profiles.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
		final Cursor c = getContext().getContentResolver().query(
				SigarraContract.Friends.CONTENT_URI,
				new String[] { SigarraContract.FriendsColumns.CODE_FRIEND,
						SigarraContract.FriendsColumns.TYPE_FRIEND },
				SigarraContract.Friends.USER_FRIENDS,
				SigarraContract.Friends.getUserFriendsSelectionArgs(user
						.getUserCode()), null);
		try {
			if (c.moveToFirst()) {
				final ContentValues[] friends = new ContentValues[c.getCount()];
				int i = 0;
				do {
					final ContentValues friendValues = new ContentValues();
					final String friendCode = c
							.getString(c
									.getColumnIndex(SigarraContract.FriendsColumns.CODE_FRIEND));
					final String friendPic = getProfilePic(friendCode, account,
							syncResult);
					friendValues.put(SigarraContract.ProfileColumns.ID,
							friendCode);
					final String friendType = c
							.getString(c
									.getColumnIndex(SigarraContract.FriendsColumns.TYPE_FRIEND));
					final String friendPage;
					if (friendType.equals(SifeupAPI.STUDENT_TYPE)) {
						friendPage = SifeupAPI.getReply(
								SifeupAPI.getStudenProfiletUrl(friendCode),
								account, getContext());
					} else {
						friendPage = SifeupAPI.getReply(
								SifeupAPI.getEmployeeProfileUrl(friendCode),
								account, getContext());
					}
					friendValues.put(SigarraContract.ProfileColumns.CONTENT,
							friendPage);
					if (friendPic != null)
						friendValues.put(SigarraContract.ProfileColumns.PIC,
								friendPic);
					friendValues.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
					friends[i++] = friendValues;
				} while (c.moveToNext());
				getContext().getContentResolver().bulkInsert(
						SigarraContract.Profiles.CONTENT_URI, friends);
				syncResult.stats.numEntries += friends.length;
			}
		} finally {
			c.close();
		}
		// Request sync for the contacts, if it is disabled in Settings
		// the sync won't be called
		ContentResolver.requestSync(account, ContactsContract.AUTHORITY,
				new Bundle());
	}

	private void syncProfilePic(String userCode, Account account,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String picPath = getProfilePic(userCode, account, syncResult);
		if (picPath == null)
			return;
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.PIC, picPath);
		getContext().getContentResolver().update(
				SigarraContract.Profiles.PIC_CONTENT_URI, values,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles.getProfilePicSelectionArgs(userCode));

	}

	private String getProfilePic(String userCode, Account account,
			SyncResult syncResult) {
		final File f = FileUtils.getFile(getContext(), userCode);
		if (f == null)
			return null;
		Bitmap pic;
		try {
			pic = SifeupAPI.downloadBitmap(SifeupAPI.getPersonPicUrl(userCode),
					account, getContext());
			if (pic == null) {
				return null;
			}
			FileUtils.writeFile(pic, f);
			return f.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void getSubject(Account account, String code, SyncResult syncResult)
			throws JSONException, AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		if (TextUtils.isEmpty(code)) {
			if (user.getType().equals(SifeupAPI.STUDENT_TYPE))
				syncSubjects(account, syncResult);
			else
				syncTeachingService(account, syncResult);
			return;
		}
		final String subjectContent = SifeupAPI.getReply(
				SifeupAPI.getSubjectProfileUrl(code), account, getContext());
		final String subjectFiles = SifeupAPI.getReply(
				SifeupAPI.getSubjectFilestUrl(code), account, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.SubjectsColumns.USER_NAME, account.name);
		values.put(SigarraContract.SubjectsColumns.CODE, code);
		values.put(SigarraContract.SubjectsColumns.CONTENT, subjectContent);
		values.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.PRUNE);
		getContext().getContentResolver().insert(
				SigarraContract.Subjects.CONTENT_ITEM_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncSubjects(Account account, SyncResult syncResult)
			throws JSONException, AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		// Cleaning old values
		getContext().getContentResolver().delete(
				SigarraContract.Subjects.CONTENT_URI,
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects
						.getUserSubjectsSelectionArgs(account.name));
		final String subjectsPage = SifeupAPI.getReply(SifeupAPI
				.getStudentCurrentSubjectsUrl(user.getUserCode()), account, getContext());
		final Gson gson = GsonUtils.getGson();
		Type listType = new TypeToken<StudentCourse[]>() {
		}.getType();
		final StudentCourse[] courses = gson.fromJson(subjectsPage, listType);
		if (courses == null) {
			syncResult.stats.numParseExceptions++;
			LogUtils.trackException(getContext(), new RuntimeException(),
					subjectsPage, true);
			return;
		}
		final List<ContentValues> values = new ArrayList<ContentValues>();
		for (StudentCourse course : courses) {
			for (SubjectEntry subject : course.getSubjectEntries()) {
				final String subjectContent = SifeupAPI.getReply(
						SifeupAPI.getSubjectProfileUrl(subject.getOcorrid()),
						account, getContext());
				final String subjectFiles = SifeupAPI.getReply(
						SifeupAPI.getSubjectFilestUrl(subject.getOcorrid()),
						account, getContext());
				final ContentValues value = new ContentValues();
				value.put(SigarraContract.SubjectsColumns.USER_NAME,
						account.name);
				value.put(SigarraContract.SubjectsColumns.CODE,
						subject.getOcorrid());
				value.put(SigarraContract.SubjectsColumns.NAME_PT,
						subject.getUcurrnome());
				value.put(SigarraContract.SubjectsColumns.NAME_EN,
						subject.getUcurrname());
				value.put(SigarraContract.SubjectsColumns.PERIOD,
						subject.getPercodigo());
				value.put(SigarraContract.SubjectsColumns.CONTENT,
						subjectContent);
				value.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
				value.put(SigarraContract.SubjectsColumns.COURSE_ID,
						course.getCourseId());
				if (course.getCourseName() != null)
					value.put(
							SigarraContract.SubjectsColumns.COURSE_ACRONYM,
							course.getCourseAcronym() == null ? StringUtils
									.getAcronym(course.getCourseName())
									: course.getCourseAcronym());
				else
					value.put(SigarraContract.SubjectsColumns.COURSE_ACRONYM,
							course.getCourseTypeDesc());
				value.put(SigarraContract.SubjectsColumns.COURSE_ENTRY,
						gson.toJson(subject));
				value.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
				values.add(value);
			}
		}
		if (values.size() > 0)
			getContext().getContentResolver().bulkInsert(
					SigarraContract.Subjects.CONTENT_URI,
					values.toArray(new ContentValues[0]));
		else {
			SigarraProvider.updateLastSyncState(getContext(),
					SubjectsTable.TABLE);
			getContext().getContentResolver().notifyChange(
					SigarraContract.Subjects.CONTENT_URI, null);
		}
		syncResult.stats.numEntries += values.size();
	}

	private void syncTeachingService(Account account, SyncResult syncResult)
			throws JSONException, AuthenticationException, IOException {
		final User user = AccountUtils.getUser(getContext(), account.name);
		final String teachingServicePage = SifeupAPI.getReply(SifeupAPI
				.getTeachingServiceUrl(user.getUserCode(), null), account, getContext());
		final TeachingService service = GsonUtils.getGson().fromJson(
				teachingServicePage, TeachingService.class);
		if (service == null) {
			syncResult.stats.numParseExceptions++;
			LogUtils.trackException(getContext(), new RuntimeException(),
					teachingServicePage, true);
			return;
		}
		final ContentValues teachingValue = new ContentValues();
		teachingValue.put(SigarraContract.TeachingService.ID, account.name);
		teachingValue.put(SigarraContract.TeachingService.CONTENT,
				teachingServicePage);
		getContext().getContentResolver().insert(
				SigarraContract.TeachingService.CONTENT_URI, teachingValue);
		final List<ContentValues> values = new ArrayList<ContentValues>();
		for (Subject subject : service.getService()) {
			final String subjectContent = SifeupAPI.getReply(
					SifeupAPI.getSubjectProfileUrl(subject.getOcorrId()),
					account, getContext());
			final String subjectFiles = SifeupAPI.getReply(
					SifeupAPI.getSubjectFilestUrl(subject.getOcorrId()),
					account, getContext());
			final ContentValues value = new ContentValues();
			value.put(SigarraContract.SubjectsColumns.USER_NAME, account.name);
			value.put(SigarraContract.SubjectsColumns.CODE,
					subject.getOcorrId());
			value.put(SigarraContract.SubjectsColumns.NAME_PT,
					subject.getUcurrName());
			value.put(SigarraContract.SubjectsColumns.PERIOD,
					subject.getPeriodCode());
			value.put(SigarraContract.SubjectsColumns.CONTENT, subjectContent);
			value.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
			value.put(SigarraContract.SubjectsColumns.COURSE_ACRONYM,
					subject.getCourse());
			value.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
			values.add(value);
		}
		if (values.size() > 0)
			getContext().getContentResolver().bulkInsert(
					SigarraContract.Subjects.CONTENT_URI,
					values.toArray(new ContentValues[0]));
		syncResult.stats.numEntries += values.size();
	}
}
