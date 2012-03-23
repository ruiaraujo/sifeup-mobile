package pt.up.beta.mobile.ui;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.R;
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
	  public static DownloaderFragment newInstance(String title, String url , String name , String type, long size) {
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
	        filesize = getArguments().getLong(SIZE_ARG, 0);
	        final DownloadTask downloader = new DownloadTask();
	        pbarDialog = new ProgressDialog(getActivity());
			pbarDialog.setMessage("Downloading " + filename);
			pbarDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			if ( filesize != 0 )
				pbarDialog.setMax(100);
			pbarDialog.setTitle(title);
			pbarDialog.setCancelable(false);
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
		private class DownloadTask extends AsyncTask<String, Integer, Integer> {

			private final static int OK = 0;
			private final static int ERROR = -1;
			private final static int FILE_ERROR = -2;
			private final static int NO_MEMORY = -3;

			
			
			private File myFile;
			@Override
			protected void onPostExecute(Integer result)
			{
			    if ( getActivity() == null )
			        return;
				if ( result == null )
					return;
				DownloaderFragment.this.dismiss();
				switch (result){
				case OK:
				{
					Toast.makeText(getActivity(),getString(R.string.msg_download_finished, myFile.getPath()), Toast.LENGTH_SHORT).show();
					try{
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						if ( type != null  && !type.trim().equals("") )
							intent.setData(Uri.fromFile(myFile));
						else
							intent.setDataAndType(Uri.fromFile(myFile), type);
						startActivity(intent);
					} 
					catch (Exception e) {
						e.printStackTrace();
					};
					break;
				}
				case ERROR: 
					Toast.makeText(getActivity(),R.string.toast_download_error, Toast.LENGTH_SHORT).show();
					break;
				case FILE_ERROR:
					Toast.makeText(getActivity(),R.string.toast_download_error_file, Toast.LENGTH_SHORT).show();
					break;
				case NO_MEMORY:
					Toast.makeText(getActivity(),R.string.toast_download_no_memory, Toast.LENGTH_SHORT).show();
					break;
				}
			}

			protected void onProgressUpdate(Integer ... progress) {
			 	pbarDialog.setProgress(progress[0] );
			 	pbarDialog.setMax(100);
			}
			
			@Override
			protected Integer doInBackground(String ... argsDownload) 
			{
				HttpURLConnection con = null;
				DataInputStream dis;
				FileOutputStream fos;
				long myProgress = 0;
				//int  fileLen;
				int byteRead;
				byte[] buf;
				try {
					
					//lastTime = downloadBegin= System.currentTimeMillis();
					
					final String url = argsDownload[0];
					if ( url.startsWith("https") )
						con =  SifeupAPI.getUncheckedConnection(url);
					else
						con = (HttpURLConnection) new URL(url).openConnection();
					con.setRequestProperty("Cookie", SessionManager.getInstance(getActivity()).getCookie());
					con.connect();
					myFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + argsDownload[1]);
					
					// Append mode on
					fos = new FileOutputStream(myFile);
					

					dis = new DataInputStream(con.getInputStream());
					if ( filesize == 0)
						filesize = con.getContentLength();
					if ( filesize < 0  )
						filesize = 0;
					if ( type == null || type.trim().equals("") )
					    type = con.getContentType();
					// Checking if external storage has enough memory ...
					android.os.StatFs stat = new android.os.StatFs(Environment.getExternalStorageDirectory().getPath());
					if((long)stat.getBlockSize() * (long)stat.getAvailableBlocks() < filesize)
						return NO_MEMORY;

					buf = new byte[65536];
					while (/*myProgress < fileLen*/ true) {
						try{

							if ((byteRead = dis.read(buf)) != -1)
							{
								fos.write(buf, 0, byteRead);
								myProgress += byteRead;
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
						if ( filesize != 0 )
							publishProgress((int) (((float)myProgress / (float) filesize) * 100) );
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
					return FILE_ERROR;
				}
				catch(Exception e)
				{
					return ERROR;
				}
				finally {
					if ( con != null )
						con.disconnect();
				}
				return OK;
			}
		}
		
}
