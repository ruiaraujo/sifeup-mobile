package pt.up.beta.mobile.sendtosamba;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jibble.simpleftp.SimpleFTP;
import org.jibble.simpleftp.SimpleFTP.WrongCredentials;

import pt.up.beta.mobile.R;
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

public class UploaderTask extends AsyncTask<String, Integer, Boolean> {

	private final InputStreamManaged is;
	private final String filename;
	private final Context context;
	private final FinishedTaskListener listener;

	private int error = 0;
	private final static int WRONG_CREDENTIAL = 1;
	private final static int WRONG_HOST = 2;
	private final static int GENERAL_ERROR = 3;

	private NotificationManager mNotificationManager;
	private static final long MIN_TIME_BETWWEN_UPDATES = 500;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID;

	private final String folder;

	private final static String SERVER = "tom.fe.up.pt";

	public UploaderTask(final FinishedTaskListener lis, final Context con,
			final InputStreamManaged is, final String filename) {
		this.is = is;
		this.filename = filename;
		this.context = con;
		this.listener = lis;
		folder = "FEUPMobile";
		UNIQUE_ID = (int) (R.string.app_name + System.currentTimeMillis());
		mNotificationManager = (NotificationManager) con
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	protected void onPreExecute() {
		mNotificationManager.notify(
				UNIQUE_ID,
				createProgressBar(context.getString(
						R.string.notification_uploader_content, filename), "",
						0, is.getLength() == 0));
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
			ftp.mkd(folder);
			ftp.cwd(folder);
			is.setOnPercentageChangedListener(new ManagedOnPercentageChangedListener() {
				long lastNotificationTime = System.currentTimeMillis();

				public void onChanged(int nperc) {
					if ((System.currentTimeMillis() - lastNotificationTime) > MIN_TIME_BETWWEN_UPDATES) {
						publishProgress(nperc);
						lastNotificationTime = System.currentTimeMillis();
					}
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
			error = GENERAL_ERROR;
			return false;
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

	protected void onProgressUpdate(Integer... progress) {
		// update the notification object
		if (is.getLength() != 0)
			mNotificationManager.notify(UNIQUE_ID,
					updateProgressBar(progress[0], false));
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			mNotificationManager
					.notify(UNIQUE_ID,
							getSimple(
									context.getString(R.string.notification_uploader_title),
									context.getString(
											R.string.notification_uploader_finished,
											filename)).build());
		} else {
			switch (error) {
			case WRONG_CREDENTIAL:
				mNotificationManager
						.notify(UNIQUE_ID,
								getSimple(
										context.getString(R.string.notification_uploader_error_credential_title),
										context.getString(R.string.notification_uploader_error_credential))
										.build());
				break;
			case WRONG_HOST:
				mNotificationManager
						.notify(UNIQUE_ID,
								getSimple(
										context.getString(R.string.notification_uploader_error_host_title),
										context.getString(R.string.notification_uploader_error_host))
										.build());
				break;
			default:
				mNotificationManager
						.notify(UNIQUE_ID,
								getSimple(
										context.getString(R.string.notification_error_title),
										context.getString(
												R.string.notification_uploader_error,
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			builder.setProgress(100, progress, indeterminate)
					.setContentTitle(title).setContentText(content);
		} else {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.notification_upload);
			contentView.setTextViewText(android.R.id.text1, content);
			contentView.setProgressBar(android.R.id.progress, 100, progress,
					indeterminate);
			builder.setContent(contentView);
		}
		builder.setTicker(title);
		builder.setOngoing(true);
		update = builder.build();
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