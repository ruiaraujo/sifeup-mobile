package pt.up.beta.mobile.sifeup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SifeupAPI {
	final private static String WEBSERVICE = "https://sigarra.up.pt/feup/";

	final private static String EQUALS = "=";
	final private static String LINK_SEP = "&";
	final private static String WEBSERVICE_SEP = "?";

	private interface WebServices {
		String MOBILE = "mobc_geral.";
		String FACILITIES_IMG = "img.";
		String PEOPLE_PIC = "fotografias_service.";
		String SUBJECT_CONTENTS = "conteudos_service.";
		String SUBJECT_SIGARA_LINK = "disciplinas_geral.";
		String NOTIFICATION_SIGARRA = "wf_geral.";
		String ACADEMIC_PATH_SIGARRA = "alunos_ficha.";
	}

	private interface BuildingPic {
		String NAME = "edi_img_grande";
		String BUILDING = "p_edi";
		String FLOOR = "p_piso";
	}

	private interface RoomPic {
		String NAME = "sala_img";
		String BUILDING = "p_edi";
		String ROOM = "p_sala";
	}

	private interface Student {
		String NAME = "aluno";
		String CODE = "pv_codigo";
	}

	private interface Employee {
		String NAME = "ficha_func";
		String CODE = "pv_codigo";
	}
	
	private interface PersonPic {
		String NAME = "foto";
		String CODE = "pct_cod";
	}

	private interface Tuition {
		String NAME = "propinas";
		String CODE = "pv_codigo";
	}

	private interface Exams {
		String NAME = "exames";
		String CODE = "pv_codigo";
	}

	private interface Authentication {
		String NAME = "autentica";
		String CODE = "pv_login";
		String PASSWORD = "pv_password";

	}

	private interface Printing {
		String NAME = "saldo_imp";
		String CODE = "pv_codigo";
	}

	private interface Schedule {
		String NAME = "horario_aluno";
		String CODE = "pv_codigo";
		String BEGIN = "pv_semana_ini";
		String END = "pv_semana_fim";
	}

	private interface Subjects {
		String NAME = "inscricoes";
		String CODE = "pv_codigo";
		/** Not mandatory - if lacking assumed current. */
		String YEAR = "pv_a_lectivo";
	}

	private interface StudentsSearch {
		String NAME = "alunos_pesquisa";
		String QUERY = "pv_nome";
		String PAGE = "pv_primeiro";
	}

	private interface PrintingRef {
		String NAME = "gerar_propinas_mb";
		String VALUE = "pv_valor";
	}

	private interface AcademicPath {
		String NAME = "ficha_aluno";
		String CODE = "pv_codigo";
	}

	private interface AcademicPathSigarra {
		String NAME = "ficha";
		String CODE = "p_cod";
	}

	private interface SubjectDescription {
		String NAME = "ficha_uc";
		String CODE = "pv_cad_codigo";
		/** Not mandatory - if lacking assumed current. */
		String YEAR = "pv_ano_lectivo";
		/** Not mandatory - if lacking assumed current. */
		String PERIOD = "pv_periodo";
	}

	private interface SubjectContent {
		String NAME = "conteudos_uc";
		String CODE = "pv_uc_codigo";
		/** Not mandatory - if lacking assumed current. */
		String YEAR = "pv_ano_lectivo";
		/** Not mandatory - if lacking assumed current. */
		String PERIOD = "pv_periodo";
	}
	
	private interface SubjectSigarraContent {
		String NAME = "formview";
		String CODE = "p_cad_codigo";
		/** Not mandatory - if lacking assumed current. */
		String YEAR = "p_ano_lectivo";
		/** Not mandatory - if lacking assumed current. */
		String PERIOD = "p_periodo";
	}
	

	private interface SubjectFilesContent {
		String NAME = "conteudos_cont";
		String ID = "pct_id";
	}

	private interface UcSchedule {
		String NAME = "horario_uc";
		String CODE = "pv_uc_codigo";
	}

	private interface TeacherSchedule {
		String NAME = "horario_docente";
	}

	private interface RoomSchedule {
		String NAME = "horario_sala";
		String BUILDING_CODE = "pv_cod_edi";
		String ROOM_CODE = "pv_cod_sala";
	}

	private interface Park {
		String NAME = "ocupacao_parque";
		String CODE = "pv_parque";
	}

	private interface Notifications {
		String NAME = "notificacoes";
	}

	private interface NotificationsSigarra {
		String NAME = "not_form_view";
		String CODE = "pv_not_id";
	}

	private interface Canteens {
		String NAME = "cantinas";
	}

	private interface SetPassword {
		String NAME = "mudar_password";
		String LOGIN = "pv_login";
		String ACTUAL_PASSWORD = "pv_password_actual";
		String NEW_PASSWORD = "pv_password_nova";
		String CONFIRM_NEW_PASSWORD_ = "pv_password_nova_conf";
		String SYSTEM = "pv_sistema";
	}

	public interface Errors {
		int NULL_PAGE = 0;
		int NO_AUTH = 1;
		int NO_ERROR = 2;
	}

	/**
	 * The student type returned by the authenticator
	 */
	public final static String STUDENT_TYPE = "A";

	/**
	 * Authentication Url for Web Service
	 * 
	 * @param code
	 *            student code
	 * @param password
	 *            student password
	 * @return Authentication Url
	 */
	public static String getAuthenticationUrl(String code, String password) {
		return WEBSERVICE + WebServices.MOBILE + Authentication.NAME
				+ WEBSERVICE_SEP + Authentication.CODE + EQUALS + code
				+ LINK_SEP + Authentication.PASSWORD + EQUALS + password;
	}

	/**
	 * Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getScheduleUrl(String code, String begin, String end) {
		return WEBSERVICE + WebServices.MOBILE + Schedule.NAME + WEBSERVICE_SEP
				+ Schedule.CODE + EQUALS + code + LINK_SEP + Schedule.BEGIN
				+ EQUALS + begin + LINK_SEP + Schedule.END + EQUALS + end;
	}

	/**
	 * Notifications Url for Web Service
	 * 
	 * @return Notifications Url
	 */
	public static String getNotificationsUrl() {
		return WEBSERVICE + WebServices.MOBILE + Notifications.NAME;
	}
	

	/**
	 * Notifications Url for Web Service
	 * 
	 * @return Notifications Url
	 */
	public static String getNotificationsSigarraUrl(String code) {
		return WEBSERVICE + WebServices.NOTIFICATION_SIGARRA + NotificationsSigarra.NAME + WEBSERVICE_SEP +
		NotificationsSigarra.CODE + EQUALS + code;
	}


	/**
	 * Park Occupation Url for Web Service
	 * 
	 * @param code
	 * @return Park Occupation Url
	 */
	public static String getParkOccupationUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Park.NAME + WEBSERVICE_SEP
				+ Park.CODE + EQUALS + code;
	}

	/**
	 * Exams Url for Web Service
	 * 
	 * @param code
	 * @return Exams Url
	 */
	public static String getExamsUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Exams.NAME + WEBSERVICE_SEP
				+ Exams.CODE + EQUALS + code;
	}

	/**
	 * Tuition Url for Web Service
	 * 
	 * @param code
	 * @return Tuition Url
	 */
	public static String getTuitionUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Tuition.NAME + WEBSERVICE_SEP
				+ Tuition.CODE + EQUALS + code;
	}

	/**
	 * Student Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getStudentUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Student.NAME + WEBSERVICE_SEP
				+ Student.CODE + EQUALS + code;
	}

	/**
	 * Set Password Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getSetPasswordUrl(String login, String actualPassword,
			String newPassword, String confirmNewPassword, String system) {
		return WEBSERVICE + WebServices.MOBILE + SetPassword.NAME
				+ WEBSERVICE_SEP + SetPassword.LOGIN + EQUALS + login
				+ LINK_SEP + SetPassword.ACTUAL_PASSWORD + EQUALS
				+ actualPassword + LINK_SEP + SetPassword.NEW_PASSWORD + EQUALS
				+ newPassword + LINK_SEP + SetPassword.CONFIRM_NEW_PASSWORD_
				+ EQUALS + confirmNewPassword + LINK_SEP + SetPassword.SYSTEM
				+ EQUALS + system;
	}

	/**
	 * Employee Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getEmployeeUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Employee.NAME + WEBSERVICE_SEP
				+ Employee.CODE + EQUALS + code;
	}
	

	/**
	 * Pic Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getPersonPicUrl(String code) {
		return WEBSERVICE + WebServices.PEOPLE_PIC + PersonPic.NAME + WEBSERVICE_SEP
				+ PersonPic.CODE + EQUALS + code;
	}

	/**
	 * Printing Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getPrintingUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + Printing.NAME + WEBSERVICE_SEP
				+ Printing.CODE + EQUALS + code;
	}
	
	/**
	 * Printing Url for Web Service
	 * 
	 * @param id
	 * @return url
	 */
	public static String getSubjectFileContents(String id) {
		return WEBSERVICE + WebServices.SUBJECT_CONTENTS + SubjectFilesContent.NAME + WEBSERVICE_SEP
				+ SubjectFilesContent.ID + EQUALS + id;
	}

	/**
	 * Subjects Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getSubjectsUrl(String code, String year) {
		return WEBSERVICE
				+ WebServices.MOBILE
				+ Subjects.NAME
				+ WEBSERVICE_SEP
				+ Subjects.CODE
				+ EQUALS
				+ code
				+ (year == null ? ""
						: (LINK_SEP + Subjects.YEAR + EQUALS + year));
	}

	/**
	 * Subject Description Url for Web Service
	 * 
	 * @param code
	 * @param year 
	 * @param per 
	 * @return url
	 */
	public static String getSubjectDescUrl(String code, String year, String per) {
		return WEBSERVICE
				+ WebServices.MOBILE
				+ SubjectDescription.NAME
				+ WEBSERVICE_SEP
				+ SubjectDescription.CODE
				+ EQUALS
				+ code
				+ (year == null ? "" : (LINK_SEP + SubjectDescription.YEAR
						+ EQUALS + year))
				+ (per == null ? "" : (LINK_SEP + SubjectDescription.PERIOD
						+ EQUALS + per));
	}
	
	/**
	 * Subject Description Url for Web Service
	 * 
	 * @param code
	 * @param year 
	 * @param per 
	 * @return url
	 */
	public static String getSubjectSigarraUrl(String code, String year, String per) {
		return WEBSERVICE
				+ WebServices.SUBJECT_SIGARA_LINK
				+ SubjectSigarraContent.NAME
				+ WEBSERVICE_SEP
				+ SubjectSigarraContent.CODE
				+ EQUALS
				+ code
				+ (year == null ? "" : (LINK_SEP + SubjectSigarraContent.YEAR
						+ EQUALS + year))
				+ (per == null ? "" : (LINK_SEP + SubjectSigarraContent.PERIOD
						+ EQUALS + per));
	}


	/**
	 * Subject Content Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getSubjectContentUrl(String code, String year,
			String per) {
		return WEBSERVICE + WebServices.MOBILE + SubjectContent.NAME
				+ WEBSERVICE_SEP + SubjectContent.YEAR + EQUALS + year
				+ LINK_SEP + SubjectContent.CODE + EQUALS + code + LINK_SEP
				+ SubjectContent.PERIOD + EQUALS + per;
	}

	/**
	 * Printing MB Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getPrintingRefUrl(String value) {
		return WEBSERVICE + WebServices.MOBILE + PrintingRef.NAME
				+ WEBSERVICE_SEP + PrintingRef.VALUE + EQUALS + value;
	}

	/**
	 * Academic Path Url for Web Service
	 * 
	 * @param code
	 * @return url
	 */
	public static String getAcademicPathUrl(String code) {
		return WEBSERVICE + WebServices.MOBILE + AcademicPath.NAME
				+ WEBSERVICE_SEP + AcademicPath.CODE + EQUALS + code;
	}

	/**
	 * Academic Path Url for Web Service
	 * 
	 * @param code
	 * @return url
	 */
	public static String getAcademicPathSigarraUrl(String code) {
		return WEBSERVICE + WebServices.ACADEMIC_PATH_SIGARRA + AcademicPathSigarra.NAME
				+ WEBSERVICE_SEP + AcademicPathSigarra.CODE + EQUALS + code;
	}

	
	/**
	 * Students Search Url for Web Service
	 * 
	 * @param query
	 * @param numPage
	 * @return
	 */
	public static String getStudentsSearchUrl(String query, Integer numPage) {
		return WEBSERVICE + WebServices.MOBILE + StudentsSearch.NAME
				+ WEBSERVICE_SEP + StudentsSearch.QUERY + EQUALS + query
				+ LINK_SEP + StudentsSearch.PAGE + EQUALS + numPage;
	}

	/**
	 * Canteens Url for Web Service
	 * 
	 * @return
	 */
	public static String getCanteensUrl() {
		return WEBSERVICE + WebServices.MOBILE + Canteens.NAME;
	}

	/**
	 * Building pics Url for Web Service
	 * @param building 
	 * @param floor 
	 * 
	 * @return
	 */
	public static String getBuildingPicUrl(String building, String floor) {
		return WEBSERVICE + WebServices.FACILITIES_IMG + BuildingPic.NAME + WEBSERVICE_SEP
				+ BuildingPic.BUILDING + EQUALS + building + LINK_SEP
				+ BuildingPic.FLOOR + EQUALS + floor;
	}
	
	
	/**
	 * Room pics Url for Web Service
	 * @param building 
	 * @param room 
	 * 
	 * @return
	 */
	public static String getRoomPicUrl(String building, String room) {
		return WEBSERVICE + WebServices.FACILITIES_IMG + RoomPic.NAME + WEBSERVICE_SEP
				+ RoomPic.BUILDING + EQUALS + building + LINK_SEP
				+ RoomPic.ROOM + EQUALS + room;
	}
	
	/**
	 * UC Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getUcScheduleUrl(String code, String begin, String end) {
		return WEBSERVICE + WebServices.MOBILE + UcSchedule.NAME
				+ WEBSERVICE_SEP + UcSchedule.CODE + EQUALS + code + LINK_SEP
				+ Schedule.BEGIN + EQUALS + begin + LINK_SEP + Schedule.END
				+ EQUALS + end;
	}

	/**
	 * Teacher Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getTeacherScheduleUrl(String code, String begin,
			String end) {
		return WEBSERVICE + WebServices.MOBILE + TeacherSchedule.NAME
				+ WEBSERVICE_SEP + Schedule.CODE + EQUALS + code + LINK_SEP
				+ Schedule.BEGIN + EQUALS + begin + LINK_SEP + Schedule.END
				+ EQUALS + end;
	}

	/**
	 * Room Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getRoomScheduleUrl(String code, String roomCode,
			String begin, String end) {
		return WEBSERVICE + WebServices.MOBILE + RoomSchedule.NAME
				+ WEBSERVICE_SEP + RoomSchedule.BUILDING_CODE + EQUALS + code
				+ LINK_SEP + RoomSchedule.ROOM_CODE + EQUALS + roomCode
				+ LINK_SEP + Schedule.BEGIN + EQUALS + begin + LINK_SEP
				+ Schedule.END + EQUALS + end;
	}

	/**
	 * Student query Reply from web service
	 * 
	 * @param url
	 * @return page
	 */
	public static String getReply(String url) {
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(url);
				httpConn.setRequestProperty("Cookie", SessionManager
						.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if (page == null)
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * Authentication query Reply from web service
	 * 
	 * @param code
	 * @param pass
	 * @return
	 */
	public static String getAuthenticationReply(String code, String pass) {
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(getAuthenticationUrl(
						code, pass));
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				if (page == null || page.equals("")) {
					httpConn.disconnect();
					continue;
				}
				// Saving cookie for later using throughout the program
				String cookie = "";
				String headerName = null;
				for (int i = 1; (headerName = httpConn.getHeaderFieldKey(i)) != null; i++) {
					if (headerName.equalsIgnoreCase("Set-Cookie")) {
						cookie += httpConn.getHeaderField(i) + ";";
					}
				}
				SessionManager.getInstance().setCookie(cookie);
				Log.e("Login cookie", cookie);

				httpConn.disconnect();
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * Fetch data
	 * 
	 * @param in
	 * @return
	 */
	private static String getPage(InputStream in) {
		try {
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int read = 0;
			int bufSize = 512;
			byte[] buffer = new byte[bufSize];
			while (true) {
				read = bis.read(buffer);
				if (read == -1) {
					break;
				}
				baf.append(buffer, 0, read);
			}
			bis.close();
			in.close();
			return new String(baf.toByteArray(), "ISO-8859-1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static HttpsURLConnection getUncheckedConnection(String url) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(
						X509Certificate[] paramArrayOfX509Certificate,
						String paramString) throws CertificateException {

				}

				@Override
				public void checkClientTrusted(
						X509Certificate[] paramArrayOfX509Certificate,
						String paramString) throws CertificateException {
				}
			};

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			HttpsURLConnection httpConn = (HttpsURLConnection) new URL(url)
					.openConnection();
			httpConn.setSSLSocketFactory(ctx.getSocketFactory());
			httpConn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String paramString,
						SSLSession paramSSLSession) {
					return true;
				}
			});
			return httpConn;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Prints error message on Log.e() Returns true in case of a existing error.
	 * 
	 * @param page
	 *            Webpage from the webservice
	 * @return boolean True in case of error
	 * @throws JSONException
	 */
	public static int JSONError(String page) throws JSONException {
		if (page == null) {
			Log.e("JSON", "null page");
			return Errors.NULL_PAGE;
		}
		JSONObject jObject = new JSONObject(page);
		String erro = null;
		String erro_msg = null;

		if (jObject.has("erro")) {
			erro = (String) jObject.get("erro");
			Log.e("JSON", erro);
			if (erro.substring(0, 8).equals("Autoriza")) {
				erro_msg = (String) jObject.get("erro_msg");
				Log.e("JSON", erro_msg);
			}
			return Errors.NO_AUTH;
		}

		return Errors.NO_ERROR;
	}
}
