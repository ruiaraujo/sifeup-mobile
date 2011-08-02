

package pt.up.fe.mobile.ui.news;


import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import pt.up.fe.mobile.R;

public class NewsDescFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/News");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.news_item, null);
    	String theStory = null;
         
         
        
    	Bundle b = getArguments();
    	if (b == null)
    	{
     		theStory = "bad bundle?";
     	}
     	else
 		{
     		theStory = "\n" + b.getString("description") + "\n\n" + 
     						getString(R.string.news_more_info)+"\n" + b.getString("link");
	        ((TextView) root.findViewById(R.id.news_desc_title)).setText(b.getString("title"));
	        ((TextView) root.findViewById(R.id.news_desc_time)).setText(b.getString("pubdate"));
 		}
        
 		((TextView) root.findViewById(R.id.news_desc)).setText(theStory);

    	return root;

    }
   

}