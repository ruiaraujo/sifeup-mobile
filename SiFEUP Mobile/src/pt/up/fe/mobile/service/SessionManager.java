package pt.up.fe.mobile.service;

/**
 * Singleton class that holds the active Session cookie.
 */
public class SessionManager {
	
	private String cookie;
	private String loginCode;

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
		this.loginCode = transformer(loginCode);
	}
	
	//TODO: temporary hack, waiting for new feature in the webservice.
	private String transformer(String login)
	{
		if ( login.startsWith("ee") )
		{
			return login.substring(2,4) + "0503" + login.substring(4);
		}
		if ( login.startsWith("ei") )
		{
			return login.substring(2,4) + "0509" + login.substring(4);
		}
		return login;
	}
}
