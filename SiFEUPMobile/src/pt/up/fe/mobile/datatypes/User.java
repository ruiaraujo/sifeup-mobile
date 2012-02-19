package pt.up.fe.mobile.datatypes;

public class User {
	private final String user;
	private final String password;
	private final String type;
	public User(String user, String password, String type) {
		this.user = user;
		this.password = password;
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	public String getType() {
		return type;
	}
	
}
