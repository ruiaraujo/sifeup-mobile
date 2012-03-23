package pt.up.beta.mobile.ui.personalarea;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Activity of module exams and launch {@link ExamsFragment}
 *
 * @author Ângela Igreja
 *
 */
public class ExamsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new ExamsFragment();
    }

}

