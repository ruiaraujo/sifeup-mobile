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
import pt.up.fe.mobile.service.SessionManager;

import com.google.android.apps.iosched.util.ActivityHelper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead, activities should
 * inherit from {@link BaseSinglePaneActivity} or {@link BaseMultiPaneActivity}.
 */
public abstract class BaseActivity extends FragmentActivity {
    final ActivityHelper mActivityHelper = new ActivityHelper(this);

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActivityHelper.onPostCreate(savedInstanceState);
    }

   @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyLongPress(keyCode, event) ||
                super.onKeyLongPress(keyCode, event);
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyDown(keyCode, event) ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mActivityHelper.onCreateOptionsMenu(menu) ||
        			super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActivityHelper.onOptionsItemSelected(item) ||
        			super.onOptionsItemSelected(item);
    }

    /**
     * Returns the {@link ActivityHelper} object associated with this activity.
     */
    protected ActivityHelper getActivityHelper() {
        return mActivityHelper;
    }
    
    protected  void onCreate( Bundle o){
    	super.onCreate(o);
    	//Recovering the Cookie here
    	// as every activity will descend from this one.
    	if ( SessionManager.getInstance().getCookie() == null)
    	{
            SharedPreferences loginSettings = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);  
            long now = System.currentTimeMillis();
            long before = loginSettings.getLong( LoginActivity.PREF_COOKIE_TIME, 0);
            String oldCookie = loginSettings.getString( LoginActivity.PREF_COOKIE, "");
            if ( ( ( now - before )/3600000 < 24 ) &&  !oldCookie.equals("") )
            {
            	SessionManager.getInstance().setCookie(oldCookie);
            	SessionManager.getInstance().setLoginCode(loginSettings.getString(
            										LoginActivity.PREF_USERNAME_SAVED, ""));
            	
            }
            else	
            {
            	goLogin(false);
            }
    	}
    		

    }
    

    /**
     * Takes a given intent and either starts a new activity to handle it (the default behavior),
     * or creates/updates a fragment (in the case of a multi-pane activity) that can handle the
     * intent.
     *
     * Must be called from the main (UI) thread.
     */
    public void openActivityOrFragment(Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
    
	public static final int DIALOG_FETCHING = 3000;
	protected Dialog onCreateDialog(int id ) {
		switch (id) {
			case DIALOG_FETCHING: {
				ProgressDialog progressDialog =new ProgressDialog(this);
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
	
	public void goLogin( boolean logOff ){
		Intent i = new Intent(this, LoginActivity.class);
		if ( logOff )
			i.putExtra(LoginActivity.EXTRA_DIFFERENT_LOGIN, true);
		startActivity(i);
		finish();
		overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
	}
	

}
