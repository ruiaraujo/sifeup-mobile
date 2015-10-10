package pt.up.beta.mobile.sifeup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.mobile.R;
import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class SifeupAPI {
	public final static String SIGARRA = "https://sigarra.up.pt/";
	private final static String SIGARRA_HOST = SIGARRA + "feup/pt/";

	final private static String EQUALS = "=";
	final private static String LINK_SEP = "&";
	final private static String WEBSERVICE_SEP = "?";

	private interface WebServices {
		String CURRENTACCOUNT = "mob_ccorrente_geral.";
		String STUDENT = "mob_fest_geral.";
		String EMPLOYEE = "mob_func_geral.";
		String PRINTING = "mob_imp_geral.";
		String NOTIFICATIONS = "mob_wf_geral.";
		String SCHEDULE = "mob_hor_geral.";
		String CANTEENS = "mob_eme_geral.";
		String FACILITIES = "mob_instal_geral.";
		String MAIL = "mob_mail_geral.";
		String PARK = "mob_par_geral.";
		String SUBJECT = "mob_ucurr_geral.";
		String AUTHENTICATION = "mob_val_geral.";

		// External Services
		String DYNAMIC_MAIL_FILES = "mail_dinamico.";
		String PEOPLE_PIC = "fotografias_service.";
		String SUBJECT_CONTENTS = "conteudos_service.";
		String SUBJECT_SIGARA_LINK = "disciplinas_geral.";
		String NOTIFICATION_SIGARRA = "wf_geral.";
	}

	// MOB_EME_GERAL

	private interface Canteens {
		String NAME = "cantinas";
	}

	// MOB_FEST_GERAL

	private interface StudentProfile {
		String NAME = "perfil";
		String CODE = "pv_codigo";
	}

	private interface StudentsSearch {
		String NAME = "pesquisa";
		String STUDENT_CODE = "pv_n_estudante";
		String STUDENT_NAME = "pv_nome";
		String STUDENT_EMAIL = "pv_email";
		String STUDENT_STATE = "pv_estado";
		String STUDENT_FIRST_YEAR = "pv_1_inscricao_em";
		String PAGE = "pv_pag";
	}

	private interface CurrentAccount {
		String NAME = "conta_corrente";
		String CODE = "pv_codigo";
	}

	private interface StudentCurrentSubjects {
		String NAME = "ucurr_inscricoes_corrente";
		String CODE = "pv_codigo";
	}

	private interface StudentAcademicPath {
		String NAME = "percurso_academico";
		String CODE = "pv_codigo";
	}

	// MOB_FUNC_GERAL

	// .DS_DOCENTE
	private interface TeachingService {
		String NAME = "ds_docente";
		String CODE = "pv_codigo";
		String YEAR = "pv_ano_lectivo";
	}

	// .PERFIL
	private interface EmployeeProfile {
		String NAME = "perfil";
		String CODE = "pv_codigo";
	}

	// .PESQUISA
	private interface EmployeeSearch {
		String NAME = "pesquisa";
		String EMPLOYEE_NAME = "pv_nome";
		String EMPLOYEE_EMAIL = "pv_email";
		String EMPLOYEE_CODE = "pv_codigo";
		String EMPLOYEE_ACRONYM = "pv_sigla";
		String EMPLOYEE_STATE = "pv_estado";
		String PAGE = "pv_pag";
	}

	// .PERFIL
	private interface EmployeeMarkings {
		String NAME = "marcacoes";
		String CODE = "pv_codigo";
	}

	// .PERFIL
	private interface EmployeeExams {
		String NAME = "vigilancias";
		String CODE = "pv_codigo";
	}

	// MOB_HOR_GERAL

	// .DOCENTE
	private interface TeacherSchedule {
		String NAME = "docente";
		String CODE = "pv_codigo";
		String WEEK_END = "pv_semana_fim";
		String WEEK_BEGIN = "pv_semana_ini";
	}

	// .ESTUDANTE
	private interface StudentSchedule {
		String NAME = "estudante";
		String CODE = "pv_codigo";
		String WEEK_END = "pv_semana_fim";
		String WEEK_BEGIN = "pv_semana_ini";
	}

	// .SALA
	private interface RoomSchedule {
		String NAME = "sala";
		String CODE = "pv_espaco_id";
		String WEEK_END = "pv_semana_fim";
		String WEEK_BEGIN = "pv_semana_ini";
	}

	// .TURMA
	private interface ClassSchedule {
		String NAME = "turma";
		String CODE = "pv_turma_id";
		String WEEK_END = "pv_semana_fim";
		String WEEK_BEGIN = "pv_semana_ini";
	}

	// .UCURR
	private interface SubjectSchedule {
		String NAME = "ucurr";
		String CODE = "pv_ocorrencia_id";
		String WEEK_END = "pv_semana_fim";
		String WEEK_BEGIN = "pv_semana_ini";
	}

	// MOB_IMP_GERAL

	// .GERAR_REFERENCIA_MB
	private interface PrintingRef {
		String NAME = "gerar_referencia_mb";
		String CODE = "pv_codigo";
		String VALUE = "pv_valor";
	}

	// .SALDO
	private interface PrintingQuota {
		String NAME = "saldo";
		String CODE = "pv_codigo";
	}

	// MOB_INSTAL_GERAL

	// .PERFIL
	private interface RoomProfile {
		String NAME = "perfil";
		String CODE = "pv_sala_id";
	}

	// .PESQUISA
	private interface RoomSearch {
		String NAME = "pesquisa";
		String CODE = "pv_sigla";
		String PAGE = "pv_pag";
	}

	// MOB_MAIL_GERAL

	// .FICHEIROS
	private interface MailFiles {
		String NAME = "ficheiros";
		String CODE = "pv_codigo";
	}

	private interface DownloadMailFiles {
		String NAME = "download_file";
		String CODE = "p_name";
		String FILENAME = "p_cod";
	}

	// MOB_PAR_GERAL

	// .OCUPACAO
	private interface ParkOcupation {
		String NAME = "ocupacao";
		// String CODE = "pv_parque";
	}

	// MOB_UCURR_GERAL.CONTEUDOS
	private interface SubjectContent {
		String NAME = "conteudos";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_UCURR_GERAL.OUTRAS_OCORRENCIAS
	private interface SubjectOtherOccurrences {
		String NAME = "outras_ocorrencias";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_UCURR_GERAL.PERFIL
	private interface SubjectProfile {
		String NAME = "perfil";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_UCURR_GERAL.PESQUISA
	private interface SubjectSearch {
		String NAME = "pesquisa";
		String SUBJECT_CODE = "pv_uc_codigo";
		String SUBJECT_NAME = "pv_uc_nome";
		String SUBJECT_ACRONYM = "pv_uc_sigla";
		String SUBJECT_YEAR = "pv_ano_lectivo";
		String PAGE = "pv_pag";
	}

	// MOB_UCURR_GERAL.SUMARIO
	private interface SubjectSummary {
		String NAME = "sumario";
		String CODE = "pv_sumario_id";
	}

	// MOB_UCURR_GERAL.SUMARIO_EDIT

	private interface SubjectSummaryEdit {
		String NAME = "sumario_edit";
		String CODE = "pv_sumario_id";
		String SUMMARY = "pv_sumario";
		String REAL_DATE = "pv_d_efetiva";
		String NUMBER_STUDENTS = "pv_nalunos";
		String TEACHER_CODE = "pv_doc_codigo";
	}

	// MOB_UCURR_GERAL.SUM_AULAS
	private interface ClassSummaries {
		String NAME = "sum_aulas";
		String OCURENCY = "pv_ocorrencia_id";
		String CLASS = "pv_turma_id";
		String TYPE = "pv_tipo_aula";
	}

	// MOB_UCURR_GERAL.SUM_TURMAS
	private interface SubjectClassesWithSummaries {
		String NAME = "sum_turmas";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_UCURR_GERAL.UC_INSCRITOS
	private interface SubjectRegisteredStudents {
		String NAME = "uc_inscritos";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_UCURR_GERAL.UC_TURMAS
	private interface SubjectClasses {
		String NAME = "uc_turmas";
		String CODE = "pv_ocorrencia_id";
	}

	// MOB_VAL_GERAL.AUTENTICA
	private interface Authentication {
		String NAME = "autentica";
		String LOGIN = "pv_login";
		String PASSWORD = "pv_password";
	}

	// MOB_VAL_GERAL.MUDAR_PASSWORD
	private interface ChangePassword {
		String NAME = "mudar_password";
		String LOGIN = "pv_login";
		String CURRENT_PASSWORD = "pv_password_actual";
		String NEW_PASSWORD = "pv_password_nova";
		String NEW_CONFIRM_PASSWORD = "pv_password_nova_conf";
		String SYSTEM = "pv_sistema";
	}

	// MOB_WF_GERAL.NOTIFICACOES
	private interface Notifications {
		String NAME = "notificacoes";
		String CODE = "pv_codigo";
	}

	private interface BuildingPic {
		String NAME = "get_mapa";
		String SPACE = "pv_espaco_id";
		String BUILDING = "pv_edificio_id";
		String FLOOR = "pv_num_piso";
	}

	private interface RoomFinder {
		String NAME = "room_finder";
		String BUILDING = "pv_edificio_id";
		String FLOOR = "pv_num_piso";
		String X = "pv_x";
		String Y = "pv_y";
	}

	private interface PersonPic {
		String NAME = "foto";
		String CODE = "pct_cod";
	}

	private interface StudentExams {
		String NAME = "exames";
		String CODE = "pv_codigo";
	}

	private interface SubjectSigarraContent {
		String NAME = "formview";
		String CODE = "p_cad_codigo";// TODO: update
	}

	private interface SubjectFilesContent {
		String NAME = "conteudos_cont";
		String ID = "pct_id";
	}

	private interface NotificationsSigarra {
		String NAME = "not_form_view";
		String CODE = "pv_not_id";
	}

	public interface Errors {
		int NULL_PAGE = 0;
		int ERROR = 1;
		int NO_ERROR = 2;
	}

	public static String getSigarraUrl() {
		return SIGARRA_HOST;
	}

	/**
	 * The student type returned by the authenticator
	 */
	public final static String STUDENT_TYPE = "A";
	public final static String EMPLOYEE_TYPE = "F";

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
		return SIGARRA_HOST + WebServices.AUTHENTICATION + Authentication.NAME
				+ WEBSERVICE_SEP + Authentication.LOGIN + EQUALS + encode(code)
				+ LINK_SEP + Authentication.PASSWORD + EQUALS
				+ encode(password);
	}

	/**
	 * Notifications Url for Web Service
	 * 
	 * @return Notifications Url
	 */
	public static String getNotificationsUrl(String code) {
		return SIGARRA_HOST + WebServices.NOTIFICATIONS + Notifications.NAME
				+ WEBSERVICE_SEP + Notifications.CODE + EQUALS + code;
	}

	/**
	 * Notifications Url for Web Service
	 * 
	 * @return Notifications Url
	 */
	public static String getNotificationsSigarraUrl(String code) {
		return SIGARRA_HOST + WebServices.NOTIFICATION_SIGARRA
				+ NotificationsSigarra.NAME + WEBSERVICE_SEP
				+ NotificationsSigarra.CODE + EQUALS + code;
	}

	/**
	 * Park Occupation Url for Web Service
	 * 
	 * @param code
	 * @return Park Occupation Url
	 */
	public static String getParksOccupationUrl() {
		return SIGARRA_HOST + WebServices.PARK + ParkOcupation.NAME;
	}

	/**
	 * Exams Url for Web Service
	 * 
	 * @param code
	 * @return Exams Url
	 */
	public static String getStudentExamsUrl(String code) {
		return SIGARRA_HOST + WebServices.STUDENT + StudentExams.NAME
				+ WEBSERVICE_SEP + StudentExams.CODE + EQUALS + code;
	}

	/**
	 * Tuition Url for Web Service
	 * 
	 * @param code
	 * @return Tuition Url
	 */
	public static String getCurrentAccountUrl(String code) {
		return SIGARRA_HOST + WebServices.CURRENTACCOUNT + CurrentAccount.NAME
				+ WEBSERVICE_SEP + CurrentAccount.CODE + EQUALS + code;
	}

	/**
	 * Student Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getStudenProfiletUrl(String code) {
		return SIGARRA_HOST + WebServices.STUDENT + StudentProfile.NAME
				+ WEBSERVICE_SEP + StudentProfile.CODE + EQUALS + code;
	}

	/**
	 * Students Search Url for Web Service
	 * 
	 * @param name
	 * @param numPage
	 * @return
	 */
	public static String getStudentsSearchUrl(String code, String name,
			String email, String state, String firstYear, Integer numPage) {
		if (code == null)
			code = "";
		if (name == null)
			name = "";
		if (email == null)
			email = "";
		if (firstYear == null)
			firstYear = "";
		return SIGARRA_HOST
				+ WebServices.STUDENT
				+ StudentsSearch.NAME
				+ WEBSERVICE_SEP
				+ StudentsSearch.STUDENT_NAME
				+ EQUALS
				+ name
				+ LINK_SEP
				+ StudentsSearch.STUDENT_CODE
				+ EQUALS
				+ code
				+ LINK_SEP
				+ StudentsSearch.STUDENT_EMAIL
				+ EQUALS
				+ email
				+ LINK_SEP
				+ StudentsSearch.STUDENT_FIRST_YEAR
				+ EQUALS
				+ firstYear
				+ (state != null ? (LINK_SEP + StudentsSearch.STUDENT_STATE
						+ EQUALS + state) : "") + LINK_SEP
				+ StudentsSearch.PAGE + EQUALS + numPage;
	}

	/**
	 * Set Password Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getSetPasswordUrl(String login, String actualPassword,
			String newPassword, String confirmNewPassword, String system) {
		return SIGARRA_HOST + WebServices.AUTHENTICATION + ChangePassword.NAME
				+ WEBSERVICE_SEP + ChangePassword.LOGIN + EQUALS + login
				+ LINK_SEP + ChangePassword.CURRENT_PASSWORD + EQUALS
				+ actualPassword + LINK_SEP + ChangePassword.NEW_PASSWORD
				+ EQUALS + newPassword + LINK_SEP
				+ ChangePassword.NEW_CONFIRM_PASSWORD + EQUALS
				+ confirmNewPassword + LINK_SEP + ChangePassword.SYSTEM
				+ EQUALS + system;
	}

	/**
	 * Employee Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getEmployeeProfileUrl(String code) {
		return SIGARRA_HOST + WebServices.EMPLOYEE + EmployeeProfile.NAME
				+ WEBSERVICE_SEP + EmployeeProfile.CODE + EQUALS + code;
	}

	public static String getEmployeeMarkingsUrl(String code) {
		return SIGARRA_HOST + WebServices.EMPLOYEE + EmployeeMarkings.NAME
				+ WEBSERVICE_SEP + EmployeeMarkings.CODE + EQUALS + code;
	}

	public static String getEmployeeExamsUrl(String code) {
		return SIGARRA_HOST + WebServices.EMPLOYEE + EmployeeExams.NAME
				+ WEBSERVICE_SEP + EmployeeExams.CODE + EQUALS + code;
	}

	public static String getTeachingServiceUrl(String code, String year) {
		return SIGARRA_HOST
				+ WebServices.EMPLOYEE
				+ TeachingService.NAME
				+ WEBSERVICE_SEP
				+ TeachingService.CODE
				+ EQUALS
				+ code
				+ (year == null ? "" : (LINK_SEP + TeachingService.YEAR
						+ EQUALS + year));
	}

	/**
	 * Students Search Url for Web Service
	 * 
	 * @param query
	 * @param numPage
	 * @return
	 */
	public static String getEmployeeSearchUrl(String code, String name,
			String email, String state, String acronym, Integer numPage) {
		if (code == null)
			code = "";
		if (name == null)
			name = "";
		if (email == null)
			email = "";
		if (acronym == null)
			acronym = "";
		return SIGARRA_HOST
				+ WebServices.EMPLOYEE
				+ EmployeeSearch.NAME
				+ WEBSERVICE_SEP
				+ EmployeeSearch.EMPLOYEE_NAME
				+ EQUALS
				+ name
				+ LINK_SEP
				+ EmployeeSearch.EMPLOYEE_CODE
				+ EQUALS
				+ code
				+ LINK_SEP
				+ EmployeeSearch.EMPLOYEE_EMAIL
				+ EQUALS
				+ email
				+ LINK_SEP
				+ EmployeeSearch.EMPLOYEE_ACRONYM
				+ EQUALS
				+ acronym
				+ (state != null ? (LINK_SEP + EmployeeSearch.EMPLOYEE_STATE
						+ EQUALS + state) : "") + LINK_SEP
				+ EmployeeSearch.PAGE + EQUALS + numPage;
	}

	/**
	 * Pic Url for Web Service
	 * 
	 * @param code
	 * @return Student Url
	 */
	public static String getPersonPicUrl(String code) {
		return SIGARRA_HOST + WebServices.PEOPLE_PIC + PersonPic.NAME
				+ WEBSERVICE_SEP + PersonPic.CODE + EQUALS + code;
	}

	/**
	 * Printing Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getPrintingUrl(String code) {
		return SIGARRA_HOST + WebServices.PRINTING + PrintingQuota.NAME
				+ WEBSERVICE_SEP + PrintingQuota.CODE + EQUALS + code;
	}

	/**
	 * Printing MB Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getPrintingRefUrl(String code, String value) {
		return SIGARRA_HOST + WebServices.PRINTING + PrintingRef.NAME
				+ WEBSERVICE_SEP + PrintingRef.CODE + EQUALS + code + LINK_SEP
				+ PrintingRef.VALUE + EQUALS + value;
	}

	/**
	 * Printing Url for Web Service
	 * 
	 * @param id
	 * @return url
	 */
	public static String getSubjectFileContents(String id) {
		return SIGARRA_HOST + WebServices.SUBJECT_CONTENTS
				+ SubjectFilesContent.NAME + WEBSERVICE_SEP
				+ SubjectFilesContent.ID + EQUALS + id;
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public static String getStudentAcademicPathUrl(String code) {
		return SIGARRA_HOST + WebServices.STUDENT + StudentAcademicPath.NAME
				+ WEBSERVICE_SEP + StudentAcademicPath.CODE + EQUALS + code;
	}

	/**
	 * Get the current year's subjects
	 * 
	 * @param code
	 * @return
	 */
	public static String getStudentCurrentSubjectsUrl(String code) {
		return SIGARRA_HOST + WebServices.STUDENT + StudentCurrentSubjects.NAME
				+ WEBSERVICE_SEP + StudentCurrentSubjects.CODE + EQUALS + code;
	}

	/**
	 * Subject Description Url for Web Service
	 * 
	 * @param code
	 * @param year
	 * @param per
	 * @return url
	 */
	public static String getSubjectProfileUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT + SubjectProfile.NAME
				+ WEBSERVICE_SEP + SubjectProfile.CODE + EQUALS + code;
	}

	/**
	 * Subjects Search Url for Web Service
	 * 
	 * @param name
	 * @param numPage
	 * @return
	 */
	public static String getSubjectsSearchUrl(String code, String name,
			String acronym, String year, Integer numPage) {
		if (code == null)
			code = "";
		if (name == null)
			name = "";
		if (acronym == null)
			acronym = "";
		return SIGARRA_HOST
				+ WebServices.SUBJECT
				+ SubjectSearch.NAME
				+ WEBSERVICE_SEP
				+ SubjectSearch.SUBJECT_NAME
				+ EQUALS
				+ name
				+ LINK_SEP
				+ SubjectSearch.SUBJECT_CODE
				+ EQUALS
				+ code
				+ LINK_SEP
				+ SubjectSearch.SUBJECT_ACRONYM
				+ EQUALS
				+ acronym
				+ (year != null ? (LINK_SEP + SubjectSearch.SUBJECT_YEAR
						+ EQUALS + year) : "") + LINK_SEP + SubjectSearch.PAGE
				+ EQUALS + numPage;
	}

	public static String getSubjectOtherOccuencesUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT
				+ SubjectOtherOccurrences.NAME + WEBSERVICE_SEP
				+ SubjectOtherOccurrences.CODE + EQUALS + code;
	}

	public static String getSubjectClassesUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT + SubjectClasses.NAME
				+ WEBSERVICE_SEP + SubjectClasses.CODE + EQUALS + code;
	}

	public static String getSubjectEnrolledStudentsUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT
				+ SubjectRegisteredStudents.NAME + WEBSERVICE_SEP
				+ SubjectRegisteredStudents.CODE + EQUALS + code;
	}

	public static String getSubjectClassesWithSummariesUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT
				+ SubjectClassesWithSummaries.NAME + WEBSERVICE_SEP
				+ SubjectClassesWithSummaries.CODE + EQUALS + code;
	}

	public static String getClassSummariesUrl(String code, String classCode,
			String type) {
		return SIGARRA_HOST + WebServices.SUBJECT + ClassSummaries.NAME
				+ WEBSERVICE_SEP + ClassSummaries.OCURENCY + EQUALS + code
				+ LINK_SEP + ClassSummaries.CLASS + EQUALS + classCode
				+ LINK_SEP + ClassSummaries.TYPE + EQUALS + type;
	}

	public static String getSubjectSummaryUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT + SubjectSummary.NAME
				+ WEBSERVICE_SEP + SubjectSummary.CODE + EQUALS + code;
	}

	public static String getSubjectSummaryEditUrl(String code, String summary,
			String teacherCod, String realDate, String numberOfStudents) {
		return SIGARRA_HOST + WebServices.SUBJECT + SubjectSummaryEdit.NAME
				+ WEBSERVICE_SEP + SubjectSummaryEdit.CODE + EQUALS + code
				+ LINK_SEP + SubjectSummaryEdit.SUMMARY + EQUALS + summary
				+ LINK_SEP + SubjectSummaryEdit.TEACHER_CODE + EQUALS
				+ teacherCod + LINK_SEP + SubjectSummaryEdit.REAL_DATE + EQUALS
				+ realDate + LINK_SEP + SubjectSummaryEdit.NUMBER_STUDENTS
				+ EQUALS + numberOfStudents;
	}

	/**
	 * Subject Description Url for Web Service
	 * 
	 * @param code
	 * @param year
	 * @param per
	 * @return url
	 */
	public static String getSubjectSigarraUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT_SIGARA_LINK
				+ SubjectSigarraContent.NAME + WEBSERVICE_SEP
				+ SubjectSigarraContent.CODE + EQUALS + code;
	}

	/**
	 * Subject Content Url for Web Service
	 * 
	 * @param code
	 * @return
	 */
	public static String getSubjectFilestUrl(String code) {
		return SIGARRA_HOST + WebServices.SUBJECT + SubjectContent.NAME
				+ WEBSERVICE_SEP + SubjectContent.CODE + EQUALS + code;
	}

	/**
	 * Canteens Url for Web Service
	 * 
	 * @return
	 */
	public static String getCanteensUrl() {
		return SIGARRA_HOST + WebServices.CANTEENS + Canteens.NAME;
	}

	/**
	 * Dynamic Mail Files Url for Web Service
	 * 
	 * @return
	 */
	public static String getMailFilesUrl(String code) {
		return SIGARRA_HOST + WebServices.MAIL + MailFiles.NAME
				+ WEBSERVICE_SEP + MailFiles.CODE + EQUALS + code;
	}

	public static String getDownloadMailFilesUrl(String code, String name) {
		return SIGARRA_HOST + WebServices.DYNAMIC_MAIL_FILES
				+ DownloadMailFiles.NAME + WEBSERVICE_SEP
				+ DownloadMailFiles.FILENAME + EQUALS + name + LINK_SEP
				+ DownloadMailFiles.CODE + EQUALS + code;
	}

	/**
	 * Building pics Url for Web Service
	 * 
	 * @param building
	 * @param floor
	 * 
	 * @return
	 */
	public static String getBuildingPicUrl(String building, int floor) {
		return SIGARRA_HOST + WebServices.FACILITIES + BuildingPic.NAME
				+ WEBSERVICE_SEP + BuildingPic.BUILDING + EQUALS + building
				+ LINK_SEP + BuildingPic.FLOOR + EQUALS + floor;
	}

	/**
	 * Room pics Url for Web Service
	 * 
	 * @param building
	 * @param room
	 * 
	 * @return
	 */
	public static String getRoomPicUrl(String code) {
		return SIGARRA_HOST + WebServices.FACILITIES + BuildingPic.NAME
				+ WEBSERVICE_SEP + BuildingPic.SPACE + EQUALS + code;
	}

	/**
	 * Room pics Url for Web Service
	 * 
	 * @param building
	 * @param room
	 * 
	 * @return
	 */
	public static String getRoomPostFinderUrl(String building, int floor,
			int x, int y) {
		return SIGARRA_HOST + WebServices.FACILITIES + RoomFinder.NAME
				+ WEBSERVICE_SEP + RoomFinder.BUILDING + EQUALS + building
				+ LINK_SEP + RoomFinder.FLOOR + EQUALS + floor + LINK_SEP
				+ RoomFinder.X + EQUALS + x + LINK_SEP + RoomFinder.Y + EQUALS
				+ y;

	}

	/**
	 * Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getStudentScheduleUrl(String code, String begin,
			String end) {
		return SIGARRA_HOST + WebServices.SCHEDULE + StudentSchedule.NAME
				+ WEBSERVICE_SEP + StudentSchedule.CODE + EQUALS + code
				+ LINK_SEP + StudentSchedule.WEEK_BEGIN + EQUALS + begin
				+ LINK_SEP + StudentSchedule.WEEK_END + EQUALS + end;
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
		return SIGARRA_HOST + WebServices.SCHEDULE + SubjectSchedule.NAME
				+ WEBSERVICE_SEP + SubjectSchedule.CODE + EQUALS + code
				+ LINK_SEP + SubjectSchedule.WEEK_BEGIN + EQUALS + begin
				+ LINK_SEP + SubjectSchedule.WEEK_END + EQUALS + end;
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
		return SIGARRA_HOST + WebServices.SCHEDULE + TeacherSchedule.NAME
				+ WEBSERVICE_SEP + TeacherSchedule.CODE + EQUALS + code
				+ LINK_SEP + TeacherSchedule.WEEK_BEGIN + EQUALS + begin
				+ LINK_SEP + TeacherSchedule.WEEK_END + EQUALS + end;
	}

	/**
	 * Room Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getRoomScheduleUrl(String code, String begin,
			String end) {
		return SIGARRA_HOST + WebServices.SCHEDULE + RoomSchedule.NAME
				+ WEBSERVICE_SEP + RoomSchedule.CODE + EQUALS + code + LINK_SEP
				+ RoomSchedule.WEEK_BEGIN + EQUALS + begin + LINK_SEP
				+ RoomSchedule.WEEK_END + EQUALS + end;
	}

	/**
	 * Class Schedule Url for Web Service
	 * 
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getClassScheduleUrl(String code, String begin,
			String end) {
		return SIGARRA_HOST + WebServices.SCHEDULE + ClassSchedule.NAME
				+ WEBSERVICE_SEP + ClassSchedule.CODE + EQUALS + code
				+ LINK_SEP + ClassSchedule.WEEK_BEGIN + EQUALS + begin
				+ LINK_SEP + ClassSchedule.WEEK_END + EQUALS + end;
	}

	public static String getRoomProfileUrl(String code) {
		return SIGARRA_HOST + WebServices.FACILITIES + RoomProfile.NAME
				+ WEBSERVICE_SEP + RoomProfile.CODE + EQUALS + code;
	}

	/**
	 * Room Search Url for Web Service
	 * 
	 * @param query
	 * @param numPage
	 * @return
	 */
	public static String getRoomSearchUrl(String query, Integer numPage) {
		return SIGARRA_HOST + WebServices.FACILITIES + RoomSearch.NAME
				+ WEBSERVICE_SEP + RoomSearch.CODE + EQUALS + query + LINK_SEP
				+ RoomSearch.PAGE + EQUALS + numPage;
	}

	private static SSLContext sslCtx;

	public static void initSSLContext(Context context) {
		try {
			if (sslCtx != null)
				return;

			// HTTP connection reuse which was buggy pre-froyo
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
				System.setProperty("http.keepAlive", "false");
			}

			KeyStore localTrustStore = KeyStore.getInstance("BKS");

			InputStream in = context.getResources().openRawResource(
					R.raw.sigarratruststore);
			localTrustStore.load(in, "secret".toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(localTrustStore);
			sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(null, tmf.getTrustManagers(), null);

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HttpsURLConnection get(String url) {
		try {

			final HttpsURLConnection connection = (HttpsURLConnection) new URL(
					url).openConnection();
			connection.setSSLSocketFactory(sslCtx.getSocketFactory());
			connection.setRequestProperty("connection", "close");
			return connection;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getReply(String strUrl, Account account,
			Context context) throws IOException, AuthenticationException {
		final String cookie;
		try {
			cookie = AccountUtils.getAuthToken(context, account);
			try {
				initSSLContext(context);
				return getReply(strUrl, cookie);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return getReply(strUrl,
						AccountUtils.renewAuthToken(context, account, cookie));
			}

		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			e.printStackTrace();
		}
		throw new AuthenticationException();
	}

	/**
	 * Student query Reply from web service
	 * 
	 * @param url
	 * @return page
	 * @throws IOException
	 * @throws AuthenticationException
	 */
	private static String getReply(String strUrl, String cookie)
			throws IOException, AuthenticationException {
		String page = null;
		do {
			final HttpsURLConnection connection = get(strUrl);
			if (cookie != null)
				connection.setRequestProperty("Cookie", cookie);
			final InputStream pageContent;
			try {
				pageContent = connection.getInputStream();
				String charset = getContentCharSet(connection.getContentType());
				if (charset == null) {
					charset = HTTP.DEFAULT_CONTENT_CHARSET;
				}
				page = getPage(pageContent, charset);
				pageContent.close();
				final InputStream errStream = connection.getErrorStream();
				if (errStream != null)
					errStream.close();
				connection.disconnect();
			} catch (IOException e) {
				final int returnCode = connection.getResponseCode();
				final InputStream errStream = connection.getErrorStream();
				if (errStream != null)
					errStream.close();
				connection.disconnect();
				if (returnCode == HttpsURLConnection.HTTP_FORBIDDEN) {
					throw new AuthenticationException();
				} else
					throw e;
			}
			if (page == null)
				throw new IOException("Null page");
		} while (page.equals(""));
		try {
			if (JSONError(page) == Errors.ERROR) // crash with a bang
				throw new RuntimeException("page " + strUrl + "\n" + page);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("page " + strUrl + "\n" + page);
		}
		return page;
	}

	public static String[] authenticate(String username, String password,
			Context context) throws IOException, AuthenticationException {
		initSSLContext(context);
		String page = null;
		HttpsURLConnection connection = null;
		do {

			try {
				connection = get(SifeupAPI.getAuthenticationUrl(username,
						password));
				final InputStream pageContent = connection.getInputStream();
				String charset = getContentCharSet(connection.getContentType());
				if (charset == null) {
					charset = HTTP.DEFAULT_CONTENT_CHARSET;
				}
				page = getPage(pageContent, charset);
				pageContent.close();
				final InputStream errStream = connection.getErrorStream();
				if (errStream != null)
					errStream.close();
				connection.disconnect();
			} catch (IOException e) {
				final int returnCode = connection.getResponseCode();
				final InputStream errStream = connection.getErrorStream();
				if (errStream != null)
					errStream.close();
				connection.disconnect();
				if (returnCode == HttpsURLConnection.HTTP_FORBIDDEN) {
					throw new AuthenticationException();
				} else
					throw e;
			}
			if (page == null)
				throw new IOException("Null page");
		} while (page.equals(""));
		try {
			if (JSONError(page) == Errors.ERROR)
				throw new AuthenticationException("No authentication");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(page);
		}
		// Saving cookie for later using throughout the program
		String cookie = "";
		String headerName = null;
		for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.equalsIgnoreCase("Set-Cookie")) {
				cookie += connection.getHeaderField(i) + "; ";
			}
		}
		if (!TextUtils.isEmpty(cookie))
			return new String[] { page, cookie };

		throw new AuthenticationException("No authentication");
	}

	public static Bitmap downloadBitmap(String strUrl, Account account,
			Context context) throws IOException, AuthenticationException {
		final String cookie;
		try {
			cookie = AccountUtils.getAuthToken(context, account);
			try {
				initSSLContext(context);
				return downloadBitmap(strUrl, cookie);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return downloadBitmap(strUrl,
						AccountUtils.renewAuthToken(context, account, cookie));
			}

		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			e.printStackTrace();
		}
		throw new AuthenticationException();
	}

	// the actual download code
	public static Bitmap downloadBitmap(String url, String cookie)
			throws AuthenticationException, IOException {
		final HttpsURLConnection connection = get(url);
		try {
			if (cookie != null)
				connection.setRequestProperty("Cookie", cookie);
			final InputStream is = connection.getInputStream();
			final BufferedInputStream bis = new BufferedInputStream(is);
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
			is.close();
			connection.disconnect();
			return BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
					baf.length());
		} catch (IOException e) {
			final int returnCode = connection.getResponseCode();
			final InputStream errStream = connection.getErrorStream();
			if (errStream != null)
				errStream.close();
			connection.disconnect();
			if (returnCode == HttpsURLConnection.HTTP_FORBIDDEN) {
				throw new AuthenticationException();
			} else
				throw e;
		}

	}

	public static String getContentCharSet(final String contentType) {
		if (contentType == null)
			return null;
		String[] values = contentType.split(";"); // The values.length must be
													// equal to 2...
		String charset = null;
		for (String value : values) {
			value = value.trim();
			if (value.toLowerCase(Locale.getDefault()).startsWith("charset=")) {
				charset = value.substring("charset=".length());
			}
		}
		return charset;
	}

	public static String getPage(InputStream in) throws IOException {
		return getPage(in, HTTP.DEFAULT_CONTENT_CHARSET);
	}

	/**
	 * Fetch data
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String getPage(InputStream in, String encoding)
			throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int read = 0;
		byte[] buffer = new byte[512];
		while (true) {
			read = bis.read(buffer);
			if (read == -1) {
				break;
			}
			baf.append(buffer, 0, read);
		}
		bis.close();
		in.close();
		return new String(baf.toByteArray(), encoding);
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
		if (TextUtils.isEmpty(page)) {
			Log.d("JSON", "null page");
			return Errors.NULL_PAGE;
		}
		if (page.trim().startsWith("["))
			return Errors.NO_ERROR;
		JSONObject jObject = new JSONObject(page);
		String erro = null;
		String erro_msg = null;

		if (jObject.has("erro")) {
			erro = jObject.getString("erro");
			Log.d("JSON", erro);
			if (erro.substring(0, 8).equals("Autoriza")) {
				erro_msg = jObject.getString("erro_msg");
				Log.d("JSON", erro_msg);
			}
			return Errors.ERROR;
		}

		return Errors.NO_ERROR;
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
}
