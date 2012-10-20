package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.content.res.Resources;

import com.google.gson.annotations.SerializedName;

/**
 * Class Employee
 * 
 * @author Ângela Igreja
 * 
 */
public class Employee extends Profile {

	/** Employee acronym - "GAOS" */
	@SerializedName("sigla")
	private String acronym;

	/** Employee State - "A" */
	@SerializedName("estado")
	private String state;

	/** Employee External Phone. May be empty. */
	@SerializedName("ext_tel")
	private String extPhone;

	/** Employee Tele Alternative. May be empty. */
	@SerializedName("tele_alt")
	private String teleAlt;

	/** Employee Voip Ext. - 3084 */
	@SerializedName("voip_ext")
	private Integer voipExt;

	/** Employee Rooms - D109 */
	@SerializedName("salas")
	private List<Room> rooms;

	/** Employee Presentation. May be empty. */
	@SerializedName("apresentacao")
	private String presentation;
	
	/** Employee Presentation. May be empty. */
	@SerializedName("apresentacao_uk")
	private String presentationEn;
	
	/**
	 * Class Room
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	private static class Room{
		/** Edi Code - "D" */
		@SerializedName("sigla")
		private String id;

		/** Room Code - 109 */
		@SerializedName("sigla")
		private String acronym;

	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public List<ProfileDetail> getProfileContents(Resources res) {
		List<ProfileDetail> result = new ArrayList<ProfileDetail>();
		if (code != null && !code.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_code), code, null));
		}
		if (getEmail() != null && !getEmail().equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email), getEmail(),
					Type.EMAIL));
		}
		if (getEmailAlt() != null && !getEmailAlt().equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt),
					getEmailAlt(), Type.EMAIL));
		}
		if (getMobilePhone() != null && !getMobilePhone().equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile),
					getMobilePhone(), Type.MOBILE));
		}
		if (getPhone() != null && !getPhone().equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_telephone), getPhone(),
					Type.MOBILE));
		}
		if (extPhone != null && !extPhone.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_telephone), extPhone,
					null));
		}
		if (teleAlt != null && !teleAlt.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_mobile), teleAlt,
					Type.MOBILE));
		}
		if (voipExt != null && !voipExt.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_voip), voipExt
					.toString(), null));
		}
		if (getWebPage() != null && !getWebPage().equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_website), getWebPage(),
					Type.WEBPAGE));
		}

		for (Room r : rooms) {
			// 3 is the regular ammount. This is loop only runs in the case
			// of positive number under 100
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_room), r.acronym, Type.ROOM));
		}
		return result;
	}

	@Override
	public String getType() {
		return SifeupAPI.EMPLOYEE_TYPE;
	}
}
