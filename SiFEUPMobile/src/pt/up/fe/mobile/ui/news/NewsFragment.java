

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseFragment;

/**
 * Esta interface está responsável por ir buscar as notícias ao
 * servidor através dos feeds RSS e mostra-las em forma de
 * lista. Carregando num item da lista, é iniciada uma
 * Actividade NewsDescActivity.
 * TODO
 * 
 * This interface is responsible for fetching the news from the
 * server through the RSS feeds and displays them in the form of
 * list. Loading a list item starts a  Activity NewsDescActivity.
 * 
 * @author Ângela Igreja
 *
 */
public class NewsFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    /** News Feed from FEUP */
	private final String RSSFEEDOFCHOICE = "http://www.fe.up.pt/si/noticias_web.rss";
	private ListView list;
	private RSSFeed feed;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/News");

    }
    
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		 super.onCreateView(inflater, container, savedInstanceState);
		 View root = inflater.inflate(R.layout.generic_list, getParentContainer(), true);
         list = (ListView) root.findViewById(R.id.generic_list);
		 new NewsTask().execute(RSSFEEDOFCHOICE);
		 return getParentContainer(); //this is mandatory.
	 }
	 
    /** Classe privada para a busca de dados ao servidor */
    private class NewsTask extends AsyncTask<String, Void, RSSFeed> {

    	protected void onPreExecute (){
    		showLoadingScreen();
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

		         list.setAdapter(adapter); 
		         list.setOnItemClickListener(NewsFragment.this);
		         list.setSelection(0);
		         showMainScreen();
		         Log.e("JSON", "news visual list loaded");

    		}
			else{	
				Log.e("News","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.news_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
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
