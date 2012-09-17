package pt.up.beta.mobile.sifeup;

import org.acra.ACRA;
import org.json.JSONException;

import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

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
				return Notification.parseListJSON(page);
			} catch (JSONException e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n" + page));
			}
			return null;
		}

	}
}
