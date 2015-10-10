package pt.up.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class OtherSubjectOccurrences implements Parcelable {

	@SerializedName("id")
	private final String occurenceId;
	@SerializedName("nome")
	private final String name;
	@SerializedName("name")
	private final String nameEn;
	@SerializedName("ano_letivo")
	private final String year;
	@SerializedName("data_inicio")
	private final String dateBegin;
	@SerializedName("data_fim")
	private final String dateEnd;
	@SerializedName("periodo_nome")
	private final String periodName;
	@SerializedName("periodo_id")
	private final String periodId;

	private OtherSubjectOccurrences(Parcel in) {
		occurenceId = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		nameEn = ParcelUtils.readString(in);
		year = ParcelUtils.readString(in);
		dateBegin = ParcelUtils.readString(in);
		dateEnd = ParcelUtils.readString(in);
		periodName = ParcelUtils.readString(in);
		periodId = ParcelUtils.readString(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getOccurenceId() {
		return occurenceId;
	}

	public String getName() {
		return name;
	}

	public String getNameEn() {
		return nameEn;
	}

	public String getYear() {
		return year;
	}

	public String getDateBegin() {
		return dateBegin;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public String getPeriodName() {
		return periodName;
	}

	public String getPeriodId() {
		return periodId;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, occurenceId);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, nameEn);
		ParcelUtils.writeString(dest, year);
		ParcelUtils.writeString(dest, dateBegin);
		ParcelUtils.writeString(dest, dateEnd);
		ParcelUtils.writeString(dest, periodName);
		ParcelUtils.writeString(dest, periodId);
	}

	public static final Parcelable.Creator<OtherSubjectOccurrences> CREATOR = new Parcelable.Creator<OtherSubjectOccurrences>() {
		public OtherSubjectOccurrences createFromParcel(Parcel in) {
			return new OtherSubjectOccurrences(in);
		}

		public OtherSubjectOccurrences[] newArray(int size) {
			return new OtherSubjectOccurrences[size];
		}
	};

}
