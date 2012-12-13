package pt.up.beta.mobile.ui.facilities;

import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.RoomProfile;
import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.ui.utils.TouchImageView.OnTapListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;

import external.com.google.android.apps.iosched.util.UIUtils;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesDetailsFragment extends BaseFragment implements
		ResponseCommand<Bitmap>, OnNavigationListener, OnTapListener {
	public final static String BUILDING_EXTRA = "pt.up.beta.mobile.ui.facilities.BUILDING";
	public final static String ROOM_EXTRA = "pt.up.beta.mobile.ui.facilities.ROOM";

	private TouchImageView pic;
	private BuildingPicHotspot building;
	private RoomProfile room;
	private int currentFloor = 0;
	private RoomDetector roomDetector = new RoomDetector();
	private BuildingFinder buildingFinder = new BuildingFinder();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		room = getArguments().getParcelable(ROOM_EXTRA);
		if (room == null) {
			building = getArguments().getParcelable(BUILDING_EXTRA);
			setUpNavigation();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final ViewGroup root = (ViewGroup) inflateMainScreen(
				R.layout.fragment_facility_pic);
		pic = (TouchImageView) root.findViewById(R.id.facility_image);
		final ZoomControls zoom = (ZoomControls) root
				.findViewById(R.id.zoomControls);
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
		return getParentContainer();
	}

	private void setUpNavigation() {
		int selectedPos = 0;
		if (room == null)
			currentFloor = building.getFloors()[0];
		else
			currentFloor = room.getFloor();
		final String name;
		if (UIUtils.isLocalePortuguese())
			name = building.getNamePt();
		else
			name = building.getNameEn();
		getSherlockActivity().getSupportActionBar().setTitle(name);
		final String[] floors = new String[building.getFloors().length];
		int i = 0;
		for (int floor : building.getFloors()) {
			if (floor == currentFloor)
				selectedPos = i;
			floors[i++] = getString(R.string.floor_spinner, floor);
		}

		Context context = getSherlockActivity().getSupportActionBar()
				.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(
				context, R.layout.sherlock_spinner_item, floors);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSherlockActivity().getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_LIST);
		getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(
				list, this);
		getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(
				selectedPos);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (room != null)
			task = FacilitiesUtils.getBuildingHotspot(getResources()
					.openRawResource(R.raw.buildings_coordinates), room
					.getBuildingId(), buildingFinder);
		else
			task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
					currentFloor, this, getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();
		if (pic != null && pic.getDrawable() != null)
			showMainScreen();
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		if (error == ERROR_TYPE.NETWORK) {
			if (pic.getDrawable() == null)
				showRepeatTaskScreen(getString(R.string.toast_server_error));
			else {
				Toast.makeText(getActivity(), R.string.toast_server_error,
						Toast.LENGTH_SHORT).show();
				showMainScreen();
			}
		} else
			showEmptyScreen(getString(R.string.general_error));
	}

	@Override
	public void onResultReceived(Bitmap result) {
		if (getActivity() == null)
			return;
		pic.setImageBitmap(result);
		pic.setMaxZoom(6);
		pic.setOnTapListener(this);
		showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		if (room == null)
			task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
					currentFloor, this, getActivity());
		else
			task = FacilitiesUtils.getRoomPic(room.getCode(), this,
					getActivity());

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		int newFloor = building.getFloors()[itemPosition];
		if (currentFloor == newFloor)
			return true;
		showLoadingScreen();
		currentFloor = newFloor;
		if (room != null) {
			if (room.getFloor() == currentFloor) {
				task = FacilitiesUtils.getRoomPic(room.getCode(), this,
						getActivity());
				return true;
			}
		}
		task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
				currentFloor, this, getActivity());

		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		final int x = Math.round(e.getX());
		final int y = Math.round(e.getY());
		showLoadingScreen();
		FacilitiesUtils.getRoomCode(building.getBuildingCode(), currentFloor,
				x, y, roomDetector, getActivity());
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	private class RoomDetector implements ResponseCommand<RoomProfile> {

		@Override
		public void onError(ERROR_TYPE error) {
			if (getActivity() == null)
				return;
			if (error == ERROR_TYPE.NETWORK) {
				if (pic.getDrawable() == null)
					showRepeatTaskScreen(getString(R.string.toast_server_error));
				else {
					Toast.makeText(getActivity(), R.string.toast_server_error,
							Toast.LENGTH_SHORT).show();
					showMainScreen();
				}
			} else
				showEmptyScreen(getString(R.string.general_error));
		}

		@Override
		public void onResultReceived(RoomProfile results) {
			Intent i = new Intent(getActivity(), ProfileActivity.class)
					.putExtra(ProfileActivity.PROFILE_TYPE,
							ProfileActivity.PROFILE_ROOM)
					.putExtra(ProfileActivity.PROFILE_CODE, results.getCode())
					.putExtra(Intent.EXTRA_TITLE, results.getFullName());
			Toast.makeText(getActivity(), results.getFullName(),
					Toast.LENGTH_SHORT).show();
			startActivity(i);
			return;
		}

	}

	private class BuildingFinder implements
			ResponseCommand<List<BuildingPicHotspot>> {

		@Override
		public void onError(ERROR_TYPE error) {
			if (getActivity() == null)
				return;
			if (error == ERROR_TYPE.NETWORK) {
				if (pic.getDrawable() == null)
					showRepeatTaskScreen(getString(R.string.toast_server_error));
				else {
					Toast.makeText(getActivity(), R.string.toast_server_error,
							Toast.LENGTH_SHORT).show();
					showMainScreen();
				}
			} else
				showEmptyScreen(getString(R.string.general_error));
		}

		@Override
		public void onResultReceived(List<BuildingPicHotspot> results) {
			if (getActivity() == null)
				return;
			building = results.get(0);
			setUpNavigation();
			task = FacilitiesUtils.getRoomPic(room.getCode(),
					FeupFacilitiesDetailsFragment.this, getActivity());
		}

	}

}