package pt.up.mobile.ui.friends;

import java.util.List;

import pt.up.mobile.R;
import pt.up.mobile.datatypes.Friend;
import pt.up.mobile.ui.utils.LoaderDrawable;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendAdapter extends BaseAdapter {

	private final List<Friend> friends;
	private final LayoutInflater inflater;
	// private final ImageDownloader imageDownloader;
	private final Context context;
	private final LoaderManager loaderManager;

	public FriendAdapter(List<Friend> friends, LayoutInflater inflater,
			Context con, LoaderManager loaderManager) {
		this.friends = friends;
		this.inflater = inflater;
		this.context = con.getApplicationContext();
		this.loaderManager = loaderManager;
	}

	public int getCount() {
		return friends.size();
	}

	public Object getItem(int position) {
		return friends.get(position);
	}

	/**
	 * This is used in the list fragment for the context menu.
	 */
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_friend, null);
			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.friend_name);
			holder.course = (TextView) convertView
					.findViewById(R.id.friend_course);
			holder.pic = (ImageView) convertView.findViewById(R.id.friend_pic);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(friends.get(position).getName());
		holder.course.setText(friends.get(position).getCourse());
		holder.pic.setImageDrawable(new LoaderDrawable(loaderManager,
				holder.pic, friends.get(position).getCode(), context,
				((BitmapDrawable) holder.pic.getResources().getDrawable(
						R.drawable.speaker_image_empty)).getBitmap()));
		return convertView;
	}

	private static class ViewHolder {
		TextView name;
		TextView course;
		ImageView pic;
	}

}
