package pt.up.fe.mobile.ui.facilities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.FacilitiesUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.utils.TouchImageView;

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
		Toast.makeText(getActivity(), getString(R.string.toast_server_error),
				Toast.LENGTH_SHORT).show();
		getActivity().finish();
	}

	@Override
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		pic.setImageBitmap((Bitmap) results[0]);
		pic.setMaxZoom(5);
		showFastMainScreen();
	}
}