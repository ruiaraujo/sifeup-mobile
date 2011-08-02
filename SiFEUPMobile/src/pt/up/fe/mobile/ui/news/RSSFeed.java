package pt.up.fe.mobile.ui.news;


import java.util.ArrayList;
import java.util.List;


public class RSSFeed 
{
	private String title = null;
	private String pubdate = null;
	private int itemcount = 0;
	private List<RSSItem> itemlist;
	
	
	public RSSFeed()
	{
		itemlist = new ArrayList<RSSItem>(); 
	}
	
	public int addItem(RSSItem item)
	{
		itemlist.add(item);
		itemcount++;
		return itemcount;
	}
	
	public RSSItem getItem(int location)
	{
		return itemlist.get(location);
	}
	
	public List<RSSItem> getAllItems()
	{
		return itemlist;
	}
	
	public int getItemCount()
	{
		return itemcount;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public void setPubDate(String pubdate)
	{
		this.pubdate = pubdate;
	}
	public String getTitle()
	{
		return title;
	}
	public String getPubDate()
	{
		return pubdate;
	}
	
	
}
