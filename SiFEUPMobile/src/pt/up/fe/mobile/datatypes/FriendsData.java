package pt.up.fe.mobile.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;

public class FriendsData {
	
	public final static String TAG="FriendsData";
	
	ArrayList<Friend> list;
	boolean loaded=false;
	

	public FriendsData() 
	{
		list=new ArrayList<Friend>();
	}
	
	public boolean loadFromFile(Context con)
	{        
		if ( con == null )
			return false;
		SharedPreferences friends = con.getSharedPreferences(TAG,Context.MODE_PRIVATE);  
		String friendSet = friends.getString(TAG,null );
		if ( friendSet == null )
			return true;
		StringTokenizer s = new StringTokenizer(friendSet , SEPARATOR);
		while(s.hasMoreTokens())
			addFriend(new Friend(s.nextToken()));
		return true;
	}
	
	public ArrayList<Friend> getList() {
		return list;
	}

	public void setList(ArrayList<Friend> list) {
		this.list = list;
	}

	public boolean addFriend(Friend fr)
	{
		if (  !list.contains(fr))
			list.add(fr);
		else
			return false;
		return true;
	}
	
	public void removeFriend(int pos)
	{
		list.remove(pos);
	}
	
	public boolean isFriend( String code ){
		Friend fr = new Friend(code, "", "");
		return list.contains(fr);
	}

	public void removeFriend(Friend fr)
	{
		list.remove(fr);
	}
	public boolean saveToFile(Context con)
	{
		SharedPreferences friends = con.getSharedPreferences(TAG,Context.MODE_PRIVATE);  
        SharedPreferences.Editor prefEditor = friends.edit();
        prefEditor.putString(TAG, getTokenizedFriends());
        prefEditor.commit();
        return true;
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public Friend getFriend( int pos ) {
		return list.get(pos);
	}
	
	public String getTokenizedFriends (){
		StringBuilder s  =new StringBuilder();
		Collections.sort(list);
		for ( Friend f : list )
			s.append(f.toString() + SEPARATOR );
		return s.toString();
	}
	
	final public static String SEPARATOR = "?";
	
}
