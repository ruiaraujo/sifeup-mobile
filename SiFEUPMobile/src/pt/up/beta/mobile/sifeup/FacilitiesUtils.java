package pt.up.beta.mobile.sifeup;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class FacilitiesUtils {
	private FacilitiesUtils() {
	}

	public static AsyncTask<String, Void, ERROR_TYPE> getBuildingPic( String building, String floor,
			ResponseCommand command) {
		return new BitmapDownloaderTask(command).execute(SifeupAPI.getBuildingPicUrl(building, floor));
	}

	private static class BitmapDownloaderTask extends AsyncTask<String, Void, ERROR_TYPE> {
	    private Bitmap bitmap;
	    private final ResponseCommand command;
	    public BitmapDownloaderTask(ResponseCommand command) {
	    	this.command = command;
	    }

	    @Override
	    // Actual download method, run in the task thread
	    protected ERROR_TYPE doInBackground(String... params) {
	         // params comes from the execute() call: params[0] is the url.
	         bitmap = downloadBitmap(params[0]);
	         if ( bitmap == null )
	        	 return ERROR_TYPE.NETWORK;
	         return null;
	    }

	    @Override
	    // Once the image is downloaded, associates it to the imageView
	    protected void onPostExecute(ERROR_TYPE error) {
	        if (isCancelled()) {
	            bitmap = null;
	            return;
	        }
	        if ( error == null )
	        	command.onResultReceived(bitmap);
	        else
	        	command.onError(error);
	    }
	}
	
	static Bitmap downloadBitmap(String url) {
	    final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	    final HttpGet getRequest = new HttpGet(url);

	    try {
	        HttpResponse response = client.execute(getRequest);
	        final int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != HttpStatus.SC_OK) { 
	            Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url); 
	            return null;
	        }
	        
	        final HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream inputStream = null;
	            try {
	                inputStream = entity.getContent(); 
	                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	                return bitmap;
	            } finally {
	                if (inputStream != null) {
	                    inputStream.close();  
	                }
	                entity.consumeContent();
	            }
	        }
	    } catch (Exception e) {
	        // Could provide a more explicit error message for IOException or IllegalStateException
	        getRequest.abort();
	        Log.w("ImageDownloader", "Error while retrieving bitmap from " + url, e);
	    } finally {
	        if (client != null) {
	            client.close();
	        }
	    }
	    return null;
	}
}
