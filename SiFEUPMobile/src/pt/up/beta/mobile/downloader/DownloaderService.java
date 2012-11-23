package pt.up.beta.mobile.downloader;

import pt.up.beta.mobile.sendtosamba.FinishedTaskListener;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DownloaderService extends Service  implements FinishedTaskListener{
	public final static String COOKIE_ARG = "cookie";
	public final static String URL_ARG = "url";
	public final static String NAME_ARG = "name";
	public final static String TYPE_ARG = "type";
	public final static String SIZE_ARG = "size";
	private int taskRunning = 0;
	
	public static Intent newDownload(Context c, String url,
			String name, String type, long size, String cookie) {
		final Intent i = new Intent(c, DownloaderService.class);
		i.putExtra(URL_ARG, url);
		i.putExtra(NAME_ARG, name);
		i.putExtra(TYPE_ARG, type);
		i.putExtra(SIZE_ARG, size);
		i.putExtra(COOKIE_ARG, cookie);
		return i;
	}


	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.i("UploaderService", "Received start id " + startId + ": " + i);
		Intent intent = i;
		if (intent == null) {
			if (taskRunning == 0) {
				stopSelf();
				return START_NOT_STICKY;
			} else {
				return START_STICKY;
			}
		}
		try {
			taskRunning++;
			final String url = i.getStringExtra(URL_ARG);
			String filename = i.getStringExtra(NAME_ARG);
			final String type = i.getStringExtra(TYPE_ARG);
			if (type == null)
				filename = filterDots(filename);
			final long filesize = i.getLongExtra(SIZE_ARG, 0);
			final String cookie = i.getStringExtra(COOKIE_ARG);
			final DownloadTask downloader = new DownloadTask(this, url, filename, type,
					filesize, cookie, getApplicationContext());
			downloader.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LoggerServiceBinder extends Binder {
		public DownloaderService getService() {
			return DownloaderService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new LoggerServiceBinder();
	}

	@Override
	public void finishedTask() {
		taskRunning--;
		if (taskRunning == 0)
			stopSelf();
	};

	private static String filterDots(String filename) {
		if (filename == null)
			return null;
		final String[] filtered = filename.split("\\.");
		if (filtered.length <= 2)
			return filename;
		final StringBuilder st = new StringBuilder();
		for (int i = 0; i < filtered.length - 1; ++i) {
			st.append(filtered[i]);
			if (i + 1 < filtered.length - 1)
				st.append("_");
		}
		st.append(".");
		st.append(filtered[filtered.length - 1]);
		return st.toString();
	}

}
