package pt.up.beta.mobile.sifeup;

import java.io.IOException;

import org.acra.ACRA;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;

public class AuthenticationUtils {
	private AuthenticationUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> authenticate(String code,
			String password, ResponseCommand command) {
		return new Authenticator(command).execute(code, password);
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
		private final ResponseCommand command;
		private User user;

		private Authenticator(ResponseCommand com) {
			command = com;
		}

		protected void onPostExecute(ERROR_TYPE result) {
			if (result == null) {
				command.onResultReceived(user);
				return;
			}
			command.onError(result);
		}

		protected ERROR_TYPE doInBackground(String... code) {
			String[] page;
			try {
				page = SifeupAPI.authenticate(code[0], code[1]);
				if (page == null)
					return ERROR_TYPE.GENERAL;
				user = JSONUser(page[0]);
				if (user == null)
					return ERROR_TYPE.AUTHENTICATION;
			} catch (JSONException e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"));
				return ERROR_TYPE.GENERAL;
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return ERROR_TYPE.AUTHENTICATION;
			} catch (IOException e) {
				e.printStackTrace();
				return ERROR_TYPE.NETWORK;
			}
			return ERROR_TYPE.GENERAL;
		}

		@Override
		protected void onCancelled() {
			command.onError(ERROR_TYPE.CANCELLED);
		}
	}

	public static AsyncTask<String, Void, String> setPasswordReply(String code,
			String oldPassword, String newPassword, String confirmNewPassword,
			String system, ResponseCommand command) {
		return new PasswordTask(command).execute(code, oldPassword,
				newPassword, confirmNewPassword, system);
	}

	/** Classe privada para a busca de dados ao servidor */
	private static class PasswordTask extends AsyncTask<String, Void, String> {
		private final ResponseCommand com;

		private PasswordTask(ResponseCommand com) {
			this.com = com;
		}

		protected void onPostExecute(String result) {
			if (result.equals("Success")) {
				com.onResultReceived();
			} else if (result.equals("Error")) {
				com.onResultReceived(errorTitle, errorContent);
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
						strings[4]), AccountUtils.getAuthToken(null));
				int error = SifeupAPI.JSONError(page);
				switch (error) {
				case SifeupAPI.Errors.NO_AUTH:
					getError(page);
					return "Error";
				case SifeupAPI.Errors.NO_ERROR:
					return "Success";
				case SifeupAPI.Errors.NULL_PAGE:
					return "Net";
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"));
			} catch (AuthenticationException e) {
				try {
					getError(page);
					return "Error";
				} catch (JSONException e1) {
					e1.printStackTrace();
					ACRA.getErrorReporter().handleSilentException(e1);
					ACRA.getErrorReporter().handleSilentException(
							new RuntimeException("Id:"
									+ AccountUtils.getActiveUserCode(null) + "\n\n"));
				}
				e.printStackTrace();
			} catch (OperationCanceledException e) {
				e.printStackTrace();
				return "";
			} catch (AuthenticatorException e) {
				e.printStackTrace();
				return "";
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
