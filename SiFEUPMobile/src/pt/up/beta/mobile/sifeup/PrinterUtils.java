package pt.up.beta.mobile.sifeup;

import org.acra.ACRA;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.text.format.Time;

public class PrinterUtils {
	private PrinterUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getPrintRefReply(
			String code, ResponseCommand command) {
		return new FetcherTask(command, new PrintRefParser()).execute(SifeupAPI
				.getPrintingRefUrl(code));
	}

	private static class PrintRefParser implements ParserCommand {

		public Object parse(String page) {
			try {
				JSONObject jObject = new JSONObject(page);
				final RefMB ref = new RefMB();
				ref.setEntity(jObject.getLong("Entidade"));
				ref.setRef(jObject.getLong("Referencia"));
				ref.setAmount(jObject.getDouble("Valor"));
				String[] end = jObject.getString("Data Limite").split("-");
				if (end.length == 3) {
					Time endDate = new Time(Time.TIMEZONE_UTC);
					endDate.set(Integer.parseInt(end[2]),
							Integer.parseInt(end[1]) - 1,
							Integer.parseInt(end[0]));
					ref.setEndDate(endDate);
				}
				return ref;
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
