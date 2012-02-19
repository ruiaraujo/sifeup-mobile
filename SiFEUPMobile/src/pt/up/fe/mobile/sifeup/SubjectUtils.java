package pt.up.fe.mobile.sifeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Subject;
import pt.up.fe.mobile.datatypes.SubjectContent;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.os.AsyncTask;
import android.util.Log;

public class SubjectUtils {
	private SubjectUtils() {
	}


	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectsReply(
			String code, String year,ResponseCommand command) {
		return new FetcherTask(command, new SubjectsParser()).execute(SifeupAPI
				.getSubjectsUrl(code, year));
	}
	
	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectReply(
			String code, String year, String period, ResponseCommand command) {
		return new FetcherTask(command, new SubjectParser()).execute(SifeupAPI
				.getSubjectDescUrl(code, year, period));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectContentReply(
			String code, String year, String period, ResponseCommand command) {
		return new FetcherTask(command, new SubjectContentParser())
				.execute(SifeupAPI.getSubjectContentUrl(code, year, period));
	}

	private static class SubjectParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new Subject().JSONSubject(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class SubjectContentParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new SubjectContent().JSONSubjectContent(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class SubjectsParser implements ParserCommand {

		public Object parse(String page) {
			try {
				List<Subject> subjects = new ArrayList<Subject>();
				JSONObject jObject = new JSONObject(page);

				if (jObject.has("inscricoes")) {
					Log.e("JSON", "founded disciplines");
					JSONArray jArray = jObject.getJSONArray("inscricoes");

					// if year number is wrong, returns false
					if (jArray.length() == 0)
						return false;

					// iterate over jArray
					for (int i = 0; i < jArray.length(); i++) {
						// new JSONObject
						JSONObject jSubject = jArray.getJSONObject(i);
						// new Block
						Subject subject = new Subject();

						if (jSubject.has("dis_codigo"))
							subject
									.setAcronym(jSubject
											.getString("dis_codigo")); // Monday
						// is
						// index
						// 0
						if (jSubject.has("ano_curricular"))
							subject.setYear(jSubject
									.getString("ano_curricular"));
						subject.setNamePt(jSubject.optString("nome"));
						subject.setNameEn(jSubject.optString("name"));
						subject.setSemestre(jSubject.optString("periodo"));

						// add block to schedule
						subjects.add(subject);
					}
				}
				return subjects;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
