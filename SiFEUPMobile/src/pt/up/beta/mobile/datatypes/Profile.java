package pt.up.beta.mobile.datatypes;

import java.util.List;

import android.content.res.Resources;

public abstract class Profile {

	/** Employee code - "419454" */
	protected String code;
	
	/** Employee name - "Gil António Oliveira da Silva" */
	protected String name;
	
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
}
