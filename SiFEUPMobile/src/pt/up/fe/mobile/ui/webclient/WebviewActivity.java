package pt.up.fe.mobile.ui.webclient;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class WebviewActivity extends BaseSinglePaneActivity {

    WebviewFragment fragment;
    

    @Override
    protected Fragment onCreatePane() {
        return fragment = new WebviewFragment();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( android.os.Build.VERSION.SDK_INT < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public void onBackPressed(){
        if ( fragment != null )
            fragment.onBackPressed();
        else
            super.onBackPressed();
    }
}
