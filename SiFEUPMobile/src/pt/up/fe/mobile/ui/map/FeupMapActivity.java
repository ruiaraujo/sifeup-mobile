package pt.up.fe.mobile.ui.map;



import pt.up.fe.mobile.R;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;




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
