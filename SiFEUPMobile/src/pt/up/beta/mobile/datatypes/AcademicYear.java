package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import pt.up.beta.mobile.datatypes.AcademicUC;

public class AcademicYear implements Comparable<AcademicYear>, Parcelable{

	private int year;
	private List<AcademicUC> firstSemester = new ArrayList<AcademicUC>();
	private List<AcademicUC> secondSemester = new ArrayList<AcademicUC>();
	public AcademicYear(){}
	
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
	
	@Override
	public int compareTo(AcademicYear another) {
		return year-another.year;
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(year);
		dest.writeValue(firstSemester.toArray());
		dest.writeValue(secondSemester.toArray());
	}
	
	private AcademicYear(Parcel in){
		year = in.readInt();
		final AcademicUC [] first  = (AcademicUC[]) in.readValue(AcademicUC.class.getClassLoader());
		for ( AcademicUC uc : first )
			firstSemester.add(uc);
		final AcademicUC [] second  = (AcademicUC[]) in.readValue(AcademicUC.class.getClassLoader());
		for ( AcademicUC uc : second )
			secondSemester.add(uc);
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
