package pt.up.fe.mobile.sendtosamba;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jibble.simpleftp.SimpleFTP;
import org.jibble.simpleftp.SimpleFTP.WrongCredentials;

import pt.up.fe.mobile.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import external.com.google.android.apps.iosched.util.UIUtils;

public class UploaderTask extends AsyncTask<String, Integer, Boolean> {

	private final InputStreamManaged is;
	private final String filename;
	private Notification notification;
	private final Context context;
	private final FinishedTaskListener listener;

	private int error = 0;
	private final static int WRONG_CREDENTIAL = 1;
	private final static int WRONG_HOST = 2;

	private NotificationManager mNotificationManager;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID;

	private final static String SERVER = "tom.fe.up.pt";

	public UploaderTask(final FinishedTaskListener lis, final Context con,
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
		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_upload);
		if ( is.getLength() == 0 )
		    contentView.setProgressBar(R.id.progressBar, 0, 0, true);
		else
		    contentView.setProgressBar(R.id.progressBar, 100, 0, false);
		contentView.setTextViewText(R.id.text, context.getString(
				R.string.notification_uploader_content, filename));

		if (UIUtils.isHoneycomb()) {
			// Build the notification
			Notification.Builder notBuilder = new Notification.Builder(context);
			notBuilder.setOngoing(true);
			notBuilder.setContentTitle(context
					.getString(R.string.notification_uploader_title));
			notBuilder.setSmallIcon(R.drawable.ic_launcher);
			notBuilder.setContent(contentView);
			notification = notBuilder.getNotification();
		} else {
			notification = new Notification(R.drawable.ic_launcher, context
					.getString(R.string.notification_uploader_title), System
					.currentTimeMillis());
			notification.contentView = contentView;

			notification.flags |= Notification.FLAG_NO_CLEAR
					| Notification.FLAG_ONGOING_EVENT;
		}

		mNotificationManager.notify(UNIQUE_ID, notification);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		Log.i("UploadingService", "File " + filename);
		final String username = params[0];
		final String password = params[1];
		SimpleFTP ftp = null;
		try {
			ftp = new SimpleFTP();

			// Connect to an FTP server on port 21.
			ftp.connect(SERVER, 21, username, password);

			// Set binary mode.
			ftp.bin();
			// Change to a new working directory on the FTP server.
			ftp.mkd("SendToSamba");
			ftp.cwd("SendToSamba");
			is
					.setOnPercentageChangedListener(new ManagedOnPercentageChangedListener() {

						public void onChanged(int nperc) {
							publishProgress(nperc);
							System.out.println("% = " + nperc);
						}
					});
			// You can also upload from an InputStream, e.g.
			ftp.stor(is, filename);
			is.close();

		} catch (UnknownHostException e) {
			error = WRONG_HOST;
			return false;
		} catch (WrongCredentials e) {
			error = WRONG_CREDENTIAL;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ftp != null) {
				try {
					// Quit from the FTP server.
					ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	protected void onProgressUpdate(Integer... values) {
		// update the notification object
	    if ( is.getLength() != 0 )
	        notification.contentView.setProgressBar(R.id.progressBar, 100,
				values[0], false);
		// notify the notification manager on the update.
		mNotificationManager.notify(UNIQUE_ID, notification);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mNotificationManager.cancel(UNIQUE_ID);

		if (UIUtils.isHoneycomb()) {
			// Build the notification
			Notification.Builder notBuilder = new Notification.Builder(context);
			notBuilder.setOngoing(false);

			if (result) {
				notBuilder.setContentTitle(context
						.getString(R.string.notification_uploader_title));
				notBuilder.setContentText(context.getString(
						R.string.notification_uploader_finished, filename));
			} else {
				switch (error) {
				case WRONG_CREDENTIAL:
					notBuilder
							.setContentTitle(context
									.getString(R.string.notification_uploader_error_credential_title));
					notBuilder
							.setContentText(context
									.getString(R.string.notification_uploader_error_credential));
					break;
				case WRONG_HOST:
					notBuilder
							.setContentTitle(context
									.getString(R.string.notification_uploader_error_host_title));
					notBuilder
							.setContentText(context
									.getString(R.string.notification_uploader_error_host));
					break;
				default:
					notBuilder
							.setContentTitle(context
									.getString(R.string.notification_uploader_error_title));
					notBuilder.setContentText(context.getString(
							R.string.notification_uploader_error, filename));
					break;
				}
			}
			notBuilder.setSmallIcon(R.drawable.ic_launcher);
			mNotificationManager
					.notify(UNIQUE_ID, notBuilder.getNotification());
		} else {
			Notification notification = new Notification(
					R.drawable.ic_launcher, context
							.getString(R.string.notification_uploader_title),
					System.currentTimeMillis());
			if (result)
				notification.setLatestEventInfo(context, context
						.getString(R.string.notification_uploader_title),
						context.getString(
								R.string.notification_uploader_finished,
								filename), null);
			else {
				switch (error) {
				case WRONG_CREDENTIAL:
					notification
							.setLatestEventInfo(
									context,
									context
											.getString(R.string.notification_uploader_error_credential_title),
									context
											.getString(R.string.notification_uploader_error_credential),
									null);
					break;
				case WRONG_HOST:
					notification
							.setLatestEventInfo(
									context,
									context
											.getString(R.string.notification_uploader_error_host_title),
									context
											.getString(R.string.notification_uploader_error_host),
									null);
					break;
				default:
					notification
							.setLatestEventInfo(
									context,
									context
											.getString(R.string.notification_uploader_error_title),
									context
											.getString(
													R.string.notification_uploader_error,
													filename), null);
					break;
				}
			}
			mNotificationManager.notify(UNIQUE_ID, notification);
		}
		listener.finishedTask();

	}

}