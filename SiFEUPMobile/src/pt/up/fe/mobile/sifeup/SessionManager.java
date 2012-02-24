package pt.up.fe.mobile.sifeup;

import android.content.Context;
import android.content.SharedPreferences;
import pt.up.fe.mobile.datatypes.FriendsData;
import pt.up.fe.mobile.datatypes.TuitionHistory;
import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.ui.LoginActivity;


/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Ângela Igreja
 *  
 */
public class SessionManager 
{	
	
	public static final String PREF_USERNAME = "pt.up.fe.mobile.ui.USERNAME";
	public static final String PREF_PASSWORD = "pt.up.fe.mobile.ui.PASSWORD";
	public static final String PREF_USER_TYPE = "pt.up.fe.mobile.ui.USER_TYPE";
	public static final String PREF_COOKIE = "pt.up.fe.mobile.ui.COOKIE";

	
	private final Context context;
	private String cookie;
	private User user;
	public static TuitionHistory tuitionHistory=new TuitionHistory();
	public static FriendsData friends=new FriendsData();

	private SessionManager( Context context) {
		this.context = context.getApplicationContext();
	}
	private static SessionManager INSTANCE;

	/**
	 * 
	 * @param context 
	 * @return the instance of SessionManager
	 */
	public static SessionManager getInstance( Context context ) {
		if ( INSTANCE == null )
			INSTANCE = new SessionManager(context);
		return INSTANCE;
	}
	
	/**
	 * 
	 * @return the instance of SessionManager
	 */
	public static SessionManager getInstance() {
		if ( INSTANCE == null )
			throw new RuntimeException("Session Manager should init with Context");
		return INSTANCE;
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
		SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
		final SharedPreferences.Editor prefEditor = loginSettings.edit();
		prefEditor.putString(PREF_COOKIE, this.cookie);
		prefEditor.commit();
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
		SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
		final SharedPreferences.Editor prefEditor = loginSettings.edit();
		prefEditor.putString(PREF_USERNAME, user.getUser());
		prefEditor.putString(PREF_PASSWORD, user.getPassword());
		prefEditor.putString(PREF_USER_TYPE, user.getType());
		prefEditor.commit();
	}

	public boolean isUserLoaded(){
		if ( user == null )
			return false;
		if ( user.getUser() == null || user.getUser().equals("") )
			return false;
		return true;
	}
	
	
	public boolean loadSession(){
        SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        final String user = loginSettings.getString(PREF_USERNAME, "");
        final String pass = loginSettings.getString(PREF_PASSWORD, "") ;
        final String type = loginSettings.getString(PREF_USER_TYPE, "") ;
        cookie = loginSettings.getString(PREF_COOKIE, "") ;
        if ( !user.equals("") && !pass.equals("") && !type.equals("") )
        {
        	this.user = new User(user, pass, type);
        	return true;
        }
        this.user = new User("", "", "");
        return false;
	}
	
	public void cleanPrefs(){        
		SharedPreferences loginSettings = context.getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
		final SharedPreferences.Editor prefEditor = loginSettings.edit();
		prefEditor.putString(PREF_USERNAME, "");
		prefEditor.putString(PREF_PASSWORD, "");
		prefEditor.putString(PREF_USER_TYPE, "");
		prefEditor.putString(PREF_COOKIE, "");
		prefEditor.commit();
	}
}
