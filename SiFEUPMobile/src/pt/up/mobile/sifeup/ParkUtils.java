package pt.up.mobile.sifeup;

import pt.up.mobile.datatypes.Park;
import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.mobile.utils.GsonUtils;
import pt.up.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

public class ParkUtils {
	private ParkUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getParksReply(
			ResponseCommand<Park[]> command, Context context) {
		return new FetcherTask<Park[]>(command, new ParkParser(), context)
				.execute(SifeupAPI.getParksOccupationUrl());
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class ParkParser implements ParserCommand<Park[]> {

		public Park[] parse(String page) {
			try {
				return GsonUtils.getGson().fromJson(page, Park[].class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
