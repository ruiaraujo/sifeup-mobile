package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Exam;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.util.Log;

public class ExamsUtils {
	private ExamsUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getExamsReply(
			String code, ResponseCommand command) {
		return new ExamFetcher(command).execute(code);
	}

	private static class ExamFetcher extends
			AsyncTask<String, Void, ResponseCommand.ERROR_TYPE> {
		private final ResponseCommand command;
		private final List<Exam> exams = new ArrayList<Exam>();

		private ExamFetcher(ResponseCommand com) {
			command = com;
		}

		protected void onPostExecute(ERROR_TYPE result) {
			if (result == null) {
				command.onResultReceived(exams);
				return;
			}
			command.onError(result);
		}

		protected ERROR_TYPE doInBackground(String... code) {
			String page = "";
			try {
				if (code.length < 1)
					return ERROR_TYPE.GENERAL;
				page = SifeupAPI.getExamsReply(code[0]);
				int error = SifeupAPI.JSONError(page);
				switch (error) {
				case SifeupAPI.Errors.NO_AUTH:
					return ERROR_TYPE.AUTHENTICATION;
				case SifeupAPI.Errors.NO_ERROR:
					JSONExams(page);
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

		/**
		 * Parses a JSON String containing Exams info, Stores that info at
		 * Collection exams.
		 * 
		 * @param String
		 *            page
		 * @return boolean
		 * @throws JSONException
		 */
		private boolean JSONExams(String page) throws JSONException {
			JSONObject jObject = new JSONObject(page);

			if (jObject.has("exames")) {
				Log.e("JSON", "exams found");

				// iterate over exams
				JSONArray jArray = jObject.getJSONArray("exames");
				for (int i = 0; i < jArray.length(); i++) {
					// new JSONObject
					JSONObject jExam = jArray.getJSONObject(i);
					// new Exam
					final String type = jExam.optString("tipo");
					final String courseAcronym = jExam.optString("uc");
					final String courseName = jExam.optString("uc_nome");
					final String weekDay = jExam.optString("dia");
					final String date = jExam.optString("data");
					final String startTime = jExam.optString("hora_inicio");
					final String endTime = jExam.optString("hora_fim");
					final String rooms = jExam.optString("salas");
					// add exam
					exams.add(new Exam(type, courseAcronym, courseName,
							weekDay, date, startTime, endTime, rooms));
				}
				Log.e("JSON", "exams loaded");
				return true;
			}
			Log.e("JSON", "exams not found");
			return false;

		}

	}
}
