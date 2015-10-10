package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.Constants;
import pt.up.mobile.R;
import pt.up.beta.mobile.datatypes.PasswordCheck;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.AuthenticationUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.dialogs.ProgressDialogFragment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Change Password Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ChangePasswordFragment extends BaseFragment implements
		ResponseCommand<String[]> {

	private String errorTitle;
	private String errorContent;

	private EditText actualPasswordText;
	private EditText usernameText;
	private EditText newPasswordText;
	private EditText confirmNewPasswordText;
	private TextView newPasswordSecurity;
	private PasswordCheck checker;
	private int currentQuality = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.change_password);

		/** Cancel */
		Button cancel = (Button) root.findViewById(R.id.set_password_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if (getActivity() == null)
					return;
				getActivity().finish();
			}
		});

		/** Confirm */
		Button setPassword = (Button) root
				.findViewById(R.id.set_password_confirm);
		setPassword.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				String username = usernameText.getText().toString();
				if (username.equals("")) {
					Toast.makeText(getActivity(), R.string.username_empty,
							Toast.LENGTH_SHORT).show();
					usernameText.requestFocus();
					return;
				}
				String actualPassword = actualPasswordText.getText().toString();

				if (actualPassword.equals("")) {
					Toast.makeText(getActivity(), R.string.old_password_empty,
							Toast.LENGTH_SHORT).show();
					actualPasswordText.requestFocus();
					return;
				}
				String newPassword = newPasswordText.getText().toString();
				if (newPassword.equals("")) {
					Toast.makeText(getActivity(), R.string.new_password_empty,
							Toast.LENGTH_SHORT).show();
					newPasswordText.requestFocus();
					return;
				}
				if (currentQuality <= 1) {
					Toast.makeText(getActivity(),
							R.string.new_password_too_weak, Toast.LENGTH_SHORT)
							.show();
					newPasswordText.requestFocus();
					return;
				}
				String confirmNewPassword = confirmNewPasswordText.getText()
						.toString();

				if (TextUtils.isEmpty(confirmNewPassword)) {
					Toast.makeText(getActivity(),
							R.string.confirm_new_password_empty,
							Toast.LENGTH_SHORT).show();
					confirmNewPasswordText.requestFocus();
					return;
				}
				if (!confirmNewPassword.equals(newPassword)) {
					Toast.makeText(getActivity(),
							R.string.confirmation_password_different,
							Toast.LENGTH_SHORT).show();
					confirmNewPasswordText.requestFocus();
					return;
				}
				if (newPassword.length() < 8) {
					Toast.makeText(getActivity(),
							R.string.toast_login_error_password_too_short,
							Toast.LENGTH_SHORT).show();
					newPasswordText.requestFocus();
					return;
				}
				ProgressDialogFragment.newInstance(false).show(
						getFragmentManager(), DIALOG);
				AuthenticationUtils.setPasswordReply(usernameText.getText()
						.toString(), actualPasswordText.getText().toString(),
						newPasswordText.getText().toString(),
						confirmNewPasswordText.getText().toString(), "S",
						ChangePasswordFragment.this, getActivity());
			}
		});

		/** Username */
		newPasswordSecurity = (TextView) root
				.findViewById(R.id.new_password_security);

		/** Username */
		usernameText = (EditText) root.findViewById(R.id.username);

		/** Current Password */
		actualPasswordText = (EditText) root
				.findViewById(R.id.current_password);

		/** New Password */
		newPasswordText = (EditText) root.findViewById(R.id.new_password);

		newPasswordText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String password = s.toString();
				currentQuality = checker.validatePassword(password);
				String quality = null;
				switch (currentQuality) {
				case -2:
					quality = getString(R.string.new_password_too_short);
					break;
				case -1:
					quality = getString(R.string.new_password_common_word);
					break;
				case 1:
					quality = getString(R.string.new_password_very_weak);
					break;
				case 2:
					quality = getString(R.string.new_password_weak);
					break;
				case 3:
					quality = getString(R.string.new_password_medium);
					break;
				case 4:
					quality = getString(R.string.new_password_strong);
					break;
				case 5:
					quality = getString(R.string.new_password_very_strong);
					break;
				}
				newPasswordSecurity.setText(getString(
						R.string.new_password_security, quality));
			}
		});

		/** Confirm New Password */
		confirmNewPasswordText = (EditText) root
				.findViewById(R.id.confirm_new_password);

		new AsyncTask<Void, Void, Void>() {
			protected void onPostExecute(Void result) {
				showMainScreen();
			}

			protected Void doInBackground(Void... params) {
				checker = new PasswordCheck();
				return null;
			}
		}.execute();
		String user = AccountUtils.getActiveUserCode(getActivity());
		if (!user.equals("")) {
			usernameText.setText(user);

		}

		return getParentContainer();
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		removeDialog(DIALOG);
		switch (error) {
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	public static class ErrorDialog extends DialogFragment {
		public ErrorDialog() {
		}

		private String errorTitle;
		private String errorContent;

		public void setErrorTitle(String errorTitle) {
			this.errorTitle = errorTitle;
		}

		public void setErrorContent(String errorContent) {
			this.errorContent = errorContent;
		}

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity()).setTitle(errorTitle)
					.setMessage(errorContent).create();
		}

	}

	public void onResultReceived(String[] results) {
		if (getActivity() == null)
			return;
		removeDialog(DIALOG);
		if (results != null && results.length >= 2) { // error while setting it
			errorTitle = results[0].toString();
			errorContent = results[1].toString();
			ErrorDialog df = new ErrorDialog();
			df.setErrorContent(errorContent);
			df.setErrorTitle(errorTitle);
			df.show(getFragmentManager(), "Error Dialog");
			return;
		}
		// changed successfully
		final AccountManager accountManager = AccountManager.get(getActivity());
		final String userCode = AccountUtils.getActiveUserCode(getActivity());
		final String userName = AccountUtils.getActiveUserCode(getActivity());
		final String usernameTextEdit = usernameText.getText().toString();
		if (usernameTextEdit.equals(userCode)
				|| usernameTextEdit.equals(userName)) {
			accountManager.setPassword(new Account(userName,
					Constants.ACCOUNT_TYPE), newPasswordText.getText()
					.toString());
		}

		actualPasswordText.setText(newPasswordText.getText().toString());
		newPasswordText.setText("");
		confirmNewPasswordText.setText("");
		Toast.makeText(getActivity(),
				getString(R.string.password_successfully_changed),
				Toast.LENGTH_SHORT).show();

	}

	protected void onRepeat() {
		ProgressDialogFragment.newInstance(false).show(getFragmentManager(),
				DIALOG);
		AuthenticationUtils.setPasswordReply(usernameText.getText().toString(),
				actualPasswordText.getText().toString(), newPasswordText
						.getText().toString(), confirmNewPasswordText.getText()
						.toString(), "S", ChangePasswordFragment.this,
				getActivity());
	}

}
