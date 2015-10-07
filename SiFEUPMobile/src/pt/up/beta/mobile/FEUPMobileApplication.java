package pt.up.beta.mobile;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
formUri = "http://paginas.fe.up.pt/~up201304143/SiFEUP%20Mobile/crashreport.php")
public class FEUPMobileApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
}
