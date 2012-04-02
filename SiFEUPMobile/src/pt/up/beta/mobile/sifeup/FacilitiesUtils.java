package pt.up.beta.mobile.sifeup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

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
			bitmap = downloadBitmap(params[0]);
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

	static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);

					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from "
					+ url, e);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
}
