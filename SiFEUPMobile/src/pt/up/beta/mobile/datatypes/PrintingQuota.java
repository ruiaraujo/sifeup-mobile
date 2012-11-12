package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Class Park Occupation
 * 
 * @author Ângela Igreja
 * 
 */
public class PrintingQuota implements Parcelable {
	/** Free places in the park */
	@SerializedName("saldo")
	private final double quota;
	@SerializedName("login")
	private final String login;

	public String getQuotaAsString() {
		return Double.toString(quota);
	}

	public double getQuota() {
		return quota;
	}

	public String getLogin() {
		return login;
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<PrintingQuota> CREATOR = new Parcelable.Creator<PrintingQuota>() {
		public PrintingQuota createFromParcel(Parcel in) {
			return new PrintingQuota(in);
		}

		public PrintingQuota[] newArray(int size) {
			return new PrintingQuota[size];
		}
	};

	private PrintingQuota(Parcel in) {
		quota = in.readDouble();
		login = ParcelUtils.readString(in);
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(quota);
		ParcelUtils.writeString(dest, login);
	}

}
