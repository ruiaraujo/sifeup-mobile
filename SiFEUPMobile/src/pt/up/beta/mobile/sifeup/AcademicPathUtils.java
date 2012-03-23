package pt.up.beta.mobile.sifeup;

import org.json.JSONException;

import pt.up.beta.mobile.datatypes.AcademicPath;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public class AcademicPathUtils {
	private AcademicPathUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getAcademicPathReply(String code,
			ResponseCommand command) {
		return new FetcherTask(command, new AcademicPathParser()).execute(SifeupAPI
				.getAcademicPathUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class AcademicPathParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new AcademicPath().JSONAcademicPath(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
