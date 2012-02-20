package pt.up.fe.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
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
		this.code = jObject.optString("codigo");
		this.name = jObject.optString("nome");
		this.webPage = jObject.optString("pagina_web");
		this.acronym = jObject.optString("sigla");
		this.state = jObject.optString("estado");
		this.email = jObject.optString("email");
		this.emailAlt = jObject.optString("email_alt");
		this.phone = jObject.optString("telefone");
		this.extPhone = jObject.optString("ext_tel");
		if (jObject.has("voip_ext"))
			this.voipExt = jObject.getInt("voip_ext");
		this.mobilePhone = jObject.optString("telemovel");

		if (jObject.has("salas")) {
			JSONArray jArray = jObject.getJSONArray("salas");

			for (int i = 0; i < jArray.length(); i++) {
				Room room = new Room();
				JSONObject jRoom = jArray.getJSONObject(i);

				room.codEdi = jRoom.optString("cod_edi");
				if (jRoom.has("cod_sala"))
					room.codRoom = jRoom.getInt("cod_sala");

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
		for (Room r : rooms) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_room), r.codEdi
					+ r.codRoom, Type.ROOM));
		}
		return result;
	}
}
