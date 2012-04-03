package pt.up.beta.mobile.sifeup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.ImageDownloader;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class FacilitiesUtils {
	private FacilitiesUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getBuildingPic(
			String building, String block, String floor, ResponseCommand command) {
		return new BuildingsTask(command).execute(SifeupAPI.getBuildingPicUrl(
				building, block, floor));
	}

	public static AsyncTask<InputStream, Void, ERROR_TYPE> getBuildingsHotspot(
			InputStream file, ResponseCommand command) {
		return new BuildingsHotspotTask(command).execute(file);
	}
	
	public static AsyncTask<String, Void, ERROR_TYPE> getRoomCode(
			String building, String block, String floor, int x , int y, ResponseCommand command) {
		return new RoomFinderTask(command).execute(SifeupAPI.getRoomPostFinderUrl(building, block, floor, x, y));
	}


	private static class RoomFinderTask extends
			AsyncTask<String, Void, ERROR_TYPE> {
		private final ResponseCommand command;
		private String response;
		public RoomFinderTask(ResponseCommand command) {
			this.command = command;
		}
		

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(ERROR_TYPE error) {
			if (isCancelled()) {
				return;
			}
			if (error == null)
				command.onResultReceived(response);
			else
				command.onError(error);
		}

		@Override
		protected ERROR_TYPE doInBackground(String... params) {
			HttpResponse page = SifeupAPI.doPost(params[0], params[1]);
			if (page == null)
				return ERROR_TYPE.NETWORK;
			if ( !page.containsHeader("Location") )
				return ERROR_TYPE.GENERAL;
			String url = page.getFirstHeader("Location").getValue();
			String [] urlParam = url.substring(url.indexOf("?")).split("&");
			response = urlParam[0].substring(urlParam[0].indexOf("=")+1)
						+ urlParam[1].substring(urlParam[1].indexOf("=")+1);
			return null;
		}

	}
	
	
	private static class BuildingsHotspotTask extends
			AsyncTask<InputStream, Void, ERROR_TYPE> {
		private List<BuildingPicHotspot> hotspots;
		private final ResponseCommand command;

		public BuildingsHotspotTask(ResponseCommand command) {
			this.command = command;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(ERROR_TYPE error) {
			if (isCancelled()) {
				return;
			}
			if (error == null)
				command.onResultReceived(hotspots);
			else
				command.onError(error);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected ERROR_TYPE doInBackground(InputStream... params) {
			// TODO Auto-generated method stub
			String page = SifeupAPI.getPage(params[0], "UTF-8");
			if (page == null)
				return ERROR_TYPE.GENERAL;
			hotspots = (List<BuildingPicHotspot>) new BuldingHotSpotParser()
					.parse(page);
			return null;
		}

	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class BuldingHotSpotParser implements ParserCommand {

		public Object parse(String page) {
			try {
				final List<BuildingPicHotspot> hotspots = new ArrayList<BuildingPicHotspot>();
				final JSONArray buildings = new JSONArray(page);
				for (int i = 0; i < buildings.length(); ++i) {
					final JSONObject building = buildings.getJSONObject(i);
					final String namePt = building.getString("namePt");
					final String nameEn = building.getString("namePt");
					final String buildingCode = building.getString("code");
					final String buildingBlock = building.optString("block");
					final JSONArray coords = building.getJSONArray("coords");
					final int[] x = new int[coords.length() / 2];
					final int[] y = new int[coords.length() / 2];
					for (int j = 0; j < coords.length(); ++j) {
						if (j % 2 == 0)
							x[j / 2] = coords.getInt(j);
						else
							y[j / 2] = coords.getInt(j);

					}
					final JSONArray floors = building.optJSONArray("floors");

					final int[] f;
					if (floors != null) {
						f = new int[floors.length()];
						for (int j = 0; j < floors.length(); ++j)
							f[j] = floors.getInt(j);
					} else
						f = new int[0];
					hotspots.add(new BuildingPicHotspot(namePt, nameEn,
							buildingCode, buildingBlock, x, y, f));
				}
				return hotspots;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private static class BuildingsTask extends
			AsyncTask<String, Void, ERROR_TYPE> {
		private Bitmap bitmap;
		private final ResponseCommand command;

		public BuildingsTask(ResponseCommand command) {
			this.command = command;
		}

		@Override
		// Actual download method, run in the task thread
		protected ERROR_TYPE doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			bitmap = ImageDownloader.downloadBitmap(params[0]);
			if (bitmap == null)
				return ERROR_TYPE.NETWORK;
			return null;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(ERROR_TYPE error) {
			if (isCancelled()) {
				bitmap = null;
				return;
			}
			if (error == null)
				command.onResultReceived(bitmap);
			else
				command.onError(error);
		}
	}

}
