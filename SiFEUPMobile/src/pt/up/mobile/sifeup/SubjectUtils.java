package pt.up.mobile.sifeup;

import pt.up.mobile.datatypes.OtherSubjectOccurrences;
import pt.up.mobile.datatypes.Student;
import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.mobile.utils.GsonUtils;
import pt.up.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

public class SubjectUtils {
	private SubjectUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getOtherSubjectOccurrences(
			String ocorrId,
			ResponseCommand<OtherSubjectOccurrences[]> command, Context context) {
		return new FetcherTask<OtherSubjectOccurrences[]>(command,
				new OtherSubjectOccurrencesParser(), context).execute(SifeupAPI
				.getSubjectOtherOccuencesUrl(ocorrId));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectEnrolledStudents(
			String occorrId, ResponseCommand<Student[]> command, Context context) {
		return new FetcherTask<Student[]>(command,
				new EnrolledStudentsParser(), context).execute(SifeupAPI
				.getSubjectEnrolledStudentsUrl(occorrId));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class OtherSubjectOccurrencesParser implements
			ParserCommand<OtherSubjectOccurrences[]> {

		public OtherSubjectOccurrences[] parse(String page) {
			try {
				return GsonUtils.getGson().fromJson(page,
						OtherSubjectOccurrences[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class EnrolledStudentsParser implements
			ParserCommand<Student[]> {

		public Student[] parse(String page) {
			try {
				return GsonUtils.getGson().fromJson(page, Student[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}
	}

}
