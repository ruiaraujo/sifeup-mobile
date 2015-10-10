package pt.up.mobile.sifeup;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;

import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.content.Context;
import android.os.AsyncTask;

public class FetcherTask<T> extends
		AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {

	private final ResponseCommand<T> command;
	private final ParserCommand<T> parser;
	private final Context context;
	private T result;

	public FetcherTask(ResponseCommand<T> com, ParserCommand<T> parser, Context context) {
		this.command = com;
		this.parser = parser;
		this.context = context;
	}

	protected void onPostExecute(ERROR_TYPE error) {
		if (error == null) {
			command.onResultReceived(this.result);
			return;
		}
		command.onError(error);
	}

	protected ERROR_TYPE doInBackground(String... pages) {
		try {
			if (isCancelled())
				return null;
			final String page = SifeupAPI.getReply(pages[0],
					AccountUtils.getActiveAccount(context), context);
			result = parser.parse(page);
		} catch (IOException e) {
			e.printStackTrace();
			return ERROR_TYPE.NETWORK;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return ERROR_TYPE.AUTHENTICATION;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR_TYPE.GENERAL;
		}


		return null;
	}

}
