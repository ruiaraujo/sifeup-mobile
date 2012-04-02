package pt.up.beta.mobile.ui.utils;

public class BuildingPicHotspot {
	private final String namePt;
	private final String nameEn;
	private final String buildingCode;
	private final String buildingBlock;
	private final int[] polyX;
	private final int[] polyY;
	private final int[] floors;

	public BuildingPicHotspot(String namePt, String nameEn,
			String buildingCode, String buildingBlock, int[] polyX, int[] polyY,int[] floors) {
		this.nameEn = nameEn;
		this.namePt = namePt;
		if (buildingCode != null)
			this.buildingCode = buildingCode;
		else
			this.buildingCode = "";
		if (buildingBlock != null)
			this.buildingBlock = buildingBlock;
		else
			this.buildingBlock = "";
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

	public String getBuildingBlock() {
		return buildingBlock;
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

}
