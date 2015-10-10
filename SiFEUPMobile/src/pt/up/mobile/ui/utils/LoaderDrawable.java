package pt.up.mobile.ui.utils;

import pt.up.mobile.content.SigarraContract;
import pt.up.mobile.loaders.ProfilePicLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ImageView;

public class LoaderDrawable extends BitmapDrawable implements
		LoaderCallbacks<Bitmap> {
	private final ImageView image;
	private final Context context;
	private final String code;
	
	private static int LOADER_ID = LoaderDrawable.class.hashCode();

	public LoaderDrawable(LoaderManager loaderManager, ImageView image, String code, Context con, Bitmap bitmap) {
		super(con.getResources(), bitmap);
		this.image = image;
		this.context = con;
		this.code = code;
		loaderManager.restartLoader(LOADER_ID+code.hashCode(), null, this);
	}

	@Override
	public Loader<Bitmap> onCreateLoader(int loaderId, Bundle options) {
		return new ProfilePicLoader(context,
				SigarraContract.Profiles.PIC_CONTENT_URI,
				SigarraContract.Profiles.PIC_COLUMNS,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles.getProfilePicSelectionArgs(code), null);
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap bitmap) {
		if (bitmap == null)
			return;
		image.setImageBitmap(bitmap);
	}

	@Override
	public void onLoaderReset(Loader<Bitmap> loader) {
		image.setImageBitmap(getBitmap());
	}

}
