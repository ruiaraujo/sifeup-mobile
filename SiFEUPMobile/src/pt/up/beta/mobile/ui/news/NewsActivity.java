package pt.up.beta.mobile.ui.news;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class NewsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new NewsFragment();
    }
}

