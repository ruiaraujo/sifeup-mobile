package pt.up.beta.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.AccountUtils;
       
import android.util.Log;

/**
 * Class Notification 
 * 
 * @author �ngela Igreja
 *
 */
@SuppressWarnings("serial")
public class Notification  implements Serializable {
	
	/** Notification Code */
	private int code;

	/** Notification Link */
	private String link;
	
	/** Designation. Ex: Avisos  */
	private String designation;
	
	/** Subject English name - */
	private String description;
	
	/** Subject acronym - */
	private String beneficiary;
	
	/** */
	private int priority;
	
	/** */
	private String date;
	
	/** */
	private String subject;
	
	/** */
	private String message;
	
	/** */
	private String obs;
	
	private boolean read;
	
	/** 
	 * Notifications Parser
	 * Returns true in case of correct parsing.
	 * @param jObject 
	 * @return itself
	 */
    public static Notification parseJSON(JSONObject jObject){
	
    	try {
    		Notification not = new Notification();
    		if(jObject.has("codigo")) not.code = jObject.getInt("codigo");
			if(jObject.has("link")) not.link = jObject.getString("link");
			if(jObject.has("designacao")) not.setDesignation(jObject.getString("designacao"));
			if(jObject.has("descricao")) not.setDescription(jObject.getString("descricao"));
			if(jObject.has("beneficiario")) not.setBeneficiary(jObject.getString("beneficiario"));
			if(jObject.has("prioridade")) not.setPriority(jObject.getInt("prioridade"));
			if(jObject.has("data")) not.setDate(jObject.getString("data"));
			if(jObject.has("assunto")) not.setSubject(jObject.getString("assunto"));
			if(jObject.has("mensagem")) not.setMessage(jObject.getString("mensagem"));
			if(jObject.has("obs")) not.setObs(jObject.getString("obs"));
			not.setRead(false);
			return not;
		} catch (JSONException e) {
			e.printStackTrace();
			ACRA.getErrorReporter().handleSilentException(e);
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(null) + "\n\n"));
		}
		
    	Log.d("JSON", "Notification not found");
    	return null;
    }
    public static List<Notification> parseListJSON(String page) throws JSONException{
    	List<Notification> notifications = new ArrayList<Notification>();
		JSONObject jObject = new JSONObject(page);
		if (jObject.has("notificacoes")) {
			JSONArray jArray = jObject.getJSONArray("notificacoes");
			for (int i = 0; i < jArray.length(); i++) {
				notifications.add(Notification
						.parseJSON(jArray.getJSONObject(i)));
			}
		}
		return notifications;
    }

    	

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}

	public String getBeneficiary() {
		return beneficiary;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}
	
	public String getPriorityString() {
		return Integer.toString(priority);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public String getCodeString() {
		return Integer.toString(code);
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public String getObs() {
		return obs;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (code != other.code)
			return false;
		return true;
	}

}
