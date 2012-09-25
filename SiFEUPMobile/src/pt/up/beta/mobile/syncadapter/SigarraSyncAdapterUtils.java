package pt.up.beta.mobile.syncadapter;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.content.SigarraContract;
import android.accounts.Account;
import android.content.ContentResolver;
import android.os.Bundle;

public class SigarraSyncAdapterUtils {

	public static void syncSubjects(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.SUBJECT);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncSubject(final String accountName, final String code,
			final String year, final String period) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.SUBJECT);
		extras.putString(SigarraSyncAdapter.SUBJECT_CODE, code);
		extras.putString(SigarraSyncAdapter.SUBJECT_PERIOD, period);
		extras.putString(SigarraSyncAdapter.SUBJECT_YEAR, year);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncProfile(final String accountName, final String code,
			final String type) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.PROFILE);
		extras.putString(SigarraSyncAdapter.PROFILE_CODE, code);
		extras.putString(SigarraSyncAdapter.PROFILE_TYPE, type);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}
	

	public static void syncProfilePic(final String accountName, final String code) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.PROFILE_PIC);
		extras.putString(SigarraSyncAdapter.USER_CODE, code);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncExams(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.EXAMS);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncAcademicPath(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.ACADEMIC_PATH);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncTuitions(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.TUITION);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncPrintingQuota(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.PRINTING_QUOTA);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncNotifications(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.NOTIFICATIONS);
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
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.SCHEDULE);
		extras.putString(SigarraSyncAdapter.SCHEDULE_CODE, code);
		extras.putString(SigarraSyncAdapter.SCHEDULE_INITIAL, initialTime);
		extras.putString(SigarraSyncAdapter.SCHEDULE_FINAL, finalTime);
		extras.putString(SigarraSyncAdapter.SCHEDULE_TYPE, type);
		extras.putString(SigarraSyncAdapter.SCHEDULE_BASE_TIME, baseTime);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	public static void syncCanteens(final String accountName) {
		final Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		extras.putBoolean(SigarraSyncAdapter.SINGLE_REQUEST, true);
		extras.putString(SigarraSyncAdapter.REQUEST_TYPE, SigarraSyncAdapter.CANTEENS);
		ContentResolver.requestSync(new Account(accountName,
				Constants.ACCOUNT_TYPE), SigarraContract.CONTENT_AUTHORITY,
				extras);
	}

	private SigarraSyncAdapterUtils() {
	}

}
