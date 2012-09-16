package pt.up.beta.mobile;

import org.acra.*;
import org.acra.annotation.*;

import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.app.Application;
import android.util.Log;

@ReportsCrashes(formKey = "dEluZTc1WG4yNWtFMUhWZXVCS2F0Tnc6MQ") 
public class FEUPMobileApplication extends Application{

  @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
		long b = System.currentTimeMillis();
        new Thread(new Runnable() {
			@Override
			public void run() {
				SifeupAPI.initSSLContext(getApplicationContext());
			}
		}).start();
        Log.d("SSL context init time",	"Time " + ( System.currentTimeMillis() -b ));
    }
	
}



