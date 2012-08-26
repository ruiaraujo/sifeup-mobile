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
import pt.up.beta.mobile.content.SigarraProvider;
import pt.up.beta.mobile.content.tables.SubjectsTable;
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
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	private final AccountManager mAccountManager;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			mAccountManager.blockingGetAuthToken(account,
					Constants.AUTHTOKEN_TYPE, false);
			final String userCode = mAccountManager.getUserData(account,
					Constants.USER_NAME);
			final String page = SifeupAPI.getReply(SifeupAPI.getSubjectsUrl(
					userCode,
					Integer.toString(DateUtils.secondYearOfSchoolYear() - 1)));
			final List<Subject> subjects = Subject.parseSubjectList(page);
			final int secondYear = DateUtils.secondYearOfSchoolYear();
			final String year = Integer.toString(secondYear - 1) + "/"
					+ Integer.toString(secondYear);
			for (Subject subject : subjects) {
				final String subjectContent = SifeupAPI.getReply(SifeupAPI
						.getSubjectContentUrl(subject.getCode(), year,
								subject.getSemestre()));
				final String subjectFiles = SifeupAPI.getReply(SifeupAPI
						.getSubjectFilestUrl(subject.getCode(), year,
								subject.getSemestre()));
				final ContentValues values = new ContentValues();
				values.put(SubjectsTable.COLUMN_USER_CODE, userCode);
				values.put(SubjectsTable.COLUMN_CODE, subject.getCode());
				values.put(SubjectsTable.COLUMN_YEAR, year);
				values.put(SubjectsTable.COLUMN_PERIOD, subject.getSemestre());
				values.put(SubjectsTable.COLUMN_NAME_PT, subject.getNamePt());
				values.put(SubjectsTable.COLUMN_NAME_EN, subject.getNameEn());
				values.put(SubjectsTable.COLUMN_CONTENT, subjectContent);
				values.put(SubjectsTable.COLUMN_FILES, subjectFiles);
				getContext().getContentResolver().insert(
						SigarraProvider.CONTENT_SUBJECTS_URI, values);
			}
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			syncResult.stats.numAuthExceptions++;
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			syncResult.stats.numIoExceptions++;
		} catch (JSONException e) {
			syncResult.stats.numParseExceptions++;
			e.printStackTrace();
		}
	}
}
