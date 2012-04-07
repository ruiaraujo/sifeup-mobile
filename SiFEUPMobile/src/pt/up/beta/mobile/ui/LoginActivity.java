package pt.up.beta.mobile.ui;


import com.actionbarsherlock.app.SherlockFragmentActivity;

import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.AuthenticationUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.ui.dialogs.AboutDialogFragment;
import pt.up.beta.mobile.ui.dialogs.ProgressDialogFragment;
import pt.up.beta.mobile.R;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends SherlockFragmentActivity implements ResponseCommand, OnDismissListener {

	private static final String DIALOG_CONNECTING = "connecting";
	private static final String DIALOG_ABOUT = "about";
	
	public static final String EXTRA_DIFFERENT_LOGIN = "pt.up.fe.mobile.extra.DIFFERENT_LOGIN";
	public static final int EXTRA_DIFFERENT_LOGIN_LOGOUT = 1;

	private AsyncTask<?, ?, ?> logintask;
	private String user;
	private String pass;

	private EditText passwordEditText;
	private EditText username;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.login_username);
		passwordEditText = (EditText) findViewById(R.id.login_pass);

		// preencher os campos com as informações gravadas
		final SessionManager session = SessionManager.getInstance(this);
		findViewById(R.id.login_confirm).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						user = username.getText().toString().trim();
						if (user.equals("")) {
							Toast
									.makeText(
											LoginActivity.this,
											getString(R.string.toast_login_error_empty_username),
											Toast.LENGTH_SHORT).show();
							return;
						}
						pass = passwordEditText.getText().toString().trim();
						if (pass.equals("")) {
							Toast
									.makeText(
											LoginActivity.this,
											getString(R.string.toast_login_error_empty_password),
											Toast.LENGTH_SHORT).show();
							return;
						}
						ProgressDialogFragment.newInstance(getString(R.string.lb_login_cancel),true).show(getSupportFragmentManager(), DIALOG_CONNECTING);

						logintask = AuthenticationUtils.authenticate(user,
								pass, LoginActivity.this);

					}

				});

		findViewById(R.id.login_about).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						AboutDialogFragment.newInstance().show(getSupportFragmentManager(), DIALOG_ABOUT);
					}
				});

		CheckBox showPassword = (CheckBox) findViewById(R.id.show_password);
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked)
                {
                    passwordEditText.setTransformationMethod(null);
                }
                else
                {
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
		// A actividade de login pode ser chamada no launcher ou caso a pessoa
		// faça logout
		// In case of a logout.
		Intent i = getIntent();
		final int action = i.getIntExtra(EXTRA_DIFFERENT_LOGIN, 0);
		switch (action) {
		case EXTRA_DIFFERENT_LOGIN_LOGOUT:// if logging out the cookie is
			// removed
			session.cleanPrefs();
            passwordEditText.setText("");
			break;
		default: {
			if (session.getCookies().size() > 0  && session.isUserLoaded()) {
				startActivity(new Intent(this, HomeActivity.class));
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
				return;
			}
			break;
		}
		}

	}

	public void onError(ERROR_TYPE error) {
		removeDialog(DIALOG_CONNECTING);
		switch (error) {
		case AUTHENTICATION:
			final Animation shake = AnimationUtils.loadAnimation(this,
					R.anim.shake);
			Toast.makeText(this,
					getString(R.string.toast_login_error_wrong_password),
					Toast.LENGTH_SHORT).show();
			// Remove stored password...
			passwordEditText.setText("");
			passwordEditText.startAnimation(shake);
			break;
		case NETWORK:
			Toast.makeText(this, getString(R.string.toast_server_error),
					Toast.LENGTH_SHORT).show();
		default:
			Toast.makeText(this, getString(R.string.general_error),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public void onResultReceived(Object... results) {
		removeDialog(DIALOG_CONNECTING);
		User user = (User) results[0];
		SessionManager.getInstance(this).setUser(new User(this.user,user.getUser(), pass, user.getType()));
		SessionManager.tuitionHistory.setLoaded(false);
		SessionManager.getInstance(this).cleanFriends();
		startActivity(new Intent(LoginActivity.this, HomeActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
		if ( logintask != null )
			logintask.cancel(true);
	}

    protected void removeDialog(String dialog) {    // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(dialog);
        if (prev != null) {
            ft.remove(prev).commitAllowingStateLoss();
        }
    }
}
