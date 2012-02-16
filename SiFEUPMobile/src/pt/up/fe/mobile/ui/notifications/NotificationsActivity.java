package pt.up.fe.mobile.ui.notifications;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class NotificationsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
        return new NotificationsFragment();
    }
}

