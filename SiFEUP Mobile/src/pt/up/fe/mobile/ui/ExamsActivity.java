package pt.up.fe.mobile.ui;


import pt.up.fe.mobile.R;

import com.google.android.apps.iosched.ui.BaseSinglePaneActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ExamsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new ExamsFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  
	
    public static final int DIALOG_FETCHING = 3000;
	protected Dialog onCreateDialog(int id ) {
		switch (id) {
			case DIALOG_FETCHING: {
				ProgressDialog progressDialog =new ProgressDialog(ExamsActivity.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setCancelable(true);
				progressDialog.setMessage(getString(R.string.lb_data_fetching));
				progressDialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						removeDialog(DIALOG_FETCHING);
						finish();
					}
				});
				progressDialog.setIndeterminate(false);
				return progressDialog;
			}
		}
		return null;
	}    


}

