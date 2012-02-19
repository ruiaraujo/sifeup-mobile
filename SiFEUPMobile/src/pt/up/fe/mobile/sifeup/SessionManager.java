package pt.up.fe.mobile.sifeup;

import pt.up.fe.mobile.datatypes.FriendsData;
import pt.up.fe.mobile.datatypes.TuitionHistory;


/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Ângela Igreja
 *  
 */
public class SessionManager 
{	
	private String cookie;
	private String loginCode;
	public static TuitionHistory tuitionHistory=new TuitionHistory();
	public static FriendsData friends=new FriendsData();

	private SessionManager() {
	}
	 
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
	}
	
}
