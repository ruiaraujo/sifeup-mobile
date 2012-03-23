package pt.up.beta.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.datatypes.Exam;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;

public class ExamsUtils {
	private ExamsUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getExamsReply(
			String code, ResponseCommand command) {
		return new FetcherTask(command, new ExamsParser()).execute(SifeupAPI
				.getExamsUrl(code));
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class ExamsParser implements ParserCommand {

		public Object parse(String page) {
			try {
				JSONObject jObject = new JSONObject(page);
				final List<Exam> exams = new ArrayList<Exam>();

				if (jObject.has("exames")) {
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
				}
				return exams;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
