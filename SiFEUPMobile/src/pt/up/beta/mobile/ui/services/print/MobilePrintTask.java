package pt.up.beta.mobile.ui.services.print;

import javax.mail.AuthenticationFailedException;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sendtosamba.ManagedOnPercentageChangedListener;
import pt.up.beta.mobile.ui.utils.FinishedTaskListener;
import pt.up.beta.mobile.ui.utils.InputStreamManaged;
import pt.up.beta.mobile.utils.LogUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class MobilePrintTask extends AsyncTask<String, Integer, Boolean> {

	private final InputStreamManaged is;
	private final String filename;
	private final Context context;
	private final FinishedTaskListener listener;

	private int error = 0;
	private final static int WRONG_CREDENTIAL = 1;
	private final static int GENERAL_ERROR = 2;

	private NotificationManager mNotificationManager;
	private static final long MIN_TIME_BETWWEN_UPDATES = 500;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID;

	private final static String EMAIL = "mobile.print@fe.up.pt";

	public MobilePrintTask(final FinishedTaskListener lis, final Context con,
			final InputStreamManaged is, final String filename) {
		this.is = is;
		this.filename = filename;
		this.context = con;
		this.listener = lis;
		UNIQUE_ID = (int) (R.string.app_name + System.currentTimeMillis());
		mNotificationManager = (NotificationManager) con
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	protected void onPreExecute() {
		mNotificationManager.notify(
				UNIQUE_ID,
				createProgressBar(context.getString(
						R.string.mobile_print_sending, filename), "",
						0, is.getLength() == 0));
	}

	@Override
	protected Boolean doInBackground(String... args) {
		try {
			final String sender = args[0];
			final String password = args[1];
			final Mail m = new Mail(sender, password);
			String[] toArr = { EMAIL };
			m.setTo(toArr);
			m.setFrom(sender);
			m.setSubject(context.getString(R.string.app_name) + " - "
					+ context.getString(R.string.title_mobile_printing));
			m.setBody("");
			is.setOnPercentageChangedListener(new ManagedOnPercentageChangedListener() {
				long lastNotificationTime = System.currentTimeMillis();

				public void onChanged(int nperc) {
					if ((System.currentTimeMillis() - lastNotificationTime) > MIN_TIME_BETWWEN_UPDATES) {
						publishProgress(nperc);
						lastNotificationTime = System.currentTimeMillis();
					}
				}
			});
			m.addAttachment(is, filename);
			return m.send();
		} catch (AuthenticationFailedException e) {
			Log.e("FileSelectorTestActivity", "File select error", e);
			error = WRONG_CREDENTIAL;
		} catch (Exception e) {
			Log.e("FileSelectorTestActivity", "File select error", e);
			LogUtils.trackException(context, e, null, false);
			error = GENERAL_ERROR;
		}
		return false;
	}

	protected void onProgressUpdate(Integer... progress) {
		// update the notification object
		if (is.getLength() != 0)
			mNotificationManager.notify(UNIQUE_ID,
					updateProgressBar(progress[0], false));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(
							context.getString(R.string.app_name),
							context.getString(R.string.mobile_print_success,
									filename)).build());
		} else {
			switch (error) {
			case WRONG_CREDENTIAL:
				mNotificationManager
						.notify(UNIQUE_ID,
								getSimple(
										context.getString(R.string.error_credential_title),
										context.getString(R.string.error_credential))
										.build());
				break;
			default:
				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(
								context.getString(R.string.error_title),
								context.getString(R.string.mobile_print_error,
										filename)).build());
				break;
			}
		}
		listener.finishedTask();

	}

	private NotificationCompat.Builder getSimple(CharSequence title,
			CharSequence content) {
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(
				context);
		notBuilder.setSmallIcon(R.drawable.icon).setTicker(content)
				.setContentTitle(title).setContentText(content)
				.setContentIntent(getPendingIntent());
		return notBuilder;
	}

	private Notification update;

	private Notification createProgressBar(CharSequence title,
			CharSequence content, int progress, boolean indeterminate) {
		final NotificationCompat.Builder builder = getSimple(title, content);
		builder.setProgress(100, progress, indeterminate)
				.setContentTitle(title).setContentText(content);
		builder.setTicker(title);
		builder.setOngoing(true);
		update = builder.build();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.notification_upload);
			contentView.setTextViewText(android.R.id.text1, content);
			contentView.setProgressBar(android.R.id.progress, 100, progress,
					indeterminate);
			update.contentView = contentView;
		}
		return update;
	}

	private Notification updateProgressBar(int progress, boolean indeterminate) {
		update.contentView.setProgressBar(android.R.id.progress, 100, progress,
				indeterminate);
		return update;
	}

	private PendingIntent getPendingIntent() {
		return PendingIntent.getActivity(context.getApplicationContext(), 0,
				new Intent(), // add this
				// pass null
				// to intent
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}