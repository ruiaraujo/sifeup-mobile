package pt.up.beta.mobile.sifeup;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

public class AuthenticationUtils {
	private AuthenticationUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> authenticate(String code,
			String password, ResponseCommand<User> command, Context context) {
		return new Authenticator(command, context).execute(code, password);
	}

	private static User JSONUser(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		if (jObject.optBoolean("authenticated")) {
			final String user = jObject.getString("codigo");
			final String type = jObject.getString("tipo");
			return new User("", user, "", type);
		}
		return null;
	}

	private static class Authenticator extends
			AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
		private final ResponseCommand<User> command;
		private final Context context;
		private User user;

		private Authenticator(ResponseCommand<User> com, Context context) {
			command = com;
			this.context = context;
		}

		protected void onPostExecute(ERROR_TYPE result) {
			if (result == null) {
				command.onResultReceived(user);
				return;
			}
			command.onError(result);
		}

		protected ERROR_TYPE doInBackground(String... code) {
			String[] page = null;
			try {
				page = SifeupAPI.authenticate(code[0], code[1], context);
				if (page == null)
					return ERROR_TYPE.GENERAL;
				user = JSONUser(page[0]);
				if (user == null)
					return ERROR_TYPE.AUTHENTICATION;
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.trackException(context, e, page != null ? page[0]
						: null, true);
				return ERROR_TYPE.GENERAL;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return ERROR_TYPE.AUTHENTICATION;
			} catch (IOException e) {
				e.printStackTrace();
				return ERROR_TYPE.NETWORK;
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			command.onError(ERROR_TYPE.CANCELLED);
		}
	}

	public static AsyncTask<String, Void, String> setPasswordReply(String code,
			String oldPassword, String newPassword, String confirmNewPassword,
			String system, ResponseCommand<String[]> command, Context context) {
		return new PasswordTask(command, context).execute(code, oldPassword,
				newPassword, confirmNewPassword, system);
	}

	/** Classe privada para a busca de dados ao servidor */
	private static class PasswordTask extends AsyncTask<String, Void, String> {
		private final ResponseCommand<String[]> com;
		private final Context context;

		private PasswordTask(ResponseCommand<String[]> com, Context context) {
			this.com = com;
			this.context = context;
		}

		protected void onPostExecute(String result) {
			if (result.equals("Success")) {
				com.onResultReceived(null);
			} else if (result.equals("Error")) {
				com.onResultReceived(new String[] { errorTitle, errorContent });
			} else if (result.equals("Net"))
				com.onError(ERROR_TYPE.NETWORK);
			else
				com.onError(ERROR_TYPE.GENERAL);
		}

		protected String doInBackground(String... strings) {
			String page = "";
			try {
				page = SifeupAPI.getReply(SifeupAPI.getSetPasswordUrl(
						strings[0], strings[1], strings[2], strings[3],
						strings[4]), AccountUtils.getActiveAccount(context),
						context);
				int error = SifeupAPI.JSONError(page);
				switch (error) {
				case SifeupAPI.Errors.ERROR:
					getError(page);
					return "Error";
				case SifeupAPI.Errors.NO_ERROR:
					return "Success";
				case SifeupAPI.Errors.NULL_PAGE:
					return "Net";
				}
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.trackException(context, e, page, true);
			} catch (AuthenticationException e) {
				try {
					getError(page);
					return "Error";
				} catch (JSONException e1) {
					e1.printStackTrace();
					LogUtils.trackException(context, e, page, true);
				}
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				return "Net";
			}

			return "";
		}

		private String errorTitle;
		private String errorContent;

		private void getError(String page) throws JSONException {
			JSONObject jObject = new JSONObject(page);
			if (jObject.has("erro"))
				errorTitle = (String) jObject.get("erro");
			if (jObject.has("erro_msg"))
				errorContent = (String) jObject.get("erro_msg");
		}

	}
}
