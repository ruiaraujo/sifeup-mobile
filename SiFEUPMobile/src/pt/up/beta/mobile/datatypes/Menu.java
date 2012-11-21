package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Menu. Save the information of menu.
 * 
 * @author Ângela Igreja
 * 
 */
public class Menu implements Parcelable {
	@SerializedName("estado")
	private final String state;

	@SerializedName("data")
	private final String date;

	@SerializedName("pratos")
	private final Dish[] dishes;

	private Menu(Parcel in) {
		state = ParcelUtils.readString(in);
		date = ParcelUtils.readString(in);
		dishes = new Dish[in.readInt()];
		in.readTypedArray(dishes, Dish.CREATOR);
	}

	public String getState() {
		return state;
	}

	public String getDate() {
		return date;
	}

	public Dish[] getDishes() {
		return dishes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, state);
		ParcelUtils.writeString(dest, date);
		dest.writeInt(dishes.length);
		dest.writeTypedArray(dishes, flags);
	}

	public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
		public Menu createFromParcel(Parcel in) {
			return new Menu(in);
		}

		public Menu[] newArray(int size) {
			return new Menu[size];
		}
	};
}