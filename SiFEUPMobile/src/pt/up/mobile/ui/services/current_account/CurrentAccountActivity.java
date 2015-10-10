package pt.up.mobile.ui.services.current_account;

import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class CurrentAccountActivity extends BaseSinglePaneActivity {
    @Override
    protected Fragment onCreatePane() {
        return new CurrentAccountFragment();
    }

}
