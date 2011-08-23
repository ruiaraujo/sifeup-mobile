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

package pt.up.fe.mobile.ui.studentarea;




import java.util.ArrayList;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Block;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.zylinc.view.ViewPagerIndicator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Lunch Menu Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ClassDescriptionFragment extends BaseFragment 
{  
	/**
     * The key for the student code in the intent.
     */
    final public static String BLOCK = "pt.up.fe.mobile.ui.studentarea.BLOCK";
    private Block block;
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Class Description");
        Bundle args = getArguments();
        block = (Block) args.get(BLOCK);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.class_description, getParentContainer() , true);
		TextView teacher = (TextView) root.findViewById(R.id.class_teacher);
		teacher.setText(getString(R.string.class_teacher, block.getTeacherAcronym()));
		teacher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "ola", Toast.LENGTH_SHORT).show();
			}
		});
		TextView room = (TextView) root.findViewById(R.id.class_room);
		room.setText(getString(R.string.class_room, block.getBuildingCode() + block.getRoomCode()));
		showMainScreen();
		return getParentContainer();//mandatory
	}
}
