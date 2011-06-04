package pt.up.fe.mobile.service;

import java.util.ArrayList;

/**
 * Singleton class that holds the active Session cookie.
 */
public class SessionManager {
	
	private String cookie;
	private String loginCode;
	private String loginCodeToShow;
	public static TuitionHistory tuitionHistory=new TuitionHistory();
	public static FriendsData friends=new FriendsData();

	private SessionManager() {
	}
	 
	/**
	 * 
	 * @author angela
	 */
	private static class SessionCookieHolder { 
		public static final SessionManager INSTANCE = new SessionManager();
	}
	
	/**
	 * 
	 * @return
	 */
	public static SessionManager getInstance() {
		return SessionCookieHolder.INSTANCE;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * Set cookie
	 * @param cookie
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	/**
	 * Get Login Code
	 * @return
	 */
	public String getLoginCode() {
		return loginCode;
	}

	/**
	 * Set login code
	 * @param loginCode
	 */
	public void setLoginCode(String loginCode) {
		this.loginCode = loginCode;
		this.loginCodeToShow = loginCode;
	}
	
	public String getLoginCodeToShow() {
		return loginCodeToShow;
	}

	public void setLoginCodeToShow(String loginCodeToShow) {
		this.loginCodeToShow = loginCodeToShow;
	}

	public void resetLogin() 
	{
		this.loginCodeToShow = this.loginCode;		
	}
	
}
