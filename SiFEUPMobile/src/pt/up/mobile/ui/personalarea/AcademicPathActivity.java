package pt.up.mobile.ui.personalarea;

import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Academic Path Activity and launch the object {@link AcademicPathFragment}.
 * 
 * @author Ângela Igreja
 *
 */
public class AcademicPathActivity extends BaseSinglePaneActivity {

	@Override
	protected Fragment onCreatePane() {
		return new AcademicPathFragment();
	}

}
