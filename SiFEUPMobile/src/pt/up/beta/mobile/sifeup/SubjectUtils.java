package pt.up.beta.mobile.sifeup;

import java.lang.reflect.Type;

import pt.up.beta.mobile.datatypes.OtherSubjectOccurrences;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.StudentCourse;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

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
				return new Gson().fromJson(page,
						OtherSubjectOccurrences[].class);
			} catch (Exception e) {
				e.printStackTrace();
				EasyTracker.getTracker().trackException(
						"Id:" + AccountUtils.getActiveUserCode(null) + "\n"
								+ page, e, true);
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
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Student.class,
						new InstanceCreator<Student>() {
							@Override
							public Student createInstance(Type type) {
								return Student.CREATOR.createFromParcel(null);
							}
						});
				gsonBuilder.registerTypeAdapter(StudentCourse.class,
						new InstanceCreator<StudentCourse>() {
							@Override
							public StudentCourse createInstance(Type type) {
								return StudentCourse.CREATOR.createFromParcel(null);
							}
						});
				Gson gson = gsonBuilder.create();
				return gson.fromJson(page, Student[].class);
			} catch (Exception e) {
				e.printStackTrace();
				EasyTracker.getTracker().trackException(
						"Id:" + AccountUtils.getActiveUserCode(null) + "\n"
								+ page, e, true);
			}
			return null;
		}
	}

}
