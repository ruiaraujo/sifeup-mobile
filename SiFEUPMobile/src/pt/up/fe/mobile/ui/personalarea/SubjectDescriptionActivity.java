package pt.up.fe.mobile.ui.personalarea;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Subject Description Activity
 * 
 * @author Ângela Igreja
 *
 */
public class SubjectDescriptionActivity extends BaseSinglePaneActivity {
	private SubjectDescriptionFragment fragment;
	
	@Override
	protected Fragment onCreatePane() {
		fragment = new SubjectDescriptionFragment();
		return fragment;
	}

	
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
        	onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void onBackPressed(){
    	if ( fragment != null )
    		fragment.onBackPressed();
    	else
    		super.onBackPressed();
    }
}
