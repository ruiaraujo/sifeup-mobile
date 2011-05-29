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
import pt.up.fe.mobile.ui.tuition.TuitionMenuActivity;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment extends Fragment {

    public void fireTrackerEvent(String label) {
        AnalyticsUtils.getInstance(getActivity()).trackEvent(
                "Home Screen Dashboard", "Click", label, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container);

        // Attach event handlers
        root.findViewById(R.id.home_btn_schedule).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Schedule");
                startActivity(new Intent(getActivity(), ScheduleActivity.class));
                    
            }
            
        });
        
     
        root.findViewById(R.id.home_btn_friends).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Profile");
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                    
            }
            
        });

        root.findViewById(R.id.home_btn_subjects).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Subjects");
            }
        });


        root.findViewById(R.id.home_btn_tuition).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                fireTrackerEvent("Tuition");
                // Launch sessions list
                startActivity(new Intent(getActivity(), TuitionMenuActivity.class));                
            }
        });

        root.findViewById(R.id.home_btn_exams).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Launch map of conference venue
                fireTrackerEvent("Exams");
                startActivity(new Intent(getActivity(),ExamsActivity.class));
            }
        });

        root.findViewById(R.id.home_btn_printing).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        // splicing in tag streamer
                        fireTrackerEvent("Printing");
                        Intent intent = new Intent(getActivity(), PrintActivity.class);
                        startActivity(intent);
                    }
                });

        return root;
    }
}
