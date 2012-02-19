package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Canteen;
import pt.up.fe.mobile.datatypes.Exam;
import pt.up.fe.mobile.datatypes.Park;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.util.Log;

public class ParkUtils {
	private ParkUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getParkReply( String code , 
			ResponseCommand command) {
		return new FetcherTask(command, new ParkParser()).execute(SifeupAPI
				.getParkOccupationUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class ParkParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new Park().JSONParkOccupation(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
