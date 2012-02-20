package pt.up.fe.mobile.sifeup;

import pt.up.fe.mobile.datatypes.FriendsData;
import pt.up.fe.mobile.datatypes.TuitionHistory;
import pt.up.fe.mobile.datatypes.User;


/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Ângela Igreja
 *  
 */
public class SessionManager 
{	
	private String cookie;
	private User user;
	public static TuitionHistory tuitionHistory=new TuitionHistory();
	public static FriendsData friends=new FriendsData();

	private SessionManager() {
	}
	 
	private static class SessionCookieHolder { 
		public static final SessionManager INSTANCE = new SessionManager();
	}
	
	/**
	 * 
	 * @return the instance of SessionManager
	 */
	public static SessionManager getInstance() {
		return SessionCookieHolder.INSTANCE;
	}
	
	/**
	 * 
	 * @return the cookie
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
	 * @return the login code
	 */
	public String getLoginCode() {
		return user.getUser();
	}

	/**
	 * Get Login Password
	 * @return the login passowrd
	 */
	public String getLoginPassword() {
		return user.getPassword();
	}
	
	public User getUser(){
		return user;
	}
	
	public void setUser(final User user){
		this.user = user;
	}

	public boolean isUserLoaded(){
		return user != null;
	}
}
