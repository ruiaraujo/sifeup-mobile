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
package pt.up.beta.mobile.contacts;

import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;
import org.json.JSONException;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Employee;
import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;

public class ContactsSyncAdapter extends AbstractThreadedSyncAdapter {

	private final AccountManager mAccountManager;

	public ContactsSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			final List<Profile> rawContacts = new ArrayList<Profile>();
			final String userCode = mAccountManager.getUserData(account,
					Constants.USER_NAME);
			final String type = mAccountManager.getUserData(account,
					Constants.USER_TYPE);
			final Profile me = getProfile(userCode, type);
			if (me != null)
				rawContacts.add(me);

			final Cursor c = getContext().getContentResolver().query(
					SigarraContract.Friends.CONTENT_URI,
					new String[] { SigarraContract.FriendsColumns.CODE_FRIEND,
							SigarraContract.FriendsColumns.COURSE_FRIEND },
					SigarraContract.Friends.USER_FRIENDS,
					SigarraContract.Friends
							.getUserFriendsSelectionArgs(userCode), null);
			try {
				if (c.moveToFirst()) {
					do {
						final String friendCode = c
								.getString(c
										.getColumnIndex(SigarraContract.FriendsColumns.CODE_FRIEND));
						final String friendCourse = c
								.getString(c
										.getColumnIndex(SigarraContract.FriendsColumns.COURSE_FRIEND));
						final Profile friend = getProfile(friendCode,
								friendCourse != null ? SifeupAPI.STUDENT_TYPE
										: SifeupAPI.EMPLOYEE_TYPE);
						if (friend != null)
							rawContacts.add(friend);
					} while (c.moveToNext());
				}
			} finally {
				c.close();
			}
			syncResult.stats.numEntries += rawContacts.size();
			ContactManager.setAccountContactsVisibility(getContext(), account,
					true);
			ContactManager.updateContacts(getContext(), account.name,
					rawContacts);
		} catch (JSONException e) {
			syncResult.stats.numParseExceptions++;
			e.printStackTrace();
			ACRA.getErrorReporter().handleSilentException(e);
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(null)));
		}
	}

	private Profile getProfile(String userCode, String type)
			throws JSONException {
		final Cursor c = getContext().getContentResolver().query(
				SigarraContract.Profiles.CONTENT_URI,
				SigarraContract.Profiles.PROFILE_COLUMNS,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles
						.getProfileSelectionArgs(userCode, type), null);
		Profile profile = null;
		try {
			if (c.moveToFirst()) {
				final String content = c.getString(c
						.getColumnIndex(SigarraContract.Profiles.CONTENT));
				if (type.equals(SifeupAPI.STUDENT_TYPE))
					profile = Student.parseJSON(content);
				else
					profile = Employee.parseJSON(content);
			}
		} finally {
			c.close();
		}
		return profile;
	}

}