package pt.up.beta.mobile.ui.facilities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.utils.TouchImageView;
import pt.up.beta.mobile.R;

/**
 * This interface is responsible for displaying information detailed of a
 * report. Contains a link that allows see a full story in browser.
 * 
 * @author Ângela Igreja
 * 
 */
public class FeupFacilitiesFragment extends BaseFragment implements
		ResponseCommand {

	private TouchImageView pic;

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
		pic = (TouchImageView) root.findViewById(R.id.facility_image);
		return getParentContainer();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		task = FacilitiesUtils.getBuildingPic("", "", this);
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		showRepeatTaskScreen(getString(R.string.toast_server_error));
	}

	@Override
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		pic.setImageBitmap((Bitmap) results[0]);
		pic.setMaxZoom(5);
		showFastMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = FacilitiesUtils.getBuildingPic("", "", this);
	}
}