package pt.up.fe.mobile.ui;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.net.ssl.HttpsURLConnection;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;

public class DownloaderFragment extends DialogFragment {
	private final static String TITLE_ARG = "title";
	private final static String URL_ARG = "url";
	private final static String NAME_ARG = "name";
    private final static String TYPE_ARG = "type";
	private String url;
	private String filename;
	private String type;
	private ProgressDialog pbarDialog;
	  public static DownloaderFragment newInstance(String title, String url , String name) {
		  	DownloaderFragment frag = new DownloaderFragment();
	        Bundle args = new Bundle();
	        args.putString(TITLE_ARG, title);
	        args.putString(URL_ARG, url);
	        args.putString(NAME_ARG, name);
	        frag.setArguments(args);
	        
	        return frag;
	    }
	  
	  public static DownloaderFragment newInstance(String title, String url , String name , String type) {
          DownloaderFragment frag = new DownloaderFragment();
          Bundle args = new Bundle();
          args.putString(TITLE_ARG, title);
          args.putString(URL_ARG, url);
          args.putString(NAME_ARG, name);
          args.putString(TYPE_ARG, type);
          frag.setArguments(args);
          
          return frag;
      }

	  @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        String title = getArguments().getString(TITLE_ARG);
	        url = getArguments().getString(URL_ARG);
	        filename = getArguments().getString(NAME_ARG);
	        type = getArguments().getString(TYPE_ARG);
	        final DownloadTask downloader = new DownloadTask();
	        pbarDialog = new ProgressDialog(getActivity());
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pbarDialog.setMessage("Downloading " + filename);
			//pbarDialog.setMax(100);
			pbarDialog.setTitle(title);
			pbarDialog.setCancelable(true);
			pbarDialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					downloader.cancel(true);
				}
			});
			pbarDialog.setButton(getString(R.string.bt_cancel), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					downloader.cancel(true);
					DownloaderFragment.this.dismiss();
				}
			});
			
			downloader.execute(url,filename);
			
			return pbarDialog;

	    }
	    
	  
	  /** Classe privada para a busca de dados ao servidor */
		private class DownloadTask extends AsyncTask<String, Integer, String> {

			//private long lastTime;
			//private long downloadBegin;
			private File myFile;
			protected void onPostExecute(String result) //TODO: add error handling
			{
				if ( result == null )
					return;
				DownloaderFragment.this.dismiss();
				if ( type != null )
				{
					try{
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(myFile), type);
					startActivity(intent);
					} catch (Exception e) {};
				}
			}

			/*protected void onProgressUpdate(Integer ... progress) {
			 	pbarDialog.setProgress((int) ( ((float)progress[0] / (float)progress[1]  ) * 100) );
			 	long now = System.currentTimeMillis();
				if ( (now - lastTime) >= 1000 )
				{
					long kbs = 0;
					try{
						 kbs = ( ( progress[0]/ (now - downloadBegin) )*1000/1024);
						 
					}catch(ArithmeticException e){
						kbs = 0;
					}
				 	long eta = (long) (((float)progress[1] - progress[0] ) / kbs / 1024);
					pbarDialog.setMessage(getString(R.string.msg_dl_speed,kbs,eta/60,eta%60));
					lastTime = now;
				}
			}*/
			@Override
			protected String doInBackground(String ... argsDownload) 
			{
				HttpsURLConnection con = null;
				DataInputStream dis;
				FileOutputStream fos;
				//int myProgress = 0;
				//int  fileLen;
				int byteRead;
				byte[] buf;
				try {
					
					//lastTime = downloadBegin= System.currentTimeMillis();
					con =  SifeupAPI.getUncheckedConnection(argsDownload[0]);
					con.setRequestProperty("Cookie", SessionManager.getInstance().getCookie());
					con.connect();
					myFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + argsDownload[1]);
					
					// Append mode on
					fos = new FileOutputStream(myFile, true);
					

					dis = new DataInputStream(con.getInputStream());
					//fileLen = Integer.MAX_VALUE;
					//fileLen = con.getContentLength();
					if ( type == null )
					    type = con.getContentType();
					// Checking if external storage has enough memory ...
					//android.os.StatFs stat = new android.os.StatFs(Environment.getExternalStorageDirectory().getPath());
					//if((long)stat.getBlockSize() * (long)stat.getAvailableBlocks() < fileLen)
						//return "No memory";

					buf = new byte[65536];
					while (/*myProgress < fileLen*/ true) {
						try{

							if ((byteRead = dis.read(buf)) != -1)
							{
								fos.write(buf, 0, byteRead);
								//myProgress += byteRead;
							}
							else
							{
								con.disconnect();
								dis.close();
								fos.close();
								break;
							}
						}
						catch(Exception e){
							return null;
						}
						//publishProgress((int) ((myProgress / (float) fileLen) * 100),fileLen );
						if ( isCancelled() )
						{
							con.disconnect();
							dis.close();
							fos.close();
							return null;
						}
					}
				}
				catch (FileNotFoundException e)
				{
					return "File Error";
				}
				catch(Exception e)
				{
					return "Unknown Error";
				}
				finally {
					con.disconnect();
				}
				return filename;
			}
		}
		
}
