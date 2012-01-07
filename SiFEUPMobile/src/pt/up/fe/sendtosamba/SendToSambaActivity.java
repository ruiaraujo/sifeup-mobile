package pt.up.fe.sendtosamba;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jibble.simpleftp.SimpleFTP;

import pt.up.fe.mobile.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SendToSambaActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleFTP ftp = null;

        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
           
            InputStreamManaged is;
            Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
            String filename = null;
            if ( uri == null )
            {
                filename = extras.getCharSequence(Intent.EXTRA_TEXT).toString();
                if ( filename == null )
                    return;
                is = new InputStreamManaged(new ByteArrayInputStream( filename.getBytes("UTF-8")));
                is.setLength(filename.length());
                
                filename = extras.getCharSequence(Intent.EXTRA_SUBJECT).toString();
                if ( filename == null )
                {
                    filename = getString(R.string.app_name) + System.currentTimeMillis();
                }
                filename += ".txt";
            }
            else
            {
                ContentResolver cr = getContentResolver();
                is = new InputStreamManaged(cr.openInputStream(uri));
                is.setLength(new java.io.File(uri.getPath()).length());

                int offset = uri.toString().lastIndexOf('/');
                System.out.println(uri.toString());
                System.out.println("" + uri.toString().lastIndexOf('/'));
                filename = uri.toString().substring(offset + 1);
            }
            
            is.setOnPercentageChangedListener(new ManagedOnPercentageChangedListener() {
				
				public void onChanged(long nperc) {
					System.out.println("% = " + nperc);
				}
			});
            
            ftp = new SimpleFTP();
            
            // Connect to an FTP server on port 21.
            ftp.connect("tom.fe.up.pt", 21, "ee08281", "ediferente3");
            
            // Set binary mode.
            ftp.bin();
           // ftp.
            // Change to a new working directory on the FTP server.
            ftp.mkd("SendToSamba2");
            ftp.cwd("SendToSamba2");
            
            // You can also upload from an InputStream, e.g.
            ftp.stor(is, filename);
            is.close();
            Toast.makeText(this, "Done! :D", Toast.LENGTH_LONG).show();
            
        }
        catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if ( ftp != null ){
                try{
                    // Quit from the FTP server.
                    ftp.disconnect();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}