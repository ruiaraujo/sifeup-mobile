package pt.up.fe.mobile.sendtosamba;

import pt.up.fe.mobile.ui.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SendToSambaActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences loginSettings = getSharedPreferences(
				LoginActivity.class.getName(), Context.MODE_PRIVATE);
		final String user = loginSettings.getString(
				LoginActivity.PREF_USERNAME, "");
		final String pass = loginSettings.getString(
				LoginActivity.PREF_PASSWORD, "");
		Intent i = new Intent(this, UploaderService.class);
		i.replaceExtras(getIntent());
		i.putExtra(UploaderService.USERNAME_KEY, user);
		i.putExtra(UploaderService.PASSWORD_KEY, pass);
		startService(i);
		finish();
		
	}

}
