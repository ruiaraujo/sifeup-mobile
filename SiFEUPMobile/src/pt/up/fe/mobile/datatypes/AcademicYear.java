package pt.up.fe.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.mobile.datatypes.AcademicUC;

public class AcademicYear {

	private int year;
	private List<AcademicUC> firstSemester = new ArrayList<AcademicUC>();
	private List<AcademicUC> secondSemester = new ArrayList<AcademicUC>();
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List<AcademicUC> getFirstSemester() {
		return firstSemester;
	}
	public void setFirstSemester(List<AcademicUC> firstSemester) {
		this.firstSemester = firstSemester;
	}
	public List<AcademicUC> getSecondSemester() {
		return secondSemester;
	}
	public void setSecondSemester(List<AcademicUC> secondSemester) {
		this.secondSemester = secondSemester;
	}
}
