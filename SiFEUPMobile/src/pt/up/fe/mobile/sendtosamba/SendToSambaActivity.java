package pt.up.fe.mobile.sendtosamba;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SendToSambaActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SessionManager session = SessionManager.getInstance(this);
		if ( session.loadSession() )
		{
			final String user = session.getLoginCode();
			final String pass = session.getLoginPassword();
			Intent i = new Intent(this, UploaderService.class);
			i.replaceExtras(getIntent());
			i.putExtra(UploaderService.USERNAME_KEY, user);
			i.putExtra(UploaderService.PASSWORD_KEY, pass);
			startService(i);
		}
		else
		{
			Toast.makeText(this, R.string.notification_uploader_error_credential,Toast.LENGTH_SHORT).show();
		}
		finish();
	}

}
