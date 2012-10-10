package pt.up.beta.mobile.ui;

import android.os.Bundle;

public interface FragmentOpener {
	
	public void openFragment(@SuppressWarnings("rawtypes") Class fragmentClass, Bundle arguments, CharSequence title);
}
