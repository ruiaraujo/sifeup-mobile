package pt.up.beta.mobile.sifeup;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public class TuitionUtils {
	private TuitionUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getTuitionReply( String code,
			ResponseCommand command) {
		return new FetcherTask(command, new TuitionParser()).execute(SifeupAPI
				.getTuitionUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class TuitionParser implements ParserCommand {

		public Object parse(String page) {
			try {
	    		JSONObject jHistory=new JSONObject(page);
	    		if(AccountUtils.tuitionHistory.load(jHistory))
	    			return "Sucess";
	    		else
	    			return null;			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
