package pt.up.beta.mobile.sifeup;

import pt.up.beta.mobile.datatypes.Park;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;

public class ParkUtils {
	private ParkUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getParkReply(String code,
			ResponseCommand<Park> command, Context context) {
		return new FetcherTask<Park>(command, new ParkParser(), context)
				.execute(SifeupAPI.getParkOccupationUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class ParkParser implements ParserCommand<Park> {

		public Park parse(String page) {
			try {
				return new Gson().fromJson(page, Park.class);
			} catch (Exception e) {
				e.printStackTrace();
				EasyTracker.getTracker().trackException(
						"Id:" + AccountUtils.getActiveUserCode(null) + "\n"
								+ e.getMessage(), e, true);
			}
			return null;
		}

	}
}
