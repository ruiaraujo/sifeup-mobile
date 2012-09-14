package pt.up.beta.mobile;

import org.acra.*;
import org.acra.annotation.*;
import android.app.Application;

@ReportsCrashes(formKey = "dEluZTc1WG4yNWtFMUhWZXVCS2F0Tnc6MQ") 
public class FEUPMobileApplication extends Application{

  @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        super.onCreate();
    }
	
}



