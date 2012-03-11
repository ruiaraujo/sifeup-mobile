package pt.up.fe.mobile.sifeup;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import pt.up.fe.mobile.datatypes.TuitionHistory;
import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.friends.DBAdapter;
import pt.up.fe.mobile.friends.Friend;

/**
 * Singleton class that holds the active Session cookie.
 * 
 * @author Ângela Igreja
 * 
 */
public class SessionManager {

    public static final String PREF_USERNAME = "pt.up.fe.mobile.ui.USERNAME";
    public static final String PREF_DISPLAY_NAME = "pt.up.fe.mobile.ui.USERNAME_DISPLAY";
    public static final String PREF_PASSWORD = "pt.up.fe.mobile.ui.PASSWORD";
    public static final String PREF_USER_TYPE = "pt.up.fe.mobile.ui.USER_TYPE";
    public static final String PREF_COOKIE = "pt.up.fe.mobile.ui.COOKIE";

    private final Context context;
    private String cookie;
    private User user;
    public static TuitionHistory tuitionHistory = new TuitionHistory();
    private final DBAdapter storage;
	private List<Friend> listFriends;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        storage = DBAdapter.getInstance(this.context);
    }

    private static SessionManager INSTANCE;

    /**
     * 
     * @param context
     * @return the instance of SessionManager
     */
    public static SessionManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(context);
            INSTANCE.loadSession();
        }
        return INSTANCE;
    }

    /**
     * 
     * @return the instance of SessionManager
     */
    public static SessionManager getInstance() {
        if (INSTANCE == null)
            throw new RuntimeException(
                    "Session Manager should init with Context");
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
     * 
     * @param cookie
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
        SharedPreferences loginSettings = context.getSharedPreferences(
                SessionManager.class.getName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor = loginSettings.edit();
        prefEditor.putString(PREF_COOKIE, this.cookie);
        prefEditor.commit();
    }

    /**
     * Get Login Code
     * 
     * @return the login code
     */
    public String getLoginCode() {
        return user.getUser();
    }

    /**
     * Get Login Name
     * 
     * @return the login name
     */
    public String getLoginName() {
        return user.getDisplayName();
    }

    /**
     * Get Login Password
     * 
     * @return the login passowrd
     */
    public String getLoginPassword() {
        return user.getPassword();
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
        SharedPreferences loginSettings = context.getSharedPreferences(
                SessionManager.class.getName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor = loginSettings.edit();
        prefEditor.putString(PREF_USERNAME, user.getUser());
        prefEditor.putString(PREF_DISPLAY_NAME, user.getDisplayName());
        prefEditor.putString(PREF_PASSWORD, user.getPassword());
        prefEditor.putString(PREF_USER_TYPE, user.getType());
        prefEditor.commit();
    }

    public boolean isUserLoaded() {
        if (user == null)
            return false;
        if (user.getUser() == null || user.getUser().equals(""))
            return false;
        return true;
    }

    public boolean loadSession() {
        SharedPreferences loginSettings = context.getSharedPreferences(
                SessionManager.class.getName(), Context.MODE_PRIVATE);
        final String display = loginSettings.getString(PREF_DISPLAY_NAME, "");
        final String user = loginSettings.getString(PREF_USERNAME, "");
        final String pass = loginSettings.getString(PREF_PASSWORD, "");
        final String type = loginSettings.getString(PREF_USER_TYPE, "");
        cookie = loginSettings.getString(PREF_COOKIE, "");
        if (!display.equals("") && !user.equals("") && !pass.equals("")
                && !type.equals("")) {
            this.user = new User(display, user, pass, type);
            return true;
        }
        this.user = new User("", "", "", "");
        return false;
    }

    public void cleanPrefs() {
        SharedPreferences loginSettings = context.getSharedPreferences(
                SessionManager.class.getName(), Context.MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor = loginSettings.edit();
        prefEditor.putString(PREF_USERNAME, "");
        prefEditor.putString(PREF_DISPLAY_NAME, "");
        prefEditor.putString(PREF_PASSWORD, "");
        prefEditor.putString(PREF_USER_TYPE, "");
        prefEditor.putString(PREF_COOKIE, "");
        user = new User("", "", "", "");
        cookie = "";
        prefEditor.commit();
    }

	public boolean loadFriends(){
		if ( listFriends != null )
			return true;
		listFriends = storage.getFriends(user.getUser());
		Collections.sort(listFriends);
		return listFriends != null;
	}

	public List<Friend> getFriendsList() {
		return listFriends;
	}

	public boolean addFriend(Friend fr)
	{
		if ( listFriends == null )
			return false;
		if ( storage.addFriend(fr, user.getUser()) )
		{//sorted insertion
		    for ( int i = 0 ; i < listFriends.size() ; ++i ) {
		        int compare = fr.compareTo(listFriends.get(i));
		        if ( compare < 0 )
			    {
		            listFriends.add(i,fr);
		            return true;
			    }
		        else if ( compare == 0 )
		            return false;
		    }
		    listFriends.add(fr);
		}
		else
			return false;
		return true;
	}
	
	public boolean removeFriend(int pos)
	{
		if ( listFriends == null )
			return false;
		if ( storage.deleteFriend(listFriends.get(pos), user.getUser()) )
			listFriends.remove(pos);
		else
			return false;
		return true;
	}

	public Friend getFriend(int position) {
		if ( listFriends == null )
			return null;
		return listFriends.get(position);
	}

	public void cleanFriends() {
		if ( listFriends == null )
			return;
		listFriends.clear();
		listFriends = null;
	}

	public boolean isFriend(String code) {
		if ( listFriends == null )
			return false;
		return listFriends.contains(new Friend(code,"",""));
	}

	public boolean removeFriend(Friend fr) {
		if ( listFriends == null )
			return false;
		if ( storage.deleteFriend(fr, user.getUser()) )
			listFriends.remove(fr);
		else
			return false;
		return true;
	}
}
