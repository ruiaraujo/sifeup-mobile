package pt.up.beta.mobile;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dHJaSXlBN0xpVV92cDE2ZVRmQjZEbkE6MQ")
public class FEUPMobileApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
}
