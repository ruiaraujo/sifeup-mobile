
package pt.up.fe.mobile.ui.news;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class NewsDescActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new NewsDescFragment();
    }


}
