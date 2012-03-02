package pt.up.fe.mobile.ui.services.tuition;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class TuitionActivity extends BaseSinglePaneActivity {
    @Override
    protected Fragment onCreatePane() {
        return new TuitionFragment();
    }

}
