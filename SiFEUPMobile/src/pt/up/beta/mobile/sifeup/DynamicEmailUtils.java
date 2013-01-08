package pt.up.beta.mobile.sifeup;

import pt.up.beta.mobile.datatypes.DynamicMailFile;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.GsonUtils;
import pt.up.beta.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

public class DynamicEmailUtils {
	private DynamicEmailUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getDynamicEmailFiles(
			String code, ResponseCommand<DynamicMailFile[]> command,
			Context context) {
		return new FetcherTask<DynamicMailFile[]>(command,
				new DynamicEmailFilesParser(), context).execute(SifeupAPI
				.getMailFilesUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class DynamicEmailFilesParser implements
			ParserCommand<DynamicMailFile[]> {

		public DynamicMailFile[] parse(String page) {
			try {
				return GsonUtils.getGson().fromJson(page, DynamicMailFile[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
