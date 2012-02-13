package pt.up.fe.mobile.ui.utils;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class BogusViewPagerAdapter extends PagerAdapter {

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
    }

    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        return null;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return false;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

}
