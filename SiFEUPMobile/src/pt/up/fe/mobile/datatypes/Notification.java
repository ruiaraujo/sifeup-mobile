package pt.up.fe.mobile.datatypes;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
       
import android.util.Log;

/**
 * Class Notification 
 * 
 * @author Ângela Igreja
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
	
	/** 
	 * Notifications Parser
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 */
    public boolean JSONNotification(JSONObject jObject){
	
    	try {
    		if(jObject.has("codigo")) this.code = jObject.getInt("codigo");
			if(jObject.has("link")) this.link = jObject.getString("link");
			if(jObject.has("designacao")) this.setDesignation(jObject.getString("designacao"));
			if(jObject.has("descricao")) this.setDescription(jObject.getString("descricao"));
			if(jObject.has("beneficiario")) this.setBeneficiary(jObject.getString("beneficiario"));
			if(jObject.has("prioridade")) this.setPriority(jObject.getInt("prioridade"));
			if(jObject.has("data")) this.setDate(jObject.getString("data"));
			if(jObject.has("assunto")) this.setSubject(jObject.getString("assunto"));
			if(jObject.has("mensagem")) this.setMessage(jObject.getString("mensagem"));
			if(jObject.has("obs")) this.setObs(jObject.getString("obs"));
		
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
    	Log.e("JSON", "Notification not found");
    	return false;
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

}
