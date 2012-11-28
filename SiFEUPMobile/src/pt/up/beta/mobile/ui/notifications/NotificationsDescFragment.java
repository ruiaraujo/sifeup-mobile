package pt.up.beta.mobile.ui.notifications;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.Notification;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.webclient.WebviewActivity;
import pt.up.beta.mobile.ui.webclient.WebviewFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * This interface is responsible for displaying information detailed of a
 * notification.
 * 
 * @author Ângela Igreja
 * 
 */
public class NotificationsDescFragment extends Fragment {

	public final static String NOTIFICATION = "pt.up.fe.mobile.ui.notifications.NOTIFICATION";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.notifications_item, null);

		Bundle b = getArguments();
		if (b == null)
			throw new IllegalStateException(
					"Should have an notification arguments.");
		final Notification n = (Notification) b.getParcelable(NOTIFICATION);

		final NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(n.getCode().hashCode());
		((TextView) root.findViewById(R.id.notification_subject)).setText(" "
				+ n.getSubject());
		if (n.getDescription().trim().length() == 0) {
			root.findViewById(R.id.notification_description_group)
					.setVisibility(View.GONE);
		} else {
			((TextView) root.findViewById(R.id.notification_description))
					.setText(" " + n.getDescription());
		}
		((TextView) root.findViewById(R.id.notification_date)).setText(" "
				+ n.getDate());
		((TextView) root.findViewById(R.id.notification_priority)).setText(" "
				+ n.getPriority());

		((TextView) root.findViewById(R.id.notification_designation))
				.setText(" " + n.getDesignation());
		((TextView) root.findViewById(R.id.notification_message)).setText(Html
				.fromHtml(" " + n.getMessage()));
		if (!TextUtils.isEmpty(n.getLink()))
			((TextView) root.findViewById(R.id.notification_link)).setText(Html
					.fromHtml(" <a>" + SifeupAPI.getSigarraUrl() + n.getLink()
							+ "</a>"));
		else
			root.findViewById(R.id.notification_link_group).setVisibility(
					View.GONE);

		((Button) root.findViewById(R.id.notification_reply))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity(),
								WebviewActivity.class);
						i.putExtra(WebviewFragment.URL_INTENT, SifeupAPI
								.getNotificationsSigarraUrl(n.getCode()));
						startActivity(i);
					}
				});
		return root;
	}

}