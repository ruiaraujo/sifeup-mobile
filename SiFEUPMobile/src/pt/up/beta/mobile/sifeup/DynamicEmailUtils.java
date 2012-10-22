package pt.up.beta.mobile.sifeup;

import org.acra.ACRA;

import pt.up.beta.mobile.datatypes.DynamicMailFile;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

public class DynamicEmailUtils {
	private DynamicEmailUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getDynamicEmailFiles( String code , 
			ResponseCommand<DynamicMailFile[]> command, Context context) {
		return new FetcherTask<DynamicMailFile[]>(command, new DynamicEmailFilesParser(), context).execute(SifeupAPI
				.getMailFilesUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class DynamicEmailFilesParser implements ParserCommand<DynamicMailFile[]> {

		public DynamicMailFile[] parse(String page) {
			try {
				return new Gson().fromJson(page, DynamicMailFile[].class);
			} catch (Exception e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils
										.getActiveUserCode(null)
								+ "\n\n" + page));
			}
			return null;
		}

	}
}
