package pt.up.beta.mobile.syncadapter;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.SigarraContract;
import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

public class SyncAdapterUtils {

	public static void syncSubjects(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.SUBJECT);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncSubject(final String accountName, final String code,
			final String year, final String period) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.SUBJECT);
		extras.putString(SyncAdapter.SUBJECT_CODE, code);
		extras.putString(SyncAdapter.SUBJECT_PERIOD, period);
		extras.putString(SyncAdapter.SUBJECT_YEAR, year);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncProfile(final String accountName, final String code,
			final String type) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.PROFILE);
		extras.putString(SyncAdapter.PROFILE_CODE, code);
		extras.putString(SyncAdapter.PROFILE_TYPE, type);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncExams(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.EXAMS);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncAcademicPath(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.ACADEMIC_PATH);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncTuitions(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.TUITION);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncPrintingQuota(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.PRINTING_QUOTA);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncNotifications(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.NOTIFICATIONS);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncSchedule(final String accountName,
			final String code, final String initialTime,
			final String finalTime, final String type, final String baseTime) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.SCHEDULE);
		extras.putString(SyncAdapter.SCHEDULE_CODE, code);
		extras.putString(SyncAdapter.SCHEDULE_INITIAL, initialTime);
		extras.putString(SyncAdapter.SCHEDULE_FINAL, finalTime);
		extras.putString(SyncAdapter.SCHEDULE_TYPE, type);
		extras.putString(SyncAdapter.SCHEDULE_BASE_TIME, baseTime);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncCanteens(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.CANTEENS);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	private SyncAdapterUtils() {
	}

}
