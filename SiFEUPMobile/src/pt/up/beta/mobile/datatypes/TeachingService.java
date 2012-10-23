package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class TeachingService implements Parcelable {
	@SerializedName("horas_1s")
	private final double firstSemesterHours;
	@SerializedName("horas_2s")
	private final double secondSemesterHours;
	@SerializedName("horas_media")
	private final double averageSemesterHours;
	@SerializedName("servico")
	private final Subject[] service;

	private TeachingService(Parcel in) {
		firstSemesterHours = in.readDouble();
		secondSemesterHours = in.readDouble();
		averageSemesterHours = in.readDouble();
		service = new Subject[in.readInt()];
		in.readTypedArray(service, Subject.CREATOR);
	}

	public double getFirstSemesterHours() {
		return firstSemesterHours;
	}

	public double getSecondSemesterHours() {
		return secondSemesterHours;
	}

	public double getAverageSemesterHours() {
		return averageSemesterHours;
	}

	public Subject[] getService() {
		return service;
	}

	public static class Subject implements Parcelable {

		@SerializedName("ocorr_id")
		private final String ocorrId;
		@SerializedName("ucurr_nome")
		private final String ucurrName;
		@SerializedName("curso")
		private final String course;
		@SerializedName("ocorr_pa_codigo")
		private final String periodCode;
		@SerializedName("ocorr_ano_lectivo")
		private final String year;
		@SerializedName("ocorr_ano")
		private final String curriculumYear;
		@SerializedName("ocorr_tipo_aula_id")
		private final String classType;

		private Subject(Parcel in) {
			ocorrId = ParcelUtils.readString(in);
			ucurrName = ParcelUtils.readString(in);
			course = ParcelUtils.readString(in);
			periodCode = ParcelUtils.readString(in);
			year = ParcelUtils.readString(in);
			curriculumYear = ParcelUtils.readString(in);
			classType = ParcelUtils.readString(in);
		}

		public String getOcorrId() {
			return ocorrId;
		}

		public String getUcurrName() {
			return ucurrName;
		}

		public String getCourse() {
			return course;
		}

		public String getPeriodCode() {
			return periodCode;
		}

		public String getYear() {
			return year;
		}

		public String getCurriculumYear() {
			return curriculumYear;
		}

		public String getClassType() {
			return classType;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, ocorrId);
			ParcelUtils.writeString(dest, ucurrName);
			ParcelUtils.writeString(dest, course);
			ParcelUtils.writeString(dest, periodCode);
			ParcelUtils.writeString(dest, year);
			ParcelUtils.writeString(dest, curriculumYear);
			ParcelUtils.writeString(dest, classType);
		}

		public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
			public Subject createFromParcel(Parcel in) {
				return new Subject(in);
			}

			public Subject[] newArray(int size) {
				return new Subject[size];
			}
		};

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(firstSemesterHours);
		dest.writeDouble(secondSemesterHours);
		dest.writeDouble(averageSemesterHours);
		dest.writeInt(service.length);
		dest.writeTypedArray(service, flags);
	}

	public static final Parcelable.Creator<TeachingService> CREATOR = new Parcelable.Creator<TeachingService>() {
		public TeachingService createFromParcel(Parcel in) {
			return new TeachingService(in);
		}

		public TeachingService[] newArray(int size) {
			return new TeachingService[size];
		}
	};

}
