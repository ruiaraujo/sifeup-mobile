

package pt.up.beta.mobile.ui.news;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import pt.up.mobile.R;

/**
* This interface is responsible for displaying information 
* detailed of a report. Contains a link that allows see
* a full story in browser.
* 
* @author Ângela Igreja
* 
*/
public class NewsDescFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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