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

import org.json.JSONException;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.BaseColumns;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.content.SyncStates;
import pt.up.beta.mobile.datatypes.Subject;
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

	private final AccountManager mAccountManager;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			final String authToken = mAccountManager.blockingGetAuthToken(
					account, Constants.AUTHTOKEN_TYPE, false);
			if (extras.getBoolean(SINGLE_REQUEST)) {
				if (SUBJECT.equals(extras.getString(REQUEST_TYPE))) {
					getSubject(extras.getString(SUBJECT_CODE),
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
			} else
				syncProfiles(account, authToken, syncResult);
			syncSubjects(account, authToken, syncResult);
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
		}
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
		}
		c.close();
	}

	private void getSubject(String code, String year, String period,
			String authToken, SyncResult syncResult) throws JSONException {
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
