package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Exam;
import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.fe.mobile.ui.LoginActivity;
import android.os.AsyncTask;
import android.util.Log;

public class AuthenticationUtils {
	private AuthenticationUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> authenticate(String code,
			String password, ResponseCommand command) {
		return new Authenticator(command).execute(code, password);
	}

	public static ERROR_TYPE authenticate(String code, String password) {
		String page = "";
		try {
			page = SifeupAPI.getAuthenticationReply(code, password);
			if (page == null)
				return ERROR_TYPE.NETWORK;
			User user = JSONUser(page);
			if ( user == null )
				return ERROR_TYPE.AUTHENTICATION;
			SessionManager.getInstance().setUser(user);
		} catch (JSONException e) {
			e.printStackTrace();
			return ERROR_TYPE.GENERAL;
		}
		return null;
	}
	
	private static User JSONUser(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		if (jObject.optBoolean("authenticated")) {
			final String user = jObject.getString("codigo");
			final String type = jObject.getString("tipo");
			return new User(user, null, type);
		}
		return null;
	}

	private static class Authenticator extends
			AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
		private final ResponseCommand command;
		private final List<Exam> exams = new ArrayList<Exam>();

		private Authenticator(ResponseCommand com) {
			command = com;
		}

		protected void onPostExecute(ERROR_TYPE result) {
			if (result == null) {
				command.onResultReceived(exams);
				return;
			}
			command.onError(result);
		}

		protected ERROR_TYPE doInBackground(String... code) {
			return authenticate(code[0], code[1]);
		}

	}
}
