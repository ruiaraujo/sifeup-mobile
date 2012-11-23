package pt.up.beta.mobile.sifeup;

import pt.up.beta.mobile.datatypes.EmployeeMarkings;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

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
				return new Gson().fromJson(page,EmployeeMarkings[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
