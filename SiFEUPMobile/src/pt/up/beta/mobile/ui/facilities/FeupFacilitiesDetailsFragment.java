package pt.up.beta.mobile.ui.facilities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;

import external.com.google.android.apps.iosched.util.UIUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.utils.BuildingPicHotspot;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.R;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesDetailsFragment extends BaseFragment implements
		ResponseCommand, OnNavigationListener {
	public final static String BUILDING_EXTRA = "pt.up.beta.mobile.ui.facilities.BUILDING";

	private ImageView pic;
	private BuildingPicHotspot building;
	private int currentFloor = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		building = getArguments().getParcelable(BUILDING_EXTRA);

		// Special case for the garage
		currentFloor = building.getFloors()[0];

		final String name;
		if (UIUtils.isLocalePortuguese())
			name = building.getNamePt();
		else
			name = building.getNameEn();
		getSherlockActivity().getSupportActionBar().setTitle(name);
		final String [] floors = new String[building.getFloors().length];
		int i = 0;
        for ( int floor : building.getFloors() )
        	floors[i++] = getString(R.string.floor_spinner,floor);

        Context context = getSherlockActivity().getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_item, floors);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(list, this);
		
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
		task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
				building.getBuildingBlock(), Integer.toString(currentFloor),
				this);

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
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		pic.setImageBitmap((Bitmap) results[0]);
		if (pic instanceof TouchImageView)// Android 2.1 doesn't like our
											// TouchImageView
		{
			((TouchImageView) pic).setMaxZoom(6);
		}
		showFastMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
				building.getBuildingBlock(), Integer.toString(currentFloor),
				this);
	}   


    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
    	int newFloor = building.getFloors()[itemPosition];
		if ( currentFloor == newFloor )
			return true; 
		currentFloor = newFloor;
    	showLoadingScreen();
		task = FacilitiesUtils.getBuildingPic(building.getBuildingCode(),
				building.getBuildingBlock(), Integer.toString(currentFloor),
				this);
        return true;
    }
}