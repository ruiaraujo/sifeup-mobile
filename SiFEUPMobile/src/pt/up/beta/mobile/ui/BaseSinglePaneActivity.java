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

package pt.up.beta.mobile.ui;

import external.com.google.android.apps.iosched.util.UIUtils;
import pt.up.beta.mobile.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;

/**
 * A {@link BaseActivity} that simply contains a single fragment. The intent
 * used to invoke this activity is forwarded to the fragment as arguments during
 * fragment instantiation. Derived activities should only need to implement
 * {@link pt.up.beta.mobile.ui.BaseSinglePaneActivity#onCreatePane()}.
 */
public abstract class BaseSinglePaneActivity extends BaseActivity {
	private Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singlepane_empty);
		
		final String customTitle = getIntent().getStringExtra(
				Intent.EXTRA_TITLE);
		if (customTitle != null)
			actionbar.setTitle(customTitle);
		
		if (!UIUtils.isTablet(getApplicationContext()))
		{			
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			drawerList = (ListView) findViewById(R.id.drawer_list);
			
			drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.icon, 0, 0) 
	        {
	            public void onDrawerClosed(View view) 
	            {
	            	if (customTitle != null)
	            		getSupportActionBar().setTitle(customTitle);
	            	
	            	super.onDrawerClosed(view);
	            }

	            public void onDrawerOpened(View drawerView) 
	            {
	            	getSupportActionBar().setTitle(R.string.app_name);
					super.onDrawerOpened(drawerView);
	            }
	        };
	        drawerLayout.setDrawerListener(drawerToggle);
		}

		if (savedInstanceState == null) {
			mFragment = onCreatePane();
			mFragment.setArguments(intentToFragmentArguments(getIntent()));
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.root_container, mFragment).commit();
		}
	}

	/**
	 * Called in <code>onCreate</code> when the fragment constituting this
	 * activity is needed. The returned fragment's arguments will be set to the
	 * intent used to invoke this activity.
	 */
	protected abstract Fragment onCreatePane();

}
