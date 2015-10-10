package pt.up.mobile.ui.news;

import java.util.ArrayList;
import java.util.List;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class RSSFeed implements Parcelable {
	private String title = null;
	private String pubdate = null;
	private int itemcount = 0;
	private List<RSSItem> itemlist;

	public RSSFeed() {
		itemlist = new ArrayList<RSSItem>();
	}

	public int addItem(RSSItem item) {
		itemlist.add(item);
		itemcount++;
		return itemcount;
	}

	public RSSItem getItem(int location) {
		return itemlist.get(location);
	}

	public List<RSSItem> getAllItems() {
		return itemlist;
	}

	public int getItemCount() {
		return itemcount;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public String getPubDate() {
		return pubdate;
	}

	private RSSFeed(Parcel in) {
		title = ParcelUtils.readString(in);
		pubdate = ParcelUtils.readString(in);
		itemcount = in.readInt();
		itemlist = new ArrayList<RSSItem>();
		in.readTypedList(itemlist, RSSItem.CREATOR);
	}

	public static final Parcelable.Creator<RSSFeed> CREATOR = new Parcelable.Creator<RSSFeed>() {
		public RSSFeed createFromParcel(Parcel in) {
			return new RSSFeed(in);
		}

		public RSSFeed[] newArray(int size) {
			return new RSSFeed[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, title);
		ParcelUtils.writeString(dest, pubdate);
		dest.writeInt(itemcount);
		dest.writeTypedList(itemlist);
	}
}
