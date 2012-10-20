package pt.up.beta.mobile.sifeup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.acra.ACRA;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class FacilitiesUtils {
	private FacilitiesUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getBuildingPic(
			String building, int floor, ResponseCommand<Bitmap> command, Context context) {
		return new BuildingsTask(command, context).execute(SifeupAPI.getBuildingPicUrl(
				building, floor));
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getRoomPic(String room, ResponseCommand<Bitmap> command, Context context) {
		return new BuildingsTask(command,context ).execute(SifeupAPI.getRoomPicUrl(room));
	}

	public static AsyncTask<InputStream, Void, ERROR_TYPE> getBuildingsHotspot(
			InputStream file, ResponseCommand<List<BuildingPicHotspot>> command) {
		return new BuildingsHotspotTask(command, null).execute(file);
	}
	
	public static AsyncTask<InputStream, Void, ERROR_TYPE> getBuildingHotspot(
			InputStream file, String buildingCode, ResponseCommand<List<BuildingPicHotspot>> command) {
		return new BuildingsHotspotTask(command,buildingCode ).execute(file);
	}
	
	public static AsyncTask<String, Void, ERROR_TYPE> getRoomCode(
			String building,String floor, int x , int y, ResponseCommand<String> command) {
		return new RoomFinderTask(command).execute(SifeupAPI.getRoomPostFinderUrl(building, floor, x, y));
	}


	private static class RoomFinderTask extends
			AsyncTask<String, Void, ERROR_TYPE> {
		private final ResponseCommand<String> command;
		private String response;
		public RoomFinderTask(ResponseCommand<String> command) {
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
			HttpResponse page = SifeupAPI.post(params[0], params[1]);
			if (page == null)
				return ERROR_TYPE.NETWORK;
			if ( !page.containsHeader("Location") )
				return ERROR_TYPE.GENERAL;
			String url = page.getFirstHeader("Location").getValue();
			String [] urlParam = url.substring(url.indexOf("?")).split("&");
			response = urlParam[0].substring(urlParam[0].indexOf("=")+1).trim()
						+ urlParam[1].substring(urlParam[1].indexOf("=")+1).trim();
			return null;
		}

	}
	
	
	private static class BuildingsHotspotTask extends
			AsyncTask<InputStream, Void, ERROR_TYPE> {
		private List<BuildingPicHotspot> hotspots;
		private final ResponseCommand<List<BuildingPicHotspot>> command;
		private final String buildingCode;
		public BuildingsHotspotTask(ResponseCommand<List<BuildingPicHotspot>> command, String buildingCode) {
			this.command = command;
			this.buildingCode = buildingCode;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(ERROR_TYPE error) {
			if (isCancelled()) {
				return;
			}
			if (error == null)
			{
				if ( buildingCode == null )
					command.onResultReceived(hotspots);
				else
				{
					for ( BuildingPicHotspot hot : hotspots )
					{
						if (buildingCode.equals(hot.getBuildingCode()) )
						{
							List<BuildingPicHotspot> hotspot = new ArrayList<BuildingPicHotspot>();
							hotspot.add(hot);
							command.onResultReceived(hotspot);
							return;
						}
					}
				}
			}
			else
				command.onError(error);
		}


		@Override
		protected ERROR_TYPE doInBackground(InputStream... params) {
			String page;
			try {
				page = SifeupAPI.getPage(params[0], "UTF-8");
				if (page == null)
					return ERROR_TYPE.GENERAL;
				hotspots = new BuldingHotSpotParser()
						.parse(page);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * Parses a JSON String containing Exams info, Stores that info at
	 * Collection exams.
	 */

	private static class BuldingHotSpotParser implements ParserCommand<List<BuildingPicHotspot>> {

		public List<BuildingPicHotspot> parse(String page) {
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
				ACRA.getErrorReporter().handleSilentException(e);
				ACRA.getErrorReporter().handleSilentException(
						new RuntimeException("Id:"
								+ AccountUtils.getActiveUserCode(null) + "\n\n" + page));
			}
			return null;
		}

	}

	private static class BuildingsTask extends
			AsyncTask<String, Void, ERROR_TYPE> {
		private Bitmap bitmap;
		private final ResponseCommand<Bitmap> command;
		private final Context context;

		public BuildingsTask(ResponseCommand<Bitmap> command, Context context) {
			this.command = command;
			this.context = context;
		}

		@Override
		// Actual download method, run in the task thread
		protected ERROR_TYPE doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			try {
				bitmap = SifeupAPI.downloadBitmap(params[0], AccountUtils.getAuthToken(context), context);
			} catch (AuthenticationException e) {
				e.printStackTrace();
				return ERROR_TYPE.AUTHENTICATION;
			} catch (IOException e) {
				e.printStackTrace();
				return ERROR_TYPE.NETWORK;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
				return ERROR_TYPE.AUTHENTICATION;
			} catch (AuthenticatorException e) {
				e.printStackTrace();
				return ERROR_TYPE.AUTHENTICATION;
			}
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
