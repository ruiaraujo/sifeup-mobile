package pt.up.beta.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupUtils;
import android.content.res.Resources;

/**
 * Class Employee
 * 
 * @author Ângela Igreja
 * 
 */
@SuppressWarnings("serial")
public class Employee extends Profile implements Serializable {

	/** Web page. May be empty. */
	private String webPage;

	/** Employee acronym - "GAOS" */
	private String acronym;

	/** Employee State - "A" */
	private String state;

	/** Employee email - "gils@fe.up.pt" */
	private String email;

	/** Employee alternative email. May be empty. */
	private String emailAlt;

	/** Employee Phone - "22 557 4109" */
	private String phone;

	/** Employee External Phone. May be empty. */
	private String extPhone;

	/** Employee Tele Alternative. May be empty. */
	private String teleAlt;

	/** Employee Voip Ext. - 3084 */
	private Integer voipExt;

	/** Employee Mobile Phone - 913970682 */
	private String mobilePhone;

	/** Employee Rooms - D109 */
	private List<Room> rooms;

	public Employee() {
		rooms = new ArrayList<Room>();
	}

	/**
	 * Class Room
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	private class Room implements Serializable {
		/** Edi Code - "D" */
		private String codEdi;

		/** Room Code - 109 */
		private int codRoom;

	}

	/**
	 * Parses a JSON String containing Employee info.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException 
	 */
	public Employee JSONSubject(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		SifeupUtils.removeEmptyKeys(jObject);
		if(jObject.has("codigo")) this.code = jObject.getString("codigo");
		if(jObject.has("nome")) this.name = jObject.getString("nome");
		if(jObject.has("pagina_web")) this.webPage = jObject.getString("pagina_web");
		if(jObject.has("sigla")) this.acronym = jObject.getString("sigla");
		if(jObject.has("estado")) this.state = jObject.getString("estado");
		if(jObject.has("email")) this.email = jObject.getString("email");
		if(jObject.has("email_alt")) this.emailAlt = jObject.getString("email_alt");
		if(jObject.has("telefone")) this.phone = jObject.getString("telefone");
		if(jObject.has("ext_tel")) this.extPhone = jObject.getString("ext_tel");
		if(jObject.has("voip_ext")) this.voipExt = jObject.getInt("voip_ext");
		if(jObject.has("telemovel")) this.mobilePhone = jObject.getString("telemovel");
		
		if(jObject.has("salas")){
    		JSONArray jArray = jObject.getJSONArray("salas");
    		
    		for(int i = 0; i < jArray.length(); i++)
    		{
    			Room room = new Room();
    			
    			JSONObject jRoom = jArray.getJSONObject(i);
    			
    			if(jRoom.has("cod_edi")) room.codEdi = jRoom.getString("cod_edi");
    			if(jRoom.has("cod_sala")) room.codRoom = jRoom.getInt("cod_sala");

    			this.rooms.add(room);
    		}
		}
		return this;
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
			result
					.add(new ProfileDetail(res
							.getString(R.string.profile_title_code), code,
							null));
		}
		if (email != null && !email.equals("")) {
			result
					.add(new ProfileDetail(res
							.getString(R.string.profile_title_email), email,
							Type.EMAIL));
		}
		if (emailAlt != null && !emailAlt.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt), emailAlt,
					Type.EMAIL));
		}
		if (mobilePhone != null && !mobilePhone.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile), mobilePhone,
					Type.MOBILE));
		}
		if (phone != null && !phone.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_telephone), phone,
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
		if (webPage != null && !webPage.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_website), webPage,
					Type.WEBPAGE));
		}

		StringBuilder roomCode = new StringBuilder();
		for (Room r : rooms) {
			// 3 is the regular ammount. This is loop only runs in the case
			//of positive number under 100
			for ( int i = 0; i <  3 -Integer.toString(r.codRoom).length() ; ++i);
				roomCode.append('0');
			roomCode.append(r.codRoom);
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_room), r.codEdi
					+ roomCode.toString(), Type.ROOM));
		}
		return result;
	}
}
