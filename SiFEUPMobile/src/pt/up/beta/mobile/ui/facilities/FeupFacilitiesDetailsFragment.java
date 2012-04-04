package pt.up.beta.mobile.ui.facilities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;

import external.com.google.android.apps.iosched.util.UIUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ZoomControls;

import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
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
public class FeupFacilitiesDetailsFragment extends BaseFragment implements
		ResponseCommand, OnNavigationListener, OnTapListener {
	public final static String BUILDING_EXTRA = "pt.up.beta.mobile.ui.facilities.BUILDING";
	public final static String ROOM_EXTRA = "pt.up.beta.mobile.ui.facilities.ROOM";

	private TouchImageView pic;
	private BuildingPicHotspot building;
	private String room;
	private int currentFloor = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		room = getArguments().getString(ROOM_EXTRA);
		if (room == null) {
			building = getArguments().getParcelable(BUILDING_EXTRA);
			setUpNavigation();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_facility_pic, getParentContainer(), true);
		pic = (TouchImageView) root.findViewById(R.id.facility_image);
		if (pic.needsExternalZoom()) {
			ZoomControls zoom = (ZoomControls) root
					.findViewById(R.id.zoomControls);
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

	private void setUpNavigation() {
		int selectedPos = 0;
		if (room == null)
			currentFloor = building.getFloors()[0];
		else
			currentFloor = Integer.parseInt(room.charAt(1) == '-' ? room
					.substring(1, 3) : room.substring(1, 2));
		final String name;
		if (UIUtils.isLocalePortuguese())
			name = building.getNamePt();
		else
			name = building.getNameEn();
		getSherlockActivity().getSupportActionBar().setTitle(name);
		final String[] floors = new String[building.getFloors().length];
		int i = 0;
		for (int floor : building.getFloors())
		{
			if ( floor == currentFloor )
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
		getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(selectedPos);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (room != null)
			task = FacilitiesUtils.getBuildingHotspot(getResources()
					.openRawResource(R.raw.buildings_coordinates), room
					.substring(0, 1), this);
		else
			task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
					building.getBuildingBlock(),
					Integer.toString(currentFloor), this);

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
				showFastMainScreen();
			}
		} else
			showEmptyScreen(getString(R.string.general_error));
	}

	@Override
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (results[0] instanceof String) {
			Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_ROOM);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, results[0].toString());
			i.putExtra(Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg, results[0]));
			Toast.makeText(getActivity(), results[0].toString(),
					Toast.LENGTH_SHORT).show();
			startActivity(i);
			showFastMainScreen();
			return;
		}
		if (building == null) {
			building = (BuildingPicHotspot) results[0];
			setUpNavigation();
			task = FacilitiesUtils.getRoomPic(room.substring(0, 1),
					room.substring(1), this);
			return;
		}
		pic.setImageBitmap((Bitmap) results[0]);
		pic.setMaxZoom(6);
		pic.setOnTapListener(this);
		showFastMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		if (room == null)
			task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
					building.getBuildingBlock(),
					Integer.toString(currentFloor), this);
		else
			task = FacilitiesUtils.getRoomPic(room.substring(0, 1),
					room.substring(1), this);

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		int newFloor = building.getFloors()[itemPosition];
		if (currentFloor == newFloor)
			return true;
		currentFloor = newFloor;
		showLoadingScreen();
		task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
				building.getBuildingBlock(), Integer.toString(currentFloor),
				this);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		final int x = Math.round(e.getX());
		final int y = Math.round(e.getY());
		showLoadingScreen();
		FacilitiesUtils.getRoomCode(building.getBuildingCode(),
				building.getBuildingBlock(), Integer.toString(currentFloor), x,
				y, this);
		return true;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}
}