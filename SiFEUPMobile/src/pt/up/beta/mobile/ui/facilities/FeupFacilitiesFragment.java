package pt.up.beta.mobile.ui.facilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.ui.utils.TouchImageView.OnTapListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ZoomControls;
import external.com.google.android.apps.iosched.util.UIUtils;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesFragment extends BaseFragment implements
		ResponseCommand<List<BuildingPicHotspot>>, OnTapListener {

	private TouchImageView pic;
	private List<BuildingPicHotspot> hotspots;
	static final String welcomeScreenShownPref = "welcomeScreenShown";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_facility_pic, getParentContainer(), true);
		pic = (TouchImageView) root.findViewById(R.id.facility_image);
		final ZoomControls zoom = (ZoomControls) root
				.findViewById(R.id.zoomControls);
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected void onPostExecute(Bitmap result) {
				pic.setImageBitmap(result);
				pic.setMaxZoom(6);
				pic.setOnTapListener(FeupFacilitiesFragment.this);
				if (!getActivity().getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)
						|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					zoom.setOnZoomInClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							pic.zoomIn();
						}
					});
					zoom.setOnZoomOutClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							pic.zoomOut();
						}
					});
				} else
					zoom.setVisibility(View.GONE);
			}

			@Override
			protected Bitmap doInBackground(Void... params) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				final InputStream is = getResources()
						.openRawResource(R.raw.map);
				int nRead;
				byte[] data = new byte[16384];
				try {
					while ((nRead = is.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}

					buffer.flush();

					final byte[] baf = buffer.toByteArray();
					return BitmapFactory.decodeByteArray(baf, 0, baf.length);
				} catch (Exception e) {
				}
				return null;
			}
		}.execute();
		return getParentContainer();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		task = FacilitiesUtils.getBuildingsHotspot(getResources()
				.openRawResource(R.raw.buildings_coordinates), this);
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = FacilitiesUtils.getBuildingsHotspot(getResources()
				.openRawResource(R.raw.buildings_coordinates), this);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if (hotspots == null)
			return false;
		final int x = Math.round(e.getX());
		final int y = Math.round(e.getY());
		for (BuildingPicHotspot hot : hotspots) {
			if (hot.pointInPolygon(x, y)) {
				if (hot.getFloors() == null || hot.getFloors().length == 0) {
					Toast.makeText(getActivity(),
							R.string.toast_no_detailed_view, Toast.LENGTH_SHORT)
							.show();
					break;
				}
				final Intent i = new Intent(getActivity(),
						FeupFacilitiesDetailsActivity.class);
				i.putExtra(FeupFacilitiesDetailsFragment.BUILDING_EXTRA, hot);
				startActivity(i);
				break;
			}
		}
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (hotspots == null)
			return false;
		final int x = Math.round(e.getX());
		final int y = Math.round(e.getY());
		for (BuildingPicHotspot hot : hotspots) {
			if (hot.pointInPolygon(x, y)) {
				Toast.makeText(
						getActivity(),
						UIUtils.isLocalePortuguese() ? hot.getNamePt() : hot
								.getNameEn(), Toast.LENGTH_SHORT).show();
				break;
			}
		}
		return true;
	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		if (error == ERROR_TYPE.NETWORK)
			showRepeatTaskScreen(getString(R.string.toast_server_error));
		else
			showEmptyScreen(getString(R.string.general_error));
	}

	@Override
	public void onResultReceived(List<BuildingPicHotspot> results) {
		hotspots = results;
		SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref,
				false);

		if (!welcomeScreenShown) {
			Toast.makeText(getActivity(), R.string.toast_first_map_view,
					Toast.LENGTH_LONG).show();
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean(welcomeScreenShownPref, true);
			editor.commit();
		}
		showMainScreen();
	}

}