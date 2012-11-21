package pt.up.beta.mobile.ui.services.print;

import java.io.File;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * @author paulburke (ipaulpro)
 */
public class InternalMobilePrintActivity extends Activity {

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
						// Create a file instance from the URI
						final File file = FileUtils.getFile(uri);
						Toast.makeText(InternalMobilePrintActivity.this,
								"File Selected: " + file.getAbsolutePath(),
								Toast.LENGTH_LONG).show();
						Mail m = new Mail(
								AccountUtils.getActiveUserName(getApplicationContext())
										+ "@fe.up.pt",
								AccountUtils
										.getActiveUserPassword(getApplicationContext()));

						String[] toArr = { "ruka.araujo@gmail.com" };
						m.setTo(toArr);
						m.setFrom(
								AccountUtils.getActiveUserName(getApplicationContext())
								+ "@fe.up.pt");
						m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
						m.setBody("Email body.");

						try {
							m.addAttachment(file.getAbsolutePath());

							if (m.send()) {
								Toast.makeText(this,
										"Email was sent successfully.",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(this, "Email was not sent.",
										Toast.LENGTH_LONG).show();
							}
						} catch (Exception e) {
							// Toast.makeText(MailApp.this,
							// "There was a problem sending the email.",
							// Toast.LENGTH_LONG).show();
							Log.e("MailApp", "Could not send email", e);
						}
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