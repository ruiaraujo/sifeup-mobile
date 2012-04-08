package pt.up.beta.mobile.ui.dialogs;

import pt.up.beta.mobile.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutDialogFragment extends DialogFragment {

	public static AboutDialogFragment newInstance() {
		return new AboutDialogFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder aboutDialog = new Builder(getActivity());
		aboutDialog.setTitle(R.string.bt_about_title).setNegativeButton(
				R.string.bt_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setMessage(R.string.bt_about_message);
		AlertDialog welcomeAlert = aboutDialog.create();
		welcomeAlert.show();
		// Make the textview clickable. Must be called after show()
		((TextView) welcomeAlert.findViewById(android.R.id.message))
				.setMovementMethod(LinkMovementMethod.getInstance());
		return welcomeAlert;
	}
	
	
	public void onDismiss (DialogInterface dialog){
		super.onDismiss(dialog);
		if ( isCancelable() )
		{
			if ( getActivity() instanceof OnDismissListener )
			{
				((OnDismissListener) getActivity()).onDismiss(dialog);
			}
		}
	}

}
