package pt.up.beta.mobile.sifeup;

import org.json.JSONException;

import pt.up.beta.mobile.datatypes.Employee;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.ui.utils.ImageDownloader;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ProfileUtils {
	private ProfileUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getStudentReply(
			String code, ResponseCommand command) {
		return new FetcherTask(command, new StudentParser()).execute(SifeupAPI
				.getStudentUrl(code));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeReply(
			String code, ResponseCommand command) {
		return new FetcherTask(command, new EmployeeParser()).execute(SifeupAPI
				.getEmployeeUrl(code));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getPersonPic(String code,
			ResponseCommand command) {
		return new PicFetcher(command).execute(code);
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class StudentParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return Student.parseJSON(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private static class EmployeeParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return Employee.parseJSON(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private static class PicFetcher extends
			AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
		private final ResponseCommand command;
		private Bitmap bitmap = null;

		private PicFetcher(ResponseCommand com) {
			command = com;
		}

		protected void onPostExecute(ERROR_TYPE result) {
			if (result == null) {
				command.onResultReceived(bitmap);
				return;
			}
			command.onError(result);
		}

		protected ERROR_TYPE doInBackground(String... code) {
			bitmap = ImageDownloader.downloadBitmap(SifeupAPI
					.getPersonPicUrl(code[0]));
			if ( bitmap == null )
				return ERROR_TYPE.NETWORK;
			return null;
		}
	}

}
