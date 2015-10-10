package pt.up.mobile.ui.services.print;

import pt.up.mobile.R;
import pt.up.mobile.sifeup.AccountUtils;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * @author paulburke (ipaulpro)
 */
public class InternalMobilePrintActivity extends SherlockFragmentActivity {

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
					if (uri == null) {
						finish();
						return;
					}
					try {
						Intent i = new Intent(this, MobilePrintService.class);
						i.replaceExtras(data);
						i.setData(uri);

						final String sender;
						if (AccountUtils.getActiveUserName(this).endsWith(
								"@fe.up.pt"))
							sender = AccountUtils.getActiveUserName(this);
						else
							sender = AccountUtils.getActiveUserName(this)
									+ "@fe.up.pt";
						final String password = AccountUtils
								.getActiveUserPassword(this);
						i.putExtra(MobilePrintService.USERNAME_KEY, sender);
						i.putExtra(MobilePrintService.PASSWORD_KEY, password);
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
		finish();
	}
}