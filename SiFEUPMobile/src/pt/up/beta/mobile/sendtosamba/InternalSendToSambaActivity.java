package pt.up.beta.mobile.sendtosamba;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * @author paulburke (ipaulpro)
 */
public class InternalSendToSambaActivity extends Activity {

	private static final int REQUEST_CODE = 6384; // onActivityResult request
													// code

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(target,
				getString(R.string.choose_file));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();

					try {
						Intent i = new Intent(this, UploaderService.class);
						i.replaceExtras(data);
						i.setData(uri);
						i.putExtra(UploaderService.USERNAME_KEY, AccountUtils
								.getActiveUserName(getApplicationContext()));
						i.putExtra(UploaderService.PASSWORD_KEY, AccountUtils
								.getActiveUserPassword(getApplicationContext()));
						startService(i);
						finish();
					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error",
								e);
					}
				}
			}
			break;
		}
	}
}