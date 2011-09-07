package pt.up.fe.mobile.ui.notifications;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class NotificationsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
        return new NotificationsFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  

}

