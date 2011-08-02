

package pt.up.fe.mobile.ui.news;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseActivity;

public class NewsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    /** News Feed from FEUP */
	public final String RSSFEEDOFCHOICE = "http://www.fe.up.pt/si/noticias_web.rss";
	private RSSFeed feed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/News");
        new NewsTask().execute(RSSFEEDOFCHOICE);

    }

    /** Classe privada para a busca de dados ao servidor */
    private class NewsTask extends AsyncTask<String, Void, RSSFeed> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(RSSFeed result) {
        	if ( getActivity() == null ) 
        		return;
        	if ( result != null )
        	{
				Log.e("News","success");
				 String[] from = new String[] {"title", "time"};
		         int[] to = new int[] { R.id.news_title, R.id.news_time};
			         // prepare the list of all records
		         List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		         for(RSSItem e : result.getAllItems()){
		             HashMap<String, String> map = new HashMap<String, String>();
		             map.put("time", e.getPubDate());
		             map.put("title", e.getTitle() );
		             fillMaps.add(map);
		         }
				
		         
				 
		         // fill in the grid_item layout		         
		         SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_news, from, to);

		         setListAdapter(adapter); 
		         getListView().setOnItemClickListener(NewsFragment.this);
		         setSelection(0);
		         Log.e("JSON", "news visual list loaded");

    		}
			else{	
				Log.e("News","error");
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					Toast.makeText(getActivity(), getString(R.string.news_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        }

		@Override
		protected RSSFeed doInBackground(String ... urls) {
		  	try {
		  		// To run the SAX parser on this background thread
	    			Looper.prepare();
	    			if ( urls.length < 1 )
	    				return null;
	    			URL url = new URL(urls[0]);

		           // create the factory
		           SAXParserFactory factory = SAXParserFactory.newInstance();
		           // create a parser
		           SAXParser parser = factory.newSAXParser();

		           // create the reader (scanner)
		           XMLReader xmlreader = parser.getXMLReader();
		           // instantiate our handler
		           RSSHandler theRssHandler = new RSSHandler();
		           // assign our handler
		           xmlreader.setContentHandler(theRssHandler);
		           // get our data via the url class
		           InputSource is = new InputSource(url.openStream());
		           is.setEncoding("ISO-8859-1");
		           // perform the synchronous parse           
		           xmlreader.parse(is);
		           // get the results - should be a fully populated RSSFeed instance, or null on error
		           return feed = theRssHandler.getFeed();
				
			} catch (Exception e) {
				e.printStackTrace();
			} 

			return null;
		}
    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1,  int position, long id){
		if ( feed == null )
    		return;
    	Intent itemintent = new Intent(getActivity(),NewsDescActivity.class);
       	itemintent.putExtra("title", feed.getItem(position).getTitle());
    	itemintent.putExtra("description", feed.getItem(position).getDescription());
    	itemintent.putExtra("link", feed.getItem(position).getLink());
    	itemintent.putExtra("pubdate", feed.getItem(position).getPubDate());
    	
         
        startActivity(itemintent);
	}
	
	
}
