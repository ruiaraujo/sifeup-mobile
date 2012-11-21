package pt.up.beta.mobile.ui.news;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class RSSItem implements Parcelable {
	private String title = null;
	private String description = null;
	private String link = null;
	private String category = null;
	private String pubdate = null;

	public RSSItem() {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPubDate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getLink() {
		return link;
	}

	public String getCategory() {
		return category;
	}

	public String getPubDate() {
		return pubdate;
	}

	public String toString() {
		return title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, title);
		ParcelUtils.writeString(dest, description);
		ParcelUtils.writeString(dest, link);
		ParcelUtils.writeString(dest, category);
		ParcelUtils.writeString(dest, pubdate);
	}

	private RSSItem(Parcel in) {
		title = ParcelUtils.readString(in);
		description = ParcelUtils.readString(in);
		link = ParcelUtils.readString(in);
		category = ParcelUtils.readString(in);
		pubdate = ParcelUtils.readString(in);
	}
	

	public static final Parcelable.Creator<RSSItem> CREATOR = new Parcelable.Creator<RSSItem>() {
		public RSSItem createFromParcel(Parcel in) {
			return new RSSItem(in);
		}

		public RSSItem[] newArray(int size) {
			return new RSSItem[size];
		}
	};
}
