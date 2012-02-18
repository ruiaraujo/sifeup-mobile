
package pt.up.fe.mobile.ui.notifications;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class NotificationsDescActivity extends BaseSinglePaneActivity {
	
	public final static String NOTIFICATION = "pt.up.fe.mobile.ui.notifications.NOTIFICATION";
	//public final static String NOTIFICATION_SUBJECT = "pt.up.fe.mobile.ui.notifications.NOTIFICATION_SUBJECT"; 
	//public final static String NOTIFICATION_MESSAGE = "pt.up.fe.mobile.ui.notifications.NOTIFICATION_MESSAGE"; 
	//public final static String NOTIFICATION_LINK = "pt.up.fe.mobile.ui.notifications.NOTIFICATION_LINK"; 
	
	@Override
    protected Fragment onCreatePane() {
        return new NotificationsDescFragment();
    }
}
