package pt.up.beta.mobile.sifeup;

import java.lang.reflect.Type;

import pt.up.beta.mobile.datatypes.Park;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

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
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Park.class,
						new InstanceCreator<Park>() {
							@Override
							public Park createInstance(Type type) {
								return Park.CREATOR.createFromParcel(null);
							}
						});
				Gson gson = gsonBuilder.create();
				return gson.fromJson(page, Park.class);
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
