package pt.up.beta.mobile.sifeup;

import org.json.JSONException;

import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.datatypes.SubjectFiles;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public class SubjectUtils {
	private SubjectUtils() {
	}


	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectsReply(
			String code, String year,ResponseCommand command) {
		return new FetcherTask(command, new SubjectsParser()).execute(SifeupAPI
				.getSubjectsUrl(code, year));
	}
	
	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectReply(
			String code, String year, String period, ResponseCommand command) {
		return new FetcherTask(command, new SubjectParser()).execute(SifeupAPI
				.getSubjectContentUrl(code, year, period));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectContentReply(
			String code, String year, String period, ResponseCommand command) {
		return new FetcherTask(command, new SubjectContentParser())
				.execute(SifeupAPI.getSubjectFilestUrl(code, year, period));
	}

	private static class SubjectParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new Subject().JSONSubject(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class SubjectContentParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new SubjectFiles().JSONSubjectContent(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class SubjectsParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return Subject.parseSubjectList(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
