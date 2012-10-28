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

import org.acra.ACRA;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.BaseColumns;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.content.SubjectsTable;
import pt.up.beta.mobile.content.SyncStates;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.datatypes.StudentCourse;
import pt.up.beta.mobile.datatypes.SubjectEntry;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.utils.DateUtils;
import pt.up.beta.mobile.utils.FileUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

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
	}

	@TargetApi(8)
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		String authToken = null;

		broadcastManager
				.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
						.putExtra(SIGARRASYNCADAPTER_STATUS, NETWORK_ERROR));
		try {
			// brand new cookie
			authToken = mAccountManager.blockingGetAuthToken(account,
					Constants.AUTHTOKEN_TYPE, false);
			if (authToken == null) {
				throw new AuthenticatorException();
			}
			if (extras.getBoolean(SINGLE_REQUEST)) {
				Log.d(getClass().getSimpleName(), "Fetching Sigarra");
				if (SUBJECT.equals(extras.getString(REQUEST_TYPE))) {
					getSubject(account, extras.getString(CODE), authToken,
							syncResult);
					return;
				}
				if (PROFILE.equals(extras.getSerializable(REQUEST_TYPE))) {
					getProfile(extras.getString(PROFILE_CODE),
							extras.getString(PROFILE_TYPE), authToken,
							syncResult);
					return;
				}

				if (EXAMS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncExams(account, authToken, syncResult);
					return;
				}
				if (ACADEMIC_PATH.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncAcademicPath(account, authToken, syncResult);
					return;
				}
				if (PROFILE_PIC.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncProfilePic(extras.getString(CODE), authToken,
							syncResult);
					return;
				}
				if (TUITION.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncTuition(account, authToken, syncResult);
					return;
				}
				if (PRINTING_QUOTA.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncPrintingQuota(account, authToken, syncResult);
					return;
				}
				if (NOTIFICATIONS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncNotifications(account, authToken, syncResult);
					return;
				}
				if (CANTEENS.equals(extras.getSerializable(REQUEST_TYPE))) {
					syncCanteens(account, authToken, syncResult);
					return;
				}
				if (SCHEDULE.equals(extras.getSerializable(REQUEST_TYPE))) {
					getSchedule(extras.getString(SCHEDULE_CODE),
							extras.getString(SCHEDULE_TYPE),
							extras.getString(SCHEDULE_INITIAL),
							extras.getString(SCHEDULE_FINAL), SyncStates.PRUNE,
							authToken, syncResult);
					return;
				}
			} else {
				Log.d(getClass().getSimpleName(), "Sync Sigarra");
				syncProfiles(account, authToken, syncResult);
				syncSubjects(account, authToken, syncResult);
				syncExams(account, authToken, syncResult);
				syncAcademicPath(account, authToken, syncResult);
				syncTuition(account, authToken, syncResult);
				syncPrintingQuota(account, authToken, syncResult);
				syncSchedule(account, authToken, syncResult);
				syncNotifications(account, authToken, syncResult);
				syncCanteens(account, authToken, syncResult);
			}
		} catch (OperationCanceledException e) {
			e.printStackTrace();
			broadcastManager
			.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
					.putExtra(SIGARRASYNCADAPTER_STATUS, CANCELLED_ERROR));
		} catch (AuthenticatorException e) {
			syncResult.stats.numAuthExceptions++;
			e.printStackTrace();
			broadcastManager
			.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
					.putExtra(SIGARRASYNCADAPTER_STATUS, GENERAL_ERROR));
		} catch (IOException e) {
			// syncResult.stats.numIoExceptions++;
			broadcastManager
					.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
							.putExtra(SIGARRASYNCADAPTER_STATUS, NETWORK_ERROR));
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, null);
			syncResult.stats.numAuthExceptions++;
			broadcastManager
			.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
					.putExtra(SIGARRASYNCADAPTER_STATUS, AUTHENTICATION_ERROR));
		} catch (Exception e) {
			broadcastManager
			.sendBroadcast(new Intent(SIGARRASYNCADAPTER_STATUS)
					.putExtra(SIGARRASYNCADAPTER_STATUS, GENERAL_ERROR));
			e.printStackTrace();
			ACRA.getErrorReporter().handleSilentException(e);
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(getContext())));
		}
	}

	private void syncCanteens(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String canteens = SifeupAPI.getReply(SifeupAPI.getCanteensUrl(),
				authToken, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.Canteens.ID,
				SigarraContract.Canteens.DEFAULT_ID);
		values.put(SigarraContract.Canteens.CONTENT, canteens);
		getContext().getContentResolver().insert(
				SigarraContract.Canteens.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncNotifications(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String notificationReply = SifeupAPI.getReply(SifeupAPI
				.getNotificationsUrl(mAccountManager.getUserData(account,
						Constants.USER_CODE)), authToken, getContext());
		final Gson gson = new Gson();
		final Notification[] notifications = gson.fromJson(notificationReply,
				Notification[].class);
		if (notifications == null) {
			syncResult.stats.numParseExceptions++;
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(getContext())
							+ "\nPage:" + notificationReply));
			return;
		}
		ArrayList<String> fetchedNotCodes = new ArrayList<String>();
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
				getContext().getContentResolver().insert(
						SigarraContract.Notifcations.CONTENT_URI, values);
			}
		}
		final Cursor cursor = getContext().getContentResolver().query(
				SigarraContract.Notifcations.CONTENT_URI,
				new String[] { SigarraContract.Notifcations.ID_NOTIFICATION },
				SigarraContract.Notifcations.PROFILE,
				SigarraContract.Notifcations
						.getNotificationsSelectionArgs(account.name), null);
		if (cursor.moveToFirst()) {
			if (!fetchedNotCodes.contains(cursor.getString(0))) {
				getContext().getContentResolver().delete(
						SigarraContract.Notifcations.CONTENT_URI,
						SigarraContract.Notifcations.UPDATE_NOTIFICATION,
						SigarraContract.Notifcations
								.getNotificationsSelectionArgs(account.name,
										cursor.getString(0)));
			}
		} else {
			// No notifications
			final ContentValues values = new ContentValues();
			values.put(SigarraContract.LastSync.NOTIFICATIONS,
					System.currentTimeMillis());
			getContext().getContentResolver().update(
					SigarraContract.LastSync.CONTENT_URI,
					values,
					SigarraContract.LastSync.PROFILE,
					SigarraContract.LastSync
							.getLastSyncSelectionArgs(account.name));
			getContext().getContentResolver().notifyChange(
					SigarraContract.Notifcations.CONTENT_URI, null);
		}
		cursor.close();
		syncResult.stats.numEntries += notifications.length;
	}

	private void syncSchedule(Account account, String authToken,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		final String type;
		if (mAccountManager.getUserData(account, Constants.USER_TYPE).equals(
				SifeupAPI.STUDENT_TYPE))
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
		getSchedule(mAccountManager.getUserData(account, Constants.USER_CODE),
				type, initialDay, finalDay, SyncStates.KEEP, authToken,
				syncResult);
	}

	private void getSchedule(String code, String type, String initialDay,
			String finalDay, String state, String authToken,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		final String page;
		if (SigarraContract.Schedule.STUDENT.equals(type))
			page = SifeupAPI
					.getReply(SifeupAPI.getStudentScheduleUrl(code, initialDay,
							finalDay), authToken, getContext());
		else if (SigarraContract.Schedule.EMPLOYEE.equals(type)) {
			page = SifeupAPI
					.getReply(SifeupAPI.getTeacherScheduleUrl(code, initialDay,
							finalDay), authToken, getContext());
		} else if (SigarraContract.Schedule.ROOM.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getRoomScheduleUrl(code, initialDay, finalDay),
					authToken, getContext());
		} else if (SigarraContract.Schedule.UC.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getUcScheduleUrl(code, initialDay, finalDay),
					authToken, getContext());
		} else if (SigarraContract.Schedule.CLASS.equals(type)) {
			page = SifeupAPI.getReply(
					SifeupAPI.getClassScheduleUrl(code, initialDay, finalDay),
					authToken, getContext());
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

	private void syncPrintingQuota(Account account, String authToken,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		final String printing = SifeupAPI.getReply(SifeupAPI
				.getPrintingUrl(mAccountManager.getUserData(account,
						Constants.USER_CODE)), authToken, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.PrintingQuotaColumns.ID, account.name);
		values.put(SigarraContract.PrintingQuotaColumns.QUOTA, new JSONObject(
				printing).getDouble("saldo"));
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.PrintingQuota.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncTuition(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String tuition = SifeupAPI.getReply(SifeupAPI
				.getTuitionUrl(mAccountManager.getUserData(account,
						Constants.USER_CODE)), authToken, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.TuitionColumns.ID, account.name);
		values.put(SigarraContract.TuitionColumns.CONTENT, tuition);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Tuition.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncAcademicPath(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String academicPath = SifeupAPI.getReply(SifeupAPI
				.getStudentAcademicPathUrl(mAccountManager.getUserData(account,
						Constants.USER_CODE)), authToken, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.AcademicPathColumns.ID, account.name);
		values.put(SigarraContract.AcademicPathColumns.CONTENT, academicPath);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.AcademicPath.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncExams(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String exams = SifeupAPI.getReply(SifeupAPI
				.getExamsUrl(mAccountManager.getUserData(account,
						Constants.USER_CODE)), authToken, getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ExamsColumns.ID, account.name);
		values.put(SigarraContract.ExamsColumns.CONTENT, exams);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Exams.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void getProfile(String userCode, String type, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String profile;
		if (type.equals(SifeupAPI.STUDENT_TYPE))
			profile = SifeupAPI.getReply(
					SifeupAPI.getStudenProfiletUrl(userCode), authToken,
					getContext());
		else
			profile = SifeupAPI.getReply(
					SifeupAPI.getEmployeProfileeUrl(userCode), authToken,
					getContext());
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, userCode);
		values.put(SigarraContract.ProfileColumns.CONTENT, profile);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.PRUNE);
		getContext().getContentResolver().insert(
				SigarraContract.Profiles.CONTENT_URI, values);

		// Getting pic
		final ContentValues pic = new ContentValues();
		final String picPath = getProfilePic(userCode, authToken, syncResult);
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

	private void syncProfiles(Account account, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException,
			JSONException {
		final String userCode = mAccountManager.getUserData(account,
				Constants.USER_CODE);
		final String profile;
		final String type = mAccountManager.getUserData(account,
				Constants.USER_TYPE);
		if (type.equals(SifeupAPI.STUDENT_TYPE)) {
			profile = SifeupAPI.getReply(
					SifeupAPI.getStudenProfiletUrl(userCode), authToken,
					getContext());
		} else {
			profile = SifeupAPI.getReply(
					SifeupAPI.getEmployeProfileeUrl(userCode), authToken,
					getContext());
		}
		final String picPath = getProfilePic(userCode, authToken, syncResult);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, userCode);
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
				SigarraContract.Friends.getUserFriendsSelectionArgs(userCode),
				null);
		try {
			if (c.moveToFirst()) {
				final ContentValues[] friends = new ContentValues[c.getCount()];
				int i = 0;
				do {
					final ContentValues friendValues = new ContentValues();
					final String friendCode = c
							.getString(c
									.getColumnIndex(SigarraContract.FriendsColumns.CODE_FRIEND));
					final String friendPic = getProfilePic(friendCode,
							authToken, syncResult);
					friendValues.put(SigarraContract.ProfileColumns.ID,
							friendCode);
					final String friendCourse = c
							.getString(c
									.getColumnIndex(SigarraContract.FriendsColumns.TYPE_FRIEND));
					final String friendPage;
					if (friendCourse != null) {
						friendPage = SifeupAPI.getReply(
								SifeupAPI.getStudenProfiletUrl(friendCode),
								authToken, getContext());
					} else {
						friendPage = SifeupAPI.getReply(
								SifeupAPI.getEmployeProfileeUrl(friendCode),
								authToken, getContext());
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

	private void syncProfilePic(String userCode, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final String picPath = getProfilePic(userCode, authToken, syncResult);
		if (picPath == null)
			return;
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.PIC, picPath);
		getContext().getContentResolver().update(
				SigarraContract.Profiles.PIC_CONTENT_URI, values,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles.getProfilePicSelectionArgs(userCode));

	}

	private String getProfilePic(String userCode, String authToken,
			SyncResult syncResult) throws AuthenticationException, IOException {
		final File f = FileUtils.getFile(getContext(), userCode);
		if (f == null)
			return null;
		final Bitmap pic = SifeupAPI.downloadBitmap(
				SifeupAPI.getPersonPicUrl(userCode), authToken, getContext());
		if (pic == null) {
			syncResult.stats.numIoExceptions++;
			return null;
		}
		FileUtils.writeFile(pic, f);
		return f.getAbsolutePath();
	}

	private void getSubject(Account account, String code, String authToken,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		if (TextUtils.isEmpty(code)) {
			syncSubjects(account, authToken, syncResult);
			return;
		}
		final String subjectContent = SifeupAPI.getReply(
				SifeupAPI.getSubjectProfileUrl(code), authToken, getContext());
		final String subjectFiles = SifeupAPI.getReply(
				SifeupAPI.getSubjectFilestUrl(code), authToken, getContext());
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

	private void syncSubjects(Account account, String authToken,
			SyncResult syncResult) throws JSONException,
			AuthenticationException, IOException {
		// Cleaning old values
		getContext().getContentResolver().delete(
				SigarraContract.Subjects.CONTENT_URI,
				SigarraContract.Subjects.USER_SUBJECTS,
				SigarraContract.Subjects
						.getUserSubjectsSelectionArgs(account.name));
		final String subjectsPage = SifeupAPI
				.getReply(SifeupAPI
						.getStudentCurrentSubjectsUrl(mAccountManager
								.getUserData(account, Constants.USER_CODE)),
						authToken, getContext());
		final Gson gson = new Gson();
		Type listType = new TypeToken<StudentCourse[]>() {
		}.getType();
		final StudentCourse[] courses = gson.fromJson(subjectsPage, listType);
		if (courses == null) {
			syncResult.stats.numParseExceptions++;
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(getContext())
							+ "\nPage:" + subjectsPage));
			return;
		}
		final List<ContentValues> values = new ArrayList<ContentValues>();
		for (StudentCourse course : courses) {
			for (SubjectEntry subject : course.getSubjectEntries()) {
				final String subjectContent = SifeupAPI.getReply(
						SifeupAPI.getSubjectProfileUrl(subject.getOcorrid()),
						authToken, getContext());
				final String subjectFiles = SifeupAPI.getReply(
						SifeupAPI.getSubjectFilestUrl(subject.getOcorrid()),
						authToken, getContext());
				final ContentValues value = new ContentValues();
				value.put(SigarraContract.SubjectsColumns.USER_NAME,
						account.name);
				value.put(SigarraContract.SubjectsColumns.CODE,
						subject.getOcorrid());
				value.put(SigarraContract.SubjectsColumns.NAME_PT,
						subject.getUcurrnome());
				value.put(SigarraContract.SubjectsColumns.NAME_EN,
						subject.getUcurrname());
				value.put(SigarraContract.SubjectsColumns.CONTENT,
						subjectContent);
				value.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
				value.put(SigarraContract.SubjectsColumns.COURSE_ID,
						course.getCourseId());
				value.put(SigarraContract.SubjectsColumns.COURSE_NAME,
						course.getCourseName());
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

}
