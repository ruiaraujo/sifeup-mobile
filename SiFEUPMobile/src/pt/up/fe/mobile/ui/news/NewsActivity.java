package pt.up.fe.mobile.ui.news;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class NewsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new NewsFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  

}

