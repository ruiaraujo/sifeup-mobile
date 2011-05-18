package pt.fe.up.mobile.service;

public  class SifeupAPI {
	final private static String WEBSERVICE = "https://www.fe.up.pt/si/MOBC_GERAL.";
	final private static String EQUALS = "=";
	final private static String LINK_SEP = "&";
	final private static String WEBSERVICE_SEP = "?";
	private interface Student {
		String NAME = "aluno";
		String CODE = "pv_codigo";
	}

	private interface Tuiton {
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

	
	public static String getTuitonUrl( String code ){
		return WEBSERVICE + Tuiton.NAME + WEBSERVICE_SEP + Tuiton.CODE + EQUALS + code ;
	}
	
	public static String getStudentUrl( String code ){
		return WEBSERVICE + Student.NAME + WEBSERVICE_SEP + Student.CODE + EQUALS + code ;
	}
	
	public static String getPrintingUrl( String code ){
		return WEBSERVICE + Printing.NAME + WEBSERVICE_SEP + Printing.CODE + EQUALS + code ;
	}
	
}
