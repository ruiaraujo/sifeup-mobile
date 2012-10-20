package pt.up.beta.mobile.ui.utils;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class BuildingPicHotspot implements Parcelable {
	@SerializedName("namePt")
	private final String namePt;
	@SerializedName("nameEn")
	private final String nameEn;
	@SerializedName("floors")
	private final String buildingCode;
	@SerializedName("polyX")
	private final int[] polyX;
	@SerializedName("polyY")
	private final int[] polyY;
	@SerializedName("floors")
	private final int[] floors;

	public BuildingPicHotspot(String namePt, String nameEn,
			String buildingCode, String buildingBlock, int[] polyX,
			int[] polyY, int[] floors) {
		this.nameEn = nameEn;
		this.namePt = namePt;
		this.buildingCode = buildingCode;
		this.polyX = polyX;
		this.polyY = polyY;
		this.floors = floors;
	}

	/**
	 * Return true if the point is inside this poly. Copied from
	 * http://alienryderflex.com/polygon/
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return true if it is inside this hotspot
	 */
	public boolean pointInPolygon(int x, int y) {

		int i, j = polyX.length - 1;
		boolean oddNodes = false;

		for (i = 0; i < polyX.length; i++) {
			if ((((polyY[i] <= y) && (y < polyY[j])) || ((polyY[j] <= y) && (y < polyY[i])))
					&& (x < (polyX[j] - polyX[i]) * (y - polyY[i])
							/ (polyY[j] - polyY[i]) + polyX[i])) {
				oddNodes = !oddNodes;
			}

			j = i;
		}
		return oddNodes;

	}

	public String getBuildingCode() {
		return buildingCode;
	}

	public String getNameEn() {
		return nameEn;
	}

	public String getNamePt() {
		return namePt;
	}

	public int[] getFloors() {
		return floors;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// We build these objects by hand so
		// we know that these string will never be null
		dest.writeString(namePt);
		dest.writeString(nameEn);
		dest.writeString(buildingCode);
		dest.writeIntArray(polyX);
		dest.writeIntArray(polyY);
		dest.writeIntArray(floors);
	}

	private BuildingPicHotspot(Parcel in) {
		namePt = in.readString();
		nameEn = in.readString();
		buildingCode = in.readString();
		polyX = in.createIntArray();
		polyY = in.createIntArray();
		floors = in.createIntArray();

	}

	public static final Parcelable.Creator<BuildingPicHotspot> CREATOR = new Parcelable.Creator<BuildingPicHotspot>() {
		public BuildingPicHotspot createFromParcel(Parcel in) {
			return new BuildingPicHotspot(in);
		}

		public BuildingPicHotspot[] newArray(int size) {
			return new BuildingPicHotspot[size];
		}
	};

}
