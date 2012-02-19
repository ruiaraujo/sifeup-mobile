package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Canteen;
import pt.up.fe.mobile.datatypes.Exam;
import pt.up.fe.mobile.datatypes.Notification;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.util.Log;

public class NotificationUtils {
	private NotificationUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getNotificationsReply(
			ResponseCommand command) {
		return new FetcherTask(command, new NotificationsParser())
				.execute(SifeupAPI.getNotificationsUrl());
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class NotificationsParser implements ParserCommand {

		public Object parse(String page) {
			try {
				List<Notification> notifications = new ArrayList<Notification>();
				JSONObject jObject = new JSONObject(page);
				if (jObject.has("notificacoes")) {
					JSONArray jArray = jObject.getJSONArray("notificacoes");
					for (int i = 0; i < jArray.length(); i++) {
						notifications.add(new Notification()
								.JSONNotification(jArray.getJSONObject(i)));
					}
				}
				return notifications;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
