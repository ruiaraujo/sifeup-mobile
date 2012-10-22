package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Dish. Save the information of dish.
 * 
 * @author Ângela Igreja
 * 
 */
public class Dish implements Parcelable {

	@SerializedName("estado")
	private final String state;
	@SerializedName("descricao")
	private final String description;
	@SerializedName("tipo")
	private final int type;
	@SerializedName("tipo_descr")
	private final String descriptionType;

	private Dish(Parcel in) {
		state = ParcelUtils.readString(in);
		description = ParcelUtils.readString(in);
		descriptionType = ParcelUtils.readString(in);
		type = in.readInt();
	}

	public String getDescription() {
		return description;
	}

	public String getDescriptionType() {
		return descriptionType;
	}

	public String getState() {
		return state;
	}

	public int getType() {
		return type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, state);
		ParcelUtils.writeString(dest, description);
		ParcelUtils.writeString(dest, descriptionType);
		dest.writeInt(type);
	}

	public static final Parcelable.Creator<Dish> CREATOR = new Parcelable.Creator<Dish>() {
		public Dish createFromParcel(Parcel in) {
			return new Dish(in);
		}

		public Dish[] newArray(int size) {
			return new Dish[size];
		}
	};

}