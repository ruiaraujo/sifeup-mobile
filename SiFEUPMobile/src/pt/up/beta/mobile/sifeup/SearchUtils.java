package pt.up.beta.mobile.sifeup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.acra.ACRA;
import org.apache.http.auth.AuthenticationException;

import pt.up.beta.mobile.datatypes.EmployeeSearchResult;
import pt.up.beta.mobile.datatypes.ResultsPage;
import pt.up.beta.mobile.datatypes.RoomSearchResult;
import pt.up.beta.mobile.datatypes.StudentSearchResult;
import pt.up.beta.mobile.datatypes.SubjectSearchResult;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SearchUtils {
	private SearchUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getSubjectsSearchByNameReply(
			String name,
			ResponseCommand<ResultsPage<SubjectSearchResult>> command,
			Context context) {
		return new FetcherTask<ResultsPage<SubjectSearchResult>>(command,
				new SubjectsSearchParser(), context).execute(SifeupAPI
				.getSubjectsSearchUrl(null, encode(name), null, null, 1));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getRoomsSearchByNameReply(
			String name,
			ResponseCommand<ResultsPage<RoomSearchResult>> command,
			Context context) {
		return new FetcherTask<ResultsPage<RoomSearchResult>>(command,
				new RoomsSearchParser(), context).execute(SifeupAPI
				.getRoomSearchUrl(encode(name), 1));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getStudentsSearchReply(
			String code, String name, String email, String state,
			String firstYear,
			ResponseCommand<ResultsPage<StudentSearchResult>> command,
			Context context) {
		return new FetcherTask<ResultsPage<StudentSearchResult>>(command,
				new StudentsSearchParser(), context).execute(SifeupAPI
				.getStudentsSearchUrl(encode(code), encode(name),
						encode(email), encode(state), encode(firstYear), 1));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeesSearchByNameReply(
			String name,
			ResponseCommand<ResultsPage<EmployeeSearchResult>> command,
			Context context) {
		return new FetcherTask<ResultsPage<EmployeeSearchResult>>(command,
				new EmployeesSearchParser(), context).execute(SifeupAPI
				.getEmployeeSearchUrl(null, encode(name), null, null, null, 1));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getEmployeeSearchByCodeReply(
			String code,
			ResponseCommand<ResultsPage<EmployeeSearchResult>> command,
			Context context) {
		return new FetcherTask<ResultsPage<EmployeeSearchResult>>(command,
				new EmployeesSearchParser(), context).execute(SifeupAPI
				.getEmployeeSearchUrl(code, null, null, null, null, 1));
	}

	public static ResultsPage<SubjectSearchResult> getSubjectsSearchByNameReply(
			String query, int page, Context context) {
		final Gson gson = new Gson();
		return gson.fromJson(
				getJson(SifeupAPI.getSubjectsSearchUrl(null, encode(query),
						null, null, page), context),
				new TypeToken<ResultsPage<SubjectSearchResult>>() {
				}.getType());
	}

	public static ResultsPage<RoomSearchResult> getRoomsSearchByNameReply(
			String query, int page, Context context) {
		final Gson gson = new Gson();
		return gson.fromJson(
				getJson(SifeupAPI.getRoomSearchUrl(encode(query), page),
						context),
				new TypeToken<ResultsPage<RoomSearchResult>>() {
				}.getType());
	}

	public static ResultsPage<StudentSearchResult> getStudentsSearchReply(
			String code, String name,
			String email, String state, String firstYear, int page, Context context) {
		final Gson gson = new Gson();
		return gson.fromJson(
				getJson(SifeupAPI.getStudentsSearchUrl(encode(code), encode(name),
						encode(email), encode(state), encode(firstYear), page), context),
				new TypeToken<ResultsPage<StudentSearchResult>>() {
				}.getType());
	}

	public static ResultsPage<EmployeeSearchResult> getEmployeesSearchByNameReply(
			String query, int page, Context context) {
		final Gson gson = new Gson();
		return gson.fromJson(
				getJson(SifeupAPI.getEmployeeSearchUrl(null, encode(query),
						null, null, null, page), context),
				new TypeToken<ResultsPage<EmployeeSearchResult>>() {
				}.getType());
	}

	public static ResultsPage<EmployeeSearchResult> getEmployeesSearchByCodeReply(
			String code, int page, Context context) {
		final Gson gson = new Gson();
		return gson.fromJson(
				getJson(SifeupAPI.getEmployeeSearchUrl(encode(code), null,
						null, null, null, page), context),
				new TypeToken<ResultsPage<EmployeeSearchResult>>() {
				}.getType());
	}

	private static String getJson(String url, Context context) {
		try {
			return SifeupAPI.getReply(url, AccountUtils.getAuthToken(context),
					context);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String encode(String s) {
		if (s == null)
			return null;
		try {
			return URLEncoder.encode(s.trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class StudentsSearchParser implements
			ParserCommand<ResultsPage<StudentSearchResult>> {

		public ResultsPage<StudentSearchResult> parse(String page) {
			try {
				final Gson gson = new Gson();
				return gson.fromJson(page,
						new TypeToken<ResultsPage<StudentSearchResult>>() {
						}.getType());
			} catch (Exception e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"
								+ page));

			}
			return null;
		}
	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class EmployeesSearchParser implements
			ParserCommand<ResultsPage<EmployeeSearchResult>> {

		public ResultsPage<EmployeeSearchResult> parse(String page) {
			try {
				final Gson gson = new Gson();
				return gson.fromJson(page,
						new TypeToken<ResultsPage<EmployeeSearchResult>>() {
						}.getType());
			} catch (Exception e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"
								+ page));

			}
			return null;
		}
	}

	private static class RoomsSearchParser implements
			ParserCommand<ResultsPage<RoomSearchResult>> {

		public ResultsPage<RoomSearchResult> parse(String page) {
			try {
				final Gson gson = new Gson();
				return gson.fromJson(page,
						new TypeToken<ResultsPage<RoomSearchResult>>() {
						}.getType());
			} catch (Exception e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"
								+ page));

			}
			return null;
		}
	}

	private static class SubjectsSearchParser implements
			ParserCommand<ResultsPage<SubjectSearchResult>> {

		public ResultsPage<SubjectSearchResult> parse(String page) {
			try {
				final Gson gson = new Gson();
				return gson.fromJson(page,
						new TypeToken<ResultsPage<SubjectSearchResult>>() {
						}.getType());
			} catch (Exception e) {
				e.printStackTrace();
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n"
								+ page));

			}
			return null;
		}
	}

}
