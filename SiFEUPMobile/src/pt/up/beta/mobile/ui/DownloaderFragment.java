package pt.up.beta.mobile.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class DownloaderFragment extends DialogFragment {
	private final static String TITLE_ARG = "title";
	private final static String URL_ARG = "url";
	private final static String NAME_ARG = "name";
	private final static String TYPE_ARG = "type";
	private final static String SIZE_ARG = "size";

	private String url;
	private String filename;
	private String type;
	private long filesize = 0;
	private ProgressDialog pbarDialog;
	private boolean alreadyDismissed = false;
	private AsyncTask<String, Integer, Integer> task;

	public static DownloaderFragment newInstance(String title, String url,
			String name, String type, long size) {
		DownloaderFragment frag = new DownloaderFragment();
		Bundle args = new Bundle();
		args.putString(TITLE_ARG, title);
		args.putString(URL_ARG, url);
		args.putString(NAME_ARG, name);
		args.putString(TYPE_ARG, type);
		args.putLong(SIZE_ARG, size);
		frag.setArguments(args);

		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString(TITLE_ARG);
		url = getArguments().getString(URL_ARG);
		filename = getArguments().getString(NAME_ARG);
		type = getArguments().getString(TYPE_ARG);
		if ( type == null )
			filename = filterDots(filename);
		filesize = getArguments().getLong(SIZE_ARG, 0);
		final DownloadTask downloader = new DownloadTask();
		pbarDialog = new ProgressDialog(getActivity());
		pbarDialog.setMessage("Downloading " + filename);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		if (filesize != 0)
			pbarDialog.setMax(100);
		pbarDialog.setTitle(title);
		pbarDialog.setCancelable(false);
		pbarDialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				downloader.cancel(true);
			}
		});
		pbarDialog.setButton(AlertDialog.BUTTON_POSITIVE,
				getString(R.string.bt_cancel), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						downloader.cancel(true);
						pbarDialog.dismiss();
					}
				});

		task = downloader.execute(url, filename);
		setRetainInstance(true);
		alreadyDismissed = false;
		return pbarDialog;

	}  

	@Override
	public void onSaveInstanceState(Bundle saved){
		alreadyDismissed = true;
	}
	
	@Override
    public void onDestroy() {
        // Make the thread go away.
		task.cancel(true);
		super.onDestroy();
    }

	private static String filterDots(String filename) {
		if ( filename == null)
			return null;
		final String [] filtered = filename.split("\\.");
		if ( filtered.length <= 2 )
			return filename;
		final StringBuilder st = new StringBuilder();
		for ( int i = 0; i < filtered.length -1 ; ++i )
		{
			st.append(filtered[i]);
			if ( i+1 < filtered.length -1 )
				st.append("_");
		}
		st.append(".");
		st.append(filtered[filtered.length -1]);
		return st.toString();
	}


	/** Classe privada para a busca de dados ao servidor */
	private class DownloadTask extends AsyncTask<String, Integer, Integer> {

		private final static int OK = 0;
		private final static int ERROR = -1;
		private final static int FILE_ERROR = -2;
		private final static int NO_MEMORY = -3;
		private final static int NO_MEMORY_CARD = -4;

		private File myFile;

		@Override
		protected void onPostExecute(Integer result) {
			if (getActivity() == null)
				return;
			if (result == null)
				return;
			if ( !alreadyDismissed )
				DownloaderFragment.this.dismiss();
			else
				pbarDialog.dismiss();
			switch (result) {
			case OK: {
				Toast.makeText(
						getActivity(),
						getString(R.string.msg_download_finished,
								myFile.getPath()), Toast.LENGTH_SHORT).show();
				try {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if (type != null && !type.trim().equals(""))
						intent.setData(Uri.fromFile(myFile));
					else
						intent.setDataAndType(Uri.fromFile(myFile), type);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
				break;
			}
			case ERROR:
				Toast.makeText(getActivity(), R.string.toast_download_error,
						Toast.LENGTH_SHORT).show();
				break;
			case FILE_ERROR:
				Toast.makeText(getActivity(),
						R.string.toast_download_error_file, Toast.LENGTH_SHORT)
						.show();
				break;
			case NO_MEMORY:
				Toast.makeText(getActivity(),
						R.string.toast_download_no_memory, Toast.LENGTH_SHORT)
						.show();
				break;
			case NO_MEMORY_CARD:
				Toast.makeText(getActivity(),
						R.string.toast_download_no_memory_card, Toast.LENGTH_SHORT)
						.show();
				break;
				
			}
		}

		protected void onProgressUpdate(Integer... progress) {
			pbarDialog.setProgress(progress[0]);
			pbarDialog.setMax(100);
		}

		@Override
		protected Integer doInBackground(String... argsDownload) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			InputStream dis = null;
			FileOutputStream fos = null;
			long myProgress = 0;
			int byteRead;
			byte[] buf;
			try {

			    String state = Environment.getExternalStorageState();
			    if (!Environment.MEDIA_MOUNTED.equals(state)) {
					httpclient.getConnectionManager().shutdown();
					return NO_MEMORY_CARD;
			    }
				
				
				final String url = argsDownload[0];
				// Create local HTTP context
				BasicHttpContext localContext = new BasicHttpContext();

				CookieStore cookieStore = new BasicCookieStore();

				// adding cookie from the cookie store
				for (Cookie cookie : SessionManager.getInstance().getCookies())
					cookieStore.addCookie(cookie);

				// Bind custom cookie store to the local context
				localContext.setAttribute(ClientContext.COOKIE_STORE,
						cookieStore);

				// adding cookies from the cookie manager
				if (getActivity() != null)
					CookieSyncManager.createInstance(getActivity()
							.getApplicationContext());
				else {
					cancel(true);
					httpclient.getConnectionManager().shutdown();
					return null;
				}
				List<Cookie> cookies = androidCookieToApacheCookie(
						CookieManager.getInstance().getCookie(url), url);
				for (Cookie cookie : cookies)
					cookieStore.addCookie(cookie);
				HttpResponse response = httpclient.execute(new HttpGet(url),
						localContext);
				if (response == null) {
					httpclient.getConnectionManager().shutdown();
					return null;
				}
				HttpEntity entity = response.getEntity();
				final File dir;
				if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO )
					dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
							.getAbsolutePath());
				else
					dir = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + File.separator + "Download");
				dir.mkdirs();
				myFile = new File(dir,argsDownload[1] );
				if ( myFile.exists() )
				{
					myFile = getNonExistantFile(dir,argsDownload[1] );
				}
				fos = new FileOutputStream(myFile);

				dis = entity.getContent();
				if (filesize == 0)
					filesize = entity.getContentLength();
				if (filesize < 0)
					filesize = 0;
				if (type == null || type.trim().equals("")) {
					final Header contentType = entity.getContentType();
					if (contentType != null)
						type = contentType.getValue();
				}
				// Checking if external storage has enough memory ...
				android.os.StatFs stat = new android.os.StatFs(Environment
						.getExternalStorageDirectory().getPath());
				if ((long) stat.getBlockSize()
						* (long) stat.getAvailableBlocks() < filesize)
					return NO_MEMORY;

				buf = new byte[65536];
				while (true) {
					try {

						if ((byteRead = dis.read(buf)) != -1) {
							fos.write(buf, 0, byteRead);
							myProgress += byteRead;
						} else {
							httpclient.getConnectionManager().shutdown();
							dis.close();
							fos.close();
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
					if (filesize != 0)
						publishProgress((int) (((float) myProgress / (float) filesize) * 100));
					if (isCancelled()) {
						httpclient.getConnectionManager().shutdown();
						dis.close();
						fos.close();
						myFile.delete();
						return null;
					}
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if ( myFile != null )
					myFile.delete();
				return FILE_ERROR;
			} catch (Exception e) {
				e.printStackTrace();
				if ( myFile != null )
					myFile.delete();
				return ERROR;
			} finally {
				if (httpclient != null)
					httpclient.getConnectionManager().shutdown();
					try {
						if ( dis != null )
							dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if ( fos != null )
							fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			if ( isCancelled() && myFile != null )
				myFile.delete();
			while ( getActivity() == null && !isCancelled() )
			{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return OK;
		}
	}

	private static List<Cookie> androidCookieToApacheCookie(
			String cookiesAndroid, String url) {
		final List<Cookie> cookies = new ArrayList<Cookie>();
		if (cookiesAndroid == null || cookiesAndroid.trim().equals(""))
			return cookies;
		final String domain = getDomain(url);
		final String[] individualCookies = cookiesAndroid.split(";");
		for (String cookie : individualCookies) {
			final String[] cookieValues = cookie.split("=");
			BasicClientCookie c = new BasicClientCookie(cookieValues[0].trim(),
					cookieValues[1].trim());
			c.setPath("/");
			c.setVersion(0);
			c.setDomain(domain);
			cookies.add(c);
		}
		return cookies;
	}

	private static File getNonExistantFile(File baseDir, String filename) {
		if ( baseDir == null || filename == null )
			return null;
		File nonExistant;
		int finalDot = filename.lastIndexOf("."), tries = 1;
		if ( finalDot < 0 )
			finalDot = filename.length();
		do{
			nonExistant = new File(baseDir, filename.substring(0,finalDot) + "(" + tries + ")" + filename.substring(finalDot));
			tries++;
		} while ( nonExistant.exists() );
		return nonExistant;
	}

	private static String getDomain(String url) {
		if (url == null || url.trim().equals(""))
			return "";
		String noProtocol = url.substring(url.indexOf("//") + 2);
		return noProtocol.substring(0, noProtocol.indexOf('/'));
	}

}
