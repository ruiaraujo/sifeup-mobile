package pt.up.beta.mobile.ui.search;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Advanced Search Activity
 * 
 * @author Ângela Igreja
 */
public class AdvanceSearchActivity extends BaseSinglePaneActivity {

	public final static String SEARCH_TYPE = "pt.up.fe.mobile.ui.search.SEARCH_TYPE";

	public final static String STUDENT = "pt.up.fe.mobile.ui.search.STUDENT";
	public final static String EMPLOYEE = "pt.up.fe.mobile.ui.search.EMPLOYEE";
	public final static String ROOM = "pt.up.fe.mobile.ui.search.ROOM";

	@Override
	protected Fragment onCreatePane() {
		Intent i = getIntent();
		String type = i.getStringExtra(SEARCH_TYPE);

		if (type.equals(STUDENT))
			return new StudentsAdvanceSearchFragment();
		if (type.equals(EMPLOYEE))
			return new EmployeesAdvanceSearchFragment();
		if (type.equals(ROOM)) // TODO: add room advanced search and

			// so tem um parametro foi por isso que nao adicionei
			return new StudentsAdvanceSearchFragment();
		return new Fragment();

	}

}
