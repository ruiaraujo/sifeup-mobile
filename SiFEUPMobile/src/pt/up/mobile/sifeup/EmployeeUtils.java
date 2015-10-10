package pt.up.mobile.sifeup;

import pt.up.mobile.datatypes.EmployeeMarkings;
import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.mobile.utils.GsonUtils;
import pt.up.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

public class EmployeeUtils {
	private EmployeeUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeMarkingsReply( String code , 
			ResponseCommand<EmployeeMarkings[]> command, Context context) {
		return new FetcherTask<EmployeeMarkings[]>(command, new EmployeeMarkingsParser(), context).execute(SifeupAPI
				.getEmployeeMarkingsUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class EmployeeMarkingsParser implements ParserCommand<EmployeeMarkings[]> {

		public EmployeeMarkings[] parse(String page) {
			try {
				return GsonUtils.getGson().fromJson(page,EmployeeMarkings[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
