package pt.up.beta.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupUtils;
import android.content.res.Resources;

/**
 * Class that represents the Object Student
 * 
 * @author Ângela Igreja
 * 
 */
public class Student extends Profile implements Serializable {
	private static final long serialVersionUID = 1727093503991901167L;
	private String programmeAcronym;
	private String programmeName;
	private String programmeNameEn;
	private String programmeCode;
	private String registrationYear;
	private String state;
	private String academicYear;
	private String branch;

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile";
	}

	public String getProgrammeAcronym() {
		return programmeAcronym;
	}

	public void setProgrammeAcronym(String programmeAcronym) {
		this.programmeAcronym = programmeAcronym;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public String getRegistrationYear() {
		return registrationYear;
	}

	public void setRegistrationYear(String registrationYear) {
		this.registrationYear = registrationYear;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public void setProgrammeCode(String programmeCode) {
		this.programmeCode = programmeCode;
	}

	public String getProgrammeCode() {
		return programmeCode;
	}

	public void setProgrammeNameEn(String programmeNameEn) {
		this.programmeNameEn = programmeNameEn;
	}

	public String getProgrammeNameEn() {
		return programmeNameEn;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public List<ProfileDetail> getProfileContents(Resources res) {
		List<ProfileDetail> result = new ArrayList<ProfileDetail>();
		if (code != null && !code.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_code), code, null));
		}
		if (getEmail() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email), getEmail(), Type.EMAIL));
		}
		if (getEmailAlt() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt), getEmailAlt(),
					Type.EMAIL));
		}
		if (getMobilePhone() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile), getMobilePhone(),
					Type.MOBILE));
		}
		if (getPhone() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_telephone), getPhone(),
					Type.MOBILE));
		}
		if (programmeName != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_programme),
					programmeName, null));
		}
		if (branch != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_branch), branch, null));
		}
		if (state != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_status), state, null));
		}
		if (academicYear != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_year), academicYear, null));
		}
		return result;

	}

	/**
	 * Parses a JSON String containing Employee info.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public static Student parseJSON(String page) throws JSONException {
		final Student me = new Student();
		final JSONObject jObject = new JSONObject(page);
		SifeupUtils.removeEmptyKeys(jObject);
		if (jObject.has("codigo"))
			me.setCode(jObject.getString("codigo"));
		if (jObject.has("nome"))
			me.setName(jObject.getString("nome"));
		if (jObject.has("curso_sigla"))
			me.setProgrammeAcronym(jObject.getString("curso_sigla"));
		if (jObject.has("curso_nome"))
			me.setProgrammeName(jObject.getString("curso_nome"));
		if (jObject.has("ano_lect_matricula"))
			me.setRegistrationYear(jObject.getString("ano_lect_matricula"));
		if (jObject.has("estado"))
			me.setState(jObject.getString("estado"));
		if (jObject.has("ano_curricular"))
			me.setAcademicYear(jObject.getString("ano_curricular"));
		if (jObject.has("email"))
			me.setEmail(jObject.getString("email"));
		if (jObject.has("email_alternativo"))
			me.setEmailAlt(jObject.getString("email_alternativo"));
		if (jObject.has("telemovel"))
			me.setMobilePhone(jObject.getString("telemovel"));
		if (jObject.has("telefone"))
			me.setPhone(jObject.getString("telefone"));
		if (jObject.has("ramo"))
			me.setBranch(jObject.getString("ramo"));
		return me;
	}
}