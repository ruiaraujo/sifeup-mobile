package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import pt.up.beta.mobile.datatypes.AcademicUC;

public class AcademicYear implements Comparable<AcademicYear>, Parcelable{

	private int year;
	private final List<AcademicUC> firstSemester;
	private final List<AcademicUC> secondSemester;
	public AcademicYear(){
		firstSemester = new ArrayList<AcademicUC>();
		secondSemester = new ArrayList<AcademicUC>();
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List<AcademicUC> getFirstSemester() {
		return firstSemester;
	}
	public List<AcademicUC> getSecondSemester() {
		return secondSemester;
	}
	
	@Override
	public int compareTo(AcademicYear another) {
		return year-another.year;
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(year);
		dest.writeTypedList(firstSemester);
		dest.writeTypedList(secondSemester);
	}
	
	private AcademicYear(Parcel in){
		year = in.readInt();
		firstSemester = new ArrayList<AcademicUC>();
		secondSemester = new ArrayList<AcademicUC>();
		in.readTypedList(firstSemester, AcademicUC.CREATOR);
		in.readTypedList(secondSemester, AcademicUC.CREATOR);
	}
	
	public static final Parcelable.Creator<AcademicYear> CREATOR = new Parcelable.Creator<AcademicYear>() {
		public AcademicYear createFromParcel(Parcel in) {
			return new AcademicYear(in);
		}

		public AcademicYear[] newArray(int size) {
			return new AcademicYear[size];
		}
	};
}
