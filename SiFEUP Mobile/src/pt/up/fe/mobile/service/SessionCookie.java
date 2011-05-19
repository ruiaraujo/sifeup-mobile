package pt.up.fe.mobile.service;

/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Who knows?
 * 
 *
 */
public class SessionCookie {
	
	private String cookie;
	private SessionCookie() {
	}
	 
	
	private static class SessionCookieHolder { 
		public static final SessionCookie INSTANCE = new SessionCookie();
	}
	 
	public static SessionCookie getInstance() {
		return SessionCookieHolder.INSTANCE;
	}
	
	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
}
