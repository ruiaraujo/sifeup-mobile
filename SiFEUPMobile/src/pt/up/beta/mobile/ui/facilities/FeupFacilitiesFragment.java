package pt.up.beta.mobile.ui.facilities;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.ui.utils.TouchImageView.ClickListener;
import pt.up.beta.mobile.R;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesFragment extends BaseFragment implements
		ResponseCommand, ClickListener {

	private ImageView pic;
	private List<BuildingPicHotspot> hotspots;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_facility_pic, getParentContainer(), true);
		pic = (ImageView) root.findViewById(R.id.facility_image);
		return getParentContainer();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		task = FacilitiesUtils.getBuildingsHotspot(getResources().openRawResource(R.raw.buildings_coordinates), this);
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		if ( error == ERROR_TYPE.NETWORK )
			showRepeatTaskScreen(getString(R.string.toast_server_error));
		else
			showEmptyScreen(getString(R.string.general_error));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if ( hotspots == null )
		{
			hotspots =  (List<BuildingPicHotspot>) results[0];
			task = FacilitiesUtils.getBuildingPic("", "", "", this);
			return;
		}
		pic.setImageBitmap((Bitmap) results[0]);
		if ( pic instanceof TouchImageView )// Android 2.1 doesn't like our TouchImageView
		{
			((TouchImageView) pic).setMaxZoom(5);
			((TouchImageView) pic).setOnClickListener(this);
		}
		showFastMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		if ( hotspots == null )
			task = FacilitiesUtils.getBuildingsHotspot(getResources().openRawResource(R.raw.buildings_coordinates), this);
		else
			task = FacilitiesUtils.getBuildingPic("","", "", this);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if ( hotspots == null )
			return false;
		final int x = Math.round(e.getX());
		final int y = Math.round(e.getY());
		for ( BuildingPicHotspot hot : hotspots )
		{
			if ( hot.pointInPolygon(x, y)  ){
				Toast.makeText(getActivity(), "Build:" + hot.getNamePt(), Toast.LENGTH_SHORT).show();
				break;
			}
		}
		return true;
	}
}