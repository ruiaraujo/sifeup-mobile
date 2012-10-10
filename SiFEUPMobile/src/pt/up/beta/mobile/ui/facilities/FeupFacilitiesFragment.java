package pt.up.beta.mobile.ui.facilities;

import java.util.List;

import external.com.google.android.apps.iosched.util.UIUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ZoomControls;

import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseLoadingFragment;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.ui.utils.TouchImageView.OnTapListener;
import pt.up.beta.mobile.R;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesFragment extends BaseLoadingFragment implements
		ResponseCommand<Bitmap>, OnTapListener {

	private TouchImageView pic;
	private List<BuildingPicHotspot> hotspots;
	private HotspotBuilder hotspotBuilder = new HotspotBuilder();;
	
	static final String welcomeScreenShownPref = "welcomeScreenShown";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_facility_pic, getParentContainer(), true);
		pic = (TouchImageView) root.findViewById(R.id.facility_image);
		final ZoomControls zoom =  (ZoomControls) root.findViewById(R.id.zoomControls);
		if ( zoom != null) { //it will be null for Android 3.0+
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
		}
		return getParentContainer();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		task = FacilitiesUtils.getBuildingsHotspot(getResources()
				.openRawResource(R.raw.buildings_coordinates), hotspotBuilder);
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		if (error == ERROR_TYPE.NETWORK)
			showRepeatTaskScreen(getString(R.string.toast_server_error));
		else
			showEmptyScreen(getString(R.string.general_error));
	}

	@Override
	public void onResultReceived(Bitmap results) {
		if (getActivity() == null)
			return;
		pic.setImageBitmap( results);
		pic.setMaxZoom(6);
		pic.setOnTapListener(this);
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Boolean welcomeScreenShown = mPrefs.getBoolean( welcomeScreenShownPref, false);

		if (!welcomeScreenShown) {
			Toast.makeText(getActivity(),
					R.string.toast_first_map_view, Toast.LENGTH_LONG)
					.show();
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean(welcomeScreenShownPref, true);
			editor.commit();
		}
		showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		if (hotspots == null)
			task = FacilitiesUtils.getBuildingsHotspot(getResources()
					.openRawResource(R.raw.buildings_coordinates), hotspotBuilder);
		else
			task = FacilitiesUtils.getBuildingPic("", "", "", this, getActivity());
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
	
	private class HotspotBuilder implements ResponseCommand<List<BuildingPicHotspot>>{

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
			task = FacilitiesUtils.getBuildingPic("", "", "", FeupFacilitiesFragment.this, getActivity());			
		}
		
	}
}