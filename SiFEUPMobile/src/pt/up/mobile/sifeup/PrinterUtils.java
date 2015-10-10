package pt.up.mobile.sifeup;

import pt.up.mobile.datatypes.RefMB;
import pt.up.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.mobile.utils.GsonUtils;
import pt.up.mobile.utils.LogUtils;
import android.content.Context;
import android.os.AsyncTask;

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
				return GsonUtils.getGson().fromJson(page, RefMB.class);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.trackException(null, e, page, true);
			}
			return null;
		}

	}
}
