package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.datatypes.PasswordCheck;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.AuthenticationUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseActivity;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
		ResponseCommand {

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AnalyticsUtils.getInstance(getActivity()).trackPageView(
				"/Change Password");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.change_password,
				getParentContainer(), true);

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
					Toast.makeText(getActivity(),
							getString(R.string.username_empty),
							Toast.LENGTH_SHORT).show();
					usernameText.requestFocus();
					return;
				}
				String actualPassword = actualPasswordText.getText().toString();

				if (actualPassword.equals("")) {
					Toast.makeText(getActivity(),
							getString(R.string.old_password_empty),
							Toast.LENGTH_SHORT).show();
					actualPasswordText.requestFocus();
					return;
				}
				String newPassword = newPasswordText.getText().toString();
				if (newPassword.equals("")) {
					Toast.makeText(getActivity(),
							getString(R.string.new_password_empty),
							Toast.LENGTH_SHORT).show();
					newPasswordText.requestFocus();
					return;
				}
				if (currentQuality <= 1) {
					Toast.makeText(getActivity(),
							getString(R.string.new_password_too_weak),
							Toast.LENGTH_SHORT).show();
					newPasswordText.requestFocus();
					return;
				}
				String confirmNewPassword = confirmNewPasswordText.getText()
						.toString();

				if (confirmNewPassword.equals("")) {
					Toast.makeText(getActivity(),
							getString(R.string.confirm_new_password_empty),
							Toast.LENGTH_SHORT).show();
					confirmNewPasswordText.requestFocus();
					return;
				}
				if (!confirmNewPassword.equals(newPassword)) {
					Toast
							.makeText(
									getActivity(),
									getString(R.string.confirmation_password_different),
									Toast.LENGTH_SHORT).show();
					confirmNewPasswordText.requestFocus();
					return;
				}
				getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
				AuthenticationUtils.setPasswordReply(usernameText.getText()
						.toString(), actualPasswordText.getText().toString(),
						newPasswordText.getText().toString(),
						confirmNewPasswordText.getText().toString(), "S", ChangePasswordFragment.this);
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
		final SessionManager session = SessionManager.getInstance(getActivity());
		if ( session.isUserLoaded() )
		{
			String user = session.getLoginCode();
			String pass = session.getLoginPassword();
			if (!user.equals("") && !pass.equals("")) {
				usernameText.setText(user);
				actualPasswordText.setText(pass);
	
			}
		}
		return getParentContainer();
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
		switch (error) {
		case NETWORK:
			Toast.makeText(getActivity(),
					getString(R.string.toast_server_error), Toast.LENGTH_LONG)
					.show();
		default:
			// TODO: general error
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;

		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
		if (results.length > 0) { //error while setting it
			errorTitle = results[0].toString();
			errorContent = results[1].toString();
			DialogFragment df = new DialogFragment() {
				public Dialog onCreateDialog(Bundle savedInstanceState) {
					return new AlertDialog.Builder(getActivity()).setTitle(
							errorTitle).setMessage(errorContent).create();
				}

			};
			df.show(getFragmentManager(), "Error Dialog");
			return;
		}
		//changed successfully
		final SessionManager session = SessionManager.getInstance(getActivity());
		if ( session.isUserLoaded() )
		{
			User user = session.getUser();
			if ( user.getUser().equals(usernameText.getText().toString()) ||
					user.getDisplayName().equals(usernameText.getText().toString()) ) {
				session.setUser(new User(user.getDisplayName(),user.getUser(), newPasswordText.getText().toString(), user.getType()));
			 }
		}
		
		actualPasswordText.setText(newPasswordText.getText().toString());
		newPasswordText.setText("");
		confirmNewPasswordText.setText("");
		Toast.makeText(getActivity(),
				getString(R.string.password_successfully_changed),
				Toast.LENGTH_SHORT).show();

	}

	

}
