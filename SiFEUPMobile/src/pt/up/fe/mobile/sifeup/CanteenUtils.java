package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Canteen;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.util.Log;

public class CanteenUtils {
	private CanteenUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getCanteensReply(
			ResponseCommand command) {
		return new FetcherTask(command, new CanteensParser()).execute(SifeupAPI
				.getCanteensUrl());
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class CanteensParser implements ParserCommand {

		public Object parse(String page) {
			try {
				List<Canteen> canteens = new ArrayList<Canteen>();
				JSONObject jObject = new JSONObject(page);

				if (jObject.has("cantinas")) {
					Log.e("JSON", "founded cantinas");
					JSONArray jArray = jObject.getJSONArray("cantinas");

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject jBlock = jArray.getJSONObject(i);

						Canteen canteen = new Canteen();

						canteen.parseJson(jBlock);
						if (canteen.getMenus().length > 0)
							// add canteen to canteens
							canteens.add(canteen);
					}
				}
				return canteens;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
