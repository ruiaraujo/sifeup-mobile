package pt.up.fe.mobile.datatypes;

/**
 * 
 * Represents a Course, and
 * holds all data about the course
 * like, grade, name, semester, etc
 *
 */

public class AcademicUC {
	private int semester; // "reg_d_codigo"
	private int year; // "a_lectivo"
	private String grade; // "resultado" (int or string)
	private String courseAcronym; // "dis_codigo"
	private int equivalencesNumber; // "n_equiv"
	private int academicYear; // "ano_curricular"
	private String state; // "estado"
	private String type; // "tipo"
	private String name; // "nome"
	private String nameEn; // "name"
	public int getSemester() {
		return semester;
	}
	public void setSemester(int semester) {
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
	public String getName() {
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
}
