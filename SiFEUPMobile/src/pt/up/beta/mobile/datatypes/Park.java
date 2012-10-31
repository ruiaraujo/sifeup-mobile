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
public class Park implements Parcelable {
	/** Free places in the park */
	@SerializedName("lugares")
	private final int places;
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getPlaces() {
		return Integer.toString(places);
	}

	public String getName() {
		return name;
	}

	public int getPlacesNumber() {
		return places;
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Park> CREATOR = new Parcelable.Creator<Park>() {
		public Park createFromParcel(Parcel in) {
			return new Park(in);
		}

		public Park[] newArray(int size) {
			return new Park[size];
		}
	};

	private Park(Parcel in) {
		places = in.readInt();
		name = ParcelUtils.readString(in);
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(places);
		ParcelUtils.writeString(dest, name);
	}

}
