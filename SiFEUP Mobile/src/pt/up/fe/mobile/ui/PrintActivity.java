/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.fe.mobile.ui;


import pt.up.fe.mobile.R;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PrintActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new PrintFragment();
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
				ProgressDialog progressDialog =new ProgressDialog(PrintActivity.this);
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
