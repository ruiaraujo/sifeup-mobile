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

import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Employee;
import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.utils.LogUtils;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;

public class ContactsSyncAdapter extends AbstractThreadedSyncAdapter {

	public ContactsSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		EasyTracker.getInstance().setContext(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			final List<Profile> rawContacts = new ArrayList<Profile>();
			final User user = AccountUtils.getUser(getContext(), account.name);
			final Profile me = getProfile(user.getUserCode(), user.getType());
			if (me != null)
				rawContacts.add(me);

			final Cursor c = getContext().getContentResolver().query(
					SigarraContract.Friends.CONTENT_URI,
					new String[] { SigarraContract.FriendsColumns.CODE_FRIEND,
							SigarraContract.FriendsColumns.TYPE_FRIEND },
					SigarraContract.Friends.USER_FRIENDS,
					SigarraContract.Friends
							.getUserFriendsSelectionArgs(user.getUserCode()), null);
			try {
				if (c.moveToFirst()) {
					do {
						final String friendCode = c
								.getString(c
										.getColumnIndex(SigarraContract.FriendsColumns.CODE_FRIEND));
						final String friendType = c
								.getString(c
										.getColumnIndex(SigarraContract.FriendsColumns.TYPE_FRIEND));
						final Profile friend = getProfile(friendCode,
								friendType);
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
		} catch (Exception e) {
			syncResult.stats.numParseExceptions++;
			e.printStackTrace();
			LogUtils.trackException(getContext(), e, null, true);
		}
	}

	private Profile getProfile(String userCode, String type) {
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
				final Gson gson = new Gson();
				if (type.equals(SifeupAPI.STUDENT_TYPE))
					profile = gson.fromJson(content, Student.class);
				else
					profile = gson.fromJson(content, Employee.class);
			}
		} finally {
			c.close();
		}
		return profile;
	}

}
