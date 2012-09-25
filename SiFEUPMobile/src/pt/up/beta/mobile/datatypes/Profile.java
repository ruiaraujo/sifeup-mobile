package pt.up.beta.mobile.datatypes;

import java.util.List;

import android.content.res.Resources;
import android.text.TextUtils;

public abstract class Profile {
	
	/** Employee code - "419454" */
	protected String code;
	
	/** Employee name - "Gil António Oliveira da Silva" */
	protected String name;

	/** Employee email - "gils@fe.up.pt" */
	private String email;

	/** Employee alternative email. May be empty. */
	private String emailAlt;

	/** Employee Phone - "22 557 4109" */
	private String phone;
	
	/** Employee Mobile Phone - 913970682 */
	private String mobilePhone;


	/** Web page. May be empty. */
	private String webPage;

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailAlt() {
		return emailAlt;
	}

	public void setEmailAlt(String emailAlt) {
		this.emailAlt = emailAlt;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getFirstName() {
		if ( name == null )
			return null;
		final String [] names = name.split(" ");
		if ( names.length >0 )
			return names[0];
		return null;
	}
	

	public String getLastName() {
		if ( name == null )
			return null;
		final String [] names = name.split(" ");
		if ( names.length >0 )
			return names[names.length-1];
		return null;
	}
	

	public String getShortName() {
		final StringBuilder st = new StringBuilder();
		final String first = getFirstName();
		final String last = getLastName();
		if ( !TextUtils.isEmpty(first) ){
			st.append(first);
			st.append(' ');
		}
		if ( !TextUtils.isEmpty(last) )
			st.append(last);
		return st.toString();
	}

	public String getWebPage() {
		return webPage;
	}

	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile"; 
		String WEBPAGE = "webpage";
		String ROOM = "room";
	}
	/**
	 * 
	 * @author Rui Araújo
	 *
	 */
	public class ProfileDetail{
		public String title;
		public String content;
		public String type;
		public ProfileDetail(String title, String content, String type) {
			this.title = title;
			this.content = content;
			this.type = type;
		}
		
	}
	
	abstract public List<ProfileDetail> getProfileContents( Resources res );

	abstract public String getType();

}
