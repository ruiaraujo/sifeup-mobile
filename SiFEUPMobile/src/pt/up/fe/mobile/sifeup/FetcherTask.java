
package pt.up.fe.mobile.sifeup;


import org.json.JSONException;

import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public class FetcherTask extends
AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
	
	private final ResponseCommand command;
	private final ParserCommand parser;
	private Object result;

	public FetcherTask(ResponseCommand com , ParserCommand parser) {
		this.command = com;
		this.parser = parser;
	}

	protected void onPostExecute(ERROR_TYPE error) {
		if (error == null) {
			command.onResultReceived(this.result);
			return;
		}
		command.onError(error);
	}
	
	protected ERROR_TYPE doInBackground(String... pages) {
		String page = "";
		try {
			if (pages.length < 1)
				return ERROR_TYPE.GENERAL;
			page = SifeupAPI.getReply(pages[0]);
			int error = SifeupAPI.JSONError(page);
			switch (error) {
			case SifeupAPI.Errors.NO_AUTH:
				return retrieveWithAuth(pages[0]);
			case SifeupAPI.Errors.NO_ERROR:
				result = parser.parse(page);
				return null;
			case SifeupAPI.Errors.NULL_PAGE:
				return ERROR_TYPE.NETWORK;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return ERROR_TYPE.GENERAL;
		}

		return null;
	}
	
	private ERROR_TYPE retrieveWithAuth(final String url) {
		String page = "";
		try {
			final User user = SessionManager.getInstance().getUser();
			ERROR_TYPE error = AuthenticationUtils.authenticate(user
					.getUser(), user.getPassword());
			if (error != null)
				return error;
			page = SifeupAPI.getReply(url);
			switch (SifeupAPI.JSONError(page)) {
			case SifeupAPI.Errors.NO_AUTH:
				return ERROR_TYPE.AUTHENTICATION;
			case SifeupAPI.Errors.NO_ERROR:
				result = parser.parse(page);
				return null;
			case SifeupAPI.Errors.NULL_PAGE:
				return ERROR_TYPE.NETWORK;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return ERROR_TYPE.GENERAL;
		}

		return null;
	}

}
