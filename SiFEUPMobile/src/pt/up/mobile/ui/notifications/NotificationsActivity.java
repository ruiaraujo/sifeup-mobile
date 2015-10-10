package pt.up.mobile.ui.notifications;

import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class NotificationsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
        return new NotificationsFragment();
    }
}

