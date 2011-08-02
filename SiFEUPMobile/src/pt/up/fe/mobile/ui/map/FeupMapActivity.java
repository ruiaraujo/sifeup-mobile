package pt.up.fe.mobile.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import pt.up.fe.mobile.ui.studentarea.ExamsFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import com.google.android.maps.MapView.LayoutParams;  

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

 

public class FeupMapActivity extends MapActivity {
	
	 MapView mapView; 
	 MapController mc;
	 GeoPoint p;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true); 
        mapView.displayZoomControls(true);
        
        mapView.setSatellite(true);
        
        
        mc = mapView.getController();
        
        //FEUP coordinates
        String coordinates[] = {"41.177992", "-8.595740"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        p = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mc.animateTo(p);
        mc.setZoom(17); 
        mapView.invalidate();
        
        
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
  

}
