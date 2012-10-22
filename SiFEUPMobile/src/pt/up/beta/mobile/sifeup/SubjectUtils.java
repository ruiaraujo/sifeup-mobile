package pt.up.beta.mobile.sifeup;

import org.acra.ACRA;

import pt.up.beta.mobile.datatypes.OtherSubjectOccurrences;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

public class SubjectUtils {
	private SubjectUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getOtherSubjectOccurrences(
			String ucurr_id,
			ResponseCommand<OtherSubjectOccurrences[]> command, Context context) {
		return new FetcherTask<OtherSubjectOccurrences[]>(command,
				new OtherSubjectOccurrencesParser(), context).execute(SifeupAPI
				.getSubjectOtherOccuencesUrl(ucurr_id));
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
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"
								+ page));
			}
			return null;
		}

	}
}
