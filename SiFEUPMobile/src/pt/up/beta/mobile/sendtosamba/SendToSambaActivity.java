package pt.up.beta.mobile.sendtosamba;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

public class SendToSambaActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String user = AccountUtils.getActiveUserName(this);
		final String pass = AccountUtils.getActiveUserPassword(this);
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
			Intent i = new Intent(this, UploaderService.class);
			i.replaceExtras(getIntent());
			i.putExtra(UploaderService.USERNAME_KEY, user);
			i.putExtra(UploaderService.PASSWORD_KEY, pass);
			startService(i);
		} else {
			Toast.makeText(this,
					R.string.notification_uploader_error_credential,
					Toast.LENGTH_SHORT).show();
		}
		finish();
	}

}
