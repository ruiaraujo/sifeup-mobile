package pt.up.beta.mobile.sifeup;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.text.format.Time;

public class PrinterUtils {
	private PrinterUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getPrintReply( String code, 
			ResponseCommand command) {
		return new FetcherTask(command, new PrintParser()).execute(SifeupAPI
				.getPrintingUrl(code));
	}
	
	public static AsyncTask<String, Void, ERROR_TYPE> getPrintRefReply( String code, 
			ResponseCommand command) {
		return new FetcherTask(command, new PrintRefParser()).execute(SifeupAPI
				.getPrintingRefUrl(code));
	}


	private static class PrintParser implements ParserCommand {

		public Object parse(String page) {
			try {				
				return new JSONObject(page).optString("saldo");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	

	private static class PrintRefParser implements ParserCommand {

		public Object parse(String page) {
			try {				
				JSONObject jObject = new JSONObject(page);
		    	final RefMB ref = new RefMB();
		    	ref.setEntity(jObject.getLong("Entidade"));
		    	ref.setRef(jObject.getLong("Referencia"));
		    	ref.setAmount(jObject.getDouble("Valor"));
		    	String[] end=jObject.getString("Data Limite").split("-");
				if(end.length==3)
				{
					Time endDate=new Time(Time.TIMEZONE_UTC);
					endDate.set(Integer.parseInt(end[2]), Integer.parseInt(end[1])-1, Integer.parseInt(end[0]));
					ref.setEndDate(endDate);
				}
				return ref;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
