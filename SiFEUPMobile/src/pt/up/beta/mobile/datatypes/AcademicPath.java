package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * 
 * Holds All Info about 
 * Academic Path
 *
 */
public class AcademicPath implements Parcelable{
    private String code; // "numero"
	private String state; // "estado"
	private String courseAcronym; // "cur_codigo"
	private String courseName; // "cur_nome"
	private String courseNameEn; // "cur_name"
	private String average; // "media"
	private int courseYears; // "anos_curso"
	private int numberEntries; // "inscricoes_ucs"
	private int baseYear;
	private List<AcademicYear> ucs = new ArrayList<AcademicYear>();

	public AcademicPath(){}
	
	/**
	 * Parses a JSON String containing Academic Path info,
	 * Stores that info at Object 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public AcademicPath JSONAcademicPath(String page) throws JSONException {
		JSONObject jObject = new JSONObject(page);
		if(jObject.has("ucs")){
			if(jObject.has("numero")) code = jObject.getString("numero");
			if(jObject.has("estado")) state = jObject.getString("estado");
			if(jObject.has("cur_codigo")) courseAcronym = jObject.getString("cur_codigo");
			if(jObject.has("cur_nome")) courseName = jObject.getString("cur_nome");
			if(jObject.has("cur_name")) courseNameEn = jObject.getString("cur_name");
			if(jObject.has("media")) average = jObject.getString("media");
			if(jObject.has("anos_curso")) courseYears = jObject.getInt("anos_curso");
			if(jObject.has("inscricoes_ucs")) numberEntries = jObject.getInt("inscricoes_ucs");
			baseYear = Integer.MAX_VALUE;
			// iterate over ucs
			JSONArray jArray = jObject.getJSONArray("ucs");
			for(int i = 0; i < jArray.length(); i++){
				// new JSONObject
				JSONObject jUc = jArray.getJSONObject(i);
				// new UC
				AcademicUC uc = new AcademicUC();
				
				if(jUc.has("reg_d_codigo")) uc.setSemester(jUc.getInt("reg_d_codigo"));
				if(jUc.has("a_lectivo")) uc.setYear(jUc.getInt("a_lectivo"));
				if(jUc.has("resultado")) uc.setGrade(jUc.getString("resultado"));
				if(jUc.has("dis_codigo")) uc.setCourseAcronym(jUc.getString("dis_codigo"));
				if(jUc.has("n_equiv")) uc.setEquivalencesNumber(jUc.getInt("n_equiv"));
				if(jUc.has("ano_curricular")) uc.setAcademicYear(jUc.getInt("ano_curricular"));
				if(jUc.has("estado")) uc.setState(jUc.getString("estado"));
				if(jUc.has("tipo")) uc.setType(jUc.getString("tipo"));
				if(jUc.has("nome")) uc.setName(jUc.getString("nome"));
				if(jUc.has("name")) uc.setNameEn(jUc.getString("name"));
				baseYear = Math.min(baseYear, uc.getYear());
				AcademicYear year = null;
				for ( int j = 0 ; j < ucs.size() ; ++j )
				{
					if ( ucs.get(j).getYear() == uc.getYear() )
					{
				        year = ucs.get(j);
				        break;
					}
				}
				// add uc to academic path
				if ( year ==  null ) 
				{
					year = new AcademicYear();
					year.setYear(uc.getYear());
					ucs.add(year);
				}
				if ( uc.getSemester() == 1 )
				{
					year.getFirstSemester().add(uc);
				}
				else if ( uc.getSemester() == 2)
				{
					year.getSecondSemester().add(uc);
				}
			}
		}
		Collections.sort(ucs);
		return this;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCourseAcronym() {
		return courseAcronym;
	}

	public void setCourseAcronym(String courseAcronym) {
		this.courseAcronym = courseAcronym;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseNameEn() {
		return courseNameEn;
	}

	public void setCourseNameEn(String courseNameEn) {
		this.courseNameEn = courseNameEn;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	public int getCourseYears() {
		return courseYears;
	}

	public void setCourseYears(int courseYears) {
		this.courseYears = courseYears;
	}

	public int getNumberEntries() {
		return numberEntries;
	}

	public void setNumberEntries(int numberEntries) {
		this.numberEntries = numberEntries;
	}

	public int getBaseYear() {
		return baseYear;
	}

	public void setBaseYear(int baseYear) {
		this.baseYear = baseYear;
	}

	public List<AcademicYear> getUcs() {
		return ucs;
	}

	public void setUcs(List<AcademicYear> ucs) {
		this.ucs = ucs;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(code!=null?1:0);
		if ( code != null) dest.writeString(code);
		dest.writeInt(state!=null?1:0);
		if ( state != null) dest.writeString(state);
		dest.writeInt(courseAcronym!=null?1:0);
		if ( courseAcronym != null) dest.writeString(courseAcronym);
		dest.writeInt(courseName!=null?1:0);
		if ( courseName != null) dest.writeString(courseName);
		dest.writeInt(courseNameEn!=null?1:0);
		if ( courseNameEn != null) dest.writeString(courseNameEn);
		dest.writeInt(average!=null?1:0);
		if ( average != null) dest.writeString(average);
		dest.writeInt(courseYears);
		dest.writeInt(numberEntries);
		dest.writeInt(baseYear);
		dest.writeValue(ucs.toArray());
	}
	
	private AcademicPath( Parcel in ){
		if ( in.readInt() == 1 ) code = in.readString();
		if ( in.readInt() == 1 ) state = in.readString();
		if ( in.readInt() == 1 ) courseAcronym = in.readString();
		if ( in.readInt() == 1 ) courseName = in.readString();
		if ( in.readInt() == 1 ) courseNameEn = in.readString();
		if ( in.readInt() == 1 ) average = in.readString();
		courseYears = in.readInt();
		numberEntries = in.readInt();
		baseYear = in.readInt();
		final AcademicYear [] years = (AcademicYear[]) in.readValue(AcademicYear.class.getClassLoader());
		for ( AcademicYear year : years )
			ucs.add(year);
	}
	
	public static final Parcelable.Creator<AcademicPath> CREATOR = new Parcelable.Creator<AcademicPath>() {
		public AcademicPath createFromParcel(Parcel in) {
			return new AcademicPath(in);
		}

		public AcademicPath[] newArray(int size) {
			return new AcademicPath[size];
		}
	};
	
}
