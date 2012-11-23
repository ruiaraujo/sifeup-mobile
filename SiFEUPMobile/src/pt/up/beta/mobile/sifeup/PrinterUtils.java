package pt.up.beta.mobile.sifeup;

import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

public class PrinterUtils {
	private PrinterUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getPrintRefReply(
			String code, String value, ResponseCommand<RefMB> command,
			Context context) {
		return new FetcherTask<RefMB>(command, new PrintRefParser(), context)
				.execute(SifeupAPI.getPrintingRefUrl(code, value));
	}

	private static class PrintRefParser implements ParserCommand<RefMB> {

		public RefMB parse(String page) {
			try {
				final Gson gson = new Gson();
				return gson.fromJson(page, RefMB.class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
