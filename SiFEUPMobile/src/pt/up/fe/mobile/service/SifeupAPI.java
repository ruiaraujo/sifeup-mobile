package pt.up.fe.mobile.service;

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
	final private static String WEBSERVICE = "https://www.fe.up.pt/si/mobc_geral.";
	
	final private static String EQUALS = "=";
	final private static String LINK_SEP = "&";
	final private static String WEBSERVICE_SEP = "?";
	
	
	private interface Student {
		String NAME = "aluno";
		String CODE = "pv_codigo";
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
		String NAME = "horario";
		String CODE = "pv_codigo";
		String BEGIN = "pv_semana_ini";
		String END = "pv_semana_fim";
	}
	
	private interface Subjects {
		String NAME = "inscricoes";
		String CODE = "pv_codigo";
		/** Not mandatory - if lacking assumed current.*/
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
	
	public interface Errors{
		int NULL_PAGE = 0;
		int NO_AUTH = 1;
		int NO_ERROR = 2;
	}
	
	/**
	 * Authentication Url for Web Service
	 * @param code student code
	 * @param password student password
	 * @return Authentication Url
	 */
	public static String getAuthenticationUrl( String code , String password ){
		return WEBSERVICE + Authentication.NAME + WEBSERVICE_SEP + Authentication.CODE + 
					EQUALS + code + LINK_SEP + Authentication.PASSWORD + EQUALS + password;
	}
	
	/**
	 * Schedule Url for Web Service
	 * @param code
	 * @param begin
	 * @param end
	 * @return Schedule Url
	 */
	public static String getScheduleUrl( String code , String begin , String end ){
		return WEBSERVICE + Schedule.NAME + WEBSERVICE_SEP + Schedule.CODE + 
					EQUALS + code + LINK_SEP + Schedule.BEGIN+ EQUALS + begin +
					LINK_SEP + Schedule.END + EQUALS + end;
	}
	
	/**
	 * Exams Url for Web Service
	 * @param code
	 * @return Exams Url
	 */
	public static String getExamsUrl( String code ){
		return WEBSERVICE + Exams.NAME + WEBSERVICE_SEP + Exams.CODE + EQUALS + code ;
	}

	/**
	 * Tuition Url for Web Service
	 * @param code
	 * @return Tuition Url
	 */
	public static String getTuitionUrl( String code ){
		return WEBSERVICE + Tuition.NAME + WEBSERVICE_SEP + Tuition.CODE + EQUALS + code ;
	}
	
	/**
	 * Student Url for Web Service
	 * @param code
	 * @return Student Url
	 */
	public static String getStudentUrl( String code ){
		return WEBSERVICE + Student.NAME + WEBSERVICE_SEP + Student.CODE + EQUALS + code ;
	}
	
	/**
	 * Printing Url for Web Service
	 * @param code
	 * @return
	 */
	public static String getPrintingUrl( String code ){
		return WEBSERVICE + Printing.NAME + WEBSERVICE_SEP + Printing.CODE + EQUALS + code ;
	}
	
	/**
	 * Subjects Url for Web Service
	 * @param code
	 * @return 
	 */
	public static String getSubjectsUrl( String code , String year){
		return WEBSERVICE + Subjects.NAME + WEBSERVICE_SEP + Subjects.CODE + EQUALS + code +
							( year==null?"":(LINK_SEP + Subjects.YEAR + EQUALS + year));
	}
	
	
	/**
	 * Printing MB Url for Web Service
	 * @param code
	 * @return 
	 */
	public static String getPrintingRefUrl( String value ){
		return WEBSERVICE + PrintingRef.NAME + WEBSERVICE_SEP + PrintingRef.VALUE + EQUALS + value;
	}
	
	/**
	 * Academic Path Url for Web Service
	 * @param code
	 * @return 
	 */
	public static String getAcademicPathUrl( String code ){
		return WEBSERVICE + AcademicPath.NAME + WEBSERVICE_SEP + AcademicPath.CODE + EQUALS + code;
	}
	

	/**
	 * Students Search Url for Web Service
	 * @param numPage 
	 * @param name
	 * @return 
	 */
	public static String getStudentsSearchUrl( String query, Integer numPage ){
		return WEBSERVICE + StudentsSearch.NAME + WEBSERVICE_SEP + StudentsSearch.QUERY + EQUALS + query + 
						LINK_SEP + StudentsSearch.PAGE + EQUALS + numPage;
	}
	

	/**
	 * Canteens Url for Web Service
	 * @return 
	 */
	public static String getCanteensUrl(){
		return WEBSERVICE + "cantinas";
	}
	
	
	/**
	 * Get Canteens Reply
	 * @param value
	 * @return
	 */
	public static String getCanteensReply()
	{
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(getCanteensUrl());
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	
	/**
	 * GetPrinting MB  Reply
	 * @param value
	 * @return
	 */
	public static String getPrintingRefReply( String value  ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getPrintingRefUrl(value ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Get Academic Path Reply
	 * @param value
	 * @return
	 */
	public static String getAcademicPathReply( String code  ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getAcademicPathUrl( code ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Get Subjects Reply
	 * @param code
	 * @return
	 */
	public static String getSubjectsReply( String code , String year ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getSubjectsUrl(code, year));
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	/**
	 * Get Printing Reply
	 * @param code
	 * @return
	 */
	public static String getPrintingReply( String code ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getPrintingUrl( code ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	/**
	 * Printing query Reply from web service
	 * @param code
	 * @return
	 */
	public static String getStudentReply( String code ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getStudentUrl( code ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	/**
	 * Tuition query Reply from web service
	 * @param code
	 * @return
	 */
	public static String getTuitionReply( String code ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getTuitionUrl( code ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
				return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	/**
	 * Exams query Reply from web service
	 * @param code
	 * @return
	 */
	public static String getExamsReply( String code ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
											getExamsUrl( code ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Students Search query Reply from web service
	 * @param numPage 
	 * @param code
	 * @return
	 */
	public static String getStudentsSearchReply( String query, Integer numPage ){
		String page = null;
		try {
			do {
				query = query.replace(" ", "%20");
				if ( numPage == null )
					numPage = 1;
				HttpsURLConnection httpConn = getUncheckedConnection(
											getStudentsSearchUrl( query, numPage ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Schedule query Reply from web service
	 * @param code
	 * @param init
	 * @param end
	 * @return
	 */
	public static String getScheduleReply( String code, String init, String end ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
										getScheduleUrl( code, init, end ) );
				httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				httpConn.disconnect();
				if ( page == null )
					return null;
			} while (page.equals(""));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Authentication query Reply from web service
	 * @param code
	 * @param pass
	 * @return
	 */
	public static String getAuthenticationReply( String code , String pass ){
		String page = null;
		try {
			do {
				HttpsURLConnection httpConn = getUncheckedConnection(
										getAuthenticationUrl( code , pass) );
				httpConn.connect();
				page = getPage(httpConn.getInputStream());
				if ( page == null || page.equals(""))
				{
					httpConn.disconnect();
					continue;
				}
				//Saving cookie for later using throughout the program
				String cookie = "";
					String headerName=null;
				for (int i=1; (headerName = httpConn.getHeaderFieldKey(i)) != null; i++) {
				    if (headerName.equalsIgnoreCase("Set-Cookie")) {
				    	cookie +=httpConn.getHeaderField(i)+";";
				    }
				}
				SessionManager.getInstance().setCookie(cookie);
				Log.e("Login cookie" ,  cookie);
	
				httpConn.disconnect();
		} while (page.equals(""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	/**
	 * Fetch data
	 * @param in
	 * @return
	 */
	private static String getPage( InputStream in  ){
		try {
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int read = 0;
			int bufSize = 512;
			byte[] buffer = new byte[bufSize];
			while ( true ) {
				read = bis.read( buffer );
				if( read == -1 ){
					break;
				}
				baf.append(buffer, 0, read);
			}
			bis.close();
			in.close();
				return new String(baf.toByteArray(),"ISO-8859-1");
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	 public static HttpsURLConnection getUncheckedConnection(String url){ 
			try {
				X509TrustManager tm = new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {

					}

					@Override
					public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
					}
				};
				
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new TrustManager[] { tm }, null);
				HttpsURLConnection httpConn = (HttpsURLConnection) new URL(url).openConnection();
				httpConn.setSSLSocketFactory(ctx.getSocketFactory());
				httpConn.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String paramString, SSLSession paramSSLSession) {
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
	 * Prints error message on Log.e()
	 * Returns true in case of a existing error.
	 *  
	 * @param page Webpage from the webservice
	 * @return boolean True in case of error
	 * @throws JSONException
	 */
	public static int JSONError(String page) throws JSONException{
		if ( page == null )
		{
			Log.e("JSON", "null page");
			return Errors.NULL_PAGE;
		}
		JSONObject jObject = new JSONObject(page);
		String erro = null;
		String erro_msg = null;
		
		if(jObject.has("erro")){
			erro = (String) jObject.get("erro");
			Log.e("JSON", erro);
			if(erro.substring(0, 8).equals("Autoriza")){
				erro_msg = (String) jObject.get("erro_msg");
				Log.e("JSON", erro_msg);
			}
			return Errors.NO_AUTH;
		}
		
		return Errors.NO_ERROR;
	}
}
