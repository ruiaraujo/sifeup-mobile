package pt.up.mobile.ui.profile;

import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Profile Activity
 * 
 * @author Ângela Igreja
 */
public class ProfileActivity extends BaseSinglePaneActivity {

	public final static String PROFILE_TYPE = "pt.up.fe.mobile.ui.profile.PROFILE_TYPE";
	public final static String PROFILE_CODE = "pt.up.fe.mobile.ui.profile.PROFILE_CODE";

	public final static String PROFILE_STUDENT = "pt.up.fe.mobile.ui.profile.STUDENT";
	public final static String PROFILE_EMPLOYEE = "pt.up.fe.mobile.ui.profile.EMPLOYEE";
	public final static String PROFILE_ROOM = "pt.up.fe.mobile.ui.profile.ROOM";

	@Override
	protected Fragment onCreatePane() {
		Intent i = getIntent();
		String type = i.getStringExtra(PROFILE_TYPE);

		if (type.equals(PROFILE_STUDENT))
			return new StudentProfileFragment();
		if (type.equals(PROFILE_EMPLOYEE))
			return new EmployeeProfileFragment();
		if (type.equals(PROFILE_ROOM))
			return new RoomProfileFragment();
		throw new RuntimeException("unknown type, caller: "
				+ getIntent().getStringExtra("caller"));

	}

}
