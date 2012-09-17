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

import java.io.IOException;
import java.util.List;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.BaseColumns;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.content.SyncStates;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.utils.DateUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	public final static String SINGLE_REQUEST = "single_request";
	public final static String REQUEST_TYPE = "request_type";

	public final static String SUBJECT = "subject";
	public final static String SUBJECT_CODE = "subject_code";
	public final static String SUBJECT_YEAR = "subject_year";
	public final static String SUBJECT_PERIOD = "subject_period";

	public final static String PROFILE = "profile";
	public final static String PROFILE_CODE = "profile_code";
	public final static String PROFILE_TYPE = "profile_type";

	public final static String EXAMS = "exams";
	public final static String TUITION = "tuition";
	public final static String ACADEMIC_PATH = "academic_path";
	public final static String PRINTING_QUOTA = "printing_quota";
	public final static String NOTIFICATIONS = "notifications";
	public final static String USER_CODE = "code";

	public final static String SCHEDULE = "schedule";
	public final static String SCHEDULE_CODE = "schedule_code";
	public final static String SCHEDULE_TYPE = "schedule_type";
	public final static String SCHEDULE_INITIAL = "initial";
	public final static String SCHEDULE_FINAL = "final";
	public final static String SCHEDULE_BASE_TIME = "schedule_time";

	private final AccountManager mAccountManager;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			SifeupAPI.initSSLContext(getContext().getApplicationContext());
			mAccountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE,
					mAccountManager.blockingGetAuthToken(account,
							Constants.AUTHTOKEN_TYPE, false));
			// brand new cookie
			final String authToken = mAccountManager.blockingGetAuthToken(
					account, Constants.AUTHTOKEN_TYPE, false);
			if (extras.getBoolean(SINGLE_REQUEST)) {
				if (SUBJECT.equals(extras.getString(REQUEST_TYPE))) {
					getSubject(account, extras.getString(SUBJECT_CODE),
							extras.getString(SUBJECT_YEAR),
							extras.getString(SUBJECT_PERIOD), authToken,
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
				if (SCHEDULE.equals(extras.getSerializable(REQUEST_TYPE))) {
					getSchedule(extras.getString(SCHEDULE_CODE),
							extras.getString(SCHEDULE_TYPE),
							extras.getString(SCHEDULE_INITIAL),
							extras.getString(SCHEDULE_FINAL),
							extras.getString(SCHEDULE_BASE_TIME),
							SyncStates.PRUNE, authToken, syncResult);
					return;
				}
			} else {
				syncProfiles(account, authToken, syncResult);
				syncSubjects(account, authToken, syncResult);
				syncExams(account, authToken, syncResult);
				syncAcademicPath(account, authToken, syncResult);
				syncTuition(account, authToken, syncResult);
				syncPrintingQuota(account, authToken, syncResult);
				syncSchedule(account, authToken, syncResult);
				syncNotifications(account, authToken, syncResult);
			}
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			syncResult.stats.numAuthExceptions++;
			e.printStackTrace();
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			e.printStackTrace();
		} catch (JSONException e) {
			syncResult.stats.numParseExceptions++;
			e.printStackTrace();
			ACRA.getErrorReporter().handleSilentException(e);
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(null)));
		}
	}

	private void syncNotifications(Account account, String authToken,
			SyncResult syncResult) throws JSONException {
		final String notificationReply = SifeupAPI.getReply(
				SifeupAPI.getNotificationsUrl(), authToken);
		JSONObject jObject = new JSONObject(notificationReply);
		JSONArray jArray = jObject.getJSONArray("notificacoes");
		for (int i = 0; i < jArray.length(); i++) {
			Notification not = Notification.parseJSON(jArray.getJSONObject(i));
			final ContentValues values = new ContentValues();
			values.put(SigarraContract.Notifcations.CONTENT, jArray
					.getJSONObject(i).toString());
			if (getContext().getContentResolver().update(
					SigarraContract.Notifcations.CONTENT_URI,
					values,
					SigarraContract.Notifcations.UPDATE_NOTIFICATION,
					SigarraContract.Notifcations.getNotificationsSelectionArgs(
							account.name, Integer.toString(not.getCode()))) == 0) {
				values.put(SigarraContract.Notifcations.CODE, account.name);
				values.put(SigarraContract.Notifcations.ID_NOTIFICATION,
						Integer.toString(not.getCode()));
				values.put(SigarraContract.Notifcations.STATE,
						SigarraContract.Notifcations.NEW);
				values.put(SigarraContract.Notifcations.CODE, account.name);
				getContext().getContentResolver().insert(
						SigarraContract.Notifcations.CONTENT_URI, values);
			}
		}
	}

	private void syncSchedule(Account account, String authToken,
			SyncResult syncResult) throws JSONException {
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
		getSchedule(mAccountManager.getUserData(account, Constants.USER_NAME),
				type, initialDay, finalDay, Long.toString(mondayMillis),
				SyncStates.KEEP, authToken, syncResult);
	}

	private void getSchedule(String code, String type, String initialDay,
			String finalDay, String baseTime, String state, String authToken,
			SyncResult syncResult) throws JSONException {
		final String page;
		if (SigarraContract.Schedule.STUDENT.equals(type))
			page = SifeupAPI.getReply(
					SifeupAPI.getScheduleUrl(code, initialDay, finalDay),
					authToken);
		else if (SigarraContract.Schedule.EMPLOYEE.equals(type)) {
			page = SifeupAPI
					.getReply(SifeupAPI.getTeacherScheduleUrl(code, initialDay,
							finalDay), authToken);
		} else if (SigarraContract.Schedule.ROOM.equals(type)) {
			page = SifeupAPI
					.getReply(SifeupAPI.getRoomScheduleUrl(
							code.substring(0, 1), code.substring(1),
							initialDay, finalDay), authToken);
		} else
			page = SifeupAPI.getReply(
					SifeupAPI.getUcScheduleUrl(code, initialDay, finalDay),
					authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ScheduleColumns.CODE, code);
		values.put(SigarraContract.ScheduleColumns.TYPE, type);
		values.put(SigarraContract.ScheduleColumns.CONTENT, page);
		values.put(SigarraContract.ScheduleColumns.INITIAL_DAY, initialDay);
		values.put(SigarraContract.ScheduleColumns.FINAL_DAY, finalDay);
		values.put(SigarraContract.ScheduleColumns.BASE_TIME, baseTime);
		values.put(BaseColumns.COLUMN_STATE, state);
		getContext().getContentResolver().insert(
				SigarraContract.Schedule.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncPrintingQuota(Account account, String authToken,
			SyncResult syncResult) throws JSONException {
		final String printing = SifeupAPI.getReply(SifeupAPI
				.getPrintingUrl(mAccountManager.getUserData(account,
						Constants.USER_NAME)), authToken);
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
			SyncResult syncResult) {
		final String tuition = SifeupAPI.getReply(SifeupAPI
				.getTuitionUrl(mAccountManager.getUserData(account,
						Constants.USER_NAME)), authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.TuitionColumns.ID, account.name);
		values.put(SigarraContract.TuitionColumns.CONTENT, tuition);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Tuition.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncAcademicPath(Account account, String authToken,
			SyncResult syncResult) {
		final String academicPath = SifeupAPI.getReply(SifeupAPI
				.getAcademicPathUrl(mAccountManager.getUserData(account,
						Constants.USER_NAME)), authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.AcademicPathColumns.ID, account.name);
		values.put(SigarraContract.AcademicPathColumns.CONTENT, academicPath);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.AcademicPath.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncExams(Account account, String authToken,
			SyncResult syncResult) {
		final String exams = SifeupAPI.getReply(SifeupAPI
				.getExamsUrl(mAccountManager.getUserData(account,
						Constants.USER_NAME)), authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ExamsColumns.ID, account.name);
		values.put(SigarraContract.ExamsColumns.CONTENT, exams);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Exams.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void getProfile(String userCode, String type, String authToken,
			SyncResult syncResult) {
		final String profile;
		if (type.equals(SifeupAPI.STUDENT_TYPE))
			profile = SifeupAPI.getReply(SifeupAPI.getStudentUrl(userCode),
					authToken);
		else
			profile = SifeupAPI.getReply(SifeupAPI.getEmployeeUrl(userCode),
					authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, userCode);
		values.put(SigarraContract.ProfileColumns.CONTENT, profile);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.PRUNE);
		getContext().getContentResolver().insert(
				SigarraContract.Profiles.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncProfiles(Account account, String authToken,
			SyncResult syncResult) {
		final String userCode = mAccountManager.getUserData(account,
				Constants.USER_NAME);
		final String profile;
		final String type = mAccountManager.getUserData(account,
				Constants.USER_TYPE);
		if (type.equals(SifeupAPI.STUDENT_TYPE))
			profile = SifeupAPI.getReply(SifeupAPI.getStudentUrl(userCode),
					authToken);
		else
			profile = SifeupAPI.getReply(SifeupAPI.getEmployeeUrl(userCode),
					authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.ProfileColumns.ID, userCode);
		values.put(SigarraContract.ProfileColumns.CONTENT, profile);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
		getContext().getContentResolver().insert(
				SigarraContract.Profiles.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
		final Cursor c = getContext().getContentResolver().query(
				SigarraContract.Friends.CONTENT_URI,
				new String[] { SigarraContract.FriendsColumns.CODE_FRIEND },
				SigarraContract.Friends.USER_FRIENDS,
				SigarraContract.Friends.getUserFriendsSelectionArgs(userCode),
				null);
		if (c.moveToFirst()) {
			final ContentValues[] friends = new ContentValues[c.getCount()];
			int i = 0;
			do {
				final ContentValues friend = new ContentValues();
				final String friendCode = c.getString(0);
				friend.put(SigarraContract.ProfileColumns.ID, friendCode);
				friend.put(SigarraContract.ProfileColumns.CONTENT, SifeupAPI
						.getReply(SifeupAPI.getStudentUrl(friendCode),
								authToken));
				friend.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
				friends[i++] = friend;
			} while (c.moveToNext());
			getContext().getContentResolver().bulkInsert(
					SigarraContract.Profiles.CONTENT_URI, friends);
			syncResult.stats.numEntries += friends.length;
		}
		c.close();

	}

	private void getSubject(Account account, String code, String year,
			String period, String authToken, SyncResult syncResult)
			throws JSONException {
		if (TextUtils.isEmpty(code)) {
			syncSubjects(account, authToken, syncResult);
			return;
		}
		final String subjectContent = SifeupAPI.getReply(
				SifeupAPI.getSubjectContentUrl(code, year, period), authToken);
		final String subjectFiles = SifeupAPI.getReply(
				SifeupAPI.getSubjectFilestUrl(code, year, period), authToken);
		final ContentValues values = new ContentValues();
		values.put(SigarraContract.SubjectsColumns.USER_NAME, "");
		values.put(SigarraContract.SubjectsColumns.CODE, code);
		values.put(SigarraContract.SubjectsColumns.YEAR, year);
		values.put(SigarraContract.SubjectsColumns.PERIOD, period);
		values.put(SigarraContract.SubjectsColumns.CONTENT, subjectContent);
		values.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
		values.put(BaseColumns.COLUMN_STATE, SyncStates.PRUNE);
		getContext().getContentResolver().insert(
				SigarraContract.Subjects.CONTENT_URI, values);
		syncResult.stats.numEntries += 1;
	}

	private void syncSubjects(Account account, String authToken,
			SyncResult syncResult) throws JSONException {
		final String page = SifeupAPI.getReply(SifeupAPI.getSubjectsUrl(
				mAccountManager.getUserData(account, Constants.USER_NAME),
				Integer.toString(DateUtils.secondYearOfSchoolYear() - 1)),
				authToken);
		final List<Subject> subjects = Subject.parseSubjectList(page);
		final int secondYear = DateUtils.secondYearOfSchoolYear();
		final String year = Integer.toString(secondYear - 1) + "/"
				+ Integer.toString(secondYear);
		final ContentValues[] values = new ContentValues[subjects.size()];
		int i = 0;
		for (Subject subject : subjects) {
			final String subjectContent = SifeupAPI.getReply(SifeupAPI
					.getSubjectContentUrl(subject.getCode(), year,
							subject.getSemestre()), authToken);
			final String subjectFiles = SifeupAPI.getReply(SifeupAPI
					.getSubjectFilestUrl(subject.getCode(), year,
							subject.getSemestre()), authToken);
			final ContentValues value = new ContentValues();
			value.put(SigarraContract.SubjectsColumns.USER_NAME, account.name);
			value.put(SigarraContract.SubjectsColumns.CODE, subject.getCode());
			value.put(SigarraContract.SubjectsColumns.YEAR, year);
			value.put(SigarraContract.SubjectsColumns.PERIOD,
					subject.getSemestre());
			value.put(SigarraContract.SubjectsColumns.NAME_PT,
					subject.getNamePt());
			value.put(SigarraContract.SubjectsColumns.NAME_EN,
					subject.getNameEn());
			value.put(SigarraContract.SubjectsColumns.CONTENT, subjectContent);
			value.put(SigarraContract.SubjectsColumns.FILES, subjectFiles);
			value.put(BaseColumns.COLUMN_STATE, SyncStates.KEEP);
			values[i++] = value;
		}
		if (values.length > 0)
			getContext().getContentResolver().bulkInsert(
					SigarraContract.Subjects.CONTENT_URI, values);
		syncResult.stats.numEntries += subjects.size();
	}
}
