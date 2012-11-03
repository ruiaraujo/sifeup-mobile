package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class ServicesActivity extends BaseSinglePaneActivity {

	@Override
	protected Fragment onCreatePane() {
		return new ServicesFragment();
	}

}
