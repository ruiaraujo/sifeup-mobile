package pt.up.beta.mobile.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Represents a Course, and holds all data about the course like, grade, name,
 * semester, etc
 * 
 */

public class AcademicUC implements Parcelable {
	private String semester; // "reg_d_codigo"
	private int year; // "a_lectivo"
	private String grade; // "resultado" (int or string)
	private String courseAcronym; // "dis_codigo"
	private int equivalencesNumber; // "n_equiv"
	private int academicYear; // "ano_curricular"
	private String state; // "estado"
	private String type; // "tipo"
	private String name; // "nome"
	private String nameEn; // "name"

	public AcademicUC() {
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCourseAcronym() {
		return courseAcronym;
	}

	public void setCourseAcronym(String courseAcronym) {
		this.courseAcronym = courseAcronym;
	}

	public int getEquivalencesNumber() {
		return equivalencesNumber;
	}

	public void setEquivalencesNumber(int equivalencesNumber) {
		this.equivalencesNumber = equivalencesNumber;
	}

	public int getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(int academicYear) {
		this.academicYear = academicYear;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNamePt() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(semester != null ? 1 : 0);
		if (semester != null)
			dest.writeString(semester);
		dest.writeInt(year);
		dest.writeInt(grade != null ? 1 : 0);
		if (grade != null)
			dest.writeString(grade);
		dest.writeInt(courseAcronym != null ? 1 : 0);
		if (courseAcronym != null)
			dest.writeString(courseAcronym);
		dest.writeInt(equivalencesNumber);
		dest.writeInt(academicYear);
		dest.writeInt(state != null ? 1 : 0);
		if (state != null)
			dest.writeString(state);
		dest.writeInt(type != null ? 1 : 0);
		if (type != null)
			dest.writeString(type);
		dest.writeInt(name != null ? 1 : 0);
		if (name != null)
			dest.writeString(name);
		dest.writeInt(nameEn != null ? 1 : 0);
		if (nameEn != null)
			dest.writeString(nameEn);
	}

	private AcademicUC(Parcel in) {
		if (in.readInt() == 1)
			semester = in.readString();
		year = in.readInt();
		if (in.readInt() == 1)
			grade = in.readString();
		if (in.readInt() == 1)
			courseAcronym = in.readString();
		equivalencesNumber = in.readInt();
		academicYear = in.readInt();
		if (in.readInt() == 1)
			state = in.readString();
		if (in.readInt() == 1)
			type = in.readString();
		if (in.readInt() == 1)
			name = in.readString();
		if (in.readInt() == 1)
			nameEn = in.readString();
	}

	public static final Parcelable.Creator<AcademicUC> CREATOR = new Parcelable.Creator<AcademicUC>() {
		public AcademicUC createFromParcel(Parcel in) {
			return new AcademicUC(in);
		}

		public AcademicUC[] newArray(int size) {
			return new AcademicUC[size];
		}
	};
}
