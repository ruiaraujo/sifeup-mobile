package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class this. Save the name and menus of the this.
 * 
 * @author Ângela Igreja
 * 
 */

public class Canteen implements Parcelable {

	@SerializedName("codigo")
	private final int code;
	
	@SerializedName("descricao")
	private final String description;
	
	@SerializedName("horario")
	private final String timetable;
	
	@SerializedName("ementas")
	private final Menu[] menus;

	private Canteen(Parcel in) {
		code = in.readInt();
		description = ParcelUtils.readString(in);
		timetable = ParcelUtils.readString(in);
		menus = new Menu[in.readInt()];
		in.readTypedArray(menus, Menu.CREATOR);
	}

	public Menu[] getMenus() {
		return menus;
	}

	public String getDescription() {
		return description;
	}

	public String getDate(int groupPosition) {
		return menus[groupPosition].getDate();
	}
	
	public boolean isClosed(int groupPosition) {
		return menus[groupPosition].getState().equals("Fechado");
	}

	public String getTimetable() {
		return timetable;
	}

	public int getMenuCount() {
		return menus.length;
	}

	public Dish getDish(int groupPosition, int childPosition) {
		return menus[groupPosition].getDishes()[childPosition];
	}

	public int getDishesCount(int groupPosition) {
		return menus[groupPosition].getDishes().length;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(code);
		ParcelUtils.writeString(out, description);
		ParcelUtils.writeString(out, timetable);
		out.writeInt(menus.length);
		out.writeTypedArray(menus, flags);
	}

	public static final Parcelable.Creator<Canteen> CREATOR = new Parcelable.Creator<Canteen>() {
		public Canteen createFromParcel(Parcel in) {
			return new Canteen(in);
		}

		public Canteen[] newArray(int size) {
			return new Canteen[size];
		}
	};

}
