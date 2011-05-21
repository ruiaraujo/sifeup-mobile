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

import android.util.Log;

public  class SifeupAPI {
	final private static String WEBSERVICE = "https://www.fe.up.pt/si/MOBC_GERAL.";
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
	
	
	public static String getAuthenticationUrl( String code , String password ){
		return WEBSERVICE + Authentication.NAME + WEBSERVICE_SEP + Authentication.CODE + 
					EQUALS + code + LINK_SEP + Authentication.PASSWORD + EQUALS + password;
	}
	
	
	public static String getScheduleUrl( String code , String begin , String end ){
		return WEBSERVICE + Schedule.NAME + WEBSERVICE_SEP + Schedule.CODE + 
					EQUALS + code + LINK_SEP + Schedule.BEGIN+ EQUALS + begin +
					LINK_SEP + Schedule.END + EQUALS + end;
	}
	
	public static String getExamsUrl( String code ){
		return WEBSERVICE + Exams.NAME + WEBSERVICE_SEP + Exams.CODE + EQUALS + code ;
	}

	
	public static String getTuitionUrl( String code ){
		return WEBSERVICE + Tuition.NAME + WEBSERVICE_SEP + Tuition.CODE + EQUALS + code ;
	}
	
	public static String getStudentUrl( String code ){
		return WEBSERVICE + Student.NAME + WEBSERVICE_SEP + Student.CODE + EQUALS + code ;
	}
	
	public static String getPrintingUrl( String code ){
		return WEBSERVICE + Printing.NAME + WEBSERVICE_SEP + Printing.CODE + EQUALS + code ;
	}
	
	public static String getPrintingReply( String code ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getPrintingUrl( code ) );
			httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
			httpConn.connect();
			page = getPage(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	public static String getStudentReply( String code ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getStudentUrl( code ) );
			httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
			httpConn.connect();
			page = getPage(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	public static String getTuitionReply( String code ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getTuitionUrl( code ) );
			httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
			httpConn.connect();
			page = getPage(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}	
	
	public static String getExamsReply( String code ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getExamsUrl( code ) );
			httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
			httpConn.connect();
			page = getPage(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	public static String getScheduleReply( String code, String init, String end ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getScheduleUrl( code, init, end ) );
			httpConn.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
			httpConn.connect();
			page = getPage(httpConn.getInputStream());
			httpConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
	public static String getAuthenticationReply( String code , String pass ){
		String page = null;
		try {
			HttpsURLConnection httpConn = getUncheckedConnection(
										getAuthenticationUrl( code , pass) );
			httpConn.connect();
			page = getPage(httpConn.getInputStream());

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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return page;
	}
	
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
			return new String(baf.toByteArray());
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
	
	
}
