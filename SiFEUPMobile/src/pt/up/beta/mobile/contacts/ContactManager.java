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

import java.util.List;

import pt.up.beta.mobile.Constants;
import pt.up.mobile.R;
import pt.up.beta.mobile.datatypes.Profile;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Settings;
import android.util.Log;

/**
 * Class for managing contacts sync related mOperations
 */
public class ContactManager {
	private static final String TAG = "ContactManager";

	/**
	 * Take a list of updated contacts and apply those changes to the contacts
	 * database. Typically this list of contacts would have been returned from
	 * the server, and we want to apply those changes locally.
	 * 
	 * @param context
	 *            The context of Authenticator Activity
	 * @param account
	 *            The username for the account
	 * @param rawContacts
	 *            The list of contacts to update
	 * @param lastSyncMarker
	 *            The previous server sync-state
	 * @return the server syncState that should be used in our next sync
	 *         request.
	 */
	public static synchronized void updateContacts(Context context,
			String account, List<Profile> rawContacts) {

		final ContentResolver resolver = context.getContentResolver();
		final BatchOperation batchOperation = new BatchOperation(context,
				resolver);

		Log.d(TAG, "In SyncContacts");
		for (final Profile rawContact : rawContacts) {

			// If the server returned a clientId for this user, then it's likely
			// that the user was added here, and was just pushed to the server
			// for the first time. In that case, we need to update the main
			// row for this contact so that the RawContacts.SOURCE_ID value
			// contains the correct serverId.
			final long rawContactId = lookupRawContact(resolver,
					rawContact.getCode());
			if (rawContactId != 0) { // TODO:Check if this is the correct
										// behaviour
				if (isDeletedRawContact(resolver, rawContact.getCode())) {
					deleteContact(context, rawContactId, batchOperation);
				} else
					updateContact(context, resolver, rawContact, true, true,
							rawContactId, batchOperation);
			} else {
				Log.d(TAG, "In addContact");
				addContact(context, account, rawContact, true, batchOperation);
			}
			// A sync adapter should batch operations on multiple contacts,
			// because it will make a dramatic performance difference.
			// (UI updates, etc)
			if (batchOperation.size() >= 50) {
				batchOperation.execute();
			}
		}
		batchOperation.execute();
	}

	/**
	 * Adds a single contact to the platform contacts provider. This can be used
	 * to respond to a new contact found as part of sync information returned
	 * from the server, or because a user added a new contact.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param accountName
	 *            the account the contact belongs to
	 * @param rawContact
	 *            the sample SyncAdapter User object
	 * @param groupId
	 *            the id of the sample group
	 * @param inSync
	 *            is the add part of a client-server sync?
	 * @param batchOperation
	 *            allow us to batch together multiple operations into a single
	 *            provider call
	 */
	public static void addContact(Context context, String accountName,
			Profile rawContact, boolean inSync, BatchOperation batchOperation) {

		// Put the data in the contacts provider
		final ContactOperations contactOp = ContactOperations.createNewContact(
				context, rawContact.getCode(), accountName, inSync,
				batchOperation);

		contactOp
				.addName(rawContact.getName(), null, null)
				.addEmail(rawContact.getEmail())
				.addAltEmail(rawContact.getEmailAlt())
				.addWebPage(rawContact.getWebPage())
				.addPhone(rawContact.getMobilePhone(), Phone.TYPE_WORK_MOBILE)
				.addPhone(rawContact.getPhone(), Phone.TYPE_WORK)
				.addAvatar(context.getContentResolver(), rawContact.getCode())
				.addProfileAction(rawContact.getCode(), rawContact.getType(),
						rawContact.getShortName());
	}

	/**
	 * Updates a single contact to the platform contacts provider. This method
	 * can be used to update a contact from a sync operation or as a result of a
	 * user editing a contact record.
	 * 
	 * This operation is actually relatively complex. We query the database to
	 * find all the rows of info that already exist for this Contact. For rows
	 * that exist (and thus we're modifying existing fields), we create an
	 * update operation to change that field. But for fields we're adding, we
	 * create "add" operations to create new rows for those fields.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param resolver
	 *            the ContentResolver to use
	 * @param rawContact
	 *            the sample SyncAdapter contact object
	 * @param updateStatus
	 *            should we update this user's status
	 * @param updateAvatar
	 *            should we update this user's avatar image
	 * @param inSync
	 *            is the update part of a client-server sync?
	 * @param rawContactId
	 *            the unique Id for this rawContact in contacts provider
	 * @param batchOperation
	 *            allow us to batch together multiple operations into a single
	 *            provider call
	 */
	public static void updateContact(Context context, ContentResolver resolver,
			Profile rawContact, boolean updateAvatar, boolean inSync,
			long rawContactId, BatchOperation batchOperation) {

		boolean existingCellPhone = false;
		boolean existingWorkPhone = false;
		boolean existingEmail = false;
		boolean existingAltEmail = false;
		boolean existingAvatar = false;
		boolean existingActions = false;

		final Cursor c = resolver.query(DataQuery.CONTENT_URI,
				DataQuery.PROJECTION, DataQuery.SELECTION_RAW,
				new String[] { String.valueOf(rawContactId) }, null);
		final ContactOperations contactOp = ContactOperations
				.updateExistingContact(context, rawContactId, inSync,
						batchOperation);
		try {
			// Iterate over the existing rows of data, and update each one
			// with the information we received from the server.
			while (c.moveToNext()) {
				final long id = c.getLong(DataQuery.COLUMN_ID);
				final String mimeType = c.getString(DataQuery.COLUMN_MIMETYPE);
				final Uri uri = ContentUris
						.withAppendedId(Data.CONTENT_URI, id);
				if (mimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					contactOp.updateName(uri,
							c.getString(DataQuery.COLUMN_GIVEN_NAME),
							c.getString(DataQuery.COLUMN_FAMILY_NAME),
							c.getString(DataQuery.COLUMN_FULL_NAME),
							rawContact.getFirstName(),
							rawContact.getLastName(), rawContact.getName());
				} else if (mimeType.equals(Phone.CONTENT_ITEM_TYPE)) {
					final int type = c.getInt(DataQuery.COLUMN_PHONE_TYPE);
					if (type == Phone.TYPE_MOBILE) {
						existingCellPhone = true;
						contactOp.updatePhone(
								c.getString(DataQuery.COLUMN_PHONE_NUMBER),
								rawContact.getMobilePhone(), uri);
					} else if (type == Phone.TYPE_WORK) {
						existingWorkPhone = true;
						contactOp.updatePhone(
								c.getString(DataQuery.COLUMN_PHONE_NUMBER),
								rawContact.getPhone(), uri);
					}
				} else if (mimeType.equals(Email.CONTENT_ITEM_TYPE)) {
					final int emailType = c.getInt(DataQuery.COLUMN_EMAIL_TYPE);
					if (emailType == Email.TYPE_WORK) {
						existingEmail = true;
						contactOp.updateEmail(rawContact.getEmail(),
								c.getString(DataQuery.COLUMN_EMAIL_ADDRESS),
								Email.TYPE_WORK, uri);
					} else if (emailType == Email.TYPE_OTHER) {
						existingAltEmail = true;
						contactOp.updateEmail(rawContact.getEmailAlt(),
								c.getString(DataQuery.COLUMN_EMAIL_ADDRESS),
								Email.TYPE_OTHER, uri);
					}
				} else if (mimeType.equals(Photo.CONTENT_ITEM_TYPE)) {
					existingAvatar = true;
					contactOp.updateAvatar(resolver, rawContact.getCode(), uri);
				} else if (mimeType.equals(ProfileContactColumns.MIME)) {
					existingActions = true;
					contactOp.updateProfileAction(rawContact.getShortName(),
							uri);
				}
			} // while
		} finally {
			c.close();
		}

		// Add the cell phone, if present and not updated above
		if (!existingCellPhone) {
			contactOp.addPhone(rawContact.getMobilePhone(), Phone.TYPE_MOBILE);
		}

		// Add the work phone, if present and not updated above
		if (!existingWorkPhone) {
			contactOp.addPhone(rawContact.getPhone(), Phone.TYPE_WORK);
		}
		// Add the email address, if present and not updated above
		if (!existingEmail) {
			contactOp.addEmail(rawContact.getEmail());
		}
		// Add the email address, if present and not updated above
		if (!existingAltEmail) {
			contactOp.addAltEmail(rawContact.getEmailAlt());
		}
		// Add the avatar if we didn't update the existing avatar
		if (!existingAvatar) {
			contactOp.addAvatar(context.getContentResolver(),
					rawContact.getCode());
		}
		// Add the profile actions if we didn't update the existing actions
		if (!existingActions) {
			contactOp.addProfileAction(rawContact.getCode(),
					rawContact.getType(), rawContact.getShortName());
		}
	}

	public static String[] getProfileDataContact(Context context, Uri uri) {
		final Cursor c = context
				.getContentResolver()
				.query(DetailsQuery.CONTENT_URI,
						DetailsQuery.PROJECTION_PROFILE,
						DetailsQuery.SELECTION,
						new String[] { String.valueOf(ContentUris.parseId(uri)) },
						null);
		final String[] profileInfo = new String[4];
		try {
			// Iterate over the existing rows of data, and update each one
			// with the information we received from the server.
			while (c.moveToNext()) {
				final String mimeType = c
						.getString(DetailsQuery.COLUMN_MIMETYPE);
				if (mimeType.equals(ProfileContactColumns.MIME)) {
					profileInfo[0] = c.getString(
							DetailsQuery.COLUMN_ACTION_PROFILE).equals(
							context.getString(R.string.action_view_schedule)) ? ProfileContactColumns.MIME_SCHEDULE
							: ProfileContactColumns.MIME_PROFILE;
					profileInfo[1] = c
							.getString(DetailsQuery.COLUMN_PROFILE_CODE);
					profileInfo[2] = c
							.getString(DetailsQuery.COLUMN_PROFILE_TYPE);
					profileInfo[3] = c
							.getString(DetailsQuery.COLUMN_PROFILE_NAME);
					return profileInfo;
				}
			} // while
		} finally {
			c.close();
		}
		return profileInfo;
	}

	/**
	 * When we first add a sync adapter to the system, the contacts from that
	 * sync adapter will be hidden unless they're merged/grouped with an
	 * existing contact. But typically we want to actually show those contacts,
	 * so we need to mess with the Settings table to get them to show up.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param account
	 *            the Account who's visibility we're changing
	 * @param visible
	 *            true if we want the contacts visible, false for hidden
	 */
	public static void setAccountContactsVisibility(Context context,
			Account account, boolean visible) {
		ContentValues values = new ContentValues();
		values.put(RawContacts.ACCOUNT_NAME, account.name);
		values.put(RawContacts.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		values.put(Settings.UNGROUPED_VISIBLE, visible ? 1 : 0);

		context.getContentResolver().insert(Settings.CONTENT_URI, values);
	}

	/**
	 * Deletes a contact from the platform contacts provider. This method is
	 * used both for contacts that were deleted locally and then that deletion
	 * was synced to the server, and for contacts that were deleted on the
	 * server and the deletion was synced to the client.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param rawContactId
	 *            the unique Id for this rawContact in contacts provider
	 */
	private static void deleteContact(Context context, long rawContactId,
			BatchOperation batchOperation) {
		batchOperation.add(ContactOperations.newDeleteCpo(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI,
						rawContactId), true, true).build());
	}

	/**
	 * Deletes a contact from the platform contacts provider. This method is
	 * used both for contacts that were deleted locally and then that deletion
	 * was synced to the server, and for contacts that were deleted on the
	 * server and the deletion was synced to the client.
	 * 
	 * @param context
	 *            the Authenticator Activity context
	 * @param rawContactId
	 *            the unique Id for this rawContact in contacts provider
	 */
	public static void deleteContact(Context context, String serverId) {
		final long rawContactId = lookupRawContact(
				context.getContentResolver(), serverId);
		if (rawContactId != 0) {
			final BatchOperation batchOperation = new BatchOperation(context,
					context.getContentResolver());
			batchOperation.add(ContactOperations.newDeleteCpo(
					ContentUris.withAppendedId(RawContacts.CONTENT_URI,
							rawContactId), true, true).build());
			batchOperation.execute();
		}
	}

	private static boolean isDeletedRawContact(ContentResolver resolver,
			String serverContactId) {

		boolean deleted = false;
		final Cursor c = resolver.query(DeletedQuery.CONTENT_URI,
				DeletedQuery.PROJECTION, DeletedQuery.SELECTION,
				new String[] { serverContactId }, null);
		try {
			if ((c != null) && c.moveToFirst()) {
				deleted = c.getInt(DeletedQuery.COLUMN_RAW_CONTACT_DELETED) == 1;
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return deleted;
	}

	/**
	 * Returns the Profile id for a sample SyncAdapter contact, or 0 if the
	 * sample SyncAdapter user isn't found.
	 * 
	 * @param resolver
	 *            the content resolver to use
	 * @param serverContactId
	 *            the sample SyncAdapter user ID to lookup
	 * @return the Profile id, or 0 if not found
	 */
	private static long lookupRawContact(ContentResolver resolver,
			String serverContactId) {

		long rawContactId = 0;
		final Cursor c = resolver.query(UserIdQuery.CONTENT_URI,
				UserIdQuery.PROJECTION, UserIdQuery.SELECTION,
				new String[] { serverContactId }, null);
		try {
			if ((c != null) && c.moveToFirst()) {
				rawContactId = c.getLong(UserIdQuery.COLUMN_RAW_CONTACT_ID);
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return rawContactId;
	}

	final public static class EditorQuery {

		private EditorQuery() {
		}

		public static final String[] PROJECTION = new String[] {
				RawContacts.ACCOUNT_NAME, Data._ID, RawContacts.Entity.DATA_ID,
				Data.MIMETYPE, Data.DATA1, Data.DATA2, Data.DATA3, Data.DATA15,
				Data.SYNC1 };

		public static final int COLUMN_ACCOUNT_NAME = 0;
		public static final int COLUMN_RAW_CONTACT_ID = 1;
		public static final int COLUMN_DATA_ID = 2;
		public static final int COLUMN_MIMETYPE = 3;
		public static final int COLUMN_DATA1 = 4;
		public static final int COLUMN_DATA2 = 5;
		public static final int COLUMN_DATA3 = 6;
		public static final int COLUMN_DATA15 = 7;
		public static final int COLUMN_SYNC1 = 8;

		public static final int COLUMN_PHONE_NUMBER = COLUMN_DATA1;
		public static final int COLUMN_PHONE_TYPE = COLUMN_DATA2;
		public static final int COLUMN_EMAIL_ADDRESS = COLUMN_DATA1;
		public static final int COLUMN_EMAIL_TYPE = COLUMN_DATA2;
		public static final int COLUMN_FULL_NAME = COLUMN_DATA1;
		public static final int COLUMN_GIVEN_NAME = COLUMN_DATA2;
		public static final int COLUMN_FAMILY_NAME = COLUMN_DATA3;
		public static final int COLUMN_AVATAR_IMAGE = COLUMN_DATA15;
		public static final int COLUMN_SYNC_DIRTY = COLUMN_SYNC1;

		public static final String SELECTION_RAW = Data.RAW_CONTACT_ID + "=?";

	}

	/**
	 * Constants for a query to find a contact given a Sigarra user ID.
	 */
	final private static class UserIdQuery {

		private UserIdQuery() {
		}

		public final static String[] PROJECTION = new String[] { RawContacts._ID };

		public final static int COLUMN_RAW_CONTACT_ID = 0;

		public final static Uri CONTENT_URI = RawContacts.CONTENT_URI;

		public static final String SELECTION = RawContacts.ACCOUNT_TYPE + "='"
				+ Constants.ACCOUNT_TYPE + "' AND " + RawContacts.SOURCE_ID
				+ "=?";
	}

	/**
	 * Constants for a query to find a contact given a Sigarra user ID.
	 */
	final private static class DeletedQuery {

		private DeletedQuery() {
		}

		public final static String[] PROJECTION = new String[] { RawContacts.DELETED };

		public final static int COLUMN_RAW_CONTACT_DELETED = 0;

		public final static Uri CONTENT_URI = RawContacts.CONTENT_URI;

		public static final String SELECTION = RawContacts.ACCOUNT_TYPE + "='"
				+ Constants.ACCOUNT_TYPE + "' AND " + RawContacts.SOURCE_ID
				+ "=?";
	}

	/**
	 * Constants for a query to get contact data for a given rawContactId
	 */
	final private static class DataQuery {

		private DataQuery() {
		}

		public static final String[] PROJECTION = new String[] { Data._ID,
				RawContacts.SOURCE_ID, Data.MIMETYPE, Data.DATA1, Data.DATA2,
				Data.DATA3, Data.DATA15, Data.SYNC1 };

		public static final int COLUMN_ID = 0;
		public static final int COLUMN_MIMETYPE = 2;
		public static final int COLUMN_DATA1 = 3;
		public static final int COLUMN_DATA2 = 4;
		public static final int COLUMN_DATA3 = 5;

		public static final Uri CONTENT_URI = Data.CONTENT_URI;

		public static final int COLUMN_PHONE_NUMBER = COLUMN_DATA1;
		public static final int COLUMN_PHONE_TYPE = COLUMN_DATA2;
		public static final int COLUMN_EMAIL_ADDRESS = COLUMN_DATA1;
		public static final int COLUMN_EMAIL_TYPE = COLUMN_DATA2;
		public static final int COLUMN_FULL_NAME = COLUMN_DATA1;
		public static final int COLUMN_GIVEN_NAME = COLUMN_DATA2;
		public static final int COLUMN_FAMILY_NAME = COLUMN_DATA3;

		public static final String SELECTION_RAW = Data.RAW_CONTACT_ID + "=?";
	}

	final private static class DetailsQuery {

		public static final Uri CONTENT_URI = Data.CONTENT_URI;

		public static final String[] PROJECTION_PROFILE = new String[] {

		ProfileContactColumns.DATA_CODE, ProfileContactColumns.DATA_NAME,
				ProfileContactColumns.DATA_TYPE,
				ProfileContactColumns.DATA_DETAIL, Data.MIMETYPE };

		public static final int COLUMN_MIMETYPE = 4;
		public static final int COLUMN_DATA1 = 0;
		public static final int COLUMN_DATA3 = 3;
		public static final int COLUMN_DATA4 = 2;
		public static final int COLUMN_DATA5 = 1;
		public static final int COLUMN_PROFILE_NAME = COLUMN_DATA5;
		public static final int COLUMN_PROFILE_TYPE = COLUMN_DATA4;
		public static final int COLUMN_PROFILE_CODE = COLUMN_DATA1;
		public static final int COLUMN_ACTION_PROFILE = COLUMN_DATA3;
		public static final String SELECTION = Data._ID + "=?";
	}

	/**
	 * Constants for a query to read basic contact columns
	 */
	final public static class ContactQuery {
		private ContactQuery() {
		}

		public static final String[] PROJECTION = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME };

		public static final int COLUMN_ID = 0;
		public static final int COLUMN_DISPLAY_NAME = 1;
	}
}
