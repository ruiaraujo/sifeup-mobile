package pt.up.beta.mobile.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sendtosamba.FinishedTaskListener;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

public class DownloadTask extends AsyncTask<Void, Integer, Integer> {

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID;

	private final static int OK = 0;
	private final static int ERROR = -1;
	private final static int FILE_ERROR = -2;
	private final static int NO_MEMORY = -3;
	private final static int NO_MEMORY_CARD = -4;

	private static final long MIN_TIME_BETWWEN_UPDATES = 500;

	private File myFile;
	private final String cookie;
	private final Context context;

	private final String url;
	private final String filename;
	private String type;
	private long filesize = 0;
	final FinishedTaskListener listener;

	private NotificationManager mNotificationManager;

	public DownloadTask(final FinishedTaskListener lis, String url,
			String filename, String type, long filesize, String cookie,
			Context c) {
		this.listener = lis;
		this.cookie = cookie;
		this.context = c;
		this.url = url;
		this.filename = filename;
		this.type = type;
		this.filesize = filesize;
		UNIQUE_ID = (int) (R.string.app_name + System.currentTimeMillis());
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@TargetApi(12)
	@Override
	protected void onPostExecute(Integer result) {
		if (listener != null)
			listener.finishedTask();
		switch (result) {
		case OK: {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			if (TextUtils.isEmpty(type))
				type = getMimeType(myFile.getAbsolutePath());
			if (TextUtils.isEmpty(type))
				intent.setData(Uri.fromFile(myFile));
			else
				intent.setDataAndType(Uri.fromFile(myFile), type);
			final NotificationCompat.Builder notBuilder = getSimple(
					context.getString(R.string.app_name),
					context.getString(R.string.msg_download_finished, filename));
			notBuilder.setContentIntent(PendingIntent.getActivity(
					context.getApplicationContext(), 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT));
			mNotificationManager.notify(UNIQUE_ID, notBuilder.build());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
				DownloadManager manager = (DownloadManager) context
						.getSystemService(Context.DOWNLOAD_SERVICE);
				if (!TextUtils.isEmpty(type))
					manager.addCompletedDownload(filename, url, true, type,
							myFile.getAbsolutePath(), filesize, false);
			}
			break;
		}
		case ERROR:
			mNotificationManager
					.notify(UNIQUE_ID,
							getSimple(
									context.getString(R.string.notification_error_title),
									context.getString(R.string.toast_download_error))
									.build());
			break;
		case FILE_ERROR:
			mNotificationManager
					.notify(UNIQUE_ID,
							getSimple(
									context.getString(R.string.notification_error_title),
									context.getString(R.string.toast_download_error_file))
									.build());
			break;
		case NO_MEMORY:
			mNotificationManager
					.notify(UNIQUE_ID,
							getSimple(
									context.getString(R.string.notification_error_title),
									context.getString(R.string.toast_download_no_memory))
									.build());
			break;
		case NO_MEMORY_CARD:
			mNotificationManager
					.notify(UNIQUE_ID,
							getSimple(
									context.getString(R.string.notification_error_title),
									context.getString(R.string.toast_download_no_memory_card))
									.build());
			break;

		}
	}

	protected void onProgressUpdate(Integer... progress) {
		// notify the notification manager on the update.
		mNotificationManager.notify(UNIQUE_ID,
				updateProgressBar(progress[0], filesize == 0));
	}

	@TargetApi(8)
	@Override
	protected Integer doInBackground(Void... voi) {
		InputStream dis = null;
		FileOutputStream fos = null;
		long myProgress = 0;
		int byteRead;
		byte[] buf;
		try {
			String state = Environment.getExternalStorageState();
			if (!Environment.MEDIA_MOUNTED.equals(state)) {
				return NO_MEMORY_CARD;
			}

			// HTTP connection reuse which was buggy pre-froyo
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
				System.setProperty("http.keepAlive", "false");
			}

			final URLConnection connection;
			if (url.startsWith(SifeupAPI.SIGARRA)) {
				SifeupAPI.initSSLContext(context);
				connection = SifeupAPI.get(url);
			} else
				connection = new URL(url).openConnection();

			connection.setRequestProperty("connection", "close");
			if (cookie != null)
				connection.setRequestProperty("Cookie", cookie);

			dis = connection.getInputStream();
			if (filesize == 0)
				filesize = connection.getContentLength();
			if (filesize < 0)
				filesize = 0;
			// Checking if external storage has enough memory ...
			android.os.StatFs stat = new android.os.StatFs(Environment
					.getExternalStorageDirectory().getPath());
			if ((long) stat.getBlockSize() * (long) stat.getAvailableBlocks() < filesize)
				return NO_MEMORY;

			mNotificationManager.notify(
					UNIQUE_ID,
					createProgressBar(context.getString(
							R.string.msg_downloading, filename), "", 0,
							filesize == 0));

			final File dir;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				dir = new File(Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
			else
				dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + File.separator + "Download");
			dir.mkdirs();
			myFile = new File(dir, filename);
			if (myFile.exists()) {
				myFile = getNonExistantFile(dir, filename);
			}
			fos = new FileOutputStream(myFile);

			long lastNotificationTime = System.currentTimeMillis();
			buf = new byte[65536];
			while (true) {
				try {

					if ((byteRead = dis.read(buf)) != -1) {
						fos.write(buf, 0, byteRead);
						myProgress += byteRead;
					} else {
						dis.close();
						fos.close();
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					dis.close();
					fos.close();
					return null;
				}
				if (filesize != 0) {
					if ((System.currentTimeMillis() - lastNotificationTime) > MIN_TIME_BETWWEN_UPDATES) {
						publishProgress((int) (((float) myProgress / (float) filesize) * 100));
						lastNotificationTime = System.currentTimeMillis();
					}
				}
				if (isCancelled()) {
					dis.close();
					fos.close();
					myFile.delete();
					return null;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (myFile != null)
				myFile.delete();
			return FILE_ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			if (myFile != null)
				myFile.delete();
			return ERROR;
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (filesize == 0)
			filesize = myProgress;
		if (isCancelled() && myFile != null)
			myFile.delete();
		return OK;
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
		if (filesize == 0)
			indeterminate = true;
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

	private static File getNonExistantFile(File baseDir, String filename) {
		if (baseDir == null || filename == null)
			return null;
		File nonExistant;
		int finalDot = filename.lastIndexOf("."), tries = 1;
		if (finalDot < 0)
			finalDot = filename.length();
		do {
			nonExistant = new File(baseDir, filename.substring(0, finalDot)
					+ "(" + tries + ")" + filename.substring(finalDot));
			tries++;
		} while (nonExistant.exists());
		return nonExistant;
	}

	public static String getMimeType(String path) {
		String type = null;
		String extension = path.substring(path.lastIndexOf(".") + 1);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}
}
