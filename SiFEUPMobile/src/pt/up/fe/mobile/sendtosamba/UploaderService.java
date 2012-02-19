package pt.up.fe.mobile.sendtosamba;

import java.io.ByteArrayInputStream;

import pt.up.fe.mobile.R;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class UploaderService extends Service {
	/**
	 * Username that should be the intent
	 */
	public final static String USERNAME_KEY = "pt.up.fe.sendtosamba.USERNAME";

	/**
	 * Password that should be the intent
	 */
	public final static String PASSWORD_KEY = "pt.up.fe.sendtosamba.PASSWORD";

	@Override
	public int onStartCommand(Intent i, int flags, int startId) {
		Log.i("LoggerService", "Received start id " + startId + ": " + i);

		Intent intent = i;
		Bundle extras = intent.getExtras();
		final String username = intent.getStringExtra(USERNAME_KEY);
		final String password = intent.getStringExtra(PASSWORD_KEY);
		try {
			final InputStreamManaged is;
			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
			String filename = null;
			if (uri == null) {
				filename = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
				if (filename == null)
					return START_NOT_STICKY;
				is = new InputStreamManaged(new ByteArrayInputStream(filename
						.getBytes("UTF-8")));
				is.setLength(filename.length());

				filename = extras.getCharSequence(Intent.EXTRA_SUBJECT)
						.toString();
				if (filename == null) {
					filename = getString(R.string.app_name)
							+ System.currentTimeMillis();
				}
				filename += ".txt";
			} else {
				ContentResolver cr = getContentResolver();
				is = new InputStreamManaged(cr.openInputStream(uri));
				is.setLength(new java.io.File(uri.getPath()).length());

				int offset = uri.toString().lastIndexOf('/');
				System.out.println(uri.toString());
				System.out.println("" + uri.toString().lastIndexOf('/'));
				filename = uri.toString().substring(offset + 1);
			}

			final UploaderTask task = new UploaderTask(this, is, filename);
			task.execute(username, password);

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
		public UploaderService getService() {
			return UploaderService.this;
		}
	}

	public IBinder onBind(Intent arg0) {
		return new LoggerServiceBinder();
	};

	
}
