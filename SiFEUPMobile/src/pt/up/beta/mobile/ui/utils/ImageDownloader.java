package pt.up.beta.mobile.ui.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.ByteArrayBuffer;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.utils.FileUtils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloader {

	Map<String, Bitmap> imageCache;

	public ImageDownloader() {
		imageCache = new HashMap<String, Bitmap>();

	}

	public void download(String url, ImageView imageView) {
		download(url, imageView, null, null);
	}

	// download function
	public void download(String url, ImageView imageView, Bitmap placeholder,
			Resources res) {
		if (cancelPotentialDownload(url, imageView)) {

			// Caching code right here
			String filename = String.valueOf(url.hashCode());
			File f = new File(FileUtils.getCacheDirectory(imageView.getContext()),
					filename);

			// Is the bitmap in our memory cache?
			Bitmap bitmap = null;

			bitmap = (Bitmap) imageCache.get(f.getPath());

			if (bitmap == null) {

				bitmap = BitmapFactory.decodeFile(f.getPath());

				if (bitmap != null) {
					imageCache.put(f.getPath(), bitmap);
				}

			}
			// No? download it
			if (bitmap == null) {
				final BitmapDownloaderTask task = new BitmapDownloaderTask(
						imageView);
				final DownloadedDrawable downloadedDrawable;
				if (res == null || placeholder == null) {
					downloadedDrawable = new DownloadedDrawable(task);
				} else {
					downloadedDrawable = new DownloadedDrawable(task, res,
							placeholder);
				}
				imageView.setImageDrawable(downloadedDrawable);
				task.execute(url);
			} else {
				// Yes? set the image
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	// cancel a download (internal only)
	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
				bitmapDownloaderTask.cancel(true);
			} else {
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	// gets an existing download if one exists for the imageview
	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable) {
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}



	// /////////////////////

	// download asynctask
	public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			url = (String) params[0];
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			if ( bitmap == null )
				return; //nothing to do in case of error
			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with
				// it
				if (this == bitmapDownloaderTask) {
					imageView.setImageBitmap(bitmap);

					// cache the image

					String filename = String.valueOf(url.hashCode());
					File f = new File(
					        FileUtils.getCacheDirectory(imageView.getContext()), filename);

					imageCache.put(f.getPath(), bitmap);

					FileUtils.writeFile(bitmap, f);
				}
			}
		}

	}

	static class DownloadedDrawable extends BitmapDrawable {
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask,
				Resources res, Bitmap bitmap) {
			super(res, bitmap);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		@SuppressWarnings("deprecation")
		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
			super();
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask() {
			return bitmapDownloaderTaskReference.get();
		}
	}

	// the actual download code
	static Bitmap downloadBitmap(String url) {
		HttpsURLConnection httpConn = SifeupAPI.getUncheckedConnection(url);
		httpConn.setRequestProperty("Cookie", SessionManager.getInstance()
				.getCookie());
		Bitmap bitmap;
		try {
			httpConn.connect();

			try {
				BufferedInputStream bis = new BufferedInputStream(
						httpConn.getInputStream());
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int read = 0;
				int bufSize = 512;
				byte[] buffer = new byte[bufSize];
				while (true) {
					read = bis.read(buffer);
					if (read == -1) {
						break;
					}
					baf.append(buffer, 0, read);
				}
				bis.close();
				httpConn.getInputStream().close();
				bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
						baf.length());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			httpConn.disconnect();
		}
		return bitmap;

	}
}
