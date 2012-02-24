package pt.up.fe.mobile.ui;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.sifeup.AuthenticationUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity implements ResponseCommand {

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
		// como os objectos sao declarados em xml para termos a referencia deles
		// temos de fazer isto
		username = (EditText) findViewById(R.id.login_username);
		passwordEditText = (EditText) findViewById(R.id.login_pass);

		// verficar se o utilizador carregou no remember me e se sim
		// preencher os campos com as informações gravadas
		final SessionManager session = SessionManager.getInstance(this);
		session.loadSession();
		user = session.getLoginCode();
		pass = session.getLoginPassword();
		if (!user.equals("") && !pass.equals("")) {
			username.setText(user);
			passwordEditText.setText(pass);
		}
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
						showDialog(DIALOG_CONNECTING);
						logintask = AuthenticationUtils.authenticate(user,
								pass, LoginActivity.this);

					}

				});

		findViewById(R.id.login_about).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						showDialog(DIALOG_ABOUT);
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
			break;
		default: {
			final String cookie = session.getCookie();
			final String type = session.getUser().getType();
			if (!cookie.equals("") && !type.equals("")) {
				SessionManager.getInstance().setCookie(cookie);
				SessionManager.getInstance()
						.setUser(new User(user, pass, type));
				startActivity(new Intent(this, HomeActivity.class));
				finish();
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
				return;
			}
			break;
		}
		}

	}

	private static final int DIALOG_CONNECTING = 3000;
	private static final int DIALOG_ABOUT = 3001;

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CONNECTING: {
			ProgressDialog progressDialog = new ProgressDialog(
					LoginActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(true);
			progressDialog.setMessage(getString(R.string.lb_login_cancel));
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (LoginActivity.this.logintask != null)
						LoginActivity.this.logintask.cancel(true);
					removeDialog(DIALOG_CONNECTING);
				}
			});
			progressDialog.setIndeterminate(false);
			return progressDialog;
		}
		case DIALOG_ABOUT: {
			AlertDialog.Builder aboutDialog = new Builder(this);
			aboutDialog.setTitle(R.string.bt_about_title).setNegativeButton(
					R.string.bt_cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							removeDialog(DIALOG_ABOUT);
						}
					}).setMessage(R.string.bt_about_message);
			AlertDialog welcomeAlert = aboutDialog.create();
			welcomeAlert.show();
			// Make the textview clickable. Must be called after show()
			((TextView) welcomeAlert.findViewById(android.R.id.message))
					.setMovementMethod(LinkMovementMethod.getInstance());
			return welcomeAlert;
		}
		}
		return null;
	}


	public void onError(ERROR_TYPE error) {
		removeDialog(DIALOG_CONNECTING);
		switch (error) {
		case AUTHENTICATION:
			final Animation shake = AnimationUtils.loadAnimation(this,
					R.anim.shake);
			Toast.makeText(this,
					getString(R.string.toast_login_error_wrong_password),
					Toast.LENGTH_LONG).show();
			// Remove stored password...
			passwordEditText.setText("");
			passwordEditText.startAnimation(shake);
			break;
		case NETWORK:
			Toast.makeText(this, getString(R.string.toast_server_error),
					Toast.LENGTH_LONG).show();
		default:
			// TODO: general error
			break;
		}
	}

	public void onResultReceived(Object... results) {
		removeDialog(DIALOG_CONNECTING);
		User user = (User) results[0];
		SessionManager.getInstance().setUser(new User(user.getUser(), pass, user.getType()));
		SessionManager.tuitionHistory.setLoaded(false);
		SessionManager.friends.setLoaded(false);
		startActivity(new Intent(LoginActivity.this, HomeActivity.class));
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

}
