package pt.up.fe.mobile.sifeup;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.datatypes.Employee;
import pt.up.fe.mobile.datatypes.Student;
import pt.up.fe.mobile.sifeup.ResponseCommand.ERROR_TYPE;
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

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class StudentParser implements ParserCommand {

		public Object parse(String page) {
			try {
				Student me = new Student();
				JSONObject jObject = new JSONObject(page);
				me.setCode(jObject.optString("codigo"));
				me.setName(jObject.optString("nome"));
				me.setProgrammeAcronym(jObject.optString("curso_sigla"));
				me.setProgrammeName(jObject.optString("curso_nome"));
				me.setRegistrationYear(jObject.optString("ano_lect_matricula"));
				me.setState(jObject.optString("estado"));
				me.setAcademicYear(jObject.optString("ano_curricular"));
				me.setEmail(jObject.optString("email"));
				me.setEmailAlt(jObject.optString("email_alternativo"));
				me.setMobile(jObject.optString("telemovel"));
				me.setTelephone(jObject.optString("telefone"));
				me.setBranch(jObject.optString("ramo"));
				return me;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	private static class EmployeeParser implements ParserCommand {

		public Object parse(String page) {
			try {
				return new Employee().JSONSubject(page);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}
