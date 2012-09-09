package pt.up.beta.mobile.sifeup;

import java.io.IOException;

import org.json.JSONException;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.AsyncTask;

public class FetcherTask extends
		AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {

	private final ResponseCommand command;
	private final ParserCommand parser;
	private Object result;

	public FetcherTask(ResponseCommand com, ParserCommand parser) {
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
			boolean secondTry = false;
			do {
				if (isCancelled())
					return null;
				String cookie = AccountUtils.getAuthToken(null);
				page = SifeupAPI.getReply(pages[0], cookie);
				int error = SifeupAPI.JSONError(page);
				switch (error) {
				case SifeupAPI.Errors.NO_AUTH: {
					if (secondTry) {
						return ERROR_TYPE.AUTHENTICATION;
					}
					cookie = AccountUtils.renewAuthToken(null, cookie);
					secondTry = true;
					break;
				}
				case SifeupAPI.Errors.NO_ERROR:
					result = parser.parse(page);
					return null;
				case SifeupAPI.Errors.NULL_PAGE:
					return ERROR_TYPE.NETWORK;
				}
			} while (secondTry);
		} catch (JSONException e) {
			e.printStackTrace();
			return ERROR_TYPE.GENERAL;
		} catch (OperationCanceledException e) {
			e.printStackTrace();
			return ERROR_TYPE.CANCELLED;
		} catch (AuthenticatorException e) {
			e.printStackTrace();
			return ERROR_TYPE.AUTHENTICATION;
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_TYPE.NETWORK;
		}

		return null;
	}

}
