

package pt.up.fe.mobile.ui.notifications;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Notification;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.tracker.AnalyticsUtils;

/**
* This interface is responsible for displaying information 
* detailed of a notification.
* 
* @author Ângela Igreja
* 
*/
public class NotificationsDescFragment extends Fragment {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Notifications");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.notifications_item, null);
    	        
    	Bundle b = getArguments();
    	if ( b == null )
    		throw new IllegalStateException("Should have an notification arguments.");
    	final Notification n = (Notification) b.getSerializable(NotificationsDescActivity.NOTIFICATION);
	   ((TextView) root.findViewById(R.id.notification_subject)).setText(" "+n.getSubject());
	   if( n.getDescription().trim().length() == 0 )
	   {
		   root.findViewById(R.id.notification_description_group).setVisibility(View.GONE);
	   }
	   else
	   {
		   ((TextView) root.findViewById(R.id.notification_description)).setText(" "+n.getDescription());
	   }
	   ((TextView) root.findViewById(R.id.notification_date)).setText(" "+n.getDate());
	   ((TextView) root.findViewById(R.id.notification_priority)).setText(" "+n.getPriorityString());
	  
	   ((TextView) root.findViewById(R.id.notification_designation)).setText(" "+n.getDesignation());
	   ((TextView) root.findViewById(R.id.notification_message)).setText(" "+n.getMessage());
	   ((TextView) root.findViewById(R.id.notification_link)).setText(" "+n.getLink());
	   ((TextView) root.findViewById(R.id.notification_reply)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = "https://www.fe.up.pt/si/wf_geral.not_form_view?pv_not_id="+n.getCode();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});    
    	return root;
    }
   
}