package pt.up.mobile.ui.dialogs;

import pt.up.mobile.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	private final static String MESSAGE_ARG = "message";
	private final static String CANCELABLE_ARG = "cancelable";

	private ProgressDialog pbarDialog;
	public static ProgressDialogFragment newInstance(String message, boolean can) {
		ProgressDialogFragment frag = new ProgressDialogFragment();
		Bundle args = new Bundle(); 
		args.putBoolean(CANCELABLE_ARG, can);
		args.putString(MESSAGE_ARG, message);
		frag.setArguments(args);
		return frag;
	}

	public static ProgressDialogFragment newInstance(boolean can) {
		Bundle args = new Bundle(); 
		args.putBoolean(CANCELABLE_ARG, can);
		ProgressDialogFragment frag = new ProgressDialogFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String message = getArguments().getString(MESSAGE_ARG);
		final boolean cancelable = getArguments().getBoolean(CANCELABLE_ARG);
		pbarDialog = new ProgressDialog(getActivity());
		if ( message ==  null )
			pbarDialog.setMessage(getString(R.string.lb_data_fetching));
		else
			pbarDialog.setMessage(message);
		pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pbarDialog.setIndeterminate(true);
		pbarDialog.setCancelable(cancelable);
		setCancelable(cancelable);
		setRetainInstance(true);
		return pbarDialog;

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
